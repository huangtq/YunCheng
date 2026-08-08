package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcGuestTaskRule;
public interface IYcGuestTaskRuleService {
    YcGuestTaskRule selectYcGuestTaskRuleById(Long ruleId);
    List<YcGuestTaskRule> selectYcGuestTaskRuleList(YcGuestTaskRule rule);
    Map<String, Object> selectRuleStats(Long activityId);
    int insertYcGuestTaskRule(YcGuestTaskRule rule);
    int updateYcGuestTaskRule(YcGuestTaskRule rule);
    int deleteYcGuestTaskRuleByIds(Long[] ruleIds);
}