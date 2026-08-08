package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcApplyField;
import com.ruoyi.system.mapper.YcApplyFieldMapper;
import com.ruoyi.system.service.IYcApplyFieldService;

@Service
public class YcApplyFieldServiceImpl implements IYcApplyFieldService
{
    @Autowired
    private YcApplyFieldMapper ycApplyFieldMapper;

    @Override
    public YcApplyField selectYcApplyFieldById(Long fieldId)
    {
        return ycApplyFieldMapper.selectYcApplyFieldById(fieldId);
    }

    @Override
    public List<YcApplyField> selectYcApplyFieldList(YcApplyField field)
    {
        return ycApplyFieldMapper.selectYcApplyFieldList(field);
    }

    private void fillDefaults(YcApplyField field)
    {
        if (StringUtils.isEmpty(field.getFieldScope()))
        {
            field.setFieldScope("extend");
        }
        if (StringUtils.isEmpty(field.getFieldKey()))
        {
            field.setFieldKey("");
        }
        if (StringUtils.isEmpty(field.getFieldType()))
        {
            field.setFieldType("input");
        }
        if (field.getPlaceholder() == null)
        {
            field.setPlaceholder("");
        }
        if (field.getOptionsJson() == null)
        {
            field.setOptionsJson("");
        }
        if (field.getShowCondition() == null)
        {
            field.setShowCondition("");
        }
        if (StringUtils.isEmpty(field.getRequiredFlag()))
        {
            field.setRequiredFlag("0");
        }
        if (StringUtils.isEmpty(field.getEnabledFlag()))
        {
            field.setEnabledFlag("1");
        }
        if (field.getSortOrder() == null)
        {
            field.setSortOrder(100);
        }
    }

    @Override
    public int insertYcApplyField(YcApplyField field)
    {
        fillDefaults(field);
        return ycApplyFieldMapper.insertYcApplyField(field);
    }

    @Override
    public int updateYcApplyField(YcApplyField field)
    {
        fillDefaults(field);
        return ycApplyFieldMapper.updateYcApplyField(field);
    }

    @Override
    public int deleteYcApplyFieldByIds(Long[] fieldIds)
    {
        return ycApplyFieldMapper.deleteYcApplyFieldByIds(fieldIds);
    }

    @Override
    public int updateEnabledByIds(Long[] fieldIds, String enabledFlag, String updateBy)
    {
        YcApplyField field = new YcApplyField();
        field.setEnabledFlag(enabledFlag);
        field.setUpdateBy(updateBy);
        Map<String, Object> params = new HashMap<>();
        params.put("fieldIds", fieldIds);
        field.setParams(params);
        return ycApplyFieldMapper.updateEnabledByIds(field);
    }

    @Override
    public void ensureStandardFields(Long channelId, Long activityId, String createBy)
    {
        if (channelId == null || activityId == null)
        {
            return;
        }
        if (ycApplyFieldMapper.countByChannelId(channelId) > 0)
        {
            return;
        }
        List<YcApplyField> list = buildStandardFields(channelId, activityId, createBy);
        if (!list.isEmpty())
        {
            ycApplyFieldMapper.batchInsertYcApplyField(list);
        }
    }

    private List<YcApplyField> buildStandardFields(Long channelId, Long activityId, String createBy)
    {
        List<YcApplyField> list = new ArrayList<>();
        list.add(createStandard(channelId, activityId, createBy, "gender", "\u6027\u522b", "radio", "", "[\"\u7537\",\"\u5973\"]", "1", "1", 1));
        list.add(createStandard(channelId, activityId, createBy, "region", "\u7701\u5e02\u533a", "system", "", "", "1", "1", 2));
        list.add(createStandard(channelId, activityId, createBy, "company", "\u5355\u4f4d", "input", "", "", "1", "1", 3));
        list.add(createStandard(channelId, activityId, createBy, "position", "\u804c\u52a1", "input", "", "", "1", "1", 4));
        list.add(createStandard(channelId, activityId, createBy, "hotel", "\u662f\u5426\u9884\u5b9a\u9152\u5e97", "system", "", "", "1", "1", 5));
        list.add(createStandard(channelId, activityId, createBy, "idCard", "\u8eab\u4efd\u8bc1", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "age", "\u5e74\u9f84", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "department", "\u79d1\u5ba4", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "grassroots", "\u662f\u5426\u6765\u81ea\u57fa\u5c42", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "westProvince", "\u662f\u5426\u96b6\u5c5e\u897f\u90e8\u5341\u4e8c\u7701", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "attendType", "\u53c2\u4f1a\u5f62\u5f0f", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "email", "\u90ae\u7bb1", "input", "", "", "0", "0", 100));
        list.add(createStandard(channelId, activityId, createBy, "title", "\u804c\u79f0", "input", "\u8bf7\u8f93\u5165\u804c\u79f0", "", "0", "0", 100));
        return list;
    }

    private YcApplyField createStandard(Long channelId, Long activityId, String createBy, String fieldKey, String fieldName,
            String fieldType, String placeholder, String optionsJson, String requiredFlag, String enabledFlag, Integer sortOrder)
    {
        YcApplyField field = new YcApplyField();
        field.setChannelId(channelId);
        field.setActivityId(activityId);
        field.setFieldScope("standard");
        field.setFieldKey(fieldKey);
        field.setFieldName(fieldName);
        field.setFieldType(fieldType);
        field.setPlaceholder(placeholder);
        field.setOptionsJson(optionsJson);
        field.setRequiredFlag(requiredFlag);
        field.setEnabledFlag(enabledFlag);
        field.setSortOrder(sortOrder);
        field.setCreateBy(createBy);
        return field;
    }
}