package com.ruoyi.system.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/** Attachment owned by a reusable meeting content page. */
public class YcMeetingContentAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long attachmentId;
    private Long contentId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String visibility;
    private Date validStart;
    private Date validEnd;
    private Integer sortOrder;
    private String status;
    private String delFlag;

    public Long getAttachmentId() { return attachmentId; }
    public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }
    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public Date getValidStart() { return validStart; }
    public void setValidStart(Date validStart) { this.validStart = validStart; }
    public Date getValidEnd() { return validEnd; }
    public void setValidEnd(Date validEnd) { this.validEnd = validEnd; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
