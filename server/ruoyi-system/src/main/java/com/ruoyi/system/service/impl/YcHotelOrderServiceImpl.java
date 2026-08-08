package com.ruoyi.system.service.impl;
import java.math.BigDecimal; import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcHotel; import com.ruoyi.system.domain.YcHotelOrder;
import com.ruoyi.system.mapper.YcHotelMapper; import com.ruoyi.system.mapper.YcHotelOrderMapper;
import com.ruoyi.system.service.IYcHotelOrderService;
@Service
public class YcHotelOrderServiceImpl implements IYcHotelOrderService {
    @Autowired private YcHotelOrderMapper mapper; @Autowired private YcHotelMapper hotelMapper;
    @Override public YcHotelOrder selectYcHotelOrderById(Long id){return mapper.selectYcHotelOrderById(id);}
    @Override public List<YcHotelOrder> selectYcHotelOrderList(YcHotelOrder q){return mapper.selectYcHotelOrderList(q);}
    @Override public Map<String,Object> selectHotelOrderStats(Long activityId){
        Map<String,Object> s=mapper.selectHotelOrderStats(activityId);
        if(s==null){s=new HashMap<>();s.put("totalCount",0);s.put("confirmedCount",0);s.put("cancelledCount",0);} return s;
    }
    private void fill(YcHotelOrder e){
        if(e.getHotelId()==null) throw new ServiceException("请选择酒店");
        YcHotel h=hotelMapper.selectYcHotelById(e.getHotelId());
        if(h==null) throw new ServiceException("酒店不存在");
        e.setActivityId(h.getActivityId());
        if(e.getPhone()==null)e.setPhone(""); if(e.getRoomCount()==null)e.setRoomCount(1);
        if(e.getAmount()==null)e.setAmount(BigDecimal.ZERO);
        if(StringUtils.isEmpty(e.getOrderStatus()))e.setOrderStatus("0");
    }
    @Override public int insertYcHotelOrder(YcHotelOrder e){fill(e);return mapper.insertYcHotelOrder(e);}
    @Override public int updateYcHotelOrder(YcHotelOrder e){fill(e);return mapper.updateYcHotelOrder(e);}
    @Override public int deleteYcHotelOrderByIds(Long[] ids){return mapper.deleteYcHotelOrderByIds(ids);}
}