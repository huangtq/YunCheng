package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcGridBottom;
public interface YcGridBottomMapper {
    YcGridBottom selectYcGridBottomById(Long bottomId);
    List<YcGridBottom> selectYcGridBottomList(YcGridBottom q);
    Map<String, Object> selectBottomStats(Long activityId);
    int insertYcGridBottom(YcGridBottom e);
    int updateYcGridBottom(YcGridBottom e);
    int deleteYcGridBottomByIds(Long[] ids);
}