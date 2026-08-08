package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Apply channel yc_apply_channel
 */
public class YcApplyChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long channelId;
    private Long activityId;
    private String channelName;
    private String isMain;
    private Long parentId;
    private Integer sortOrder;
    private String priceType;
    private BigDecimal price;
    private Integer quota;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;
    private String needInvite;
    private String needAudit;
    private String needInvoice;
    private String visible;
    private String smsNotify;
    private String delFlag;

    /** parent channel name (list only) */
    private String parentName;
    /** configured field count (list only) */
    private Integer fieldCount;

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public String getIsMain() { return isMain; }
    public void setIsMain(String isMain) { this.isMain = isMain; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getPriceType() { return priceType; }
    public void setPriceType(String priceType) { this.priceType = priceType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuota() { return quota; }
    public void setQuota(Integer quota) { this.quota = quota; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public String getNeedInvite() { return needInvite; }
    public void setNeedInvite(String needInvite) { this.needInvite = needInvite; }
    public String getNeedAudit() { return needAudit; }
    public void setNeedAudit(String needAudit) { this.needAudit = needAudit; }
    public String getNeedInvoice() { return needInvoice; }
    public void setNeedInvoice(String needInvoice) { this.needInvoice = needInvoice; }
    public String getVisible() { return visible; }
    public void setVisible(String visible) { this.visible = visible; }
    public String getSmsNotify() { return smsNotify; }
    public void setSmsNotify(String smsNotify) { this.smsNotify = smsNotify; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public Integer getFieldCount() { return fieldCount; }
    public void setFieldCount(Integer fieldCount) { this.fieldCount = fieldCount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("channelId", channelId)
            .append("activityId", activityId)
            .append("channelName", channelName)
            .toString();
    }
}