package com.ruoyi.system.domain;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcHotelRoom extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long roomId; private Long activityId; private Long hotelId; private String roomName;
    private String bedType; private BigDecimal price; private Integer stock; private Integer sortOrder;
    private String delFlag; private String hotelName;
    public Long getRoomId(){return roomId;} public void setRoomId(Long v){roomId=v;}
    public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;}
    public Long getHotelId(){return hotelId;} public void setHotelId(Long v){hotelId=v;}
    public String getRoomName(){return roomName;} public void setRoomName(String v){roomName=v;}
    public String getBedType(){return bedType;} public void setBedType(String v){bedType=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public Integer getStock(){return stock;} public void setStock(Integer v){stock=v;}
    public Integer getSortOrder(){return sortOrder;} public void setSortOrder(Integer v){sortOrder=v;}
    public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
    public String getHotelName(){return hotelName;} public void setHotelName(String v){hotelName=v;}
    @Override public String toString(){return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE).append("roomId",roomId).append("roomName",roomName).toString();}
}