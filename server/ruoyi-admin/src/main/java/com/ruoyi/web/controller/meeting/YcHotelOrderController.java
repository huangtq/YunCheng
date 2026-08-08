package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcHotelOrder;
import com.ruoyi.system.service.IYcHotelOrderService;
@RestController
@RequestMapping("/meeting/hotel/order")
public class YcHotelOrderController extends BaseController {
    @Autowired private IYcHotelOrderService service;
    @PreAuthorize("@ss.hasPermi('meeting:horder:list')") @GetMapping("/list")
    public TableDataInfo list(YcHotelOrder q){ startPage(); return getDataTable(service.selectYcHotelOrderList(q)); }
    @PreAuthorize("@ss.hasPermi('meeting:horder:list')") @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId){ return success(service.selectHotelOrderStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:horder:list')") @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId){ return success(service.selectYcHotelOrderById(orderId)); }
    @PreAuthorize("@ss.hasPermi('meeting:horder:add')") @Log(title="HotelOrder", businessType=BusinessType.INSERT) @PostMapping
    public AjaxResult add(@RequestBody YcHotelOrder e){ e.setCreateBy(getUsername()); return toAjax(service.insertYcHotelOrder(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:horder:edit')") @Log(title="HotelOrder", businessType=BusinessType.UPDATE) @PutMapping
    public AjaxResult edit(@RequestBody YcHotelOrder e){ e.setUpdateBy(getUsername()); return toAjax(service.updateYcHotelOrder(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:horder:remove')") @Log(title="HotelOrder", businessType=BusinessType.DELETE) @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds){ return toAjax(service.deleteYcHotelOrderByIds(orderIds)); }
}