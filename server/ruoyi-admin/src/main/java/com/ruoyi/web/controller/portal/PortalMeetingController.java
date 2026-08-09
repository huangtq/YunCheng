package com.ruoyi.web.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IYcPortalMeetingService;

@Anonymous
@RestController
@RequestMapping("/portal/meeting")
public class PortalMeetingController extends BaseController
{
    @Autowired
    private IYcPortalMeetingService portalMeetingService;

    @GetMapping("/activity/{activityId}")
    public AjaxResult activity(@PathVariable Long activityId)
    {
        return success(portalMeetingService.getActivityHome(activityId));
    }

    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "current") String type)
    {
        return success(portalMeetingService.listActivities(type));
    }

    @GetMapping("/grid/{activityId}")
    public AjaxResult grid(@PathVariable Long activityId)
    {
        return success(portalMeetingService.listGrid(activityId));
    }

    @GetMapping("/menu/{activityId}")
    public AjaxResult menu(@PathVariable Long activityId)
    {
        // 兼容旧客户端；会议内容页导航与首页入口统一读取九宫格。
        return success(portalMeetingService.listMenu(activityId));
    }

    @GetMapping("/bottom/{activityId}")
    public AjaxResult bottom(@PathVariable Long activityId)
    {
        return success(portalMeetingService.listBottom(activityId));
    }

    @GetMapping("/module/{moduleKey}/{activityId}")
    public AjaxResult module(@PathVariable String moduleKey, @PathVariable Long activityId)
    {
        return success(portalMeetingService.listModule(moduleKey, activityId));
    }

    @GetMapping("/apply/channels/{activityId}")
    public AjaxResult channels(@PathVariable Long activityId)
    {
        return success(portalMeetingService.listApplyChannels(activityId));
    }
}