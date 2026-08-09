package com.ruoyi.system.service.impl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.mapper.MeetingResourceMigrationMapper;
import com.ruoyi.system.mapper.SysFileInfoMapper;
import com.ruoyi.system.service.IMeetingResourceMigrationService;

/**
 * 会议参考资源迁移服务实现。
 */
@Service
public class MeetingResourceMigrationServiceImpl implements IMeetingResourceMigrationService
{
    private static final Logger log = LoggerFactory.getLogger(MeetingResourceMigrationServiceImpl.class);

    private static final String RESOURCE_DIR = "static/reference/meeting-38569/";
    private static final String OLD_PREFIX = "/reference/meeting-38569/";
    private static final String NEW_PREFIX = "/profile/upload/reference/meeting-38569/";
    private static final Long ACTIVITY_ID = 12L;

    private static final List<String> RESOURCE_FILES = Arrays.asList(
            "agenda.jpg",
            "audio.mp3",
            "background.jpg",
            "contact.jpg",
            "guest.jpg",
            "guide.jpg",
            "hero.png",
            "notice.jpg",
            "support-logo.png",
            "tile-address.png",
            "tile-agenda.png",
            "tile-contact.png",
            "tile-guest.png",
            "tile-guide.png",
            "tile-live.png",
            "tile-notice.png",
            "tile-register.png");

    @Autowired
    private SysFileInfoMapper sysFileInfoMapper;

    @Autowired
    private MeetingResourceMigrationMapper migrationMapper;

    /**
     * 参考资源随服务启动自动迁移，避免增加一次性后台按钮。
     * 迁移过程按文件路径幂等，重复启动不会产生重复记录。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void migrateReferenceResourcesOnStartup()
    {
        try
        {
            Map<String, Object> result = migrateMeeting38569("system");
            log.info("会议参考资源迁移完成：复制{}个，登记{}个，替换{}条配置",
                    result.get("copiedCount"), result.get("registeredCount"), result.get("replacedCount"));
        }
        catch (Exception e)
        {
            log.warn("会议参考资源自动迁移失败，不影响服务启动：{}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> migrateMeeting38569(String createBy) throws Exception
    {
        Path targetDirectory = Paths.get(RuoYiConfig.getProfile(), "upload", "reference", "meeting-38569");
        Files.createDirectories(targetDirectory);

        int copiedCount = 0;
        int registeredCount = 0;
        for (String fileName : RESOURCE_FILES)
        {
            ClassPathResource resource = new ClassPathResource(RESOURCE_DIR + fileName);
            if (!resource.exists())
            {
                throw new IllegalStateException("参考资源不存在: " + RESOURCE_DIR + fileName);
            }

            Path targetPath = targetDirectory.resolve(fileName);
            try (InputStream inputStream = resource.getInputStream())
            {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            copiedCount++;

            String storedPath = NEW_PREFIX + fileName;
            SysFileInfo existing = sysFileInfoMapper.selectSysFileInfoByFileName(storedPath);
            if (existing == null)
            {
                SysFileInfo fileInfo = new SysFileInfo();
                fileInfo.setOriginalName(fileName);
                fileInfo.setFileName(storedPath);
                fileInfo.setUrl(storedPath);
                fileInfo.setActivityId(ACTIVITY_ID);
                fileInfo.setFileSuffix(getSuffix(fileName));
                fileInfo.setFileSize(resource.contentLength());
                fileInfo.setCreateBy(createBy);
                fileInfo.setCreateTime(DateUtils.getNowDate());
                fileInfo.setRemark("会议参考资源迁移：meeting-38569");
                sysFileInfoMapper.insertSysFileInfo(fileInfo);
                registeredCount++;
            }
            else if (existing.getActivityId() == null)
            {
                sysFileInfoMapper.updateSysFileInfoActivityIdByFileName(storedPath, ACTIVITY_ID);
            }
        }

        int replacedCount = 0;
        replacedCount += migrationMapper.replaceActivityCover(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceActivityConfigMedia(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceActivityGridMedia(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceGridBottomIcon(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceActivityNavCover(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceVenueCover(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceHotelCover(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceGuestAvatar(OLD_PREFIX, NEW_PREFIX);
        replacedCount += migrationMapper.replaceExhibitorLogo(OLD_PREFIX, NEW_PREFIX);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("resourceCount", RESOURCE_FILES.size());
        result.put("copiedCount", copiedCount);
        result.put("registeredCount", registeredCount);
        result.put("replacedCount", replacedCount);
        result.put("targetDirectory", targetDirectory.toString());
        result.put("targetPrefix", NEW_PREFIX);
        return result;
    }

    private String getSuffix(String fileName)
    {
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index + 1).toLowerCase() : "";
    }
}
