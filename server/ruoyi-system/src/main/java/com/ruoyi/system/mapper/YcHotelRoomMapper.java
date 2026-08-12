package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map; import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YcHotelRoom;
public interface YcHotelRoomMapper {
    YcHotelRoom selectYcHotelRoomById(Long roomId);
    List<YcHotelRoom> selectYcHotelRoomList(YcHotelRoom q);
    Map<String, Object> selectRoomStats(Long activityId);
    int insertYcHotelRoom(YcHotelRoom e);
    int updateYcHotelRoom(YcHotelRoom e);
    int deleteYcHotelRoomByIds(Long[] ids);
    int reserveStock(@Param("roomId") Long roomId, @Param("quantity") Integer quantity);
    int releaseStock(@Param("roomId") Long roomId, @Param("quantity") Integer quantity);
}
