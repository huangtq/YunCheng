package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Apply form field yc_apply_field
 */
public class YcApplyField extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long fieldId;
    private Long channelId;
    private Long activityId;
    private String fieldScope;
    private String fieldKey;
    private String fieldName;
    private String fieldType;
    private String placeholder;
    private String optionsJson;
    /** show condition JSON: {"fieldKey":"gender","value":"男"} */
    private String showCondition;
    private String requiredFlag;
    private String enabledFlag;
    private Integer sortOrder;
    private String delFlag;

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getFieldScope() { return fieldScope; }
    public void setFieldScope(String fieldScope) { this.fieldScope = fieldScope; }
    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
    public String getShowCondition() { return showCondition; }
    public void setShowCondition(String showCondition) { this.showCondition = showCondition; }
    public String getRequiredFlag() { return requiredFlag; }
    public void setRequiredFlag(String requiredFlag) { this.requiredFlag = requiredFlag; }
    public String getEnabledFlag() { return enabledFlag; }
    public void setEnabledFlag(String enabledFlag) { this.enabledFlag = enabledFlag; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("fieldId", fieldId)
            .append("channelId", channelId)
            .append("fieldName", fieldName)
            .append("fieldType", fieldType)
            .append("showCondition", showCondition)
            .toString();
    }
}