package com.ruoyi.system.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/** Reusable meeting content page. */
public class YcMeetingContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long contentId;
    private Long activityId;
    private String title;
    private String summary;
    private String contentHtml;
    private String coverUrl;
    private String visibility;
    private String status;
    private Date validStart;
    private Date validEnd;
    private Integer sortOrder;
    private String delFlag;

    public Long getContentId() { return contentId; }
    public void setContentId(Long contentId) { this.contentId = contentId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getValidStart() { return validStart; }
    public void setValidStart(Date validStart) { this.validStart = validStart; }
    public Date getValidEnd() { return validEnd; }
    public void setValidEnd(Date validEnd) { this.validEnd = validEnd; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
