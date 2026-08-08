package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcGuestTrip;
import com.ruoyi.system.service.IYcGuestTripService;

@RestController
@RequestMapping("/meeting/guest/trip")
public class YcGuestTripController extends BaseController {
    @Autowired private IYcGuestTripService ycGuestTripService;

    @PreAuthorize("@ss.hasPermi('meeting:trip:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcGuestTrip trip) {
        startPage();
        return getDataTable(ycGuestTripService.selectYcGuestTripList(trip));
    }
    @PreAuthorize("@ss.hasPermi('meeting:trip:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycGuestTripService.selectTripStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:trip:list')")
    @GetMapping("/{tripId}")
    public AjaxResult getInfo(@PathVariable Long tripId) { return success(ycGuestTripService.selectYcGuestTripById(tripId)); }
    @PreAuthorize("@ss.hasPermi('meeting:trip:add')")
    @Log(title="GuestTrip", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcGuestTrip trip) { trip.setCreateBy(getUsername()); return toAjax(ycGuestTripService.insertYcGuestTrip(trip)); }
    @PreAuthorize("@ss.hasPermi('meeting:trip:edit')")
    @Log(title="GuestTrip", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcGuestTrip trip) { trip.setUpdateBy(getUsername()); return toAjax(ycGuestTripService.updateYcGuestTrip(trip)); }
    @PreAuthorize("@ss.hasPermi('meeting:trip:remove')")
    @Log(title="GuestTrip", businessType=BusinessType.DELETE)
    @DeleteMapping("/{tripIds}")
    public AjaxResult remove(@PathVariable Long[] tripIds) { return toAjax(ycGuestTripService.deleteYcGuestTripByIds(tripIds)); }
}