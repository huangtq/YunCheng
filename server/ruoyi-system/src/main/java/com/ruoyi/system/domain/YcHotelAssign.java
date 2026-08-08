package com.ruoyi.system.domain;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcHotelAssign extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long assignId; private Long activityId; private Long hotelId; private Long orderId; private Long roomId;
    private String guestName; private String phone; private String roomNumber;
    @JsonFormat(pattern="yyyy-MM-dd") private Date checkInDate;
    @JsonFormat(pattern="yyyy-MM-dd") private Date checkOutDate;
    private String assignStatus; private String delFlag;
    private String hotelName; private String roomName;
    public Long getAssignId(){return assignId;} public void setAssignId(Long v){assignId=v;}
    public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;}
    public Long getHotelId(){return hotelId;} public void setHotelId(Long v){hotelId=v;}
    public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
    public Long getRoomId(){return roomId;} public void setRoomId(Long v){roomId=v;}
    public String getGuestName(){return guestName;} public void setGuestName(String v){guestName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getRoomNumber(){return roomNumber;} public void setRoomNumber(String v){roomNumber=v;}
    public Date getCheckInDate(){return checkInDate;} public void setCheckInDate(Date v){checkInDate=v;}
    public Date getCheckOutDate(){return checkOutDate;} public void setCheckOutDate(Date v){checkOutDate=v;}
    public String getAssignStatus(){return assignStatus;} public void setAssignStatus(String v){assignStatus=v;}
    public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
    public String getHotelName(){return hotelName;} public void setHotelName(String v){hotelName=v;}
    public String getRoomName(){return roomName;} public void setRoomName(String v){roomName=v;}
    @Override public String toString(){return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE).append("assignId",assignId).append("guestName",guestName).toString();}
}