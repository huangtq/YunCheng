package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcGuestFee;
public interface YcGuestFeeMapper {
    YcGuestFee selectYcGuestFeeById(Long feeId);
    List<YcGuestFee> selectYcGuestFeeList(YcGuestFee fee);
    Map<String, Object> selectFeeStats(Long activityId);
    int insertYcGuestFee(YcGuestFee fee);
    int updateYcGuestFee(YcGuestFee fee);
    int deleteYcGuestFeeByIds(Long[] feeIds);
}