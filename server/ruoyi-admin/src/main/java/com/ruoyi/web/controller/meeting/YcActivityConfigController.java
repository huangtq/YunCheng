package com.ruoyi.web.controller.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcActivityConfig;
import com.ruoyi.system.service.IYcActivityConfigService;

@RestController
@RequestMapping("/meeting/config")
public class YcActivityConfigController extends BaseController
{
    @Autowired
    private IYcActivityConfigService ycActivityConfigService;

    @PreAuthorize("@ss.hasPermi('meeting:activity:query')")
    @GetMapping("/{activityId}")
    public AjaxResult getInfo(@PathVariable Long activityId)
    {
        return success(ycActivityConfigService.getOrCreate(activityId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('meeting:activity:edit')")
    @Log(title = "会议配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcActivityConfig config)
    {
        config.setUpdateBy(getUsername());
        return toAjax(ycActivityConfigService.updateYcActivityConfig(config));
    }
}