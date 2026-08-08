package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcGuest;
public interface IYcGuestService {
    YcGuest selectYcGuestById(Long guestId);
    List<YcGuest> selectYcGuestList(YcGuest guest);
    Map<String, Object> selectGuestStats(Long activityId);
    int insertYcGuest(YcGuest guest);
    int updateYcGuest(YcGuest guest);
    int deleteYcGuestByIds(Long[] guestIds);
}