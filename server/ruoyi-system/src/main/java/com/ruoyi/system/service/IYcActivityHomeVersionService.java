package com.ruoyi.system.service;

import java.util.List;
import java.util.Date;
import com.ruoyi.system.domain.YcActivityHomeVersion;
import com.ruoyi.system.domain.YcActivityHomeDraft;

public interface IYcActivityHomeVersionService
{
    YcActivityHomeVersion selectYcActivityHomeVersionById(Long versionId);

    List<YcActivityHomeVersion> selectYcActivityHomeVersionList(YcActivityHomeVersion version);

    YcActivityHomeVersion selectLatestPublishedByActivityId(Long activityId);

    YcActivityHomeVersion saveDraft(YcActivityHomeVersion version);

    YcActivityHomeVersion saveDraft(YcActivityHomeDraft draft, String username);

    YcActivityHomeVersion publish(Long versionId, String publishedBy, String publishRemark);

    YcActivityHomeVersion schedule(Long versionId, Date publishAt, String scheduledBy);
}
