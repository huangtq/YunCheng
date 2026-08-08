package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcStaff;
public interface YcStaffMapper {
    YcStaff selectYcStaffById(Long staffId);
    List<YcStaff> selectYcStaffList(YcStaff staff);
    Map<String, Object> selectStaffStats(Long activityId);
    int insertYcStaff(YcStaff staff);
    int updateYcStaff(YcStaff staff);
    int deleteYcStaffByIds(Long[] staffIds);
}