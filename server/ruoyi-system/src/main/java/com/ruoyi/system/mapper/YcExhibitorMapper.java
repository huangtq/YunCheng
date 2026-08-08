package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcExhibitor;
public interface YcExhibitorMapper {
    YcExhibitor selectYcExhibitorById(Long exhibitorId);
    List<YcExhibitor> selectYcExhibitorList(YcExhibitor exhibitor);
    Map<String, Object> selectExhibitorStats(Long activityId);
    int insertYcExhibitor(YcExhibitor exhibitor);
    int updateYcExhibitor(YcExhibitor exhibitor);
    int deleteYcExhibitorByIds(Long[] exhibitorIds);
}