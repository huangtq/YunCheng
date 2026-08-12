package com.ruoyi.web.controller.meeting;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IYcOnSiteService;
@RestController @RequestMapping("/meeting/checkin") public class YcCheckinController extends BaseController {
    @Autowired private IYcOnSiteService service;
    @PreAuthorize("@ss.hasPermi('meeting:checkin:verify')") @PostMapping("/verify") public AjaxResult verify(@RequestBody Map<String,Object> body) { return success(service.verify(str(body.get("credential")), str(body.get("activityId")), str(body.get("checkpoint")), getUsername(), str(body.get("deviceId")), str(body.get("idempotencyKey")))); }
    @PreAuthorize("@ss.hasPermi('meeting:checkin:revoke')") @PostMapping("/revoke") public AjaxResult revoke(@RequestBody Map<String,Object> body) { return success(service.revoke(Long.valueOf(str(body.get("applyOrderId"))),str(body.get("checkpoint")),getUsername(),str(body.get("deviceId")),str(body.get("idempotencyKey")))); }
    @PreAuthorize("@ss.hasPermi('meeting:checkin:verify')") @GetMapping("/stats/{activityId}") public AjaxResult stats(@PathVariable Long activityId) { return success(service.checkpointStats(activityId)); }
    private String str(Object value){return value==null?"":String.valueOf(value);}
}
