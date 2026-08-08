package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcHotelAssign;
public interface YcHotelAssignMapper {
    YcHotelAssign selectYcHotelAssignById(Long assignId);
    List<YcHotelAssign> selectYcHotelAssignList(YcHotelAssign q);
    Map<String, Object> selectAssignStats(Long activityId);
    int insertYcHotelAssign(YcHotelAssign e);
    int updateYcHotelAssign(YcHotelAssign e);
    int deleteYcHotelAssignByIds(Long[] ids);
}