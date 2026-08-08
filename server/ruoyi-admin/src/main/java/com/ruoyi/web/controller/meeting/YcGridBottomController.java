package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcGridBottom;
import com.ruoyi.system.service.IYcGridBottomService;
@RestController
@RequestMapping("/meeting/bottom")
public class YcGridBottomController extends BaseController {
    @Autowired private IYcGridBottomService service;
    @PreAuthorize("@ss.hasPermi('meeting:bottom:list')") @GetMapping("/list")
    public TableDataInfo list(YcGridBottom q){ startPage(); return getDataTable(service.selectYcGridBottomList(q)); }
    @PreAuthorize("@ss.hasPermi('meeting:bottom:list')") @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId){ return success(service.selectBottomStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:bottom:list')") @GetMapping("/{bottomId}")
    public AjaxResult getInfo(@PathVariable Long bottomId){ return success(service.selectYcGridBottomById(bottomId)); }
    @PreAuthorize("@ss.hasPermi('meeting:bottom:add')") @Log(title="GridBottom", businessType=BusinessType.INSERT) @PostMapping
    public AjaxResult add(@RequestBody YcGridBottom e){ e.setCreateBy(getUsername()); return toAjax(service.insertYcGridBottom(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:bottom:edit')") @Log(title="GridBottom", businessType=BusinessType.UPDATE) @PutMapping
    public AjaxResult edit(@RequestBody YcGridBottom e){ e.setUpdateBy(getUsername()); return toAjax(service.updateYcGridBottom(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:bottom:remove')") @Log(title="GridBottom", businessType=BusinessType.DELETE) @DeleteMapping("/{bottomIds}")
    public AjaxResult remove(@PathVariable Long[] bottomIds){ return toAjax(service.deleteYcGridBottomByIds(bottomIds)); }
}