package com.ruoyi.system.service;
import java.util.Map;import com.ruoyi.common.core.domain.model.MpLoginUser;
public interface IYcMealService {Map<String,Object> myCoupons(Long activityId,MpLoginUser user);Map<String,Object> redeem(String credential,String activityId,String checkpoint,String operator,String deviceId,String idempotencyKey);Map<String,Object> revoke(Long couponId,String checkpoint,String operator,String deviceId,String idempotencyKey);int issue(Long activityId,Long ticketId,Long applyOrderId,String operator);}
