package com.ruoyi.system.domain;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcAttendeePass extends BaseEntity {
    private Long passId; private Long activityId; private Long applyOrderId; private String passStatus; private Date validStart; private Date validEnd; private String delFlag;
    public Long getPassId(){return passId;} public void setPassId(Long v){passId=v;} public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;} public Long getApplyOrderId(){return applyOrderId;} public void setApplyOrderId(Long v){applyOrderId=v;} public String getPassStatus(){return passStatus;} public void setPassStatus(String v){passStatus=v;} public Date getValidStart(){return validStart;} public void setValidStart(Date v){validStart=v;} public Date getValidEnd(){return validEnd;} public void setValidEnd(Date v){validEnd=v;} public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
}
