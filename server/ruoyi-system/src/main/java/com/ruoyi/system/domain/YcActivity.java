package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会议活动对象 yc_activity
 *
 * @author ruoyi
 */
public class YcActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会议ID */
    private Long activityId;

    /** 会议编号 */
    private String activityCode;

    /** 会议名称 */
    private String activityName;

    /** 主视觉地址 */
    private String coverUrl;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 详细地址 */
    private String address;

    /** 第三方链接 */
    private String thirdPartyUrl;

    /** 是否展示（0否 1是） */
    private String isShow;

    /** 是否热门（0否 1是） */
    private String isHot;

    /** 是否首页（0否 1是） */
    private String isHome;

    /** 报名人数 */
    private Integer registerCount;

    /** 点击/访问 */
    private Integer visitCount;

    /** 流量/观看 */
    private Integer viewCount;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 名称或编号关键词 */
    private String keyword;

    public Long getActivityId()
    {
        return activityId;
    }

    public void setActivityId(Long activityId)
    {
        this.activityId = activityId;
    }

    public String getActivityCode()
    {
        return activityCode;
    }

    public void setActivityCode(String activityCode)
    {
        this.activityCode = activityCode;
    }

    public String getActivityName()
    {
        return activityName;
    }

    public void setActivityName(String activityName)
    {
        this.activityName = activityName;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getThirdPartyUrl()
    {
        return thirdPartyUrl;
    }

    public void setThirdPartyUrl(String thirdPartyUrl)
    {
        this.thirdPartyUrl = thirdPartyUrl;
    }

    public String getIsShow()
    {
        return isShow;
    }

    public void setIsShow(String isShow)
    {
        this.isShow = isShow;
    }

    public String getIsHot()
    {
        return isHot;
    }

    public void setIsHot(String isHot)
    {
        this.isHot = isHot;
    }

    public String getIsHome()
    {
        return isHome;
    }

    public void setIsHome(String isHome)
    {
        this.isHome = isHome;
    }

    public Integer getRegisterCount()
    {
        return registerCount;
    }

    public void setRegisterCount(Integer registerCount)
    {
        this.registerCount = registerCount;
    }

    public Integer getVisitCount()
    {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount)
    {
        this.visitCount = visitCount;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("activityId", getActivityId())
            .append("activityCode", getActivityCode())
            .append("activityName", getActivityName())
            .append("coverUrl", getCoverUrl())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("province", getProvince())
            .append("city", getCity())
            .append("address", getAddress())
            .append("thirdPartyUrl", getThirdPartyUrl())
            .append("isShow", getIsShow())
            .append("isHot", getIsHot())
            .append("isHome", getIsHome())
            .append("registerCount", getRegisterCount())
            .append("visitCount", getVisitCount())
            .append("viewCount", getViewCount())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}