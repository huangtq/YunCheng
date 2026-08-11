package com.ruoyi.web.controller.meeting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivity;
import com.ruoyi.system.domain.YcApplyField;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.system.service.IYcActivityService;
import com.ruoyi.system.service.IYcApplyFieldService;
import com.ruoyi.system.service.IYcApplyOrderService;

@RestController
@RequestMapping("/meeting/apply/order")
public class YcApplyOrderController extends BaseController
{
    @Autowired
    private IYcApplyOrderService ycApplyOrderService;

    @Autowired
    private IYcActivityService ycActivityService;

    @Autowired
    private IYcApplyFieldService ycApplyFieldService;

    @PreAuthorize("@ss.hasPermi('meeting:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcApplyOrder order)
    {
        startPage();
        List<YcApplyOrder> list = ycApplyOrderService.selectYcApplyOrderList(order);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('meeting:order:export')")
    @Log(title = "报名订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YcApplyOrder order) throws IOException
    {
        List<YcApplyOrder> list = ycApplyOrderService.selectYcApplyOrderList(order);
        Long activityId = order.getActivityId();
        if (activityId == null && !list.isEmpty())
        {
            activityId = list.get(0).getActivityId();
        }
        List<YcApplyField> fields = listExportFields(activityId);
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        columns.put("orderNo", "订单号");
        for (YcApplyField field : fields)
        {
            if (StringUtils.isNotEmpty(field.getFieldKey()))
            {
                columns.putIfAbsent(field.getFieldKey(), fieldLabel(field.getFieldKey(), field.getFieldName()));
            }
        }
        // Keep fields from older orders even if their configuration was later changed.
        for (YcApplyOrder item : list)
        {
            for (String key : parseFormData(item.getFormJson()).keySet())
            {
                if (StringUtils.isNotEmpty(key))
                {
                    columns.putIfAbsent(key, fieldLabel(key, null));
                }
            }
        }
        columns.put("orderStatus", "订单状态");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        FileUtils.setAttachmentResponseHeader(response, buildExportFileName(activityId));
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(500))
        {
            Sheet sheet = workbook.createSheet("报名订单");
            Row header = sheet.createRow(0);
            int headerIndex = 0;
            for (String name : columns.values())
            {
                header.createCell(headerIndex++).setCellValue(name);
            }

            int rowIndex = 1;
            for (YcApplyOrder item : list)
            {
                Row row = sheet.createRow(rowIndex++);
                JSONObject formData = parseFormData(item.getFormJson());
                int columnIndex = 0;
                for (String key : columns.keySet())
                {
                    row.createCell(columnIndex++).setCellValue(resolveExportValue(key, item, formData));
                }
            }
            for (int i = 0; i < columns.size(); i++)
            {
                sheet.setColumnWidth(i, 20 * 256);
            }
            workbook.write(response.getOutputStream());
            workbook.dispose();
        }
    }

    private List<YcApplyField> listExportFields(Long activityId)
    {
        if (activityId == null)
        {
            return new ArrayList<>();
        }
        YcApplyField query = new YcApplyField();
        query.setActivityId(activityId);
        query.setEnabledFlag("1");
        List<YcApplyField> fields = ycApplyFieldService.selectYcApplyFieldList(query);
        fields.sort(Comparator.comparing(YcApplyField::getSortOrder, Comparator.nullsLast(Integer::compareTo))
            .thenComparing(YcApplyField::getFieldId));
        return fields;
    }

    private JSONObject parseFormData(String formJson)
    {
        if (StringUtils.isEmpty(formJson))
        {
            return new JSONObject();
        }
        try
        {
            JSONObject result = JSON.parseObject(formJson);
            return result == null ? new JSONObject() : result;
        }
        catch (Exception ignored)
        {
            return new JSONObject();
        }
    }

    private String resolveExportValue(String key, YcApplyOrder order, Map<String, Object> formData)
    {
        if ("orderNo".equals(key)) return StringUtils.defaultString(order.getOrderNo());
        if ("orderStatus".equals(key)) return "2".equals(order.getOrderStatus()) ? "已取消" : "已报名";

        Object value = formData.get(key);
        if (value != null) return String.valueOf(value);
        if ("name".equals(key) || "contactName".equals(key)) return valueOrEmpty(order.getContactName());
        if ("mobile".equals(key) || "phone".equals(key)) return valueOrEmpty(order.getMobile());
        if ("gender".equals(key)) return valueOrEmpty(order.getGender());
        if ("company".equals(key)) return valueOrEmpty(order.getCompany());
        return "";
    }

    private String fieldLabel(String key, String configuredName)
    {
        if (StringUtils.isNotEmpty(configuredName)) return configuredName;
        Map<String, String> standardLabels = new LinkedHashMap<>();
        standardLabels.put("name", "姓名");
        standardLabels.put("contactName", "姓名");
        standardLabels.put("mobile", "手机号");
        standardLabels.put("phone", "手机号");
        standardLabels.put("gender", "性别");
        standardLabels.put("age", "年龄");
        standardLabels.put("company", "单位");
        standardLabels.put("department", "科室");
        standardLabels.put("position", "职务");
        standardLabels.put("region", "省市区");
        standardLabels.put("idCard", "身份证");
        standardLabels.put("email", "邮箱");
        return standardLabels.getOrDefault(key, key);
    }

    private String valueOrEmpty(String value)
    {
        return value == null ? "" : value;
    }

    private String buildExportFileName(Long activityId)
    {
        String activityName = "报名订单";
        if (activityId != null)
        {
            YcActivity activity = ycActivityService.selectYcActivityById(activityId);
            if (activity != null && StringUtils.isNotEmpty(activity.getActivityName()))
            {
                activityName = activity.getActivityName();
            }
        }
        String safeName = activityName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        return safeName + "_报名订单.xlsx";
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
