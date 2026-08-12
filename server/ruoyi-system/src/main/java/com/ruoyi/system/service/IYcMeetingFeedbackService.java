package com.ruoyi.system.service;
import java.util.List; import java.util.Map; import com.ruoyi.common.core.domain.model.MpLoginUser; import com.ruoyi.system.domain.YcMeetingFeedback;
public interface IYcMeetingFeedbackService { int submit(Long activityId, Integer rating, String content, MpLoginUser user); List<YcMeetingFeedback> list(Long activityId); Map<String,Object> stats(Long activityId); }
