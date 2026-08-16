package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivity;
import com.ruoyi.system.domain.YcActivityConfig;
import com.ruoyi.system.domain.YcActivityGrid;
import com.ruoyi.system.domain.YcActivityGridAttachment;
import com.ruoyi.system.domain.YcActivityNav;
import com.ruoyi.system.domain.YcApplyChannel;
import com.ruoyi.system.domain.YcApplyField;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.system.domain.YcExhibitor;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.domain.YcHotel;
import com.ruoyi.system.domain.YcHotelRoom;
import com.ruoyi.system.domain.YcHotelOrder;
import com.ruoyi.system.domain.YcHotelAssign;
import com.ruoyi.system.domain.YcMealTicket;
import com.ruoyi.system.domain.YcSchedule;
import com.ruoyi.system.domain.YcVenue;
import com.ruoyi.system.domain.YcActivityHomeVersion;
import com.ruoyi.system.domain.YcMeetingContent;
import com.ruoyi.system.domain.YcMeetingContentAttachment;
import com.ruoyi.system.mapper.YcActivityConfigMapper;
import com.ruoyi.system.mapper.YcActivityGridMapper;
import com.ruoyi.system.mapper.YcActivityGridAttachmentMapper;
import com.ruoyi.system.mapper.YcActivityMapper;
import com.ruoyi.system.mapper.YcActivityNavMapper;
import com.ruoyi.system.mapper.YcApplyChannelMapper;
import com.ruoyi.system.mapper.YcApplyFieldMapper;
import com.ruoyi.system.mapper.YcApplyOrderMapper;
import com.ruoyi.system.mapper.YcExhibitorMapper;
import com.ruoyi.system.mapper.YcGuestMapper;
import com.ruoyi.system.mapper.YcHotelMapper;
import com.ruoyi.system.mapper.YcHotelRoomMapper;
import com.ruoyi.system.mapper.YcHotelOrderMapper;
import com.ruoyi.system.mapper.YcHotelAssignMapper;
import com.ruoyi.system.mapper.YcMealTicketMapper;
import com.ruoyi.system.mapper.YcScheduleMapper;
import com.ruoyi.system.mapper.YcVenueMapper;
import com.ruoyi.system.mapper.YcMeetingContentMapper;
import com.ruoyi.system.mapper.YcMeetingContentAttachmentMapper;
import com.ruoyi.system.service.IYcApplyOrderService;
import com.ruoyi.system.service.IYcPortalMeetingService;

@Service
public class YcPortalMeetingServiceImpl implements IYcPortalMeetingService
{
    @Autowired private YcActivityMapper activityMapper;
    @Autowired private YcActivityConfigMapper configMapper;
    @Autowired private YcActivityGridMapper gridMapper;
    @Autowired private YcActivityGridAttachmentMapper gridAttachmentMapper;
    @Autowired private YcScheduleMapper scheduleMapper;
    @Autowired private YcGuestMapper guestMapper;
    @Autowired private YcVenueMapper venueMapper;
    @Autowired private YcHotelRoomMapper hotelRoomMapper;
    @Autowired private YcHotelOrderMapper hotelOrderMapper;
    @Autowired private YcHotelAssignMapper hotelAssignMapper;
    @Autowired private YcActivityNavMapper navMapper;
    @Autowired private YcExhibitorMapper exhibitorMapper;
    @Autowired private YcMealTicketMapper mealMapper;
    @Autowired private YcHotelMapper hotelMapper;
    @Autowired private YcApplyChannelMapper channelMapper;
    @Autowired private YcApplyFieldMapper fieldMapper;
    @Autowired private YcApplyOrderMapper orderMapper;
    @Autowired private IYcApplyOrderService applyOrderService;
    @Autowired private com.ruoyi.system.mapper.YcActivityHomeVersionMapper homeVersionMapper;
    @Autowired private YcMeetingContentMapper contentMapper;
    @Autowired private YcMeetingContentAttachmentMapper contentAttachmentMapper;

