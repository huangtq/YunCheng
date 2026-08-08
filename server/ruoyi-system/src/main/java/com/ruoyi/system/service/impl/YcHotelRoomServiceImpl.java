package com.ruoyi.system.service.impl;
import java.math.BigDecimal; import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.YcHotel; import com.ruoyi.system.domain.YcHotelRoom;
import com.ruoyi.system.mapper.YcHotelMapper; import com.ruoyi.system.mapper.YcHotelRoomMapper;
import com.ruoyi.system.service.IYcHotelRoomService;
@Service
public class YcHotelRoomServiceImpl implements IYcHotelRoomService {
    @Autowired private YcHotelRoomMapper mapper; @Autowired private YcHotelMapper hotelMapper;
    @Override public YcHotelRoom selectYcHotelRoomById(Long id){return mapper.selectYcHotelRoomById(id);}
    @Override public List<YcHotelRoom> selectYcHotelRoomList(YcHotelRoom q){return mapper.selectYcHotelRoomList(q);}
    @Override public Map<String,Object> selectRoomStats(Long activityId){
        Map<String,Object> s=mapper.selectRoomStats(activityId);
        if(s==null){s=new HashMap<>();s.put("totalCount",0);} return s;
    }
    private void fill(YcHotelRoom e){
        if(e.getHotelId()==null) throw new ServiceException("请选择酒店");
        YcHotel h=hotelMapper.selectYcHotelById(e.getHotelId());
        if(h==null) throw new ServiceException("酒店不存在");
        e.setActivityId(h.getActivityId());
        if(e.getBedType()==null)e.setBedType(""); if(e.getPrice()==null)e.setPrice(BigDecimal.ZERO);
        if(e.getStock()==null)e.setStock(0); if(e.getSortOrder()==null)e.setSortOrder(0);
    }
    @Override public int insertYcHotelRoom(YcHotelRoom e){fill(e);return mapper.insertYcHotelRoom(e);}
    @Override public int updateYcHotelRoom(YcHotelRoom e){fill(e);return mapper.updateYcHotelRoom(e);}
    @Override public int deleteYcHotelRoomByIds(Long[] ids){return mapper.deleteYcHotelRoomByIds(ids);}
}