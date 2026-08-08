package com.ruoyi.web.controller.meeting;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcApplyChannel;
import com.ruoyi.system.domain.YcApplyField;
import com.ruoyi.system.service.IYcApplyChannelService;
import com.ruoyi.system.service.IYcApplyFieldService;

@RestController
@RequestMapping("/meeting/apply/field")
public class YcApplyFieldController extends BaseController
{
    @Autowired
    private IYcApplyFieldService ycApplyFieldService;

    @Autowired
    private IYcApplyChannelService ycApplyChannelService;

    @PreAuthorize("@ss.hasPermi('meeting:apply:list')")
    @GetMapping("/list")
    public AjaxResult list(YcApplyField field)
    {
        if (field.getChannelId() != null)
        {
            YcApplyChannel channel = ycApplyChannelService.selectYcApplyChannelById(field.getChannelId());
            if (channel != null)
            {
                ycApplyFieldService.ensureStandardFields(channel.getChannelId(), channel.getActivityId(), getUsername());
            }
        }
        List<YcApplyField> list = ycApplyFieldService.selectYcApplyFieldList(field);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:list')")
    @GetMapping(value = "/{fieldId}")
    public AjaxResult getInfo(@PathVariable Long fieldId)
    {
        return success(ycApplyFieldService.selectYcApplyFieldById(fieldId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:add')")
    @Log(title = "ApplyField", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcApplyField field)
    {
        field.setCreateBy(getUsername());
        if (field.getActivityId() == null && field.getChannelId() != null)
        {
            YcApplyChannel channel = ycApplyChannelService.selectYcApplyChannelById(field.getChannelId());
            if (channel != null)
            {
                field.setActivityId(channel.getActivityId());
            }
        }
        if (field.getFieldScope() == null || field.getFieldScope().isEmpty())
        {
            field.setFieldScope("extend");
        }
        return toAjax(ycApplyFieldService.insertYcApplyField(field));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:edit')")
    @Log(title = "ApplyField", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcApplyField field)
    {
        field.setUpdateBy(getUsername());
        return toAjax(ycApplyFieldService.updateYcApplyField(field));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:edit')")
    @Log(title = "ApplyFieldEnabled", businessType = BusinessType.UPDATE)
    @PutMapping("/enabled")
    public AjaxResult changeEnabled(@RequestBody Map<String, Object> body)
    {
        Object idsObj = body.get("fieldIds");
        String enabledFlag = String.valueOf(body.get("enabledFlag"));
        if (idsObj == null)
        {
            return error("fieldIds required");
        }
        List<?> idList = (List<?>) idsObj;
        Long[] fieldIds = idList.stream().map(item -> Long.valueOf(String.valueOf(item))).toArray(Long[]::new);
        return toAjax(ycApplyFieldService.updateEnabledByIds(fieldIds, enabledFlag, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:remove')")
    @Log(title = "ApplyField", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fieldIds}")
    public AjaxResult remove(@PathVariable Long[] fieldIds)
    {
        return toAjax(ycApplyFieldService.deleteYcApplyFieldByIds(fieldIds));
    }
}