package com.ruoyi.system.domain;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcActivityNav extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long navId; private Long activityId; private String title; private String address;
    private String longitude; private String latitude; private String phone; private String coverUrl;
    private Integer sortOrder; private String status; private String delFlag;
    public Long getNavId(){return navId;} public void setNavId(Long v){navId=v;}
    public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getLongitude(){return longitude;} public void setLongitude(String v){longitude=v;}
    public String getLatitude(){return latitude;} public void setLatitude(String v){latitude=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getCoverUrl(){return coverUrl;} public void setCoverUrl(String v){coverUrl=v;}
    public Integer getSortOrder(){return sortOrder;} public void setSortOrder(Integer v){sortOrder=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
    @Override public String toString(){return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE).append("navId",navId).append("title",title).toString();}
}