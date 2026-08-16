package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/** Attachment configured for a legacy grid content page. */
public class YcActivityGridAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long attachmentId;
    private Long activityId;
    private Long gridId;
    private String displayName;
    private String downloadName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Integer sortOrder;
    private String status;
    private String delFlag;

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Long getGridId() { return gridId; }
    public void setGridId(Long gridId) { this.gridId = gridId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDownloadName() { return downloadName; }
    public void setDownloadName(String downloadName) { this.downloadName = downloadName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
