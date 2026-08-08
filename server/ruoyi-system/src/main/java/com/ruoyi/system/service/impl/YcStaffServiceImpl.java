package com.ruoyi.system.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.YcStaff;
import com.ruoyi.system.mapper.YcStaffMapper;
import com.ruoyi.system.service.IYcStaffService;
@Service
public class YcStaffServiceImpl implements IYcStaffService {
    @Autowired private YcStaffMapper ycStaffMapper;
    @Override public YcStaff selectYcStaffById(Long staffId) { return ycStaffMapper.selectYcStaffById(staffId); }
    @Override public List<YcStaff> selectYcStaffList(YcStaff staff) { return ycStaffMapper.selectYcStaffList(staff); }
    @Override public Map<String, Object> selectStaffStats(Long activityId) {
        Map<String, Object> stats = ycStaffMapper.selectStaffStats(activityId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalCount", 0);
        }
        return stats;
    }
    private void fill(YcStaff s) {
        if (s.getPhone() == null) s.setPhone("");
        if (s.getRoleName() == null) s.setRoleName("");
        if (s.getSortOrder() == null) s.setSortOrder(0);
    }
    @Override public int insertYcStaff(YcStaff staff) { fill(staff); return ycStaffMapper.insertYcStaff(staff); }
    @Override public int updateYcStaff(YcStaff staff) { fill(staff); return ycStaffMapper.updateYcStaff(staff); }
    @Override public int deleteYcStaffByIds(Long[] staffIds) { return ycStaffMapper.deleteYcStaffByIds(staffIds); }
}