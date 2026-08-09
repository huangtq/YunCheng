package com.ruoyi.system.service;

import java.util.Map;

/**
 * 会议参考资源迁移服务。
 */
public interface IMeetingResourceMigrationService
{
    /**
     * 将会议参考资源复制到文件管理目录并替换业务配置路径。
     *
     * @param createBy 创建者
     * @return 迁移统计
     */
    Map<String, Object> migrateMeeting38569(String createBy) throws Exception;
}
