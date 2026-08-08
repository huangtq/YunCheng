package com.ruoyi.system.service;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcHotel;
public interface IYcHotelService {
    YcHotel selectYcHotelById(Long hotelId);
    List<YcHotel> selectYcHotelList(YcHotel q);
    Map<String, Object> selectHotelStats(Long activityId);
    int insertYcHotel(YcHotel e);
    int updateYcHotel(YcHotel e);
    int deleteYcHotelByIds(Long[] ids);
}