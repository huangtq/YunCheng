package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcGuestTaskRule;
public interface YcGuestTaskRuleMapper {
    YcGuestTaskRule selectYcGuestTaskRuleById(Long ruleId);
    List<YcGuestTaskRule> selectYcGuestTaskRuleList(YcGuestTaskRule rule);
    Map<String, Object> selectRuleStats(Long activityId);
    int insertYcGuestTaskRule(YcGuestTaskRule rule);
    int updateYcGuestTaskRule(YcGuestTaskRule rule);
    int deleteYcGuestTaskRuleByIds(Long[] ruleIds);
}