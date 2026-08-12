package com.ruoyi.system.service;

import java.util.List;
import java.util.Date;
import com.ruoyi.system.domain.YcActivityHomeVersion;

public interface IYcActivityHomeVersionService
{
    YcActivityHomeVersion selectYcActivityHomeVersionById(Long versionId);

    List<YcActivityHomeVersion> selectYcActivityHomeVersionList(YcActivityHomeVersion version);

    YcActivityHomeVersion selectLatestPublishedByActivityId(Long activityId);

    YcActivityHomeVersion saveDraft(YcActivityHomeVersion version);

    YcActivityHomeVersion publish(Long versionId, String publishedBy, String publishRemark);

    YcActivityHomeVersion schedule(Long versionId, Date publishAt, String scheduledBy);
}
