package com.ruoyi.system.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcMealTicket;
import com.ruoyi.system.mapper.YcMealTicketMapper;
import com.ruoyi.system.service.IYcMealTicketService;
@Service
public class YcMealTicketServiceImpl implements IYcMealTicketService {
    @Autowired private YcMealTicketMapper ycMealTicketMapper;
    @Override public YcMealTicket selectYcMealTicketById(Long ticketId) { return ycMealTicketMapper.selectYcMealTicketById(ticketId); }
    @Override public List<YcMealTicket> selectYcMealTicketList(YcMealTicket ticket) { return ycMealTicketMapper.selectYcMealTicketList(ticket); }
    @Override public Map<String, Object> selectMealStats(Long activityId) {
        Map<String, Object> stats = ycMealTicketMapper.selectMealStats(activityId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalCount", 0); stats.put("enabledCount", 0); stats.put("usedCount", 0);
        }
        return stats;
    }
    private void fill(YcMealTicket t) {
        if (StringUtils.isEmpty(t.getMealType())) t.setMealType("lunch");
        if (t.getTotalQuota() == null) t.setTotalQuota(0);
        if (t.getUsedCount() == null) t.setUsedCount(0);
        if (StringUtils.isEmpty(t.getEnabled())) t.setEnabled("1");
        if (t.getSortOrder() == null) t.setSortOrder(0);
    }
    @Override public int insertYcMealTicket(YcMealTicket ticket) { fill(ticket); return ycMealTicketMapper.insertYcMealTicket(ticket); }
    @Override public int updateYcMealTicket(YcMealTicket ticket) { fill(ticket); return ycMealTicketMapper.updateYcMealTicket(ticket); }
    @Override public int deleteYcMealTicketByIds(Long[] ticketIds) { return ycMealTicketMapper.deleteYcMealTicketByIds(ticketIds); }
}