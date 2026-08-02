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
import com.ruoyi.system.domain.YcApplyChannel;
import com.ruoyi.system.service.IYcApplyChannelService;

@RestController
@RequestMapping("/meeting/apply/channel")
public class YcApplyChannelController extends BaseController
{
    @Autowired
    private IYcApplyChannelService ycApplyChannelService;

    @PreAuthorize("@ss.hasPermi('meeting:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcApplyChannel channel)
    {
        startPage();
        List<YcApplyChannel> list = ycApplyChannelService.selectYcApplyChannelList(channel);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:list')")
    @GetMapping(value = "/{channelId}")
    public AjaxResult getInfo(@PathVariable Long channelId)
    {
        return success(ycApplyChannelService.selectYcApplyChannelById(channelId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:add')")
    @Log(title = "报名通道", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcApplyChannel channel)
    {
        channel.setCreateBy(getUsername());
        return toAjax(ycApplyChannelService.insertYcApplyChannel(channel));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:edit')")
    @Log(title = "报名通道", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcApplyChannel channel)
    {
        channel.setUpdateBy(getUsername());
        return toAjax(ycApplyChannelService.updateYcApplyChannel(channel));
    }

    @PreAuthorize("@ss.hasPermi('meeting:apply:remove')")
    @Log(title = "报名通道", businessType = BusinessType.DELETE)
    @DeleteMapping("/{channelIds}")
    public AjaxResult remove(@PathVariable Long[] channelIds)
    {
        return toAjax(ycApplyChannelService.deleteYcApplyChannelByIds(channelIds));
    }
}