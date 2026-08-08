package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

public class YcVenue extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long venueId;
    private Long activityId;
    private String venueName;
    private String isLive;
    private String coverUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date liveStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date liveEnd;
    private String liveStatus;
    private Integer sortOrder;
    private String delFlag;
    private Integer topicCount;
    private Integer scheduleCount;

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getIsLive() { return isLive; }
    public void setIsLive(String isLive) { this.isLive = isLive; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public Date getLiveStart() { return liveStart; }
    public void setLiveStart(Date liveStart) { this.liveStart = liveStart; }
    public Date getLiveEnd() { return liveEnd; }
    public void setLiveEnd(Date liveEnd) { this.liveEnd = liveEnd; }
    public String getLiveStatus() { return liveStatus; }
    public void setLiveStatus(String liveStatus) { this.liveStatus = liveStatus; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public Integer getTopicCount() { return topicCount; }
    public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
    public Integer getScheduleCount() { return scheduleCount; }
    public void setScheduleCount(Integer scheduleCount) { this.scheduleCount = scheduleCount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("venueId", venueId).append("venueName", venueName).toString();
    }
}