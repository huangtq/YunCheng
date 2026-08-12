package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map; import org.apache.ibatis.annotations.Param; import com.ruoyi.system.domain.YcMeetingFeedback;
public interface YcMeetingFeedbackMapper { int insert(YcMeetingFeedback feedback); List<YcMeetingFeedback> selectList(@Param("activityId") Long activityId); Map<String,Object> selectStats(@Param("activityId") Long activityId); }
