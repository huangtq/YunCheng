package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcGuestTrip;
public interface YcGuestTripMapper {
    YcGuestTrip selectYcGuestTripById(Long tripId);
    List<YcGuestTrip> selectYcGuestTripList(YcGuestTrip trip);
    Map<String, Object> selectTripStats(Long activityId);
    int insertYcGuestTrip(YcGuestTrip trip);
    int updateYcGuestTrip(YcGuestTrip trip);
    int deleteYcGuestTripByIds(Long[] tripIds);
}