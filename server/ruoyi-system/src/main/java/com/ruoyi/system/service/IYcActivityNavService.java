package com.ruoyi.system.service;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcActivityNav;
public interface IYcActivityNavService {
    YcActivityNav selectYcActivityNavById(Long navId);
    List<YcActivityNav> selectYcActivityNavList(YcActivityNav q);
    Map<String, Object> selectNavStats(Long activityId);
    int insertYcActivityNav(YcActivityNav e);
    int updateYcActivityNav(YcActivityNav e);
    int deleteYcActivityNavByIds(Long[] ids);
}