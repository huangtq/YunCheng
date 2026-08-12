package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcActivityHomeVersion;

public interface YcActivityHomeVersionMapper
{
    YcActivityHomeVersion selectYcActivityHomeVersionById(Long versionId);

    List<YcActivityHomeVersion> selectYcActivityHomeVersionList(YcActivityHomeVersion version);

    YcActivityHomeVersion selectLatestPublishedByActivityId(Long activityId);

    Integer selectNextVersionNo(Long activityId);

    int insertYcActivityHomeVersion(YcActivityHomeVersion version);

    int updateYcActivityHomeVersion(YcActivityHomeVersion version);

    int archivePublishedByActivityId(Long activityId);

    int publishVersion(Long versionId, String publishedBy, String publishRemark);
}
