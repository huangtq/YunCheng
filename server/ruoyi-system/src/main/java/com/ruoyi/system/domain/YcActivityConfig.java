package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会议配置开关 yc_activity_config
 */
public class YcActivityConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long activityId;
    private String mpShow;
    private String homeBanner;
    private String hotShow;
    private String showCountdown;
    private String countdownStyle;
    private String showRegisterCount;
    private String hotelNeedRegister;
    private String liveNeedRegister;
    private String registerShowLive;
    private String registerShowHotel;
    private String hotelOnce;
    private String cancelRegisterCancelHotel;
    private String loginSms;
    private String registerForceMobile;
    private String gridTemplate;
    private String qrUrl;
    private String mobileTemplate;
    private String mobileThemeColor;
    private String mobileBackgroundUrl;
    private String mobileBlocksJson;
    private String mobileNotice;
    private String audioUrl;
    private String audioAutoplay;
    private String audioLoop;
    private String footerEnabled;
    private String footerText;
    private String footerCompany;
    private String footerLogoUrl;
    private String footerLinkUrl;

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getMpShow() { return mpShow; }
    public void setMpShow(String mpShow) { this.mpShow = mpShow; }
    public String getHomeBanner() { return homeBanner; }
    public void setHomeBanner(String homeBanner) { this.homeBanner = homeBanner; }
    public String getHotShow() { return hotShow; }
    public void setHotShow(String hotShow) { this.hotShow = hotShow; }
    public String getShowCountdown() { return showCountdown; }
    public void setShowCountdown(String showCountdown) { this.showCountdown = showCountdown; }
    public String getCountdownStyle() { return countdownStyle; }
    public void setCountdownStyle(String countdownStyle) { this.countdownStyle = countdownStyle; }
    public String getShowRegisterCount() { return showRegisterCount; }
    public void setShowRegisterCount(String showRegisterCount) { this.showRegisterCount = showRegisterCount; }
    public String getHotelNeedRegister() { return hotelNeedRegister; }
    public void setHotelNeedRegister(String hotelNeedRegister) { this.hotelNeedRegister = hotelNeedRegister; }
    public String getLiveNeedRegister() { return liveNeedRegister; }
    public void setLiveNeedRegister(String liveNeedRegister) { this.liveNeedRegister = liveNeedRegister; }
    public String getRegisterShowLive() { return registerShowLive; }
    public void setRegisterShowLive(String registerShowLive) { this.registerShowLive = registerShowLive; }
    public String getRegisterShowHotel() { return registerShowHotel; }
    public void setRegisterShowHotel(String registerShowHotel) { this.registerShowHotel = registerShowHotel; }
    public String getHotelOnce() { return hotelOnce; }
    public void setHotelOnce(String hotelOnce) { this.hotelOnce = hotelOnce; }
    public String getCancelRegisterCancelHotel() { return cancelRegisterCancelHotel; }
    public void setCancelRegisterCancelHotel(String cancelRegisterCancelHotel) { this.cancelRegisterCancelHotel = cancelRegisterCancelHotel; }
    public String getLoginSms() { return loginSms; }
    public void setLoginSms(String loginSms) { this.loginSms = loginSms; }
    public String getRegisterForceMobile() { return registerForceMobile; }
    public void setRegisterForceMobile(String registerForceMobile) { this.registerForceMobile = registerForceMobile; }
    public String getGridTemplate() { return gridTemplate; }
    public void setGridTemplate(String gridTemplate) { this.gridTemplate = gridTemplate; }
    public String getQrUrl() { return qrUrl; }
    public void setQrUrl(String qrUrl) { this.qrUrl = qrUrl; }
    public String getMobileTemplate() { return mobileTemplate; }
    public void setMobileTemplate(String mobileTemplate) { this.mobileTemplate = mobileTemplate; }
    public String getMobileThemeColor() { return mobileThemeColor; }
    public void setMobileThemeColor(String mobileThemeColor) { this.mobileThemeColor = mobileThemeColor; }
    public String getMobileBackgroundUrl() { return mobileBackgroundUrl; }
    public void setMobileBackgroundUrl(String mobileBackgroundUrl) { this.mobileBackgroundUrl = mobileBackgroundUrl; }
    public String getMobileBlocksJson() { return mobileBlocksJson; }
    public void setMobileBlocksJson(String mobileBlocksJson) { this.mobileBlocksJson = mobileBlocksJson; }
    public String getMobileNotice() { return mobileNotice; }
    public void setMobileNotice(String mobileNotice) { this.mobileNotice = mobileNotice; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getAudioAutoplay() { return audioAutoplay; }
    public void setAudioAutoplay(String audioAutoplay) { this.audioAutoplay = audioAutoplay; }
    public String getAudioLoop() { return audioLoop; }
    public void setAudioLoop(String audioLoop) { this.audioLoop = audioLoop; }
    public String getFooterEnabled() { return footerEnabled; }
    public void setFooterEnabled(String footerEnabled) { this.footerEnabled = footerEnabled; }
    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }
    public String getFooterCompany() { return footerCompany; }
    public void setFooterCompany(String footerCompany) { this.footerCompany = footerCompany; }
    public String getFooterLogoUrl() { return footerLogoUrl; }
    public void setFooterLogoUrl(String footerLogoUrl) { this.footerLogoUrl = footerLogoUrl; }
    public String getFooterLinkUrl() { return footerLinkUrl; }
    public void setFooterLinkUrl(String footerLinkUrl) { this.footerLinkUrl = footerLinkUrl; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("activityId", activityId)
            .append("mpShow", mpShow)
            .append("gridTemplate", gridTemplate)
            .append("qrUrl", qrUrl)
            .append("mobileTemplate", mobileTemplate)
            .append("mobileThemeColor", mobileThemeColor)
            .toString();
    }
}