package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcStaff;
import com.ruoyi.system.service.IYcStaffService;

@RestController
@RequestMapping("/meeting/staff")
public class YcStaffController extends BaseController {
    @Autowired private IYcStaffService ycStaffService;

    @PreAuthorize("@ss.hasPermi('meeting:staff:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcStaff staff) {
        startPage();
        return getDataTable(ycStaffService.selectYcStaffList(staff));
    }
    @PreAuthorize("@ss.hasPermi('meeting:staff:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycStaffService.selectStaffStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:staff:list')")
    @GetMapping("/{staffId}")
    public AjaxResult getInfo(@PathVariable Long staffId) { return success(ycStaffService.selectYcStaffById(staffId)); }
    @PreAuthorize("@ss.hasPermi('meeting:staff:add')")
    @Log(title="Staff", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcStaff staff) { staff.setCreateBy(getUsername()); return toAjax(ycStaffService.insertYcStaff(staff)); }
    @PreAuthorize("@ss.hasPermi('meeting:staff:edit')")
    @Log(title="Staff", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcStaff staff) { staff.setUpdateBy(getUsername()); return toAjax(ycStaffService.updateYcStaff(staff)); }
    @PreAuthorize("@ss.hasPermi('meeting:staff:remove')")
    @Log(title="Staff", businessType=BusinessType.DELETE)
    @DeleteMapping("/{staffIds}")
    public AjaxResult remove(@PathVariable Long[] staffIds) { return toAjax(ycStaffService.deleteYcStaffByIds(staffIds)); }
}