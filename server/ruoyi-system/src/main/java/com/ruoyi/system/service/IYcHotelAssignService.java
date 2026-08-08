package com.ruoyi.system.service;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcHotelAssign;
public interface IYcHotelAssignService {
    YcHotelAssign selectYcHotelAssignById(Long assignId);
    List<YcHotelAssign> selectYcHotelAssignList(YcHotelAssign q);
    Map<String, Object> selectAssignStats(Long activityId);
    YcHotelAssign createFromOrder(Long orderId, String createBy);
    int insertYcHotelAssign(YcHotelAssign e);
    int updateYcHotelAssign(YcHotelAssign e);
    int deleteYcHotelAssignByIds(Long[] ids);
}