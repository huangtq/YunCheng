package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcMealTicket;
import com.ruoyi.system.service.IYcMealTicketService;

@RestController
@RequestMapping("/meeting/meal")
public class YcMealTicketController extends BaseController {
    @Autowired private IYcMealTicketService ycMealTicketService;

    @PreAuthorize("@ss.hasPermi('meeting:meal:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcMealTicket ticket) {
        startPage();
        return getDataTable(ycMealTicketService.selectYcMealTicketList(ticket));
    }
    @PreAuthorize("@ss.hasPermi('meeting:meal:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycMealTicketService.selectMealStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:meal:list')")
    @GetMapping("/{ticketId}")
    public AjaxResult getInfo(@PathVariable Long ticketId) { return success(ycMealTicketService.selectYcMealTicketById(ticketId)); }
    @PreAuthorize("@ss.hasPermi('meeting:meal:add')")
    @Log(title="MealTicket", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcMealTicket ticket) { ticket.setCreateBy(getUsername()); return toAjax(ycMealTicketService.insertYcMealTicket(ticket)); }
    @PreAuthorize("@ss.hasPermi('meeting:meal:edit')")
    @Log(title="MealTicket", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcMealTicket ticket) { ticket.setUpdateBy(getUsername()); return toAjax(ycMealTicketService.updateYcMealTicket(ticket)); }
    @PreAuthorize("@ss.hasPermi('meeting:meal:remove')")
    @Log(title="MealTicket", businessType=BusinessType.DELETE)
    @DeleteMapping("/{ticketIds}")
    public AjaxResult remove(@PathVariable Long[] ticketIds) { return toAjax(ycMealTicketService.deleteYcMealTicketByIds(ticketIds)); }
}