package com.ruoyi.system.domain;
import java.math.BigDecimal; import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcHotelOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long orderId; private Long activityId; private Long hotelId; private Long roomId;
    private String guestName; private String phone;
    @JsonFormat(pattern="yyyy-MM-dd") private Date checkInDate;
    @JsonFormat(pattern="yyyy-MM-dd") private Date checkOutDate;
    private Integer roomCount; private BigDecimal amount; private String orderStatus; private String delFlag;
    private String hotelName; private String roomName;
    public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
    public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;}
    public Long getHotelId(){return hotelId;} public void setHotelId(Long v){hotelId=v;}
    public Long getRoomId(){return roomId;} public void setRoomId(Long v){roomId=v;}
    public String getGuestName(){return guestName;} public void setGuestName(String v){guestName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public Date getCheckInDate(){return checkInDate;} public void setCheckInDate(Date v){checkInDate=v;}
    public Date getCheckOutDate(){return checkOutDate;} public void setCheckOutDate(Date v){checkOutDate=v;}
    public Integer getRoomCount(){return roomCount;} public void setRoomCount(Integer v){roomCount=v;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getOrderStatus(){return orderStatus;} public void setOrderStatus(String v){orderStatus=v;}
    public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
    public String getHotelName(){return hotelName;} public void setHotelName(String v){hotelName=v;}
    public String getRoomName(){return roomName;} public void setRoomName(String v){roomName=v;}
    @Override public String toString(){return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE).append("orderId",orderId).append("guestName",guestName).toString();}
}