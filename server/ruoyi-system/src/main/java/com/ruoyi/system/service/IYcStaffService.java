package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcStaff;
public interface IYcStaffService {
    YcStaff selectYcStaffById(Long staffId);
    List<YcStaff> selectYcStaffList(YcStaff staff);
    Map<String, Object> selectStaffStats(Long activityId);
    int insertYcStaff(YcStaff staff);
    int updateYcStaff(YcStaff staff);
    int deleteYcStaffByIds(Long[] staffIds);
}