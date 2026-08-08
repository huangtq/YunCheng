package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcActivityNav;
import com.ruoyi.system.service.IYcActivityNavService;
@RestController
@RequestMapping("/meeting/nav")
public class YcActivityNavController extends BaseController {
    @Autowired private IYcActivityNavService service;
    @PreAuthorize("@ss.hasPermi('meeting:nav:list')") @GetMapping("/list")
    public TableDataInfo list(YcActivityNav q){ startPage(); return getDataTable(service.selectYcActivityNavList(q)); }
    @PreAuthorize("@ss.hasPermi('meeting:nav:list')") @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId){ return success(service.selectNavStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:nav:list')") @GetMapping("/{navId}")
    public AjaxResult getInfo(@PathVariable Long navId){ return success(service.selectYcActivityNavById(navId)); }
    @PreAuthorize("@ss.hasPermi('meeting:nav:add')") @Log(title="ActivityNav", businessType=BusinessType.INSERT) @PostMapping
    public AjaxResult add(@RequestBody YcActivityNav e){ e.setCreateBy(getUsername()); return toAjax(service.insertYcActivityNav(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:nav:edit')") @Log(title="ActivityNav", businessType=BusinessType.UPDATE) @PutMapping
    public AjaxResult edit(@RequestBody YcActivityNav e){ e.setUpdateBy(getUsername()); return toAjax(service.updateYcActivityNav(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:nav:remove')") @Log(title="ActivityNav", businessType=BusinessType.DELETE) @DeleteMapping("/{navIds}")
    public AjaxResult remove(@PathVariable Long[] navIds){ return toAjax(service.deleteYcActivityNavByIds(navIds)); }
}