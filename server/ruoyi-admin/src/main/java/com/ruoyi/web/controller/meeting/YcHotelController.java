package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcHotel;
import com.ruoyi.system.service.IYcHotelService;
@RestController
@RequestMapping("/meeting/hotel")
public class YcHotelController extends BaseController {
    @Autowired private IYcHotelService service;
    @PreAuthorize("@ss.hasPermi('meeting:hotel:list')") @GetMapping("/list")
    public TableDataInfo list(YcHotel q){ startPage(); return getDataTable(service.selectYcHotelList(q)); }
    @PreAuthorize("@ss.hasPermi('meeting:hotel:list')") @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId){ return success(service.selectHotelStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:hotel:list')") @GetMapping("/{hotelId}")
    public AjaxResult getInfo(@PathVariable Long hotelId){ return success(service.selectYcHotelById(hotelId)); }
    @PreAuthorize("@ss.hasPermi('meeting:hotel:add')") @Log(title="Hotel", businessType=BusinessType.INSERT) @PostMapping
    public AjaxResult add(@RequestBody YcHotel e){ e.setCreateBy(getUsername()); return toAjax(service.insertYcHotel(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:hotel:edit')") @Log(title="Hotel", businessType=BusinessType.UPDATE) @PutMapping
    public AjaxResult edit(@RequestBody YcHotel e){ e.setUpdateBy(getUsername()); return toAjax(service.updateYcHotel(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:hotel:remove')") @Log(title="Hotel", businessType=BusinessType.DELETE) @DeleteMapping("/{hotelIds}")
    public AjaxResult remove(@PathVariable Long[] hotelIds){ return toAjax(service.deleteYcHotelByIds(hotelIds)); }
}