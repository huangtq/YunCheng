package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcHotelAssign;
import com.ruoyi.system.service.IYcHotelAssignService;
@RestController
@RequestMapping("/meeting/hotel/assign")
public class YcHotelAssignController extends BaseController {
    @Autowired private IYcHotelAssignService service;
    @PreAuthorize("@ss.hasPermi('meeting:assign:list')") @GetMapping("/list")
    public TableDataInfo list(YcHotelAssign q){ startPage(); return getDataTable(service.selectYcHotelAssignList(q)); }
    @PreAuthorize("@ss.hasPermi('meeting:assign:list')") @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId){ return success(service.selectAssignStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:assign:list')") @GetMapping("/{assignId}")
    public AjaxResult getInfo(@PathVariable Long assignId){ return success(service.selectYcHotelAssignById(assignId)); }
    @PreAuthorize("@ss.hasPermi('meeting:assign:add')")
    @Log(title="HotelAssign", businessType=BusinessType.INSERT)
    @PostMapping("/fromOrder/{orderId}")
    public AjaxResult fromOrder(@PathVariable Long orderId)
    {
        return success(service.createFromOrder(orderId, getUsername()));
    }
    @PreAuthorize("@ss.hasPermi('meeting:assign:add')") @Log(title="HotelAssign", businessType=BusinessType.INSERT) @PostMapping
    public AjaxResult add(@RequestBody YcHotelAssign e){ e.setCreateBy(getUsername()); return toAjax(service.insertYcHotelAssign(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:assign:edit')") @Log(title="HotelAssign", businessType=BusinessType.UPDATE) @PutMapping
    public AjaxResult edit(@RequestBody YcHotelAssign e){ e.setUpdateBy(getUsername()); return toAjax(service.updateYcHotelAssign(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:assign:remove')") @Log(title="HotelAssign", businessType=BusinessType.DELETE) @DeleteMapping("/{assignIds}")
    public AjaxResult remove(@PathVariable Long[] assignIds){ return toAjax(service.deleteYcHotelAssignByIds(assignIds)); }
}