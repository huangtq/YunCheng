package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YcApplyOrder;

public interface YcApplyOrderMapper
{
    public YcApplyOrder selectYcApplyOrderById(Long orderId);

    public List<YcApplyOrder> selectYcApplyOrderList(YcApplyOrder order);

    public List<YcApplyOrder> selectPortalUserOrders(@Param("activityId") Long activityId,
        @Param("openid") String openid, @Param("mobile") String mobile,
        @Param("orderStatus") String orderStatus);

    public Map<String, Object> selectOrderStats(Long activityId);

    public int insertYcApplyOrder(YcApplyOrder order);

    public int updateYcApplyOrder(YcApplyOrder order);

    /** Atomically transition an active, unchecked order to checked in. */
    public int checkinIfPending(@Param("orderId") Long orderId, @Param("updateBy") String updateBy);

    public int revokeCheckin(@Param("orderId") Long orderId, @Param("updateBy") String updateBy);

    public int deleteYcApplyOrderByIds(Long[] orderIds);
}
