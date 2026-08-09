package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 会议参考资源迁移数据层。
 */
public interface MeetingResourceMigrationMapper
{
    int replaceActivityCover(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceActivityConfigMedia(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceActivityGridMedia(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceGridBottomIcon(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceActivityNavCover(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceVenueCover(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceHotelCover(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceGuestAvatar(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);

    int replaceExhibitorLogo(@Param("oldPrefix") String oldPrefix, @Param("newPrefix") String newPrefix);
}
