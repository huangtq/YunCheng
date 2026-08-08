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
import com.ruoyi.system.domain.YcSchedule;
import com.ruoyi.system.service.IYcScheduleService;

@RestController
@RequestMapping("/meeting/schedule")
public class YcScheduleController extends BaseController {
    @Autowired private IYcScheduleService ycScheduleService;

    @PreAuthorize("@ss.hasPermi('meeting:schedule:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcSchedule schedule) { startPage(); return getDataTable(ycScheduleService.selectYcScheduleList(schedule)); }
    @PreAuthorize("@ss.hasPermi('meeting:schedule:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycScheduleService.selectScheduleStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:schedule:list')")
    @GetMapping("/expert/{activityId}")
    public AjaxResult expert(@PathVariable Long activityId,
        @RequestParam(required=false) String expertName,
        @RequestParam(required=false) String role,
        @RequestParam(required=false) String onlyConflict) {
        return success(ycScheduleService.selectExpertTasks(activityId, expertName, role, onlyConflict));
    }
    @PreAuthorize("@ss.hasPermi('meeting:schedule:list')")
    @GetMapping("/{scheduleId}")
    public AjaxResult getInfo(@PathVariable Long scheduleId) { return success(ycScheduleService.selectYcScheduleById(scheduleId)); }
    @PreAuthorize("@ss.hasPermi('meeting:schedule:add')")
    @Log(title="Schedule", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcSchedule schedule) { schedule.setCreateBy(getUsername()); return toAjax(ycScheduleService.insertYcSchedule(schedule)); }
    @PreAuthorize("@ss.hasPermi('meeting:schedule:edit')")
    @Log(title="Schedule", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcSchedule schedule) { schedule.setUpdateBy(getUsername()); return toAjax(ycScheduleService.updateYcSchedule(schedule)); }
    @PreAuthorize("@ss.hasPermi('meeting:schedule:remove')")
    @Log(title="Schedule", businessType=BusinessType.DELETE)
    @DeleteMapping("/{scheduleIds}")
    public AjaxResult remove(@PathVariable Long[] scheduleIds) { return toAjax(ycScheduleService.deleteYcScheduleByIds(scheduleIds)); }
}