package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Apply order yc_apply_order
 */
public class YcApplyOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long activityId;
    private Long channelId;
    private String orderNo;
    private String contactName;
    private String mobile;
    private String gender;
    private String company;
    /** 0 registered, 2 cancelled */
    private String orderStatus;
    /** 0 not checked in, 1 checked in */
    private String checkinStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkinTime;
    private String formJson;
    private String delFlag;

    /** join fields */
    private String channelName;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getCheckinStatus() { return checkinStatus; }
    public void setCheckinStatus(String checkinStatus) { this.checkinStatus = checkinStatus; }
    public Date getCheckinTime() { return checkinTime; }
    public void setCheckinTime(Date checkinTime) { this.checkinTime = checkinTime; }
    public String getFormJson() { return formJson; }
    public void setFormJson(String formJson) { this.formJson = formJson; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", orderId)
            .append("orderNo", orderNo)
            .append("contactName", contactName)
            .append("mobile", mobile)
            .toString();
    }
}