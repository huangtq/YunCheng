package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.common.core.domain.model.MpLoginUser;

public interface IYcPortalMeetingService
{
    Map<String, Object> getActivityHome(Long activityId);

    List<Map<String, Object>> listActivities(String type);

    List<?> listGrid(Long activityId);

    List<?> listBottom(Long activityId);

    List<?> listModule(String moduleKey, Long activityId);

    Map<String, Object> listApplyChannels(Long activityId);

    YcApplyOrder submitApply(MpLoginUser user, Map<String, Object> body);

    List<YcApplyOrder> myApplyOrders(MpLoginUser user, Long activityId);
}
