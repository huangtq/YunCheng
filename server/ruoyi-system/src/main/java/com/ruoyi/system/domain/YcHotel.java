package com.ruoyi.system.domain;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcHotel extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long hotelId; private Long activityId; private String hotelName; private String phone;
    private String address; private String coverUrl; private String saleStatus; private Integer sortOrder;
    private String delFlag; private Integer roomCount; private Integer orderCount;
    public Long getHotelId(){return hotelId;} public void setHotelId(Long v){hotelId=v;}
    public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;}
    public String getHotelName(){return hotelName;} public void setHotelName(String v){hotelName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getCoverUrl(){return coverUrl;} public void setCoverUrl(String v){coverUrl=v;}
    public String getSaleStatus(){return saleStatus;} public void setSaleStatus(String v){saleStatus=v;}
    public Integer getSortOrder(){return sortOrder;} public void setSortOrder(Integer v){sortOrder=v;}
    public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
    public Integer getRoomCount(){return roomCount;} public void setRoomCount(Integer v){roomCount=v;}
    public Integer getOrderCount(){return orderCount;} public void setOrderCount(Integer v){orderCount=v;}
    @Override public String toString(){return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE).append("hotelId",hotelId).append("hotelName",hotelName).toString();}
}