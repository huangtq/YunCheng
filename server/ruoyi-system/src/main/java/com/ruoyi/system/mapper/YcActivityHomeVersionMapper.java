package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YcActivityHomeVersion;

public interface YcActivityHomeVersionMapper
{
    YcActivityHomeVersion selectYcActivityHomeVersionById(Long versionId);

    List<YcActivityHomeVersion> selectYcActivityHomeVersionList(YcActivityHomeVersion version);

    YcActivityHomeVersion selectLatestPublishedByActivityId(Long activityId);

    YcActivityHomeVersion selectLatestGridConfigDraftByActivityId(Long activityId);

    Integer selectNextVersionNo(Long activityId);

    int insertYcActivityHomeVersion(YcActivityHomeVersion version);

    int updateYcActivityHomeVersion(YcActivityHomeVersion version);

    int archivePublishedByActivityId(Long activityId);

    int archiveOtherGridConfigDraftsByActivityId(@Param("activityId") Long activityId, @Param("versionId") Long versionId);

    int publishVersion(Long versionId, String publishedBy, String publishRemark);
}
