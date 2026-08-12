package com.ruoyi.system.mapper;
import java.util.List; import org.apache.ibatis.annotations.Param; import com.ruoyi.system.domain.YcMeetingNotice;
public interface YcMeetingNoticeMapper { List<YcMeetingNotice> selectList(YcMeetingNotice query); YcMeetingNotice selectById(Long noticeId); int insert(YcMeetingNotice notice); int update(YcMeetingNotice notice); int markRead(@Param("noticeId") Long noticeId,@Param("userId") Long userId); boolean hasRead(@Param("noticeId") Long noticeId,@Param("userId") Long userId); }
