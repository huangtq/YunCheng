package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map;
import com.ruoyi.system.domain.YcActivityNav;
public interface YcActivityNavMapper {
    YcActivityNav selectYcActivityNavById(Long navId);
    List<YcActivityNav> selectYcActivityNavList(YcActivityNav q);
    Map<String, Object> selectNavStats(Long activityId);
    int insertYcActivityNav(YcActivityNav e);
    int updateYcActivityNav(YcActivityNav e);
    int deleteYcActivityNavByIds(Long[] ids);
}