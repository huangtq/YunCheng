package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import jakarta.servlet.http.HttpServletRequest;

public interface IYcPortalMeetingService
{
    Map<String, Object> getActivityHome(Long activityId);

    Map<String, Object> getHomePage(Long activityId, MpLoginUser user);

    Map<String, Object> getPublicContent(Long activityId, Long contentId, MpLoginUser user);

    String getPublicAttachmentUrl(Long activityId, Long attachmentId, MpLoginUser user);

    List<Map<String, Object>> listActivities(String type);

    List<?> listGrid(Long activityId);

    List<?> listModule(String moduleKey, Long activityId);

    List<Map<String, Object>> listPublicSchedules(Long activityId);

    List<Map<String, Object>> listPublicGuests(Long activityId);

    List<Map<String, Object>> listPublicNavigation(Long activityId);

    Map<String, Object> listApplyChannels(Long activityId);

    YcApplyOrder submitApply(MpLoginUser user, Map<String, Object> body);

    List<YcApplyOrder> myApplyOrders(MpLoginUser user, Long activityId);

    Map<String, Object> hotelOverview(Long activityId, MpLoginUser user);

    Map<String, Object> myHotelOrders(MpLoginUser user, Long activityId);

    Map<String, Object> submitHotelOrder(MpLoginUser user, Map<String, Object> body);
}
