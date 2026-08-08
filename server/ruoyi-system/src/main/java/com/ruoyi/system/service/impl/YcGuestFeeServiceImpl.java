package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.domain.YcGuestFee;
import com.ruoyi.system.domain.YcGuestTaskRule;
import com.ruoyi.system.mapper.YcGuestFeeMapper;
import com.ruoyi.system.mapper.YcGuestMapper;
import com.ruoyi.system.mapper.YcGuestTaskRuleMapper;
import com.ruoyi.system.service.IYcGuestFeeService;
import com.ruoyi.system.service.IYcScheduleService;

@Service
public class YcGuestFeeServiceImpl implements IYcGuestFeeService
{
    @Autowired
    private YcGuestFeeMapper ycGuestFeeMapper;
    @Autowired
    private YcGuestMapper ycGuestMapper;
    @Autowired
    private YcGuestTaskRuleMapper ycGuestTaskRuleMapper;
    @Autowired
    private IYcScheduleService ycScheduleService;

    @Override
    public YcGuestFee selectYcGuestFeeById(Long feeId)
    {
        return ycGuestFeeMapper.selectYcGuestFeeById(feeId);
    }

    @Override
    public List<YcGuestFee> selectYcGuestFeeList(YcGuestFee fee)
    {
        return ycGuestFeeMapper.selectYcGuestFeeList(fee);
    }

    @Override
    public Map<String, Object> selectFeeStats(Long activityId)
    {
        Map<String, Object> stats = ycGuestFeeMapper.selectFeeStats(activityId);
        if (stats == null)
        {
            stats = new HashMap<>();
            stats.put("totalCount", 0);
            stats.put("paidCount", 0);
            stats.put("unpaidCount", 0);
            stats.put("totalAmount", 0);
        }
        return stats;
    }

    private void fill(YcGuestFee f)
    {
        if (f.getFeeAmount() == null)
        {
            f.setFeeAmount(BigDecimal.ZERO);
        }
        if (StringUtils.isEmpty(f.getProgressStatus()))
        {
            f.setProgressStatus("0");
        }
        if (StringUtils.isEmpty(f.getPayStatus()))
        {
            f.setPayStatus("0");
        }
        if (f.getBankName() == null)
        {
            f.setBankName("");
        }
        if (f.getBankAccount() == null)
        {
            f.setBankAccount("");
        }
    }

    private void assertGuest(YcGuestFee fee)
    {
        if (fee.getGuestId() == null)
        {
            throw new ServiceException("请选择嘉宾");
        }
        YcGuest guest = ycGuestMapper.selectYcGuestById(fee.getGuestId());
        if (guest == null)
        {
            throw new ServiceException("嘉宾不存在");
        }
        if (fee.getActivityId() != null && !fee.getActivityId().equals(guest.getActivityId()))
        {
            throw new ServiceException("嘉宾不属于当前会议");
        }
        fee.setActivityId(guest.getActivityId());
    }

    @Override
    public int insertYcGuestFee(YcGuestFee fee)
    {
        assertGuest(fee);
        fill(fee);
        return ycGuestFeeMapper.insertYcGuestFee(fee);
    }

    @Override
    public int updateYcGuestFee(YcGuestFee fee)
    {
        assertGuest(fee);
        fill(fee);
        return ycGuestFeeMapper.updateYcGuestFee(fee);
    }

    @Override
    public int deleteYcGuestFeeByIds(Long[] feeIds)
    {
        return ycGuestFeeMapper.deleteYcGuestFeeByIds(feeIds);
    }

    @Override
    @Transactional
    public Map<String, Object> generateFromRules(Long activityId, String createBy)
    {
        if (activityId == null)
        {
            throw new ServiceException("会议ID不能为空");
        }
        YcGuestTaskRule ruleQ = new YcGuestTaskRule();
        ruleQ.setActivityId(activityId);
        ruleQ.setEnabled("1");
        List<YcGuestTaskRule> rules = ycGuestTaskRuleMapper.selectYcGuestTaskRuleList(ruleQ);
        if (rules == null || rules.isEmpty())
        {
            throw new ServiceException("请先配置并启用任务规则");
        }
        Map<String, YcGuestTaskRule> ruleByRole = new LinkedHashMap<>();
        for (YcGuestTaskRule rule : rules)
        {
            if (StringUtils.isEmpty(rule.getRoleType()))
            {
                continue;
            }
            ruleByRole.putIfAbsent(rule.getRoleType(), rule);
        }

        YcGuest guestQ = new YcGuest();
        guestQ.setActivityId(activityId);
        List<YcGuest> guests = ycGuestMapper.selectYcGuestList(guestQ);
        Map<String, YcGuest> guestByName = new HashMap<>();
        for (YcGuest guest : guests)
        {
            if (guest.getGuestName() != null)
            {
                guestByName.putIfAbsent(guest.getGuestName().trim(), guest);
            }
        }

        YcGuestFee feeQ = new YcGuestFee();
        feeQ.setActivityId(activityId);
        Set<Long> existingGuestIds = new HashSet<>();
        for (YcGuestFee fee : ycGuestFeeMapper.selectYcGuestFeeList(feeQ))
        {
            if (fee.getGuestId() != null)
            {
                existingGuestIds.add(fee.getGuestId());
            }
        }

        List<Map<String, Object>> tasks = ycScheduleService.selectExpertTasks(activityId, null, null, null);
        Map<Long, BigDecimal> amountByGuest = new LinkedHashMap<>();
        Map<Long, Set<String>> rolesByGuest = new LinkedHashMap<>();
        Set<String> unmatchedNames = new LinkedHashSet<>();
        int matchedTaskCount = 0;

        for (Map<String, Object> task : tasks)
        {
            String name = String.valueOf(task.getOrDefault("expertName", "")).trim();
            String role = String.valueOf(task.getOrDefault("role", "")).trim();
            if (name.isEmpty() || role.isEmpty())
            {
                continue;
            }
            YcGuest guest = guestByName.get(name);
            if (guest == null)
            {
                unmatchedNames.add(name);
                continue;
            }
            YcGuestTaskRule rule = ruleByRole.get(role);
            if (rule == null || rule.getFeeAmount() == null)
            {
                continue;
            }
            matchedTaskCount++;
            amountByGuest.merge(guest.getGuestId(), rule.getFeeAmount(), BigDecimal::add);
            rolesByGuest.computeIfAbsent(guest.getGuestId(), k -> new LinkedHashSet<>()).add(role);
        }

        int createdCount = 0;
        int skippedCount = 0;
        for (Map.Entry<Long, BigDecimal> entry : amountByGuest.entrySet())
        {
            Long guestId = entry.getKey();
            if (existingGuestIds.contains(guestId))
            {
                skippedCount++;
                continue;
            }
            YcGuestFee fee = new YcGuestFee();
            fee.setActivityId(activityId);
            fee.setGuestId(guestId);
            fee.setFeeAmount(entry.getValue());
            fee.setProgressStatus("0");
            fee.setPayStatus("0");
            fee.setCreateBy(createBy);
            Set<String> roles = rolesByGuest.get(guestId);
            fee.setRemark("按任务规则自动生成：" + String.join(",", roles));
            fill(fee);
            ycGuestFeeMapper.insertYcGuestFee(fee);
            existingGuestIds.add(guestId);
            createdCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("createdCount", createdCount);
        result.put("skippedCount", skippedCount);
        result.put("matchedTaskCount", matchedTaskCount);
        result.put("unmatchedCount", unmatchedNames.size());
        result.put("unmatchedNames", unmatchedNames);
        return result;
    }
}
