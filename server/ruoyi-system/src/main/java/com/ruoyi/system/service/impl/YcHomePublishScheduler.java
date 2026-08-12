package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.YcActivityHomeVersion;
import com.ruoyi.system.mapper.YcActivityHomeVersionMapperExtra;
import com.ruoyi.system.service.IYcActivityHomeVersionService;

/** Publishes due drafts through the same guarded transactional workflow as a manual release. */
@Component
public class YcHomePublishScheduler
{
    @Autowired private YcActivityHomeVersionMapperExtra extraMapper;
    @Autowired private IYcActivityHomeVersionService homeVersionService;

    @Scheduled(fixedDelay = 30000)
    public void publishDue()
    {
        List<YcActivityHomeVersion> due = extraMapper.selectDueScheduled();
        if (due == null) return;
        for (YcActivityHomeVersion version : due)
        {
            try { homeVersionService.publish(version.getVersionId(), "scheduler", "scheduled publication"); }
            catch (ServiceException ignored) { /* Leave the draft intact for the operator to correct. */ }
        }
    }
}
