package com.ruoyi.system.domain;

/**
 * Editor payload for one mutable meeting home page draft.
 *
 * The page JSON is the versioned H5 snapshot. The configuration fields are
 * persisted with it so the editor workspace and legacy fallback stay aligned.
 */
public class YcActivityHomeDraft
{
    private Long versionId;
    private Long activityId;
    private String schemaVersion;
    private String pageJson;
    private String gridTemplate;
    private String configRemark;
    private String mobileBackgroundUrl;

    public Long getVersionId() { return versionId; }
    public void setVersionId(Long versionId) { this.versionId = versionId; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }
    public String getPageJson() { return pageJson; }
    public void setPageJson(String pageJson) { this.pageJson = pageJson; }
    public String getGridTemplate() { return gridTemplate; }
    public void setGridTemplate(String gridTemplate) { this.gridTemplate = gridTemplate; }
    public String getConfigRemark() { return configRemark; }
    public void setConfigRemark(String configRemark) { this.configRemark = configRemark; }
    public String getMobileBackgroundUrl() { return mobileBackgroundUrl; }
    public void setMobileBackgroundUrl(String mobileBackgroundUrl) { this.mobileBackgroundUrl = mobileBackgroundUrl; }
}
