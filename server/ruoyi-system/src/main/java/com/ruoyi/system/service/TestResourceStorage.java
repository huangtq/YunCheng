package com.ruoyi.system.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.config.TestResourceProperties;

/**
 * Keeps local development resources mirrored with a server-only test directory.
 */
@Service
public class TestResourceStorage
{
    private static final Logger log = LoggerFactory.getLogger(TestResourceStorage.class);

    @Autowired
    private TestResourceProperties properties;

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup()
    {
        if (!properties.isEnabled())
        {
            return;
        }
        try
        {
            syncToLocal();
        }
        catch (Exception e)
        {
            log.error("Unable to synchronize test resources from the server. Existing local cache will be used.", e);
        }
    }

    public void upload(String fileName)
    {
        if (!properties.isEnabled())
        {
            return;
        }

        Path localFile = getLocalPath(fileName);
        if (!Files.isRegularFile(localFile))
        {
            throw new ServiceException("Local resource file does not exist: " + fileName);
        }

        SftpConnection connection = null;
        try
        {
            connection = openConnection();
            String remoteFile = getRemotePath(fileName);
            ensureDirectory(connection.sftp, getParentPath(remoteFile));
            connection.sftp.put(localFile.toString(), remoteFile);
        }
        catch (Exception e)
        {
            throw new ServiceException("Unable to upload the test resource to the server")
                    .setDetailMessage(e.getMessage());
        }
        finally
        {
            closeConnection(connection);
        }
    }

    public void delete(String fileName)
    {
        if (!properties.isEnabled())
        {
            return;
        }

        SftpConnection connection = null;
        try
        {
            connection = openConnection();
            connection.sftp.rm(getRemotePath(fileName));
        }
        catch (SftpException e)
        {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE)
            {
                throw new ServiceException("Unable to delete the test resource from the server")
                        .setDetailMessage(e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("Unable to delete the test resource from the server")
                    .setDetailMessage(e.getMessage());
        }
        finally
        {
            closeConnection(connection);
        }
    }

    private void syncToLocal() throws Exception
    {
        SftpConnection connection = null;
        try
        {
            connection = openConnection();
            Path localRoot = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
            Files.createDirectories(localRoot);
            downloadDirectory(connection.sftp, normalizeRemoteDirectory(properties.getRemotePath()), localRoot, localRoot);
            log.info("Synchronized test resources from {}", properties.getRemotePath());
        }
        finally
        {
            closeConnection(connection);
        }
    }

    private void downloadDirectory(ChannelSftp sftp, String remoteDirectory, Path localDirectory, Path localRoot)
            throws SftpException, java.io.IOException
    {
        Files.createDirectories(localDirectory);
        @SuppressWarnings("unchecked")
        Vector<ChannelSftp.LsEntry> entries = sftp.ls(remoteDirectory);
        for (ChannelSftp.LsEntry entry : entries)
        {
            String name = entry.getFilename();
            if (".".equals(name) || "..".equals(name))
            {
                continue;
            }

            Path localTarget = localDirectory.resolve(name).normalize();
            if (!localTarget.startsWith(localRoot))
            {
                throw new ServiceException("Invalid path in the test resource store");
            }

            String remoteTarget = remoteDirectory + "/" + name;
            if (entry.getAttrs().isDir())
            {
                downloadDirectory(sftp, remoteTarget, localTarget, localRoot);
            }
            else if (!entry.getAttrs().isLink())
            {
                Files.createDirectories(localTarget.getParent());
                sftp.get(remoteTarget, localTarget.toString());
            }
        }
    }

    private SftpConnection openConnection() throws JSchException
    {
        validateConfiguration();
        JSch jsch = new JSch();
        jsch.setKnownHosts(properties.getKnownHosts());
        jsch.addIdentity(properties.getPrivateKey());

        Session session = jsch.getSession(properties.getUsername(), properties.getHost(), properties.getPort());
        session.setConfig("StrictHostKeyChecking", "yes");
        session.connect(10000);

        Channel channel = session.openChannel("sftp");
        channel.connect(10000);
        return new SftpConnection(session, (ChannelSftp) channel);
    }

    private void validateConfiguration()
    {
        if (StringUtils.isEmpty(properties.getHost()) || StringUtils.isEmpty(properties.getUsername())
                || StringUtils.isEmpty(properties.getRemotePath()) || !properties.getRemotePath().startsWith("/"))
        {
            throw new ServiceException("Test resource storage configuration is incomplete");
        }
        if (!Files.isRegularFile(Paths.get(properties.getPrivateKey())) || !Files.isRegularFile(Paths.get(properties.getKnownHosts())))
        {
            throw new ServiceException("Test resource SSH key or known-hosts file is unavailable");
        }
    }

    private Path getLocalPath(String fileName)
    {
        String relativePath = getRelativePath(fileName);
        Path localRoot = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
        Path localFile = localRoot.resolve(relativePath.replace('/', File.separatorChar)).normalize();
        if (!localFile.startsWith(localRoot))
        {
            throw new ServiceException("Invalid test resource path");
        }
        return localFile;
    }

    private String getRemotePath(String fileName)
    {
        return normalizeRemoteDirectory(properties.getRemotePath()) + "/" + getRelativePath(fileName);
    }

    private String getRelativePath(String fileName)
    {
        if (StringUtils.isEmpty(fileName) || !fileName.startsWith(Constants.RESOURCE_PREFIX + "/"))
        {
            throw new ServiceException("Invalid test resource path");
        }
        String relativePath = fileName.substring((Constants.RESOURCE_PREFIX + "/").length());
        if (relativePath.contains("..") || relativePath.startsWith("/"))
        {
            throw new ServiceException("Invalid test resource path");
        }
        return relativePath;
    }

    private String normalizeRemoteDirectory(String directory)
    {
        return directory.endsWith("/") ? directory.substring(0, directory.length() - 1) : directory;
    }

    private String getParentPath(String path)
    {
        return path.substring(0, path.lastIndexOf('/'));
    }

    private void ensureDirectory(ChannelSftp sftp, String directory) throws SftpException
    {
        String[] segments = directory.split("/");
        String current = "";
        for (String segment : segments)
        {
            if (StringUtils.isEmpty(segment))
            {
                continue;
            }
            current += "/" + segment;
            try
            {
                sftp.stat(current);
            }
            catch (SftpException e)
            {
                if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE)
                {
                    throw e;
                }
                sftp.mkdir(current);
            }
        }
    }

    private void closeConnection(SftpConnection connection)
    {
        if (connection == null)
        {
            return;
        }
        if (connection.sftp.isConnected())
        {
            connection.sftp.disconnect();
        }
        if (connection.session.isConnected())
        {
            connection.session.disconnect();
        }
    }

    private static class SftpConnection
    {
        private final Session session;
        private final ChannelSftp sftp;

        private SftpConnection(Session session, ChannelSftp sftp)
        {
            this.session = session;
            this.sftp = sftp;
        }
    }
}
