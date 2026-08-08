package com.ruoyi.web.controller.meeting;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcTopic;
import com.ruoyi.system.service.IYcTopicService;

@RestController
@RequestMapping("/meeting/topic")
public class YcTopicController extends BaseController {
    @Autowired private IYcTopicService ycTopicService;

    @PreAuthorize("@ss.hasPermi('meeting:topic:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcTopic topic) { startPage(); return getDataTable(ycTopicService.selectYcTopicList(topic)); }
    @PreAuthorize("@ss.hasPermi('meeting:topic:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycTopicService.selectTopicStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:topic:list')")
    @GetMapping("/{topicId}")
    public AjaxResult getInfo(@PathVariable Long topicId) { return success(ycTopicService.selectYcTopicById(topicId)); }
    @PreAuthorize("@ss.hasPermi('meeting:topic:add')")
    @Log(title="Topic", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcTopic topic) { topic.setCreateBy(getUsername()); return toAjax(ycTopicService.insertYcTopic(topic)); }
    @PreAuthorize("@ss.hasPermi('meeting:topic:edit')")
    @Log(title="Topic", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcTopic topic) { topic.setUpdateBy(getUsername()); return toAjax(ycTopicService.updateYcTopic(topic)); }
    @PreAuthorize("@ss.hasPermi('meeting:topic:remove')")
    @Log(title="Topic", businessType=BusinessType.DELETE)
    @DeleteMapping("/{topicIds}")
    public AjaxResult remove(@PathVariable Long[] topicIds) { return toAjax(ycTopicService.deleteYcTopicByIds(topicIds)); }
}