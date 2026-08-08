package com.ruoyi.system.service.impl;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcGridBottom;
import com.ruoyi.system.mapper.YcGridBottomMapper;
import com.ruoyi.system.service.IYcGridBottomService;
@Service
public class YcGridBottomServiceImpl implements IYcGridBottomService {
    @Autowired private YcGridBottomMapper mapper;
    @Override public YcGridBottom selectYcGridBottomById(Long id){return mapper.selectYcGridBottomById(id);}
    @Override public List<YcGridBottom> selectYcGridBottomList(YcGridBottom q){return mapper.selectYcGridBottomList(q);}
    @Override public Map<String,Object> selectBottomStats(Long activityId){
        Map<String,Object> s=mapper.selectBottomStats(activityId);
        if(s==null){s=new HashMap<>();s.put("totalCount",0);s.put("enabledCount",0);} return s;
    }
    private void fill(YcGridBottom e){
        if(StringUtils.isEmpty(e.getBottomType()))e.setBottomType("link");
        if(e.getLinkUrl()==null)e.setLinkUrl(""); if(e.getModuleKey()==null)e.setModuleKey("");
        if(e.getPhone()==null)e.setPhone(""); if(e.getIconUrl()==null)e.setIconUrl("");
        if(e.getSortOrder()==null)e.setSortOrder(0);
        if(StringUtils.isEmpty(e.getStatus()))e.setStatus("1");
    }
    @Override public int insertYcGridBottom(YcGridBottom e){fill(e);return mapper.insertYcGridBottom(e);}
    @Override public int updateYcGridBottom(YcGridBottom e){fill(e);return mapper.updateYcGridBottom(e);}
    @Override public int deleteYcGridBottomByIds(Long[] ids){return mapper.deleteYcGridBottomByIds(ids);}
}