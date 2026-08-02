package com.ruoyi.web.controller.meeting;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivity;
import com.ruoyi.system.service.IYcActivityService;

/**
 * 会议活动 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/meeting/activity")
public class YcActivityController extends BaseController
{
    @Autowired
    private IYcActivityService ycActivityService;

    /**
     * 查询会议列表
     */
    @PreAuthorize("@ss.hasPermi('meeting:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcActivity ycActivity)
    {
        startPage();
        List<YcActivity> list = ycActivityService.selectYcActivityList(ycActivity);
        return getDataTable(list);
    }

    /**
     * 获取会议详细信息
     */
    @PreAuthorize("@ss.hasPermi('meeting:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable Long activityId)
    {
        return success(ycActivityService.selectYcActivityById(activityId));
    }

    /**
     * 随机生成会议编号
     */
    @PreAuthorize("@ss.hasAnyPermi('meeting:activity:add,meeting:activity:edit')")
    @GetMapping("/genCode")
    public AjaxResult genCode()
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("data", ycActivityService.generateActivityCode());
        return ajax;
    }

    /**
     * 新增会议
     */
    @PreAuthorize("@ss.hasPermi('meeting:activity:add')")
    @Log(title = "会议管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody YcActivity ycActivity)
    {
        if (StringUtils.isNotEmpty(ycActivity.getActivityCode())
                && !ycActivityService.checkActivityCodeUnique(ycActivity))
        {
            return error("新增会议失败，会议编号已存在");
        }
        ycActivity.setCreateBy(getUsername());
        return toAjax(ycActivityService.insertYcActivity(ycActivity));
    }

    /**
     * 修改会议
     */
    @PreAuthorize("@ss.hasPermi('meeting:activity:edit')")
    @Log(title = "会议管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody YcActivity ycActivity)
    {
        if (StringUtils.isNotEmpty(ycActivity.getActivityCode())
                && !ycActivityService.checkActivityCodeUnique(ycActivity))
        {
            return error("修改会议失败，会议编号已存在");
        }
        ycActivity.setUpdateBy(getUsername());
        return toAjax(ycActivityService.updateYcActivity(ycActivity));
    }

    /**
     * 删除会议
     */
    @PreAuthorize("@ss.hasPermi('meeting:activity:remove')")
    @Log(title = "会议管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds)
    {
        return toAjax(ycActivityService.deleteYcActivityByIds(activityIds));
    }
}