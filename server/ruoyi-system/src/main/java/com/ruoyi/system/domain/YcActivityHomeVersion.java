package com.ruoyi.system.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/** Versioned mobile home page for one meeting activity. */
public class YcActivityHomeVersion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long versionId;
    private Long activityId;
    private Integer versionNo;
    private String status;
    private String schemaVersion;
    private String pageJson;
    private String publishRemark;
    private Date publishAt;
    private String publishMode;
    private String publishedBy;
    private Date publishedTime;
    private String delFlag;

    public Long getVersionId() { return versionId; }
    public void setVersionId(Long versionId) { this.versionId = versionId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }
    public String getPageJson() { return pageJson; }
    public void setPageJson(String pageJson) { this.pageJson = pageJson; }
    public String getPublishRemark() { return publishRemark; }
    public void setPublishRemark(String publishRemark) { this.publishRemark = publishRemark; }
    public Date getPublishAt() { return publishAt; }
    public void setPublishAt(Date publishAt) { this.publishAt = publishAt; }
    public String getPublishMode() { return publishMode; }
    public void setPublishMode(String publishMode) { this.publishMode = publishMode; }
    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }
    public Date getPublishedTime() { return publishedTime; }
    public void setPublishedTime(Date publishedTime) { this.publishedTime = publishedTime; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
