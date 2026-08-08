package com.ruoyi.system.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcVenue;
import com.ruoyi.system.mapper.YcVenueMapper;
import com.ruoyi.system.service.IYcVenueService;
@Service
public class YcVenueServiceImpl implements IYcVenueService {
    @Autowired private YcVenueMapper ycVenueMapper;
    @Override public YcVenue selectYcVenueById(Long venueId) { return ycVenueMapper.selectYcVenueById(venueId); }
    @Override public List<YcVenue> selectYcVenueList(YcVenue venue) { return ycVenueMapper.selectYcVenueList(venue); }
    @Override public Map<String, Object> selectVenueStats(Long activityId) {
        Map<String, Object> stats = ycVenueMapper.selectVenueStats(activityId);
        if (stats == null) { stats = new HashMap<>(); stats.put("totalCount",0); stats.put("liveCount",0); stats.put("normalCount",0); }
        return stats;
    }
    private void fill(YcVenue v) {
        if (StringUtils.isEmpty(v.getIsLive())) v.setIsLive("0");
        if (v.getCoverUrl()==null) v.setCoverUrl("");
        if (StringUtils.isEmpty(v.getLiveStatus())) v.setLiveStatus("0");
        if (v.getSortOrder()==null) v.setSortOrder(0);
    }
    @Override public int insertYcVenue(YcVenue venue) { fill(venue); return ycVenueMapper.insertYcVenue(venue); }
    @Override public int updateYcVenue(YcVenue venue) { fill(venue); return ycVenueMapper.updateYcVenue(venue); }
    @Override public int deleteYcVenueByIds(Long[] venueIds) { return ycVenueMapper.deleteYcVenueByIds(venueIds); }
}