package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcVenue;
public interface IYcVenueService {
    YcVenue selectYcVenueById(Long venueId);
    List<YcVenue> selectYcVenueList(YcVenue venue);
    Map<String, Object> selectVenueStats(Long activityId);
    int insertYcVenue(YcVenue venue);
    int updateYcVenue(YcVenue venue);
    int deleteYcVenueByIds(Long[] venueIds);
}