package com.ruoyi.system.service.impl;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.system.domain.YcAttendeePass;
import com.ruoyi.system.domain.YcCheckinLog;
import com.ruoyi.system.mapper.YcApplyOrderMapper;
import com.ruoyi.system.mapper.YcAttendeePassMapper;
import com.ruoyi.system.mapper.YcCheckinLogMapper;
import com.ruoyi.system.service.IYcApplyOrderService;
import com.ruoyi.system.service.IYcOnSiteService;
@Service
public class YcOnSiteServiceImpl implements IYcOnSiteService {
    @Autowired private YcApplyOrderMapper orderMapper;
    @Autowired private YcAttendeePassMapper passMapper;
    @Autowired private YcCheckinLogMapper logMapper;
    @Autowired private IYcApplyOrderService orderService;
    @Value("${token.secret:meeting-onsite-secret}") private String secret;

    @Override @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> getMyAttendance(Long activityId, MpLoginUser user) {
        if (user == null || user.getUserId() == null) throw new ServiceException("please login via wechat", 401);
        List<YcApplyOrder> orders = orderMapper.selectPortalUserOrders(activityId, user.getOpenid(), user.getPhone(), "0");
        Map<String,Object> result = new HashMap<>(); result.put("activityId", activityId); result.put("registered", orders != null && !orders.isEmpty());
        if (orders == null || orders.isEmpty()) { result.put("status", "not_registered"); return result; }
        YcApplyOrder order = orders.get(0); YcAttendeePass pass = passMapper.selectByOrderId(order.getOrderId());
        if (pass == null) { pass = new YcAttendeePass(); pass.setActivityId(activityId); pass.setApplyOrderId(order.getOrderId()); pass.setPassStatus("active"); pass.setCreateBy(user.getOpenid()); passMapper.insert(pass); pass = passMapper.selectByOrderId(order.getOrderId()); }
        Map<String,Object> attendee = new HashMap<>(); attendee.put("name", order.getContactName()); attendee.put("company", order.getCompany()); attendee.put("orderStatus", order.getOrderStatus()); attendee.put("checkinStatus", order.getCheckinStatus()); attendee.put("checkinTime", order.getCheckinTime());
        result.put("status", "checked_in".equals(order.getCheckinStatus()) ? "checked_in" : "ready"); result.put("attendee", attendee); result.put("pass", passView(pass)); return result;
    }
    private Map<String,Object> passView(YcAttendeePass pass) { Map<String,Object> view = new HashMap<>(); view.put("passId", pass.getPassId()); view.put("status", pass.getPassStatus()); view.put("credential", credential(pass, System.currentTimeMillis() / 1000L)); view.put("expiresIn", 60); return view; }
    private String credential(YcAttendeePass pass, long now) { String payload = pass.getPassId()+"."+(now+60); return payload+"."+sign(payload); }
    private String sign(String value) { try { Mac mac=Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")); return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8))); } catch (Exception e) { throw new ServiceException("credential signing failed"); } }
    @Override @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> verify(String credential, String activityIdValue, String checkpoint, String operator, String deviceId, String idempotencyKey) {
        if (StringUtils.isEmpty(credential) || StringUtils.isEmpty(activityIdValue)) throw new ServiceException("credential and activityId required");
        if (StringUtils.isEmpty(idempotencyKey)) idempotencyKey = credential;
        YcCheckinLog previous = logMapper.selectByIdempotencyKey(idempotencyKey); if (previous != null) return verifyResult(previous.getResult(), previous.getReason(), previous.getApplyOrderId());
        String[] parts = credential.split("\\."); if (parts.length != 3) throw new ServiceException("invalid credential");
        long expiry; try { expiry = Long.parseLong(parts[1]); } catch (NumberFormatException e) { throw new ServiceException("invalid credential"); }
        if (expiry < System.currentTimeMillis()/1000L || !MessageDigest.isEqual(sign(parts[0]+"."+parts[1]).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) throw new ServiceException("credential expired or invalid");
        Long passId; Long activityId; try { passId=Long.valueOf(parts[0]); activityId=Long.valueOf(activityIdValue); } catch (NumberFormatException e) { throw new ServiceException("invalid credential"); }
        YcAttendeePass pass=passMapper.selectById(passId); YcApplyOrder order=pass==null?null:orderMapper.selectYcApplyOrderById(pass.getApplyOrderId());
        if (pass==null || order==null || !activityId.equals(pass.getActivityId()) || !"active".equals(pass.getPassStatus()) || !"0".equals(order.getOrderStatus())) throw new ServiceException("credential not available");
        String result="success", reason="";
        if ("1".equals(order.getCheckinStatus()) || orderService.checkin(order.getOrderId(), operator) != 1) result="already_checked_in";
        YcCheckinLog log=new YcCheckinLog(); log.setActivityId(activityId); log.setPassId(passId); log.setApplyOrderId(order.getOrderId()); log.setCheckinType("general"); log.setAction("checkin"); log.setCheckpoint(checkpoint==null?"":checkpoint); log.setOperatorName(operator==null?"":operator); log.setDeviceId(deviceId==null?"":deviceId); log.setOccurredTime(new Date()); log.setIdempotencyKey(idempotencyKey); log.setResult(result); log.setReason(reason); logMapper.insert(log); return verifyResult(result, reason, order.getOrderId());
    }
    @Override @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> revoke(Long applyOrderId, String checkpoint, String operator, String deviceId, String idempotencyKey) {
        if (applyOrderId == null) throw new ServiceException("applyOrderId required");
        if (StringUtils.isEmpty(idempotencyKey)) idempotencyKey="revoke:"+applyOrderId+":"+System.currentTimeMillis();
        YcCheckinLog prior=logMapper.selectByIdempotencyKey(idempotencyKey); if(prior!=null)return verifyResult(prior.getResult(),prior.getReason(),prior.getApplyOrderId());
        YcApplyOrder order=orderMapper.selectYcApplyOrderById(applyOrderId); if(order==null)throw new ServiceException("registration order not found");
        YcAttendeePass pass=passMapper.selectByOrderId(applyOrderId); if(pass==null)throw new ServiceException("attendee pass not found");
        String result=orderMapper.revokeCheckin(applyOrderId,operator)==1?"success":"not_checked_in";
        YcCheckinLog log=new YcCheckinLog(); log.setActivityId(order.getActivityId()); log.setPassId(pass.getPassId()); log.setApplyOrderId(applyOrderId); log.setCheckinType("general"); log.setAction("revoke"); log.setCheckpoint(checkpoint==null?"":checkpoint); log.setOperatorName(operator==null?"":operator); log.setDeviceId(deviceId==null?"":deviceId); log.setOccurredTime(new Date()); log.setIdempotencyKey(idempotencyKey); log.setResult(result); log.setReason(""); logMapper.insert(log); return verifyResult(result,"",applyOrderId);
    }
    @Override public List<Map<String,Object>> checkpointStats(Long activityId) { if(activityId==null)throw new ServiceException("activityId required"); return logMapper.selectCheckpointStats(activityId); }
    private Map<String,Object> verifyResult(String result, String reason, Long orderId) { Map<String,Object> map=new HashMap<>(); map.put("result", result); map.put("reason", reason); map.put("orderId", orderId); return map; }
}
