package com.ruoyi.web.controller.portal;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.framework.web.service.MpTokenService;
import com.ruoyi.system.service.IYcPortalMeetingService;
import com.ruoyi.system.service.IYcMealService;
import jakarta.servlet.http.HttpServletRequest;

@Anonymous
@RestController
@RequestMapping("/portal/meeting/apply")
public class PortalApplyController extends BaseController
{
    @Autowired
    private IYcPortalMeetingService portalMeetingService;
    @Autowired
    private MpTokenService mpTokenService;
    @Autowired private IYcMealService mealService;

    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody Map<String, Object> body, HttpServletRequest request)
    {
        MpLoginUser user = mpTokenService.requireLoginUser(request);
        return success(portalMeetingService.submitApply(user, body));
    }

    @GetMapping("/my")
    public AjaxResult my(@RequestParam(required = false) Long activityId, HttpServletRequest request)
    {
        MpLoginUser user = mpTokenService.requireLoginUser(request);
        return success(portalMeetingService.myApplyOrders(user, activityId));
    }

    @GetMapping("/meal-coupons")
    public AjaxResult mealCoupons(@RequestParam Long activityId, HttpServletRequest request)
    {
        return success(mealService.myCoupons(activityId, mpTokenService.requireLoginUser(request)));
    }

    @GetMapping("/hotel/orders")
    public AjaxResult hotelOrders(@RequestParam Long activityId, HttpServletRequest request)
    {
        return success(portalMeetingService.myHotelOrders(mpTokenService.requireLoginUser(request), activityId));
    }

    @PostMapping("/hotel/order")
    public AjaxResult bookHotel(@RequestBody Map<String, Object> body, HttpServletRequest request)
    {
        return success(portalMeetingService.submitHotelOrder(mpTokenService.requireLoginUser(request), body));
    }
}
