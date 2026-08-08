package com.ruoyi.system.service.impl;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivityNav;
import com.ruoyi.system.mapper.YcActivityNavMapper;
import com.ruoyi.system.service.IYcActivityNavService;
@Service
public class YcActivityNavServiceImpl implements IYcActivityNavService {
    @Autowired private YcActivityNavMapper mapper;
    @Override public YcActivityNav selectYcActivityNavById(Long id){return mapper.selectYcActivityNavById(id);}
    @Override public List<YcActivityNav> selectYcActivityNavList(YcActivityNav q){return mapper.selectYcActivityNavList(q);}
    @Override public Map<String,Object> selectNavStats(Long activityId){
        Map<String,Object> s=mapper.selectNavStats(activityId);
        if(s==null){s=new HashMap<>();s.put("totalCount",0);s.put("enabledCount",0);} return s;
    }
    private void fill(YcActivityNav e){
        if(e.getAddress()==null)e.setAddress(""); if(e.getLongitude()==null)e.setLongitude("");
        if(e.getLatitude()==null)e.setLatitude(""); if(e.getPhone()==null)e.setPhone("");
        if(e.getCoverUrl()==null)e.setCoverUrl(""); if(e.getSortOrder()==null)e.setSortOrder(0);
        if(StringUtils.isEmpty(e.getStatus()))e.setStatus("1");
    }
    @Override public int insertYcActivityNav(YcActivityNav e){fill(e);return mapper.insertYcActivityNav(e);}
    @Override public int updateYcActivityNav(YcActivityNav e){fill(e);return mapper.updateYcActivityNav(e);}
    @Override public int deleteYcActivityNavByIds(Long[] ids){return mapper.deleteYcActivityNavByIds(ids);}
}