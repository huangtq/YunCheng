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
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.system.service.IYcApplyOrderService;

@RestController
@RequestMapping("/meeting/apply/order")
public class YcApplyOrderController extends BaseController
{
    @Autowired
    private IYcApplyOrderService ycApplyOrderService;

    @PreAuthorize("@ss.hasPermi('meeting:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcApplyOrder order)
    {
        startPage();
        List<YcApplyOrder> list = ycApplyOrderService.selectYcApplyOrderList(order);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId)
    {
        return success(ycApplyOrderService.selectOrderStats(activityId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:list')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(ycApplyOrderService.selectYcApplyOrderById(orderId));
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:add')")
    @Log(title = "ApplyOrder", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcApplyOrder order)
    {
        order.setCreateBy(getUsername());
        return toAjax(ycApplyOrderService.insertYcApplyOrder(order));
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:edit')")
    @Log(title = "ApplyOrder", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcApplyOrder order)
    {
        order.setUpdateBy(getUsername());
        return toAjax(ycApplyOrderService.updateYcApplyOrder(order));
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:edit')")
    @Log(title = "ApplyOrderCheckin", businessType = BusinessType.UPDATE)
    @PutMapping("/checkin/{orderId}")
    public AjaxResult checkin(@PathVariable Long orderId)
    {
        return toAjax(ycApplyOrderService.checkin(orderId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:remove')")
    @Log(title = "ApplyOrder", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(ycApplyOrderService.deleteYcApplyOrderByIds(orderIds));
    }
}