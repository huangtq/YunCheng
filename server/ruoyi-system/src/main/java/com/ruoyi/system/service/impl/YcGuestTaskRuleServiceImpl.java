package com.ruoyi.system.service.impl;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcGuestTaskRule;
import com.ruoyi.system.mapper.YcGuestTaskRuleMapper;
import com.ruoyi.system.service.IYcGuestTaskRuleService;
@Service
public class YcGuestTaskRuleServiceImpl implements IYcGuestTaskRuleService {
    @Autowired private YcGuestTaskRuleMapper ycGuestTaskRuleMapper;
    @Override public YcGuestTaskRule selectYcGuestTaskRuleById(Long ruleId) { return ycGuestTaskRuleMapper.selectYcGuestTaskRuleById(ruleId); }
    @Override public List<YcGuestTaskRule> selectYcGuestTaskRuleList(YcGuestTaskRule rule) { return ycGuestTaskRuleMapper.selectYcGuestTaskRuleList(rule); }
    @Override public Map<String, Object> selectRuleStats(Long activityId) {
        Map<String, Object> stats = ycGuestTaskRuleMapper.selectRuleStats(activityId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalCount", 0); stats.put("enabledCount", 0);
        }
        return stats;
    }
    private void fill(YcGuestTaskRule r) {
        if (StringUtils.isEmpty(r.getRoleType())) r.setRoleType("speaker");
        if (r.getFeeAmount() == null) r.setFeeAmount(BigDecimal.ZERO);
        if (StringUtils.isEmpty(r.getEnabled())) r.setEnabled("1");
        if (r.getSortOrder() == null) r.setSortOrder(0);
    }
    @Override public int insertYcGuestTaskRule(YcGuestTaskRule rule) { fill(rule); return ycGuestTaskRuleMapper.insertYcGuestTaskRule(rule); }
    @Override public int updateYcGuestTaskRule(YcGuestTaskRule rule) { fill(rule); return ycGuestTaskRuleMapper.updateYcGuestTaskRule(rule); }
    @Override public int deleteYcGuestTaskRuleByIds(Long[] ruleIds) { return ycGuestTaskRuleMapper.deleteYcGuestTaskRuleByIds(ruleIds); }
}