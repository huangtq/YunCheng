package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

public class YcTopic extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long topicId;
    private Long activityId;
    private Long venueId;
    private String topicName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private String chairNames;
    private String hostNames;
    private String discussNames;
    private Integer sortOrder;
    private String delFlag;
    private String venueName;
    private Integer scheduleCount;

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getChairNames() { return chairNames; }
    public void setChairNames(String chairNames) { this.chairNames = chairNames; }
    public String getHostNames() { return hostNames; }
    public void setHostNames(String hostNames) { this.hostNames = hostNames; }
    public String getDiscussNames() { return discussNames; }
    public void setDiscussNames(String discussNames) { this.discussNames = discussNames; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public Integer getScheduleCount() { return scheduleCount; }
    public void setScheduleCount(Integer scheduleCount) { this.scheduleCount = scheduleCount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("topicId", topicId).append("topicName", topicName).toString();
    }
}