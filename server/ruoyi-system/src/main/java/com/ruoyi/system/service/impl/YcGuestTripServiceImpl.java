package com.ruoyi.system.service.impl;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.domain.YcGuestTrip;
import com.ruoyi.system.mapper.YcGuestMapper;
import com.ruoyi.system.mapper.YcGuestTripMapper;
import com.ruoyi.system.service.IYcGuestTripService;
@Service
public class YcGuestTripServiceImpl implements IYcGuestTripService {
    @Autowired private YcGuestTripMapper ycGuestTripMapper;
    @Autowired private YcGuestMapper ycGuestMapper;
    @Override public YcGuestTrip selectYcGuestTripById(Long tripId) { return ycGuestTripMapper.selectYcGuestTripById(tripId); }
    @Override public List<YcGuestTrip> selectYcGuestTripList(YcGuestTrip trip) { return ycGuestTripMapper.selectYcGuestTripList(trip); }
    @Override public Map<String, Object> selectTripStats(Long activityId) {
        Map<String, Object> stats = ycGuestTripMapper.selectTripStats(activityId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalCount", 0); stats.put("comeCount", 0); stats.put("returnCount", 0); stats.put("issuedCount", 0);
        }
        return stats;
    }
    private void fill(YcGuestTrip t) {
        if (StringUtils.isEmpty(t.getTripType())) t.setTripType("come");
        if (StringUtils.isEmpty(t.getTransportType())) t.setTransportType("plane");
        if (t.getTransportNo() == null) t.setTransportNo("");
        if (t.getFromPlace() == null) t.setFromPlace("");
        if (t.getToPlace() == null) t.setToPlace("");
        if (t.getPrice() == null) t.setPrice(BigDecimal.ZERO);
        if (StringUtils.isEmpty(t.getTicketStatus())) t.setTicketStatus("0");
        if (StringUtils.isEmpty(t.getPickupStatus())) t.setPickupStatus("0");
    }
    private void assertGuest(YcGuestTrip trip) {
        if (trip.getGuestId() == null) throw new ServiceException("请选择嘉宾");
        YcGuest guest = ycGuestMapper.selectYcGuestById(trip.getGuestId());
        if (guest == null) throw new ServiceException("嘉宾不存在");
        if (trip.getActivityId() != null && !trip.getActivityId().equals(guest.getActivityId())) {
            throw new ServiceException("嘉宾不属于当前会议");
        }
        trip.setActivityId(guest.getActivityId());
    }
    @Override public int insertYcGuestTrip(YcGuestTrip trip) { assertGuest(trip); fill(trip); return ycGuestTripMapper.insertYcGuestTrip(trip); }
    @Override public int updateYcGuestTrip(YcGuestTrip trip) { assertGuest(trip); fill(trip); return ycGuestTripMapper.updateYcGuestTrip(trip); }
    @Override public int deleteYcGuestTripByIds(Long[] tripIds) { return ycGuestTripMapper.deleteYcGuestTripByIds(tripIds); }
}