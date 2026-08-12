package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcMeetingContent;

public interface YcMeetingContentMapper
{
    YcMeetingContent selectYcMeetingContentById(Long contentId);

    List<YcMeetingContent> selectYcMeetingContentList(YcMeetingContent content);

    int insertYcMeetingContent(YcMeetingContent content);

    int updateYcMeetingContent(YcMeetingContent content);

    int deleteYcMeetingContentByIds(Long[] contentIds);
}
