package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivity;
import com.ruoyi.system.domain.YcActivityConfig;
import com.ruoyi.system.domain.YcActivityGrid;
import com.ruoyi.system.domain.YcActivityNav;
import com.ruoyi.system.domain.YcApplyChannel;
import com.ruoyi.system.domain.YcApplyField;
import com.ruoyi.system.domain.YcApplyOrder;
import com.ruoyi.system.domain.YcExhibitor;
import com.ruoyi.system.domain.YcGridBottom;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.domain.YcHotel;
import com.ruoyi.system.domain.YcMealTicket;
import com.ruoyi.system.domain.YcSchedule;
import com.ruoyi.system.domain.YcVenue;
import com.ruoyi.system.mapper.YcActivityConfigMapper;
import com.ruoyi.system.mapper.YcActivityGridMapper;
import com.ruoyi.system.mapper.YcActivityMapper;
import com.ruoyi.system.mapper.YcActivityNavMapper;
import com.ruoyi.system.mapper.YcApplyChannelMapper;
import com.ruoyi.system.mapper.YcApplyFieldMapper;
import com.ruoyi.system.mapper.YcApplyOrderMapper;
import com.ruoyi.system.mapper.YcExhibitorMapper;
import com.ruoyi.system.mapper.YcGridBottomMapper;
import com.ruoyi.system.mapper.YcGuestMapper;
import com.ruoyi.system.mapper.YcHotelMapper;
import com.ruoyi.system.mapper.YcMealTicketMapper;
import com.ruoyi.system.mapper.YcScheduleMapper;
import com.ruoyi.system.mapper.YcVenueMapper;
import com.ruoyi.system.service.IYcApplyOrderService;
import com.ruoyi.system.service.IYcPortalMeetingService;

@Service
public class YcPortalMeetingServiceImpl implements IYcPortalMeetingService
{
    @Autowired private YcActivityMapper activityMapper;
    @Autowired private YcActivityConfigMapper configMapper;
    @Autowired private YcActivityGridMapper gridMapper;
    @Autowired private YcGridBottomMapper bottomMapper;
    @Autowired private YcScheduleMapper scheduleMapper;
    @Autowired private YcGuestMapper guestMapper;
    @Autowired private YcVenueMapper venueMapper;
    @Autowired private YcActivityNavMapper navMapper;
    @Autowired private YcExhibitorMapper exhibitorMapper;
    @Autowired private YcMealTicketMapper mealMapper;
    @Autowired private YcHotelMapper hotelMapper;
    @Autowired private YcApplyChannelMapper channelMapper;
    @Autowired private YcApplyFieldMapper fieldMapper;
    @Autowired private YcApplyOrderMapper orderMapper;
    @Autowired private IYcApplyOrderService applyOrderService;

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
            item.put("isHot", activity.getIsHot());
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
        list.sort((a, b) -> Integer.compare(
            a.getSortOrder() == null ? 0 : a.getSortOrder(),
            b.getSortOrder() == null ? 0 : b.getSortOrder()));
        return list;
    }

    @Override
    public List<?> listBottom(Long activityId)
    {
        requireActivity(activityId);
        YcGridBottom q = new YcGridBottom();
        q.setActivityId(activityId);
        q.setStatus("1");
        List<YcGridBottom> list = bottomMapper.selectYcGridBottomList(q);
        list.sort((a, b) -> Integer.compare(
            a.getSortOrder() == null ? 0 : a.getSortOrder(),
            b.getSortOrder() == null ? 0 : b.getSortOrder()));
        return list;
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
                YcSchedule q = new YcSchedule();
                q.setActivityId(activityId);
                return scheduleMapper.selectYcScheduleList(q);
            }
            case "guest":
            {
                YcGuest q = new YcGuest();
                q.setActivityId(activityId);
                return guestMapper.selectYcGuestList(q);
            }
            case "venue":
            {
                YcVenue q = new YcVenue();
                q.setActivityId(activityId);
                return venueMapper.selectYcVenueList(q);
            }
            case "nav":
            {
                YcActivityNav q = new YcActivityNav();
                q.setActivityId(activityId);
                q.setStatus("1");
                return navMapper.selectYcActivityNavList(q);
            }
            case "exhibitor":
            {
                YcExhibitor q = new YcExhibitor();
                q.setActivityId(activityId);
                return exhibitorMapper.selectYcExhibitorList(q);
            }
            case "meal":
            {
                YcMealTicket q = new YcMealTicket();
                q.setActivityId(activityId);
                q.setEnabled("1");
                return mealMapper.selectYcMealTicketList(q);
            }
            case "hotel":
            {
                YcHotel q = new YcHotel();
                q.setActivityId(activityId);
                return hotelMapper.selectYcHotelList(q);
            }
            case "apply":
            {
                Object channels = listApplyChannels(activityId).get("channels");
                return channels instanceof List ? (List<?>) channels : new ArrayList<>();
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

        YcApplyOrder existQ = new YcApplyOrder();
        existQ.setActivityId(activityId);
        existQ.setChannelId(channelId);
        existQ.setOrderStatus("0");
        existQ.setMobile(user.getPhone());
        List<YcApplyOrder> exists = orderMapper.selectYcApplyOrderList(existQ);
        if (exists != null && !exists.isEmpty())
        {
            throw new ServiceException("already registered");
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
        YcApplyOrder q = new YcApplyOrder();
        if (activityId != null)
        {
            q.setActivityId(activityId);
        }
        if (StringUtils.isNotEmpty(user.getPhone()))
        {
            q.setMobile(user.getPhone());
        }
        List<YcApplyOrder> list = orderMapper.selectYcApplyOrderList(q);
        List<YcApplyOrder> mine = new ArrayList<>();
        for (YcApplyOrder order : list)
        {
            if (user.getOpenid() != null && user.getOpenid().equals(order.getCreateBy()))
            {
                mine.add(order);
            }
            else if (StringUtils.isNotEmpty(user.getPhone()) && user.getPhone().equals(order.getMobile()))
            {
                mine.add(order);
            }
        }
        return mine;
    }

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
        layout.put("notice", config.getMobileNotice());
        layout.put("gridTemplate", normalizeGridTemplate(gridTemplate));
        layout.put("gridColumns", resolveGridColumns(gridTemplate));
        layout.put("gridStyle", resolveGridStyle(gridTemplate));
        layout.put("showCountdown", "1".equals(config.getShowCountdown()));
        layout.put("countdownStyle", countdownStyle);
        layout.put("showRegisterCount", "1".equals(config.getShowRegisterCount()));
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