    @Override
    public Map<String, Object> getActivityHome(Long activityId)
    {
        YcActivity activity = requireActivity(activityId);
        YcActivityConfig config = configMapper.selectYcActivityConfigById(activityId);
        if (config == null)
        {
            config = new YcActivityConfig();
            config.setActivityId(activityId);
            config.setShowCountdown("0");
            config.setShowRegisterCount("0");
            config.setRegisterForceMobile("1");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("activity", activity);
        data.put("config", config);
        data.put("layout", mobileLayout(config));
        return data;
    }

    @Override
    public Map<String, Object> getHomePage(Long activityId, MpLoginUser user)
    {
        YcActivity activity = requireActivity(activityId);
        YcActivityHomeVersion version = selectLatestPublishedVersionSafely(activityId);
        JSONObject page;
        String versionStatus;
        Integer versionNo = null;
        Long versionId = null;
        if (version != null && StringUtils.isNotEmpty(version.getPageJson()) && isGridConfigVersion(version.getPageJson()))
        {
            try
            {
                page = JSON.parseObject(version.getPageJson());
            }
            catch (Exception e)
            {
                page = legacyPage(activityId);
            }
            versionStatus = version.getStatus();
            versionNo = version.getVersionNo();
            versionId = version.getVersionId();
        }
        else
        {
            page = legacyPage(activityId);
            versionStatus = "compatibility";
        }

        decorateEntryStates(page.getJSONArray("entryTree"), activityId, user);

        Map<String, Object> data = new HashMap<>();
        data.put("activity", publicActivity(activity));
        Map<String, Object> versionView = new HashMap<>();
        versionView.put("versionId", versionId);
        versionView.put("versionNo", versionNo);
        versionView.put("status", versionStatus);
        versionView.put("schemaVersion", version == null ? "legacy-1" : version.getSchemaVersion());
        data.put("version", versionView);
        data.put("page", page);
        data.put("capabilities", capabilities(activityId));
        Map<String, Object> context = new HashMap<>();
        context.put("stage", meetingStage(activity));
        context.put("loggedIn", user != null && user.getUserId() != null);
        context.put("registered", user != null && hasActiveRegistration(activityId, user));
        data.put("context", context);
        return data;
    }

    /**
     * Keep the public meeting page compatible while the additive version table is
     * being rolled out. The legacy grid data remains the authoritative fallback
     * until the migration has been applied.
     */
    private YcActivityHomeVersion selectLatestPublishedVersionSafely(Long activityId)
    {
        try
        {
            return homeVersionMapper.selectLatestPublishedByActivityId(activityId);
        }
        catch (DataAccessException e)
        {
            String message = e.getMessage();
            if (message != null && message.contains("yc_activity_home_version")
                && (message.contains("doesn't exist") || message.contains("does not exist")
                    || message.contains("不存在")))
            {
                return null;
            }
            throw e;
        }
    }

    /**
     * The grid editor is the single operational source for the mobile meeting
     * page. Older standalone composer snapshots remain in history but do not
     * override subsequent grid configuration.
     */
    private boolean isGridConfigVersion(String pageJson)
    {
        try
        {
            return "grid-config".equals(JSON.parseObject(pageJson).getString("source"));
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    /** The client receives a final action state and never decides eligibility from labels or local data. */
    private void decorateEntryStates(JSONArray entries, Long activityId, MpLoginUser user)
    {
        if (entries == null) return;
        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = entries.getJSONObject(i);
            if (entry == null) continue;
            boolean enabled = !Boolean.FALSE.equals(entry.getBoolean("enabled"));
            String targetType = entry.getString("targetType");
            boolean loginRequired = false;
            boolean registeredRequired = false;
            boolean available = enabled;
            String unavailableMessage = "";
            if ("content".equals(targetType) && !entry.containsKey("legacyContent"))
            {
                Object target = entry.get("target");
                Long contentId = target instanceof JSONObject
                    ? ((JSONObject) target).getLong("contentId") : toLong(target);
                YcMeetingContent content = contentId == null ? null : contentMapper.selectYcMeetingContentById(contentId);
                available = enabled && content != null && activityId.equals(content.getActivityId());
                if (available && "login".equals(content.getVisibility())) loginRequired = true;
                if (available && "registered".equals(content.getVisibility())) { loginRequired = true; registeredRequired = true; }
                if (available && !isContentVisible(content, user)) unavailableMessage = registeredRequired ? "报名后可查看" : "登录后可查看";
            }
            else if ("module".equals(targetType))
            {
                Object target = entry.get("target");
                String moduleKey = target instanceof JSONObject
                    ? ((JSONObject) target).getString("moduleKey") : entry.getString("target");
                loginRequired = "apply".equals(moduleKey) || "my-attendance".equals(moduleKey);
                if ("my-attendance".equals(moduleKey)) registeredRequired = true;
                if (loginRequired && (user == null || user.getUserId() == null)) unavailableMessage = "请先登录";
                if (registeredRequired && user != null && user.getUserId() != null && !hasActiveRegistration(activityId, user)) unavailableMessage = "报名后可使用";
                if (!"apply".equals(moduleKey) && !"my-attendance".equals(moduleKey) && !hasModuleData(moduleKey, activityId))
                {
                    available = false;
                    unavailableMessage = "暂未开放";
                }
            }
            entry.put("available", available);
            entry.put("loginRequired", loginRequired);
            entry.put("registeredRequired", registeredRequired);
            entry.put("unavailableMessage", unavailableMessage);
            decorateEntryStates(entry.getJSONArray("children"), activityId, user);
        }
    }

    @Override
    public Map<String, Object> getPublicContent(Long activityId, Long contentId, MpLoginUser user)
    {
        requireActivity(activityId);
        YcMeetingContent content = contentMapper.selectYcMeetingContentById(contentId);
        if (content == null || !activityId.equals(content.getActivityId()) || !isContentVisible(content, user))
        {
            throw new ServiceException("content not available");
        }
        Map<String, Object> view = new HashMap<>();
        view.put("contentId", content.getContentId());
        view.put("title", content.getTitle());
        view.put("summary", content.getSummary());
        view.put("contentHtml", sanitizePublicHtml(content.getContentHtml()));
        view.put("coverUrl", content.getCoverUrl());
        view.put("visibility", content.getVisibility());
        view.put("attachments", publicAttachments(activityId, contentId, user));
        return view;
    }

    @Override
    public String getPublicAttachmentUrl(Long activityId, Long attachmentId, MpLoginUser user)
    {
        requireActivity(activityId);
        YcMeetingContentAttachment attachment = contentAttachmentMapper.selectYcMeetingContentAttachmentById(attachmentId);
        if (attachment == null) throw new ServiceException("attachment not found");
        YcMeetingContent content = contentMapper.selectYcMeetingContentById(attachment.getContentId());
        if (content == null || !activityId.equals(content.getActivityId()) || !isContentVisible(content, user) || !isAttachmentVisible(attachment, activityId, user))
        {
            throw new ServiceException("attachment not available");
        }
        return attachment.getFileUrl();
    }

    private Map<String, Object> publicActivity(YcActivity activity)
    {
        Map<String, Object> view = new HashMap<>();
        view.put("activityId", activity.getActivityId());
        view.put("activityName", activity.getActivityName());
        view.put("coverUrl", activity.getCoverUrl());
        view.put("startTime", activity.getStartTime());
        view.put("endTime", activity.getEndTime());
        view.put("province", activity.getProvince());
        view.put("city", activity.getCity());
        view.put("address", activity.getAddress());
        view.put("thirdPartyUrl", activity.getThirdPartyUrl());
        view.put("registerCount", activity.getRegisterCount());
        return view;
    }

    private Map<String, Object> capabilities(Long activityId)
    {
        Map<String, Object> caps = new HashMap<>();
        caps.put("apply", true);
        caps.put("schedule", hasModuleData("schedule", activityId));
        caps.put("guest", hasModuleData("guest", activityId));
        caps.put("venue", hasModuleData("venue", activityId));
        caps.put("nav", hasModuleData("nav", activityId));
        caps.put("hotel", hasModuleData("hotel", activityId));
        caps.put("meal", true);
        caps.put("exhibitor", hasModuleData("exhibitor", activityId));
        caps.put("checkin", true);
        caps.put("my-attendance", true);
        return caps;
    }

    private boolean hasModuleData(String moduleKey, Long activityId)
    {
        try
        {
            List<?> rows = listModule(moduleKey, activityId);
            return rows != null && !rows.isEmpty();
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private boolean hasActiveRegistration(Long activityId, MpLoginUser user)
    {
        if (user == null || user.getUserId() == null) return false;
        List<YcApplyOrder> orders = orderMapper.selectPortalUserOrders(activityId, user.getOpenid(), user.getPhone(), "0");
        return orders != null && !orders.isEmpty();
    }

    private boolean isContentVisible(YcMeetingContent content, MpLoginUser user)
    {
        if (!"published".equals(content.getStatus())) return false;
        Date now = new Date();
        if (content.getValidStart() != null && now.before(content.getValidStart())) return false;
        if (content.getValidEnd() != null && now.after(content.getValidEnd())) return false;
        if ("public".equals(content.getVisibility())) return true;
        if (user == null || user.getUserId() == null) return false;
        return "login".equals(content.getVisibility())
            || ("registered".equals(content.getVisibility()) && hasActiveRegistration(content.getActivityId(), user));
    }

    private boolean isAttachmentVisible(YcMeetingContentAttachment attachment, Long activityId, MpLoginUser user)
    {
        if (!"1".equals(attachment.getStatus())) return false;
        Date now = new Date();
        if (attachment.getValidStart() != null && now.before(attachment.getValidStart())) return false;
        if (attachment.getValidEnd() != null && now.after(attachment.getValidEnd())) return false;
        if ("public".equals(attachment.getVisibility())) return true;
        if (user == null || user.getUserId() == null) return false;
        return "login".equals(attachment.getVisibility())
            || ("registered".equals(attachment.getVisibility()) && hasActiveRegistration(activityId, user));
    }

    private List<Map<String, Object>> publicAttachments(Long activityId, Long contentId, MpLoginUser user)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        for (YcMeetingContentAttachment attachment : contentAttachmentMapper.selectByContentId(contentId))
        {
            if (!isAttachmentVisible(attachment, activityId, user)) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("attachmentId", attachment.getAttachmentId());
            item.put("fileName", attachment.getFileName());
            item.put("fileType", attachment.getFileType());
            item.put("fileSize", attachment.getFileSize());
            item.put("downloadUrl", "/portal/meeting/content/" + activityId + "/attachment/" + attachment.getAttachmentId());
            result.add(item);
        }
        return result;
    }

    /** Content is cleaned on write; cleaning again protects records created before that rule existed. */
    private String sanitizePublicHtml(String html)
    {
        if (StringUtils.isEmpty(html)) return "";
        org.jsoup.safety.Safelist allowList = org.jsoup.safety.Safelist.relaxed()
            .addTags("figure", "figcaption", "section")
            .addAttributes("a", "target", "rel")
            .addProtocols("a", "href", "http", "https")
            .addProtocols("img", "src", "http", "https");
        return org.jsoup.Jsoup.clean(html, "", allowList);
    }

    private List<Map<String, Object>> publicGuests(Long activityId)
    {
        YcGuest q = new YcGuest();
        q.setActivityId(activityId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (YcGuest guest : guestMapper.selectYcGuestList(q))
        {
            Map<String, Object> item = new HashMap<>();
            item.put("guestId", guest.getGuestId());
            item.put("name", guest.getGuestName());
            item.put("englishName", guest.getEnglishName());
            item.put("organization", guest.getOrgName());
            item.put("title", guest.getTitle());
            item.put("type", guest.getGuestType());
            item.put("avatar", guest.getAvatar());
            item.put("introduction", guest.getIntro());
            item.put("sortOrder", guest.getSortOrder());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> publicSchedules(Long activityId)
    {
        YcSchedule q = new YcSchedule();
        q.setActivityId(activityId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (YcSchedule schedule : scheduleMapper.selectYcScheduleList(q))
        {
            Map<String, Object> item = new HashMap<>();
            item.put("scheduleId", schedule.getScheduleId());
            item.put("topicId", schedule.getTopicId());
            item.put("topicName", schedule.getTopicName());
            item.put("venueId", schedule.getVenueId());
            item.put("venueName", schedule.getVenueName());
            item.put("name", schedule.getScheduleName());
            item.put("date", schedule.getScheduleDate());
            item.put("startTime", schedule.getStartTime());
            item.put("endTime", schedule.getEndTime());
            item.put("durationMin", schedule.getDurationMin());
            item.put("speakers", schedule.getSpeakerNames());
            item.put("hosts", schedule.getHostNames());
            item.put("discussants", schedule.getDiscussNames());
            item.put("sortOrder", schedule.getSortOrder());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listPublicSchedules(Long activityId)
    {
        requireActivity(activityId);
        return publicSchedules(activityId);
    }

    @Override
    public List<Map<String, Object>> listPublicGuests(Long activityId)
    {
        requireActivity(activityId);
        return publicGuests(activityId);
    }

    @Override
    public List<Map<String, Object>> listPublicNavigation(Long activityId)
    {
        requireActivity(activityId);
        return publicNavigation(activityId);
    }

    private List<Map<String, Object>> publicVenues(Long activityId)
    {
        YcVenue q = new YcVenue();
        q.setActivityId(activityId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (YcVenue venue : venueMapper.selectYcVenueList(q))
        {
            Map<String, Object> item = new HashMap<>();
            item.put("venueId", venue.getVenueId());
            item.put("name", venue.getVenueName());
            item.put("live", "1".equals(venue.getIsLive()));
            item.put("coverUrl", venue.getCoverUrl());
            item.put("liveStart", venue.getLiveStart());
            item.put("liveEnd", venue.getLiveEnd());
            item.put("liveStatus", venue.getLiveStatus());
            item.put("topicCount", venue.getTopicCount());
            item.put("scheduleCount", venue.getScheduleCount());
            item.put("sortOrder", venue.getSortOrder());
            result.add(item);
        }
        return result;
    }

    private List<Map<String, Object>> publicNavigation(Long activityId)
    {
        YcActivityNav q = new YcActivityNav();
        q.setActivityId(activityId);
        q.setStatus("1");
        List<Map<String, Object>> result = new ArrayList<>();
        for (YcActivityNav nav : navMapper.selectYcActivityNavList(q))
        {
            Map<String, Object> item = new HashMap<>();
            item.put("navId", nav.getNavId());
            item.put("title", nav.getTitle());
            item.put("address", nav.getAddress());
            item.put("phone", nav.getPhone());
            item.put("coverUrl", nav.getCoverUrl());
            item.put("sortOrder", nav.getSortOrder());
            Double latitude = coordinate(nav.getLatitude(), -90, 90);
            Double longitude = coordinate(nav.getLongitude(), -180, 180);
            item.put("latitude", latitude);
            item.put("longitude", longitude);
            item.put("canNavigate", latitude != null && longitude != null);
            result.add(item);
        }
        return result;
    }

    private Double coordinate(String value, double min, double max)
    {
        if (StringUtils.isEmpty(value)) return null;
        try
        {
            double coordinate = Double.parseDouble(value);
            return coordinate >= min && coordinate <= max ? coordinate : null;
        }
        catch (NumberFormatException ignored)
        {
            return null;
        }
    }

    private String meetingStage(YcActivity activity)
    {
        Date now = new Date();
        if (activity.getStartTime() != null && now.before(activity.getStartTime())) return "before";
        if (activity.getEndTime() != null && now.after(activity.getEndTime())) return "after";
        return "during";
    }

    private JSONObject legacyPage(Long activityId)
    {
        YcActivityConfig config = configMapper.selectYcActivityConfigById(activityId);
        if (config == null) config = new YcActivityConfig();
        JSONObject page = new JSONObject();
        page.put("mode", StringUtils.isEmpty(config.getMobileTemplate()) ? "standard" : config.getMobileTemplate());
        JSONObject theme = new JSONObject();
        theme.put("color", StringUtils.isEmpty(config.getMobileThemeColor()) ? "#1f6feb" : config.getMobileThemeColor());
        theme.put("backgroundUrl", config.getMobileBackgroundUrl());
        theme.put("background", parseGridBackground(config.getRemark()));
        page.put("theme", theme);
        page.put("layout", mobileLayout(config));
        JSONArray sections = new JSONArray();
        JSONObject navigation = new JSONObject();
        navigation.put("id", "legacy-navigation");
        navigation.put("type", "navigation");
        navigation.put("enabled", true);
        navigation.put("entries", legacyEntries(activityId));
        sections.add(navigation);
        page.put("sections", sections);
        page.put("entryTree", legacyEntries(activityId));
        return page;
    }

    private JSONArray legacyEntries(Long activityId)
    {
        JSONArray entries = new JSONArray();
        for (Object raw : listGrid(activityId))
        {
            if (!(raw instanceof YcActivityGrid)) continue;
            YcActivityGrid grid = (YcActivityGrid) raw;
            JSONObject entry = new JSONObject();
            entry.put("id", "legacy-grid-" + grid.getGridId());
            entry.put("title", grid.getTitle());
            entry.put("iconUrl", grid.getIconUrl());
            entry.put("iconType", grid.getIconType());
            entry.put("contentUrl", grid.getContentUrl());
            entry.put("content", grid.getContent());
            entry.put("contentType", grid.getContentType());
            entry.put("tileRow", grid.getTileRow());
            entry.put("tileCol", grid.getTileCol());
            entry.put("tileRowSpan", grid.getTileRowSpan());
            entry.put("tileColSpan", grid.getTileColSpan());
            entry.put("sort", grid.getSortOrder());
            entry.put("attachments", grid.getAttachments());
            String linkType = StringUtils.isEmpty(grid.getLinkType()) ? "none" : grid.getLinkType();
            entry.put("targetType", "module".equals(linkType) ? "module" : ("url".equals(linkType) ? "external" : ("content".equals(linkType) ? "content" : ("pdf".equals(linkType) ? "pdf" : "group"))));
            if ("module".equals(linkType)) entry.put("target", grid.getModuleKey());
            else if ("url".equals(linkType)) entry.put("target", grid.getExternalUrl());
            else if ("pdf".equals(linkType))
            {
                entry.put("target", grid.getContentUrl());
                entry.put("contentType", "pdf");
                entry.put("contentUrl", grid.getContentUrl());
            }
            else if ("content".equals(linkType))
            {
                entry.put("target", grid.getContentUrl());
                entry.put("legacyContent", grid.getContent());
                entry.put("contentType", grid.getContentType());
            }
            entries.add(entry);
        }
        return entries;
    }

    @Override
    public List<Map<String, Object>> listActivities(String type)
    {
        YcActivity query = new YcActivity();
        query.setIsShow("1");
        List<YcActivity> activities = activityMapper.selectYcActivityList(query);
        Date now = new Date();
        boolean history = "history".equalsIgnoreCase(type);
        List<Map<String, Object>> result = new ArrayList<>();
        for (YcActivity activity : activities)
        {
            YcActivityConfig config = configMapper.selectYcActivityConfigById(activity.getActivityId());
            // Keep existing visibility for meetings without a config row.
            if (config != null && "0".equals(config.getMpShow()))
            {
                continue;
            }
            Date endTime = activity.getEndTime();
            boolean ended = endTime != null && endTime.before(now);
            if (history != ended)
            {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("activityId", activity.getActivityId());
            item.put("activityName", activity.getActivityName());
            item.put("coverUrl", activity.getCoverUrl());
            item.put("startTime", activity.getStartTime());
            item.put("endTime", activity.getEndTime());
            item.put("province", activity.getProvince());
            item.put("city", activity.getCity());
            item.put("address", activity.getAddress());
            item.put("thirdPartyUrl", activity.getThirdPartyUrl());
            item.put("registerCount", activity.getRegisterCount());
            // hotShow controls whether the activity participates in the H5 hot section.
            item.put("isHot", config != null && "0".equals(config.getHotShow()) ? "0" : activity.getIsHot());
            result.add(item);
        }
        result.sort((a, b) -> {
            Date left = (Date) a.get(history ? "endTime" : "startTime");
            Date right = (Date) b.get(history ? "endTime" : "startTime");
            if (left == null && right == null) return 0;
            if (left == null) return 1;
            if (right == null) return -1;
            return history ? right.compareTo(left) : left.compareTo(right);
        });
        return result;
    }

    @Override
    public List<?> listGrid(Long activityId)
    {
        requireActivity(activityId);
        YcActivityGrid q = new YcActivityGrid();
        q.setActivityId(activityId);
        q.setStatus("1");
        List<YcActivityGrid> list = gridMapper.selectYcActivityGridList(q);
        for (YcActivityGrid grid : list)
        {
            grid.setAttachments(loadGridAttachments(grid.getGridId()));
        }
        list.sort((a, b) -> Integer.compare(
            a.getSortOrder() == null ? 0 : a.getSortOrder(),
            b.getSortOrder() == null ? 0 : b.getSortOrder()));
        return list;
    }

    @Override
    public YcActivityGridAttachment getPublicGridAttachment(Long activityId, Long gridId, Long attachmentId)
    {
        requireActivity(activityId);
        YcActivityGrid grid = gridMapper.selectYcActivityGridById(gridId);
        if (grid == null || !activityId.equals(grid.getActivityId()) || !"1".equals(grid.getStatus()))
        {
            throw new ServiceException("grid attachment not available");
        }
        for (YcActivityGridAttachment attachment : loadGridAttachments(gridId))
        {
            if (attachmentId.equals(attachment.getAttachmentId()) && "1".equals(attachment.getStatus()))
            {
                return attachment;
            }
        }
        throw new ServiceException("grid attachment not available");
    }

    private List<YcActivityGridAttachment> loadGridAttachments(Long gridId)
    {
        try
        {
            return gridAttachmentMapper.selectByGridId(gridId);
        }
        catch (DataAccessException e)
        {
            String message = e.getMessage();
            if (message != null && message.contains("yc_activity_grid_attachment")
                && (message.contains("doesn't exist") || message.contains("does not exist") || message.contains("不存在")))
            {
                return new ArrayList<>();
            }
            throw e;
        }
    }

    @Override
    public List<?> listModule(String moduleKey, Long activityId)
    {
        requireActivity(activityId);
        if (StringUtils.isEmpty(moduleKey))
        {
            throw new ServiceException("moduleKey required");
        }
        switch (moduleKey)
        {
            case "schedule":
            {
                return publicSchedules(activityId);
            }
            case "guest":
            {
                return publicGuests(activityId);
            }
            case "venue":
            {
                return publicVenues(activityId);
            }
            case "nav":
            {
                return publicNavigation(activityId);
            }
            case "apply":
            {
                Object channels = listApplyChannels(activityId).get("channels");
                return channels instanceof List ? (List<?>) channels : new ArrayList<>();
            }
            case "exhibitor":
            {
                YcExhibitor query = new YcExhibitor(); query.setActivityId(activityId);
                return exhibitorMapper.selectYcExhibitorList(query);
            }
            default:
                throw new ServiceException("unsupported module: " + moduleKey);
        }
    }

    @Override
    public Map<String, Object> listApplyChannels(Long activityId)
    {
        requireActivity(activityId);
        YcApplyChannel cq = new YcApplyChannel();
        cq.setActivityId(activityId);
        cq.setVisible("1");
        List<YcApplyChannel> channels = channelMapper.selectYcApplyChannelList(cq);

        YcApplyField fq = new YcApplyField();
        fq.setActivityId(activityId);
        fq.setEnabledFlag("1");
        List<YcApplyField> fields = fieldMapper.selectYcApplyFieldList(fq);

        Map<Long, List<YcApplyField>> fieldMap = new HashMap<>();
        for (YcApplyField field : fields)
        {
            fieldMap.computeIfAbsent(field.getChannelId(), k -> new ArrayList<>()).add(field);
        }
        for (List<YcApplyField> list : fieldMap.values())
        {
            list.sort((a, b) -> Integer.compare(
                a.getSortOrder() == null ? 0 : a.getSortOrder(),
                b.getSortOrder() == null ? 0 : b.getSortOrder()));
        }

        List<Map<String, Object>> channelViews = new ArrayList<>();
        for (YcApplyChannel channel : channels)
        {
            Map<String, Object> row = new HashMap<>();
            row.put("channelId", channel.getChannelId());
            row.put("channelName", channel.getChannelName());
            row.put("isMain", channel.getIsMain());
            row.put("priceType", channel.getPriceType());
            row.put("price", channel.getPrice());
            row.put("quota", channel.getQuota());
            row.put("deadline", channel.getDeadline());
            boolean closed = channel.getDeadline() != null && channel.getDeadline().before(new Date());
            row.put("closed", closed);
            row.put("closedMessage", closed ? "本次报名已截止，欢迎下次参会" : "");
            row.put("sortOrder", channel.getSortOrder());
            row.put("fields", fieldMap.getOrDefault(channel.getChannelId(), new ArrayList<>()));
            if (channel.getQuota() != null && channel.getQuota() > 0)
            {
                YcApplyOrder oq = new YcApplyOrder();
                oq.setActivityId(activityId);
                oq.setChannelId(channel.getChannelId());
                oq.setOrderStatus("0");
                List<YcApplyOrder> used = orderMapper.selectYcApplyOrderList(oq);
                row.put("usedCount", used == null ? 0 : used.size());
            }
            channelViews.add(row);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("channels", channelViews);
        return data;
    }

    @Override
    public YcApplyOrder submitApply(MpLoginUser user, Map<String, Object> body)
    {
        if (user == null)
        {
            throw new ServiceException("please login via wechat", 401);
        }
        if (StringUtils.isEmpty(user.getPhone()))
        {
            throw new ServiceException("please bind phone first");
        }
        Long activityId = toLong(body.get("activityId"));
        Long channelId = toLong(body.get("channelId"));
        if (activityId == null || channelId == null)
        {
            throw new ServiceException("activityId/channelId required");
        }
        requireActivity(activityId);
        YcApplyChannel channel = channelMapper.selectYcApplyChannelById(channelId);
        if (channel == null || !activityId.equals(channel.getActivityId()))
        {
            throw new ServiceException("channel not found");
        }
        if (!"1".equals(channel.getVisible()))
        {
            throw new ServiceException("channel closed");
        }
        if (channel.getDeadline() != null && channel.getDeadline().before(new Date()))
        {
            throw new ServiceException("channel deadline passed");
        }
        if (channel.getQuota() != null && channel.getQuota() > 0)
        {
            YcApplyOrder oq = new YcApplyOrder();
            oq.setActivityId(activityId);
            oq.setChannelId(channelId);
            oq.setOrderStatus("0");
            List<YcApplyOrder> used = orderMapper.selectYcApplyOrderList(oq);
            if (used != null && used.size() >= channel.getQuota())
            {
                throw new ServiceException("quota full");
            }
        }

        // 同一会议下，同一微信/手机号已有有效报名则不允许再报（不区分通道）
        List<YcApplyOrder> exists = orderMapper.selectPortalUserOrders(
            activityId, user.getOpenid(), user.getPhone(), "0");
        if (exists != null && !exists.isEmpty())
        {
            throw new ServiceException("您已报名成功，请勿重复提交");
        }

        Object formData = body.get("formData");
        String formJson = formData == null ? "{}" : JSON.toJSONString(formData);
        String contactName = str(body.get("contactName"));
        if (StringUtils.isEmpty(contactName) && formData instanceof Map)
        {
            Object n = ((Map<?, ?>) formData).get("name");
            if (n == null) n = ((Map<?, ?>) formData).get("contactName");
            contactName = n == null ? "" : String.valueOf(n);
        }
        if (StringUtils.isEmpty(contactName))
        {
            contactName = StringUtils.isEmpty(user.getNickname()) ? user.getPhone() : user.getNickname();
        }

        YcApplyOrder order = new YcApplyOrder();
        order.setActivityId(activityId);
        order.setChannelId(channelId);
        order.setContactName(contactName);
        order.setMobile(user.getPhone());
        order.setGender(str(body.get("gender")));
        order.setCompany(str(body.get("company")));
        order.setOrderStatus("0");
        order.setCheckinStatus("0");
        order.setFormJson(formJson);
        order.setCreateBy(user.getOpenid());
        order.setRemark("mpUserId=" + user.getUserId());
        applyOrderService.insertYcApplyOrder(order);
        return orderMapper.selectYcApplyOrderById(order.getOrderId());
    }

    @Override
    public List<YcApplyOrder> myApplyOrders(MpLoginUser user, Long activityId)
    {
        if (user == null)
        {
            throw new ServiceException("please login via wechat", 401);
        }
        List<YcApplyOrder> list = orderMapper.selectPortalUserOrders(
            activityId, user.getOpenid(), user.getPhone(), null);
        return list == null ? new ArrayList<>() : list;
    }

    @Override
    public Map<String, Object> hotelOverview(Long activityId, MpLoginUser user)
    {
        requireActivity(activityId);
        YcHotel query = new YcHotel(); query.setActivityId(activityId); query.setSaleStatus("1");
        List<Map<String, Object>> hotels = new ArrayList<>();
        for (YcHotel hotel : hotelMapper.selectYcHotelList(query))
        {
            YcHotelRoom roomQuery = new YcHotelRoom(); roomQuery.setActivityId(activityId); roomQuery.setHotelId(hotel.getHotelId());
            List<Map<String, Object>> rooms = new ArrayList<>();
            for (YcHotelRoom room : hotelRoomMapper.selectYcHotelRoomList(roomQuery))
            {
                Map<String, Object> r = new HashMap<>(); r.put("roomId", room.getRoomId()); r.put("roomName", room.getRoomName());
                r.put("bedType", room.getBedType()); r.put("price", room.getPrice()); r.put("stock", room.getStock());
                int reserved = room.getReservedStock() == null ? 0 : room.getReservedStock();
                r.put("available", room.getStock() == null || room.getStock() == 0 ? null : Math.max(0, room.getStock() - reserved));
                rooms.add(r);
            }
            Map<String, Object> h = new HashMap<>(); h.put("hotelId", hotel.getHotelId()); h.put("hotelName", hotel.getHotelName());
            h.put("address", hotel.getAddress()); h.put("phone", hotel.getPhone()); h.put("coverUrl", hotel.getCoverUrl()); h.put("rooms", rooms); hotels.add(h);
        }
        Map<String, Object> result = new HashMap<>(); result.put("hotels", hotels); result.put("loggedIn", user != null && user.getUserId() != null); return result;
    }

    @Override
    public Map<String, Object> myHotelOrders(MpLoginUser user, Long activityId)
    {
        if (user == null || user.getUserId() == null) throw new ServiceException("please login via wechat", 401);
        requireActivity(activityId);
        YcHotelOrder query = new YcHotelOrder(); query.setActivityId(activityId); query.setCreateBy(user.getOpenid());
        List<Map<String, Object>> rows = new ArrayList<>();
        for (YcHotelOrder order : hotelOrderMapper.selectYcHotelOrderList(query))
        {
            Map<String, Object> row = new HashMap<>(); row.put("orderId", order.getOrderId()); row.put("hotelName", order.getHotelName());
            row.put("roomName", order.getRoomName()); row.put("checkInDate", order.getCheckInDate()); row.put("checkOutDate", order.getCheckOutDate());
            row.put("roomCount", order.getRoomCount()); row.put("orderStatus", order.getOrderStatus());
            YcHotelAssign assignQuery = new YcHotelAssign(); assignQuery.setOrderId(order.getOrderId());
            List<YcHotelAssign> assignments = hotelAssignMapper.selectYcHotelAssignList(assignQuery);
            if (assignments != null && !assignments.isEmpty()) { YcHotelAssign assign = assignments.get(0); row.put("assignStatus", assign.getAssignStatus()); row.put("roomNumber", assign.getRoomNumber()); }
            rows.add(row);
        }
        Map<String, Object> result = new HashMap<>(); result.put("orders", rows); return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitHotelOrder(MpLoginUser user, Map<String, Object> body)
    {
        if (user == null || user.getUserId() == null) throw new ServiceException("please login via wechat", 401);
        Long activityId = toLong(body.get("activityId")); Long hotelId = toLong(body.get("hotelId")); Long roomId = toLong(body.get("roomId"));
        Integer roomCount = body.get("roomCount") == null ? 1 : Integer.valueOf(String.valueOf(body.get("roomCount")));
        Date checkIn = toDate(body.get("checkInDate")); Date checkOut = toDate(body.get("checkOutDate"));
        if (activityId == null || hotelId == null || roomId == null || roomCount < 1 || roomCount > 10 || checkIn == null || checkOut == null || !checkOut.after(checkIn)) throw new ServiceException("activityId, hotelId, roomId and valid stay dates required");
        List<YcApplyOrder> applyOrders = orderMapper.selectPortalUserOrders(activityId, user.getOpenid(), user.getPhone(), "0");
        if (applyOrders == null || applyOrders.isEmpty()) throw new ServiceException("please register before booking hotel");
        YcHotel hotel = hotelMapper.selectYcHotelById(hotelId); YcHotelRoom room = hotelRoomMapper.selectYcHotelRoomById(roomId);
        if (hotel == null || room == null || !activityId.equals(hotel.getActivityId()) || !activityId.equals(room.getActivityId()) || !hotelId.equals(room.getHotelId()) || !"1".equals(hotel.getSaleStatus())) throw new ServiceException("hotel or room not available");
        if (hotelOrderMapper.selectYcHotelOrderList(myHotelOrderQuery(activityId, user.getOpenid())).size() > 0) throw new ServiceException("you already have a hotel order");
        if (hotelRoomMapper.reserveStock(roomId, roomCount) != 1) throw new ServiceException("room stock is insufficient");
        YcHotelOrder order = new YcHotelOrder(); order.setActivityId(activityId); order.setHotelId(hotelId); order.setRoomId(roomId); order.setGuestName(user.getNickname()); order.setPhone(user.getPhone()); order.setCheckInDate(checkIn); order.setCheckOutDate(checkOut); order.setRoomCount(roomCount); order.setAmount(room.getPrice() == null ? BigDecimal.ZERO : room.getPrice().multiply(BigDecimal.valueOf(roomCount))); order.setOrderStatus("0"); order.setCreateBy(user.getOpenid()); order.setRemark("portal"); hotelOrderMapper.insertYcHotelOrder(order);
        return myHotelOrders(user, activityId);
    }

    private YcHotelOrder myHotelOrderQuery(Long activityId, String openid) { YcHotelOrder q = new YcHotelOrder(); q.setActivityId(activityId); q.setCreateBy(openid); return q; }

    private Date toDate(Object value) { if (value == null || String.valueOf(value).trim().isEmpty()) return null; try { return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(value)); } catch (Exception e) { throw new ServiceException("invalid hotel date"); } }

    private YcActivity requireActivity(Long activityId)
    {
        if (activityId == null)
        {
            throw new ServiceException("activityId required");
        }
        YcActivity activity = activityMapper.selectYcActivityById(activityId);
        if (activity == null)
        {
            throw new ServiceException("activity not found");
        }
        return activity;
    }

    private Long toLong(Object v)
    {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return null;
        return Long.valueOf(s);
    }

    private String str(Object v)
    {
        return v == null ? "" : String.valueOf(v);
    }

    private Map<String, Object> parseGridBackground(String value)
    {
        Map<String, Object> background = new HashMap<>();
        try
        {
            JSONObject remark = StringUtils.isEmpty(value) ? null : JSON.parseObject(value);
            if (remark != null && remark.get("gridBackground") instanceof JSONObject)
            {
                background.putAll((JSONObject) remark.get("gridBackground"));
            }
        }
        catch (Exception ignored) { }
        return background;
    }

    private Map<String, Object> mobileLayout(YcActivityConfig config)
    {
        Map<String, Object> layout = new HashMap<>();
        String template = StringUtils.isEmpty(config.getMobileTemplate())
            ? "standard" : config.getMobileTemplate();
        String gridTemplate = StringUtils.isEmpty(config.getGridTemplate())
            ? "1" : config.getGridTemplate();
        String countdownStyle = StringUtils.isEmpty(config.getCountdownStyle())
            ? "classic" : config.getCountdownStyle();
        layout.put("template", template);
        layout.put("themeColor", StringUtils.isEmpty(config.getMobileThemeColor())
            ? "#1f6feb" : config.getMobileThemeColor());
        layout.put("backgroundUrl", config.getMobileBackgroundUrl());
        layout.put("background", parseGridBackground(config.getRemark()));
        layout.put("notice", config.getMobileNotice());
        layout.put("gridTemplate", normalizeGridTemplate(gridTemplate));
        layout.put("gridColumns", resolveGridColumns(gridTemplate));
        layout.put("gridStyle", resolveGridStyle(gridTemplate));
        layout.put("showCountdown", "1".equals(config.getShowCountdown()));
        layout.put("countdownStyle", countdownStyle);
        layout.put("showRegisterCount", "1".equals(config.getShowRegisterCount()));
        layout.put("audioUrl", config.getAudioUrl());
        layout.put("audioAutoplay", "1".equals(config.getAudioAutoplay()));
        layout.put("audioLoop", !"0".equals(config.getAudioLoop()));
        Map<String, Object> sideMenu = new HashMap<>();
        sideMenu.put("enabled", true);
        sideMenu.put("source", "grid");
        layout.put("sideMenu", sideMenu);
        Map<String, Object> footer = new HashMap<>();
        footer.put("enabled", "1".equals(config.getFooterEnabled()));
        footer.put("text", config.getFooterText());
        footer.put("company", config.getFooterCompany());
        footer.put("logoUrl", config.getFooterLogoUrl());
        footer.put("linkUrl", config.getFooterLinkUrl());
        layout.put("footer", footer);
        if (StringUtils.isNotEmpty(config.getRemark()) && config.getRemark().trim().startsWith("{"))
        {
            try
            {
                JSONObject remark = JSON.parseObject(config.getRemark());
                JSONObject visual = remark.getJSONObject("gridVisual");
                if (visual != null) layout.put("visual", visual);
            }
            catch (Exception ignored) { }
        }
        List<?> blocks = new ArrayList<>();
        if (StringUtils.isNotEmpty(config.getMobileBlocksJson()))
        {
            try
            {
                blocks = JSON.parseArray(config.getMobileBlocksJson());
            }
            catch (Exception ignored)
            {
                blocks = new ArrayList<>();
            }
        }
        layout.put("blocks", blocks);
        return layout;
    }

    /** Align with admin templateOptions: 7/71=1col, 68/681=2col, else 3col. */
    private String normalizeGridTemplate(String gridTemplate)
    {
        if ("grid3x3".equals(gridTemplate)) return "1";
        if ("grid2x2".equals(gridTemplate)) return "5";
        if ("list".equals(gridTemplate)) return "7";
        return gridTemplate;
    }

    private int resolveGridColumns(String gridTemplate)
    {
        String value = normalizeGridTemplate(gridTemplate);
        if ("tile".equals(value))
        {
            return 6;
        }
        if ("7".equals(value) || "71".equals(value))
        {
            return 1;
        }
        if ("68".equals(value) || "681".equals(value))
        {
            return 2;
        }
        return 3;
    }

    /** icon = icon only; icon-text = icon + title (default). */
    private String resolveGridStyle(String gridTemplate)
    {
        String value = normalizeGridTemplate(gridTemplate);
        if ("5".equals(value) || "71".equals(value))
        {
            return "icon";
        }
        return "icon-text";
    }
}
