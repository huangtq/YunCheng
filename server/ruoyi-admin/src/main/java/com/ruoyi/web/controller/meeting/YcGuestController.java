package com.ruoyi.web.controller.meeting;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.service.IYcGuestService;

@RestController
@RequestMapping("/meeting/guest")
public class YcGuestController extends BaseController {
    @Autowired private IYcGuestService ycGuestService;

    @PreAuthorize("@ss.hasPermi('meeting:guest:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcGuest guest) {
        startPage();
        return getDataTable(ycGuestService.selectYcGuestList(guest));
    }
    @PreAuthorize("@ss.hasPermi('meeting:guest:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycGuestService.selectGuestStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:guest:list')")
    @GetMapping("/{guestId}")
    public AjaxResult getInfo(@PathVariable Long guestId) { return success(ycGuestService.selectYcGuestById(guestId)); }
    @PreAuthorize("@ss.hasPermi('meeting:guest:add')")
    @Log(title="Guest", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcGuest guest) { guest.setCreateBy(getUsername()); return toAjax(ycGuestService.insertYcGuest(guest)); }
    @PreAuthorize("@ss.hasPermi('meeting:guest:edit')")
    @Log(title="Guest", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcGuest guest) { guest.setUpdateBy(getUsername()); return toAjax(ycGuestService.updateYcGuest(guest)); }
    @PreAuthorize("@ss.hasPermi('meeting:guest:remove')")
    @Log(title="Guest", businessType=BusinessType.DELETE)
    @DeleteMapping("/{guestIds}")
    public AjaxResult remove(@PathVariable Long[] guestIds) { return toAjax(ycGuestService.deleteYcGuestByIds(guestIds)); }
}