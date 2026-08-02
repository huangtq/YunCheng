package com.ruoyi.web.controller.meeting;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcActivityGrid;
import com.ruoyi.system.service.IYcActivityGridService;

@RestController
@RequestMapping("/meeting/grid")
public class YcActivityGridController extends BaseController
{
    @Autowired
    private IYcActivityGridService ycActivityGridService;

    @PreAuthorize("@ss.hasPermi('meeting:grid:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcActivityGrid grid)
    {
        startPage();
        List<YcActivityGrid> list = ycActivityGridService.selectYcActivityGridList(grid);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:grid:list')")
    @GetMapping(value = "/{gridId}")
    public AjaxResult getInfo(@PathVariable Long gridId)
    {
        return success(ycActivityGridService.selectYcActivityGridById(gridId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:grid:add')")
    @Log(title = "九宫格配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcActivityGrid grid)
    {
        grid.setCreateBy(getUsername());
        return toAjax(ycActivityGridService.insertYcActivityGrid(grid));
    }

    @PreAuthorize("@ss.hasPermi('meeting:grid:edit')")
    @Log(title = "九宫格配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcActivityGrid grid)
    {
        grid.setUpdateBy(getUsername());
        return toAjax(ycActivityGridService.updateYcActivityGrid(grid));
    }

    @PreAuthorize("@ss.hasPermi('meeting:grid:remove')")
    @Log(title = "九宫格配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{gridIds}")
    public AjaxResult remove(@PathVariable Long[] gridIds)
    {
        return toAjax(ycActivityGridService.deleteYcActivityGridByIds(gridIds));
    }
}