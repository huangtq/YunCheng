package com.ruoyi.system.service;
import java.util.Map; import com.ruoyi.common.core.domain.model.MpLoginUser;
public interface IYcMeetingAnalyticsService { void record(Long activityId,String entryId,String eventType,Map<String,Object> context,MpLoginUser user); Map<String,Object> dashboard(Long activityId); }
