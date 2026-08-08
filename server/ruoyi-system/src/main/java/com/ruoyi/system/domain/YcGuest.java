package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

public class YcGuest extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long guestId;
    private Long activityId;
    private String guestName;
    private String phone;
    private String orgName;
    private String title;
    private String englishName;
    private String guestType;
    private String avatar;
    private String intro;
    private String needHotel;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkInDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkOutDate;
    private String idCard;
    private String attendFlag;
    private Integer sortOrder;
    private String delFlag;
    private Integer tripCount;
    private Integer feeCount;

    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    public String getGuestType() { return guestType; }
    public void setGuestType(String guestType) { this.guestType = guestType; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
    public String getNeedHotel() { return needHotel; }
    public void setNeedHotel(String needHotel) { this.needHotel = needHotel; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getAttendFlag() { return attendFlag; }
    public void setAttendFlag(String attendFlag) { this.attendFlag = attendFlag; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Integer getTripCount() { return tripCount; }
    public void setTripCount(Integer tripCount) { this.tripCount = tripCount; }
    public Integer getFeeCount() { return feeCount; }
    public void setFeeCount(Integer feeCount) { this.feeCount = feeCount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("guestId", guestId).append("guestName", guestName).toString();
    }
}