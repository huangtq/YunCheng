package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcMeetingContent;
import com.ruoyi.system.domain.YcMeetingContentAttachment;
import com.ruoyi.system.mapper.YcMeetingContentAttachmentMapper;
import com.ruoyi.system.mapper.YcMeetingContentMapper;
import com.ruoyi.system.mapper.YcActivityMapper;
import com.ruoyi.system.service.IYcMeetingContentService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Service
public class YcMeetingContentServiceImpl implements IYcMeetingContentService
{
    @Autowired private YcMeetingContentMapper contentMapper;
    @Autowired private YcMeetingContentAttachmentMapper attachmentMapper;
    @Autowired private YcActivityMapper activityMapper;

    @Override public YcMeetingContent selectYcMeetingContentById(Long contentId) { return contentMapper.selectYcMeetingContentById(contentId); }
    @Override public List<YcMeetingContent> selectYcMeetingContentList(YcMeetingContent content) { return contentMapper.selectYcMeetingContentList(content); }

    @Override
    public int insertYcMeetingContent(YcMeetingContent content)
    {
        normalizeContent(content);
        return contentMapper.insertYcMeetingContent(content);
    }

    @Override
    public int updateYcMeetingContent(YcMeetingContent content)
    {
        normalizeContent(content);
        return contentMapper.updateYcMeetingContent(content);
    }

    @Override public int deleteYcMeetingContentByIds(Long[] contentIds) { return contentMapper.deleteYcMeetingContentByIds(contentIds); }
    @Override public List<YcMeetingContentAttachment> selectAttachments(Long contentId) { return attachmentMapper.selectByContentId(contentId); }

    @Override
    public int saveAttachment(YcMeetingContentAttachment attachment)
    {
        if (attachment == null || attachment.getContentId() == null || StringUtils.isEmpty(attachment.getFileName()) || StringUtils.isEmpty(attachment.getFileUrl()))
        {
            throw new ServiceException("contentId, fileName and fileUrl required");
        }
        if (contentMapper.selectYcMeetingContentById(attachment.getContentId()) == null)
        {
            throw new ServiceException("content not found");
        }
        if (!attachment.getFileUrl().startsWith("/") && !attachment.getFileUrl().startsWith("https://") && !attachment.getFileUrl().startsWith("http://"))
        {
            throw new ServiceException("attachment fileUrl must be a local path or http/https URL");
        }
        if (StringUtils.isEmpty(attachment.getVisibility())) attachment.setVisibility("public");
        if (!"public".equals(attachment.getVisibility()) && !"login".equals(attachment.getVisibility()) && !"registered".equals(attachment.getVisibility()))
        {
            throw new ServiceException("attachment visibility must be public, login or registered");
        }
        if (StringUtils.isEmpty(attachment.getStatus())) attachment.setStatus("1");
        if (!"0".equals(attachment.getStatus()) && !"1".equals(attachment.getStatus()))
        {
            throw new ServiceException("attachment status must be 0 or 1");
        }
        if (attachment.getValidStart() != null && attachment.getValidEnd() != null && attachment.getValidStart().after(attachment.getValidEnd()))
        {
            throw new ServiceException("attachment validStart must be before validEnd");
        }
        if (attachment.getSortOrder() == null) attachment.setSortOrder(0);
        return attachment.getAttachmentId() == null
            ? attachmentMapper.insertYcMeetingContentAttachment(attachment)
            : attachmentMapper.updateYcMeetingContentAttachment(attachment);
    }

    @Override public int deleteAttachments(Long[] attachmentIds) { return attachmentMapper.deleteYcMeetingContentAttachmentByIds(attachmentIds); }

    private void normalizeContent(YcMeetingContent content)
    {
        if (content == null || content.getActivityId() == null || StringUtils.isEmpty(content.getTitle()))
        {
            throw new ServiceException("activityId and title required");
        }
        if (activityMapper.selectYcActivityById(content.getActivityId()) == null)
        {
            throw new ServiceException("activity not found");
        }
        if (StringUtils.isEmpty(content.getVisibility())) content.setVisibility("public");
        if (!"public".equals(content.getVisibility()) && !"login".equals(content.getVisibility()) && !"registered".equals(content.getVisibility()))
        {
            throw new ServiceException("content visibility must be public, login or registered");
        }
        if (StringUtils.isEmpty(content.getStatus())) content.setStatus("draft");
        if (!"draft".equals(content.getStatus()) && !"published".equals(content.getStatus()) && !"archived".equals(content.getStatus()))
        {
            throw new ServiceException("content status must be draft, published or archived");
        }
        if (content.getValidStart() != null && content.getValidEnd() != null && content.getValidStart().after(content.getValidEnd()))
        {
            throw new ServiceException("content validStart must be before validEnd");
        }
        content.setContentHtml(sanitizeHtml(content.getContentHtml()));
        if (content.getSortOrder() == null) content.setSortOrder(0);
    }

    /** Keep rich text useful while removing executable markup and unsafe protocols. */
    private String sanitizeHtml(String html)
    {
        if (StringUtils.isEmpty(html)) return "";
        Safelist allowList = Safelist.relaxed()
            .addTags("figure", "figcaption", "section")
            .addAttributes("a", "target", "rel")
            .addProtocols("a", "href", "http", "https")
            .addProtocols("img", "src", "http", "https");
        return Jsoup.clean(html, "", allowList);
    }
}
