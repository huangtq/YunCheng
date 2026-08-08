package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcExhibitor;
import com.ruoyi.system.service.IYcExhibitorService;

@RestController
@RequestMapping("/meeting/exhibitor")
public class YcExhibitorController extends BaseController {
    @Autowired private IYcExhibitorService ycExhibitorService;

    @PreAuthorize("@ss.hasPermi('meeting:exhibitor:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcExhibitor exhibitor) {
        startPage();
        return getDataTable(ycExhibitorService.selectYcExhibitorList(exhibitor));
    }
    @PreAuthorize("@ss.hasPermi('meeting:exhibitor:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycExhibitorService.selectExhibitorStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:exhibitor:list')")
    @GetMapping("/{exhibitorId}")
    public AjaxResult getInfo(@PathVariable Long exhibitorId) { return success(ycExhibitorService.selectYcExhibitorById(exhibitorId)); }
    @PreAuthorize("@ss.hasPermi('meeting:exhibitor:add')")
    @Log(title="Exhibitor", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcExhibitor exhibitor) { exhibitor.setCreateBy(getUsername()); return toAjax(ycExhibitorService.insertYcExhibitor(exhibitor)); }
    @PreAuthorize("@ss.hasPermi('meeting:exhibitor:edit')")
    @Log(title="Exhibitor", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcExhibitor exhibitor) { exhibitor.setUpdateBy(getUsername()); return toAjax(ycExhibitorService.updateYcExhibitor(exhibitor)); }
    @PreAuthorize("@ss.hasPermi('meeting:exhibitor:remove')")
    @Log(title="Exhibitor", businessType=BusinessType.DELETE)
    @DeleteMapping("/{exhibitorIds}")
    public AjaxResult remove(@PathVariable Long[] exhibitorIds) { return toAjax(ycExhibitorService.deleteYcExhibitorByIds(exhibitorIds)); }
}