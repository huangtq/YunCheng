package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcSchedule;
public interface IYcScheduleService {
    YcSchedule selectYcScheduleById(Long scheduleId);
    List<YcSchedule> selectYcScheduleList(YcSchedule schedule);
    Map<String, Object> selectScheduleStats(Long activityId);
    List<Map<String, Object>> selectExpertTasks(Long activityId, String expertName, String role, String onlyConflict);
    int insertYcSchedule(YcSchedule schedule);
    int updateYcSchedule(YcSchedule schedule);
    int deleteYcScheduleByIds(Long[] scheduleIds);
}