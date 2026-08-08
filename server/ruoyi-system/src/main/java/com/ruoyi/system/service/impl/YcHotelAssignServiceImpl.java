package com.ruoyi.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcHotel;
import com.ruoyi.system.domain.YcHotelAssign;
import com.ruoyi.system.domain.YcHotelOrder;
import com.ruoyi.system.mapper.YcHotelAssignMapper;
import com.ruoyi.system.mapper.YcHotelMapper;
import com.ruoyi.system.mapper.YcHotelOrderMapper;
import com.ruoyi.system.service.IYcHotelAssignService;

@Service
public class YcHotelAssignServiceImpl implements IYcHotelAssignService
{
    @Autowired
    private YcHotelAssignMapper mapper;
    @Autowired
    private YcHotelMapper hotelMapper;
    @Autowired
    private YcHotelOrderMapper orderMapper;

    @Override
    public YcHotelAssign selectYcHotelAssignById(Long id)
    {
        return mapper.selectYcHotelAssignById(id);
    }

    @Override
    public List<YcHotelAssign> selectYcHotelAssignList(YcHotelAssign q)
    {
        return mapper.selectYcHotelAssignList(q);
    }

    @Override
    public Map<String, Object> selectAssignStats(Long activityId)
    {
        Map<String, Object> s = mapper.selectAssignStats(activityId);
        if (s == null)
        {
            s = new HashMap<>();
            s.put("totalCount", 0);
            s.put("checkedInCount", 0);
            s.put("checkedOutCount", 0);
        }
        return s;
    }

    private void fill(YcHotelAssign e)
    {
        if (e.getHotelId() == null)
        {
            throw new ServiceException("请选择酒店");
        }
        YcHotel h = hotelMapper.selectYcHotelById(e.getHotelId());
        if (h == null)
        {
            throw new ServiceException("酒店不存在");
        }
        e.setActivityId(h.getActivityId());
        if (e.getPhone() == null)
        {
            e.setPhone("");
        }
        if (e.getRoomNumber() == null)
        {
            e.setRoomNumber("");
        }
        if (StringUtils.isEmpty(e.getAssignStatus()))
        {
            e.setAssignStatus("0");
        }
    }

    @Override
    public YcHotelAssign createFromOrder(Long orderId, String createBy)
    {
        if (orderId == null)
        {
            throw new ServiceException("订单ID不能为空");
        }
        YcHotelOrder order = orderMapper.selectYcHotelOrderById(orderId);
        if (order == null)
        {
            throw new ServiceException("酒店订单不存在");
        }
        if ("2".equals(order.getOrderStatus()))
        {
            throw new ServiceException("已取消订单不能生成分房");
        }
        YcHotelAssign query = new YcHotelAssign();
        query.setOrderId(orderId);
        List<YcHotelAssign> exists = mapper.selectYcHotelAssignList(query);
        if (exists != null && !exists.isEmpty())
        {
            throw new ServiceException("该订单已生成过分房记录");
        }
        YcHotelAssign assign = new YcHotelAssign();
        assign.setActivityId(order.getActivityId());
        assign.setHotelId(order.getHotelId());
        assign.setOrderId(order.getOrderId());
        assign.setRoomId(order.getRoomId());
        assign.setGuestName(order.getGuestName());
        assign.setPhone(order.getPhone());
        assign.setRoomNumber("待定");
        assign.setCheckInDate(order.getCheckInDate());
        assign.setCheckOutDate(order.getCheckOutDate());
        assign.setAssignStatus("0");
        assign.setRemark(StringUtils.isEmpty(order.getRemark()) ? "由订单自动生成" : order.getRemark());
        assign.setCreateBy(createBy);
        fill(assign);
        mapper.insertYcHotelAssign(assign);
        return assign;
    }

    @Override
    public int insertYcHotelAssign(YcHotelAssign e)
    {
        fill(e);
        return mapper.insertYcHotelAssign(e);
    }

    @Override
    public int updateYcHotelAssign(YcHotelAssign e)
    {
        fill(e);
        return mapper.updateYcHotelAssign(e);
    }

    @Override
    public int deleteYcHotelAssignByIds(Long[] ids)
    {
        return mapper.deleteYcHotelAssignByIds(ids);
    }
}
