package com.ruoyi.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Settings for the server-backed resource store used by local testing. */
@Component
@ConfigurationProperties(prefix = "ruoyi.test-resource")
public class TestResourceProperties
{
    private boolean enabled;
    private String host;
    private int port = 22;
    private String username;
    private String privateKey;
    private String knownHosts;
    private String remotePath;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPrivateKey()
    {
        return privateKey;
    }

    public void setPrivateKey(String privateKey)
    {
        this.privateKey = privateKey;
    }

    public String getKnownHosts()
    {
        return knownHosts;
    }

    public void setKnownHosts(String knownHosts)
    {
        this.knownHosts = knownHosts;
    }

    public String getRemotePath()
    {
        return remotePath;
    }

    public void setRemotePath(String remotePath)
    {
        this.remotePath = remotePath;
    }
}
