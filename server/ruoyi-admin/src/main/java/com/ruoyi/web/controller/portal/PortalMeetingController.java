package com.ruoyi.web.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.YcActivityGridAttachment;
import com.ruoyi.system.service.IYcPortalMeetingService;
import com.ruoyi.system.service.IYcMeetingNoticeService;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.framework.web.service.MpTokenService;
import jakarta.servlet.http.HttpServletRequest;

@Anonymous
@RestController
@RequestMapping("/portal/meeting")
public class PortalMeetingController extends BaseController
{
    @Autowired
    private IYcPortalMeetingService portalMeetingService;
    @Autowired
    private MpTokenService mpTokenService;
    @Autowired private IYcMeetingNoticeService noticeService;

    @GetMapping("/home/{activityId}")
    public AjaxResult home(@PathVariable Long activityId, HttpServletRequest request)
    {
        MpLoginUser user = mpTokenService.getLoginUser(request);
        return success(portalMeetingService.getHomePage(activityId, user));
    }

    @GetMapping("/content/{activityId}/{contentId}")
    public AjaxResult content(@PathVariable Long activityId, @PathVariable Long contentId, HttpServletRequest request)
    {
        return success(portalMeetingService.getPublicContent(activityId, contentId, mpTokenService.getLoginUser(request)));
    }

    @GetMapping("/content/{activityId}/attachment/{attachmentId}")
    public void downloadAttachment(@PathVariable Long activityId, @PathVariable Long attachmentId,
        HttpServletRequest request, HttpServletResponse response) throws java.io.IOException
    {
        response.sendRedirect(portalMeetingService.getPublicAttachmentUrl(activityId, attachmentId,
            mpTokenService.getLoginUser(request)));
    }

    @GetMapping("/grid/{activityId}/attachment/{gridId}/{attachmentId}")
    public void downloadGridAttachment(@PathVariable Long activityId, @PathVariable Long gridId,
        @PathVariable Long attachmentId, HttpServletResponse response) throws java.io.IOException
    {
        YcActivityGridAttachment attachment = portalMeetingService.getPublicGridAttachment(activityId, gridId, attachmentId);
        String fileUrl = attachment.getFileUrl();
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://"))
        {
            response.sendRedirect(fileUrl);
            return;
        }
        if (!FileUtils.checkAllowDownload(fileUrl))
        {
            throw new IllegalArgumentException("附件地址非法");
        }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        FileUtils.setAttachmentResponseHeader(response, attachment.getDownloadName());
        String filePath = RuoYiConfig.getProfile() + FileUtils.stripPrefix(fileUrl);
        FileUtils.writeBytes(filePath, response.getOutputStream());
    }

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

    @GetMapping("/schedule/{activityId}")
    public AjaxResult schedule(@PathVariable Long activityId)
    {
        return success(portalMeetingService.listPublicSchedules(activityId));
    }

    @GetMapping("/guest/{activityId}")
    public AjaxResult guest(@PathVariable Long activityId)
    {
        return success(portalMeetingService.listPublicGuests(activityId));
    }

    @GetMapping("/navigation/{activityId}")
    public AjaxResult navigation(@PathVariable Long activityId)
    {
        return success(portalMeetingService.listPublicNavigation(activityId));
    }

    @GetMapping("/hotel/{activityId}")
    public AjaxResult hotel(@PathVariable Long activityId, HttpServletRequest request)
    {
        return success(portalMeetingService.hotelOverview(activityId, mpTokenService.getLoginUser(request)));
    }

    @GetMapping("/notice/{activityId}")
    public AjaxResult notices(@PathVariable Long activityId, HttpServletRequest request)
    {
        return success(noticeService.publicList(activityId, mpTokenService.getLoginUser(request)));
    }

    @GetMapping("/notice/{activityId}/{noticeId}")
    public AjaxResult notice(@PathVariable Long activityId, @PathVariable Long noticeId, HttpServletRequest request)
    {
        return success(noticeService.detail(activityId, noticeId, mpTokenService.getLoginUser(request)));
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
