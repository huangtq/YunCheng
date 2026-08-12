package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YcMeetingContent;
import com.ruoyi.system.domain.YcMeetingContentAttachment;

public interface IYcMeetingContentService
{
    YcMeetingContent selectYcMeetingContentById(Long contentId);

    List<YcMeetingContent> selectYcMeetingContentList(YcMeetingContent content);

    int insertYcMeetingContent(YcMeetingContent content);

    int updateYcMeetingContent(YcMeetingContent content);

    int deleteYcMeetingContentByIds(Long[] contentIds);

    List<YcMeetingContentAttachment> selectAttachments(Long contentId);

    int saveAttachment(YcMeetingContentAttachment attachment);

    int deleteAttachments(Long[] attachmentIds);
}
