package com.ruoyi.system.service;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcMealTicket;
public interface IYcMealTicketService {
    YcMealTicket selectYcMealTicketById(Long ticketId);
    List<YcMealTicket> selectYcMealTicketList(YcMealTicket ticket);
    Map<String, Object> selectMealStats(Long activityId);
    int insertYcMealTicket(YcMealTicket ticket);
    int updateYcMealTicket(YcMealTicket ticket);
    int deleteYcMealTicketByIds(Long[] ticketIds);
}