package com.ruoyi.system.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcExhibitor;
import com.ruoyi.system.mapper.YcExhibitorMapper;
import com.ruoyi.system.service.IYcExhibitorService;
@Service
public class YcExhibitorServiceImpl implements IYcExhibitorService {
    @Autowired private YcExhibitorMapper ycExhibitorMapper;
    @Override public YcExhibitor selectYcExhibitorById(Long exhibitorId) { return ycExhibitorMapper.selectYcExhibitorById(exhibitorId); }
    @Override public List<YcExhibitor> selectYcExhibitorList(YcExhibitor exhibitor) { return ycExhibitorMapper.selectYcExhibitorList(exhibitor); }
    @Override public Map<String, Object> selectExhibitorStats(Long activityId) {
        Map<String, Object> stats = ycExhibitorMapper.selectExhibitorStats(activityId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalCount", 0); stats.put("featuredCount", 0);
        }
        return stats;
    }
    private void fill(YcExhibitor e) {
        if (e.getIntro() == null) e.setIntro("");
        if (e.getContactName() == null) e.setContactName("");
        if (e.getPhone() == null) e.setPhone("");
        if (e.getLogoUrl() == null) e.setLogoUrl("");
        if (e.getLinkUrl() == null) e.setLinkUrl("");
        if (e.getBoothNo() == null) e.setBoothNo("");
        if (StringUtils.isEmpty(e.getIsFeatured())) e.setIsFeatured("0");
        if (e.getSortOrder() == null) e.setSortOrder(0);
    }
    @Override public int insertYcExhibitor(YcExhibitor exhibitor) { fill(exhibitor); return ycExhibitorMapper.insertYcExhibitor(exhibitor); }
    @Override public int updateYcExhibitor(YcExhibitor exhibitor) { fill(exhibitor); return ycExhibitorMapper.updateYcExhibitor(exhibitor); }
    @Override public int deleteYcExhibitorByIds(Long[] exhibitorIds) { return ycExhibitorMapper.deleteYcExhibitorByIds(exhibitorIds); }
}