package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcApplyOrder;

public interface IYcApplyOrderService
{
    public YcApplyOrder selectYcApplyOrderById(Long orderId);

    public List<YcApplyOrder> selectYcApplyOrderList(YcApplyOrder order);

    public Map<String, Object> selectOrderStats(Long activityId);

    public int insertYcApplyOrder(YcApplyOrder order);

    public int updateYcApplyOrder(YcApplyOrder order);

    public int checkin(Long orderId, String updateBy);

    public int deleteYcApplyOrderByIds(Long[] orderIds);
}