package com.ruoyi.web.controller.meeting;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcActivityHomeVersion;
import com.ruoyi.system.service.IYcActivityHomeVersionService;

@RestController
@RequestMapping("/meeting/home-version")
public class YcActivityHomeVersionController extends BaseController
{
    @Autowired private IYcActivityHomeVersionService homeVersionService;

    @PreAuthorize("@ss.hasPermi('meeting:home:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcActivityHomeVersion version)
    {
        startPage();
        List<YcActivityHomeVersion> list = homeVersionService.selectYcActivityHomeVersionList(version);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:home:list')")
    @GetMapping("/{versionId}")
    public AjaxResult getInfo(@PathVariable Long versionId)
    {
        return success(homeVersionService.selectYcActivityHomeVersionById(versionId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:home:edit')")
    @Log(title = "移动端首页草稿", businessType = BusinessType.UPDATE)
    @PostMapping("/draft")
    public AjaxResult saveDraft(@RequestBody YcActivityHomeVersion version)
    {
        version.setCreateBy(getUsername());
        version.setUpdateBy(getUsername());
        return success(homeVersionService.saveDraft(version));
    }

    @PreAuthorize("@ss.hasPermi('meeting:home:publish')")
    @Log(title = "移动端首页发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{versionId}/publish")
    public AjaxResult publish(@PathVariable Long versionId, @RequestBody(required = false) Map<String, Object> body,
        @RequestParam(required = false) String remark)
    {
        String publishRemark = remark;
        if (body != null && body.get("publishRemark") != null) publishRemark = String.valueOf(body.get("publishRemark"));
        return success(homeVersionService.publish(versionId, getUsername(), publishRemark));
    }

    @PreAuthorize("@ss.hasPermi('meeting:home:edit')")
    @PostMapping("/{versionId}/schedule")
    public AjaxResult schedule(@PathVariable Long versionId, @RequestParam String publishAt)
    {
        Date value;
        try { value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(publishAt); }
        catch (ParseException e) { return error("publishAt must use yyyy-MM-dd HH:mm:ss"); }
        return success(homeVersionService.schedule(versionId, value, getUsername()));
    }
}
