package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcHotel;
public interface YcHotelMapper {
    YcHotel selectYcHotelById(Long hotelId);
    List<YcHotel> selectYcHotelList(YcHotel q);
    Map<String, Object> selectHotelStats(Long activityId);
    int insertYcHotel(YcHotel e);
    int updateYcHotel(YcHotel e);
    int deleteYcHotelByIds(Long[] ids);
}