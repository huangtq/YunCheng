package com.ruoyi.system.service;
import java.util.Map;
import com.ruoyi.common.core.domain.model.MpLoginUser;
public interface IYcOnSiteService {
    Map<String,Object> getMyAttendance(Long activityId, MpLoginUser user);
    Map<String,Object> verify(String credential, String activityId, String checkpoint, String operator, String deviceId, String idempotencyKey);
    Map<String,Object> revoke(Long applyOrderId, String checkpoint, String operator, String deviceId, String idempotencyKey);
    java.util.List<Map<String,Object>> checkpointStats(Long activityId);
}
