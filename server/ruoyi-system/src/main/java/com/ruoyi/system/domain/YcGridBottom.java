package com.ruoyi.system.domain;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
public class YcGridBottom extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Long bottomId; private Long activityId; private String bottomName; private String bottomType;
    private String linkUrl; private String moduleKey; private String phone; private String iconUrl;
    private Integer sortOrder; private String status; private String delFlag;
    public Long getBottomId(){return bottomId;} public void setBottomId(Long v){bottomId=v;}
    public Long getActivityId(){return activityId;} public void setActivityId(Long v){activityId=v;}
    public String getBottomName(){return bottomName;} public void setBottomName(String v){bottomName=v;}
    public String getBottomType(){return bottomType;} public void setBottomType(String v){bottomType=v;}
    public String getLinkUrl(){return linkUrl;} public void setLinkUrl(String v){linkUrl=v;}
    public String getModuleKey(){return moduleKey;} public void setModuleKey(String v){moduleKey=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getIconUrl(){return iconUrl;} public void setIconUrl(String v){iconUrl=v;}
    public Integer getSortOrder(){return sortOrder;} public void setSortOrder(Integer v){sortOrder=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getDelFlag(){return delFlag;} public void setDelFlag(String v){delFlag=v;}
    @Override public String toString(){return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE).append("bottomId",bottomId).append("bottomName",bottomName).toString();}
}