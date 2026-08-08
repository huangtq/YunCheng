package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcGuestTaskRule;
import com.ruoyi.system.service.IYcGuestTaskRuleService;

@RestController
@RequestMapping("/meeting/guest/rule")
public class YcGuestTaskRuleController extends BaseController {
    @Autowired private IYcGuestTaskRuleService ycGuestTaskRuleService;

    @PreAuthorize("@ss.hasPermi('meeting:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcGuestTaskRule rule) {
        startPage();
        return getDataTable(ycGuestTaskRuleService.selectYcGuestTaskRuleList(rule));
    }
    @PreAuthorize("@ss.hasPermi('meeting:rule:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycGuestTaskRuleService.selectRuleStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:rule:list')")
    @GetMapping("/{ruleId}")
    public AjaxResult getInfo(@PathVariable Long ruleId) { return success(ycGuestTaskRuleService.selectYcGuestTaskRuleById(ruleId)); }
    @PreAuthorize("@ss.hasPermi('meeting:rule:add')")
    @Log(title="GuestTaskRule", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcGuestTaskRule rule) { rule.setCreateBy(getUsername()); return toAjax(ycGuestTaskRuleService.insertYcGuestTaskRule(rule)); }
    @PreAuthorize("@ss.hasPermi('meeting:rule:edit')")
    @Log(title="GuestTaskRule", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcGuestTaskRule rule) { rule.setUpdateBy(getUsername()); return toAjax(ycGuestTaskRuleService.updateYcGuestTaskRule(rule)); }
    @PreAuthorize("@ss.hasPermi('meeting:rule:remove')")
    @Log(title="GuestTaskRule", businessType=BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds) { return toAjax(ycGuestTaskRuleService.deleteYcGuestTaskRuleByIds(ruleIds)); }
}