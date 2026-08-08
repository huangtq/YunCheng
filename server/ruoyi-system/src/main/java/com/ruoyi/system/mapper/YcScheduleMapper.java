package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcSchedule;
public interface YcScheduleMapper {
    YcSchedule selectYcScheduleById(Long scheduleId);
    List<YcSchedule> selectYcScheduleList(YcSchedule schedule);
    Map<String, Object> selectScheduleStats(Long activityId);
    List<YcSchedule> selectScheduleForExpert(Long activityId);
    int insertYcSchedule(YcSchedule schedule);
    int updateYcSchedule(YcSchedule schedule);
    int deleteYcScheduleByIds(Long[] scheduleIds);
}