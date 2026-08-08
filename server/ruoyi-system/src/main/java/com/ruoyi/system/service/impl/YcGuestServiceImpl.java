package com.ruoyi.system.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.mapper.YcGuestMapper;
import com.ruoyi.system.service.IYcGuestService;
@Service
public class YcGuestServiceImpl implements IYcGuestService {
    @Autowired private YcGuestMapper ycGuestMapper;
    @Override public YcGuest selectYcGuestById(Long guestId) { return ycGuestMapper.selectYcGuestById(guestId); }
    @Override public List<YcGuest> selectYcGuestList(YcGuest guest) { return ycGuestMapper.selectYcGuestList(guest); }
    @Override public Map<String, Object> selectGuestStats(Long activityId) {
        Map<String, Object> stats = ycGuestMapper.selectGuestStats(activityId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalCount", 0); stats.put("attendCount", 0); stats.put("hotelCount", 0);
        }
        return stats;
    }
    private void fill(YcGuest g) {
        if (g.getPhone() == null) g.setPhone("");
        if (g.getOrgName() == null) g.setOrgName("");
        if (g.getTitle() == null) g.setTitle("");
        if (g.getEnglishName() == null) g.setEnglishName("");
        if (g.getGuestType() == null) g.setGuestType("");
        if (g.getAvatar() == null) g.setAvatar("");
        if (g.getIntro() == null) g.setIntro("");
        if (StringUtils.isEmpty(g.getNeedHotel())) g.setNeedHotel("0");
        if (g.getIdCard() == null) g.setIdCard("");
        if (StringUtils.isEmpty(g.getAttendFlag())) g.setAttendFlag("1");
        if (g.getSortOrder() == null) g.setSortOrder(0);
    }
    @Override public int insertYcGuest(YcGuest guest) { fill(guest); return ycGuestMapper.insertYcGuest(guest); }
    @Override public int updateYcGuest(YcGuest guest) { fill(guest); return ycGuestMapper.updateYcGuest(guest); }
    @Override
    @Transactional
    public int deleteYcGuestByIds(Long[] guestIds) {
        ycGuestMapper.deleteTripsByGuestIds(guestIds);
        ycGuestMapper.deleteFeesByGuestIds(guestIds);
        return ycGuestMapper.deleteYcGuestByIds(guestIds);
    }
}