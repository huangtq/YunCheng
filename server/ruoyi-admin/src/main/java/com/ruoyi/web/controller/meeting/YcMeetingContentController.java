package com.ruoyi.web.controller.meeting;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcMeetingContent;
import com.ruoyi.system.domain.YcMeetingContentAttachment;
import com.ruoyi.system.service.IYcMeetingContentService;

@RestController
@RequestMapping("/meeting/content")
public class YcMeetingContentController extends BaseController
{
    @Autowired private IYcMeetingContentService contentService;

    @PreAuthorize("@ss.hasPermi('meeting:content:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcMeetingContent content)
    {
        startPage();
        List<YcMeetingContent> list = contentService.selectYcMeetingContentList(content);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:list')")
    @GetMapping("/{contentId}")
    public AjaxResult getInfo(@PathVariable Long contentId)
    {
        return success(contentService.selectYcMeetingContentById(contentId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:add')")
    @Log(title = "会议内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcMeetingContent content)
    {
        content.setCreateBy(getUsername());
        return toAjax(contentService.insertYcMeetingContent(content));
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:edit')")
    @Log(title = "会议内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcMeetingContent content)
    {
        content.setUpdateBy(getUsername());
        return toAjax(contentService.updateYcMeetingContent(content));
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:remove')")
    @Log(title = "会议内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds)
    {
        return toAjax(contentService.deleteYcMeetingContentByIds(contentIds));
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:list')")
    @GetMapping("/{contentId}/attachments")
    public AjaxResult attachments(@PathVariable Long contentId)
    {
        return success(contentService.selectAttachments(contentId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:edit')")
    @Log(title = "会议内容附件", businessType = BusinessType.UPDATE)
    @PostMapping("/attachment")
    public AjaxResult saveAttachment(@RequestBody YcMeetingContentAttachment attachment)
    {
        attachment.setCreateBy(getUsername());
        attachment.setUpdateBy(getUsername());
        return toAjax(contentService.saveAttachment(attachment));
    }

    @PreAuthorize("@ss.hasPermi('meeting:content:remove')")
    @Log(title = "会议内容附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/attachment/{attachmentIds}")
    public AjaxResult removeAttachments(@PathVariable Long[] attachmentIds)
    {
        return toAjax(contentService.deleteAttachments(attachmentIds));
    }
}
