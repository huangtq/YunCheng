package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会议九宫格 yc_activity_grid
 */
public class YcActivityGrid extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long gridId;
    private Long activityId;
    private String title;
    private String iconType;
    private String iconKey;
    private String iconUrl;
    private String linkType;
    private String moduleKey;
    private String externalUrl;
    private String content;
    private String contentType;
    private String contentUrl;
    private Integer sortOrder;
    private Integer tileRow;
    private Integer tileCol;
    private Integer tileRowSpan;
    private Integer tileColSpan;
    private String status;
    private String delFlag;

    public Long getGridId() { return gridId; }
    public void setGridId(Long gridId) { this.gridId = gridId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }
    public String getIconKey() { return iconKey; }
    public void setIconKey(String iconKey) { this.iconKey = iconKey; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public String getLinkType() { return linkType; }
    public void setLinkType(String linkType) { this.linkType = linkType; }
    public String getModuleKey() { return moduleKey; }
    public void setModuleKey(String moduleKey) { this.moduleKey = moduleKey; }
    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getTileRow() { return tileRow; }
    public void setTileRow(Integer tileRow) { this.tileRow = tileRow; }
    public Integer getTileCol() { return tileCol; }
    public void setTileCol(Integer tileCol) { this.tileCol = tileCol; }
    public Integer getTileRowSpan() { return tileRowSpan; }
    public void setTileRowSpan(Integer tileRowSpan) { this.tileRowSpan = tileRowSpan; }
    public Integer getTileColSpan() { return tileColSpan; }
    public void setTileColSpan(Integer tileColSpan) { this.tileColSpan = tileColSpan; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("gridId", gridId)
            .append("activityId", activityId)
            .append("title", title)
            .append("content", content)
            .toString();
    }
}