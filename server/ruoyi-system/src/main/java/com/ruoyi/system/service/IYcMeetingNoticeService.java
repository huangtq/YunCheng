package com.ruoyi.system.service;
import java.util.List; import java.util.Map; import com.ruoyi.common.core.domain.model.MpLoginUser; import com.ruoyi.system.domain.YcMeetingNotice;
public interface IYcMeetingNoticeService { List<YcMeetingNotice> list(YcMeetingNotice query); int save(YcMeetingNotice notice); Map<String,Object> publicList(Long activityId, MpLoginUser user); Map<String,Object> detail(Long activityId, Long noticeId, MpLoginUser user); }
