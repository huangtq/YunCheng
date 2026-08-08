package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcGuestFee;
import com.ruoyi.system.service.IYcGuestFeeService;

@RestController
@RequestMapping("/meeting/guest/fee")
public class YcGuestFeeController extends BaseController {
    @Autowired private IYcGuestFeeService ycGuestFeeService;

    @PreAuthorize("@ss.hasPermi('meeting:fee:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcGuestFee fee) {
        startPage();
        return getDataTable(ycGuestFeeService.selectYcGuestFeeList(fee));
    }
    @PreAuthorize("@ss.hasPermi('meeting:fee:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycGuestFeeService.selectFeeStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:fee:add')")
    @Log(title="GuestFee", businessType=BusinessType.INSERT)
    @PostMapping("/generate/{activityId}")
    public AjaxResult generate(@PathVariable Long activityId)
    {
        return success(ycGuestFeeService.generateFromRules(activityId, getUsername()));
    }
    @PreAuthorize("@ss.hasPermi('meeting:fee:list')")
    @GetMapping("/{feeId}")
    public AjaxResult getInfo(@PathVariable Long feeId) { return success(ycGuestFeeService.selectYcGuestFeeById(feeId)); }
    @PreAuthorize("@ss.hasPermi('meeting:fee:add')")
    @Log(title="GuestFee", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcGuestFee fee) { fee.setCreateBy(getUsername()); return toAjax(ycGuestFeeService.insertYcGuestFee(fee)); }
    @PreAuthorize("@ss.hasPermi('meeting:fee:edit')")
    @Log(title="GuestFee", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcGuestFee fee) { fee.setUpdateBy(getUsername()); return toAjax(ycGuestFeeService.updateYcGuestFee(fee)); }
    @PreAuthorize("@ss.hasPermi('meeting:fee:remove')")
    @Log(title="GuestFee", businessType=BusinessType.DELETE)
    @DeleteMapping("/{feeIds}")
    public AjaxResult remove(@PathVariable Long[] feeIds) { return toAjax(ycGuestFeeService.deleteYcGuestFeeByIds(feeIds)); }
}