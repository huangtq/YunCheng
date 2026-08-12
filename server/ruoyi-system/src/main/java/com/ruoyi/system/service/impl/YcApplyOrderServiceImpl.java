package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.system.mapper.YcApplyOrderMapper;
import com.ruoyi.system.service.IYcApplyOrderService;

@Service
public class YcApplyOrderServiceImpl implements IYcApplyOrderService
{
    @Autowired
    private YcApplyOrderMapper ycApplyOrderMapper;

    @Override
    public YcApplyOrder selectYcApplyOrderById(Long orderId)
    {
        return ycApplyOrderMapper.selectYcApplyOrderById(orderId);
    }

    @Override
    public List<YcApplyOrder> selectYcApplyOrderList(YcApplyOrder order)
    {
        return ycApplyOrderMapper.selectYcApplyOrderList(order);
    }

    @Override
    public Map<String, Object> selectOrderStats(Long activityId)
    {
        Map<String, Object> stats = ycApplyOrderMapper.selectOrderStats(activityId);
        if (stats == null)
        {
            stats = new HashMap<>();
            stats.put("totalCount", 0);
            stats.put("registeredCount", 0);
            stats.put("cancelledCount", 0);
            stats.put("checkedCount", 0);
            stats.put("uncheckedCount", 0);
        }
        return stats;
    }

    private void fillDefaults(YcApplyOrder order)
    {
        if (StringUtils.isEmpty(order.getGender()))
        {
            order.setGender("");
        }
        if (StringUtils.isEmpty(order.getCompany()))
        {
            order.setCompany("");
        }
        if (StringUtils.isEmpty(order.getOrderStatus()))
        {
            order.setOrderStatus("0");
        }
        if (StringUtils.isEmpty(order.getCheckinStatus()))
        {
            order.setCheckinStatus("0");
        }
        if (order.getFormJson() == null)
        {
            order.setFormJson("");
        }
    }

    private String nextOrderNo()
    {
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return time + rand;
    }

    @Override
    public int insertYcApplyOrder(YcApplyOrder order)
    {
        fillDefaults(order);
        if (StringUtils.isEmpty(order.getOrderNo()))
        {
            order.setOrderNo(nextOrderNo());
        }
        if (StringUtils.isEmpty(order.getContactName()))
        {
            throw new ServiceException("contact name required");
        }
        if (StringUtils.isEmpty(order.getMobile()))
        {
            throw new ServiceException("mobile required");
        }
        if (order.getActivityId() == null)
        {
            throw new ServiceException("activityId required");
        }
        return ycApplyOrderMapper.insertYcApplyOrder(order);
    }

    @Override
    public int updateYcApplyOrder(YcApplyOrder order)
    {
        fillDefaults(order);
        return ycApplyOrderMapper.updateYcApplyOrder(order);
    }

    @Override
    public int checkin(Long orderId, String updateBy)
    {
        YcApplyOrder exist = ycApplyOrderMapper.selectYcApplyOrderById(orderId);
        if (exist == null)
        {
            throw new ServiceException("order not found");
        }
        if ("2".equals(exist.getOrderStatus()))
        {
            throw new ServiceException("cancelled order cannot checkin");
        }
        return ycApplyOrderMapper.checkinIfPending(orderId, updateBy);
    }

    @Override
    public int deleteYcApplyOrderByIds(Long[] orderIds)
    {
        return ycApplyOrderMapper.deleteYcApplyOrderByIds(orderIds);
    }
}
