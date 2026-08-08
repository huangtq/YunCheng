package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcGuestFee;

public interface IYcGuestFeeService
{
    YcGuestFee selectYcGuestFeeById(Long feeId);

    List<YcGuestFee> selectYcGuestFeeList(YcGuestFee fee);

    Map<String, Object> selectFeeStats(Long activityId);

    Map<String, Object> generateFromRules(Long activityId, String createBy);

    int insertYcGuestFee(YcGuestFee fee);

    int updateYcGuestFee(YcGuestFee fee);

    int deleteYcGuestFeeByIds(Long[] feeIds);
}
