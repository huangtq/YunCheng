package com.ruoyi.system.service.impl;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcHotel;
import com.ruoyi.system.mapper.YcHotelMapper;
import com.ruoyi.system.service.IYcHotelService;
@Service
public class YcHotelServiceImpl implements IYcHotelService {
    @Autowired private YcHotelMapper mapper;
    @Override public YcHotel selectYcHotelById(Long id){return mapper.selectYcHotelById(id);}
    @Override public List<YcHotel> selectYcHotelList(YcHotel q){return mapper.selectYcHotelList(q);}
    @Override public Map<String,Object> selectHotelStats(Long activityId){
        Map<String,Object> s=mapper.selectHotelStats(activityId);
        if(s==null){s=new HashMap<>();s.put("totalCount",0);s.put("openCount",0);} return s;
    }
    private void fill(YcHotel e){ if(e.getPhone()==null)e.setPhone(""); if(e.getAddress()==null)e.setAddress(""); if(e.getCoverUrl()==null)e.setCoverUrl(""); if(StringUtils.isEmpty(e.getSaleStatus()))e.setSaleStatus("1"); if(e.getSortOrder()==null)e.setSortOrder(0); }
    @Override public int insertYcHotel(YcHotel e){fill(e);return mapper.insertYcHotel(e);}
    @Override public int updateYcHotel(YcHotel e){fill(e);return mapper.updateYcHotel(e);}
    @Override public int deleteYcHotelByIds(Long[] ids){return mapper.deleteYcHotelByIds(ids);}
}