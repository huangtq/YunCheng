package com.ruoyi.system.service;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcHotelOrder;
public interface IYcHotelOrderService {
    YcHotelOrder selectYcHotelOrderById(Long orderId);
    List<YcHotelOrder> selectYcHotelOrderList(YcHotelOrder q);
    Map<String, Object> selectHotelOrderStats(Long activityId);
    int insertYcHotelOrder(YcHotelOrder e);
    int updateYcHotelOrder(YcHotelOrder e);
    int deleteYcHotelOrderByIds(Long[] ids);
}