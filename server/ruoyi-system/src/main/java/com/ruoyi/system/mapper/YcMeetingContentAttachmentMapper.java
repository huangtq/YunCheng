package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcMeetingContentAttachment;

public interface YcMeetingContentAttachmentMapper
{
    YcMeetingContentAttachment selectYcMeetingContentAttachmentById(Long attachmentId);

    List<YcMeetingContentAttachment> selectByContentId(Long contentId);

    int insertYcMeetingContentAttachment(YcMeetingContentAttachment attachment);

    int updateYcMeetingContentAttachment(YcMeetingContentAttachment attachment);

    int deleteYcMeetingContentAttachmentByIds(Long[] attachmentIds);
}
