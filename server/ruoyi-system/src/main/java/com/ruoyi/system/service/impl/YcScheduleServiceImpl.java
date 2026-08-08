package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcGuest;
import com.ruoyi.system.domain.YcSchedule;
import com.ruoyi.system.domain.YcTopic;
import com.ruoyi.system.mapper.YcGuestMapper;
import com.ruoyi.system.mapper.YcScheduleMapper;
import com.ruoyi.system.mapper.YcTopicMapper;
import com.ruoyi.system.service.IYcScheduleService;

@Service
public class YcScheduleServiceImpl implements IYcScheduleService
{
    @Autowired
    private YcScheduleMapper ycScheduleMapper;
    @Autowired
    private YcTopicMapper ycTopicMapper;
    @Autowired
    private YcGuestMapper ycGuestMapper;

    @Override
    public YcSchedule selectYcScheduleById(Long scheduleId)
    {
        return ycScheduleMapper.selectYcScheduleById(scheduleId);
    }

    @Override
    public List<YcSchedule> selectYcScheduleList(YcSchedule schedule)
    {
        return ycScheduleMapper.selectYcScheduleList(schedule);
    }

    @Override
    public Map<String, Object> selectScheduleStats(Long activityId)
    {
        Map<String, Object> stats = ycScheduleMapper.selectScheduleStats(activityId);
        if (stats == null)
        {
            stats = new HashMap<>();
            stats.put("totalCount", 0);
            stats.put("totalDuration", 0);
        }
        return stats;
    }

    private void fill(YcSchedule s)
    {
        if (s.getSpeakerNames() == null)
        {
            s.setSpeakerNames("");
        }
        if (s.getHostNames() == null)
        {
            s.setHostNames("");
        }
        if (s.getDiscussNames() == null)
        {
            s.setDiscussNames("");
        }
        if (s.getDurationMin() == null)
        {
            s.setDurationMin(0);
        }
        if (s.getSortOrder() == null)
        {
            s.setSortOrder(0);
        }
        if (s.getDurationMin() == 0 && s.getStartTime() != null && s.getEndTime() != null)
        {
            long mins = (s.getEndTime().getTime() - s.getStartTime().getTime()) / 60000L;
            if (mins > 0 && mins < Integer.MAX_VALUE)
            {
                s.setDurationMin((int) mins);
            }
        }
    }

    @Override
    public int insertYcSchedule(YcSchedule schedule)
    {
        if (schedule.getTopicId() == null)
        {
            throw new ServiceException("topicId required");
        }
        if (StringUtils.isEmpty(schedule.getScheduleName()))
        {
            throw new ServiceException("scheduleName required");
        }
        fill(schedule);
        return ycScheduleMapper.insertYcSchedule(schedule);
    }

    @Override
    public int updateYcSchedule(YcSchedule schedule)
    {
        fill(schedule);
        return ycScheduleMapper.updateYcSchedule(schedule);
    }

    @Override
    public int deleteYcScheduleByIds(Long[] scheduleIds)
    {
        return ycScheduleMapper.deleteYcScheduleByIds(scheduleIds);
    }

    @Override
    public List<Map<String, Object>> selectExpertTasks(Long activityId, String expertName, String role, String onlyConflict)
    {
        Map<String, YcGuest> guestByName = new HashMap<>();
        YcGuest guestQ = new YcGuest();
        guestQ.setActivityId(activityId);
        for (YcGuest guest : ycGuestMapper.selectYcGuestList(guestQ))
        {
            if (guest.getGuestName() != null)
            {
                guestByName.putIfAbsent(guest.getGuestName().trim(), guest);
            }
        }

        List<Map<String, Object>> tasks = new ArrayList<>();
        YcTopic topicQ = new YcTopic();
        topicQ.setActivityId(activityId);
        for (YcTopic topic : ycTopicMapper.selectYcTopicList(topicQ))
        {
            addNames(tasks, topic.getChairNames(), "chair", topic, null, guestByName);
            addNames(tasks, topic.getHostNames(), "host", topic, null, guestByName);
            addNames(tasks, topic.getDiscussNames(), "discuss", topic, null, guestByName);
        }
        for (YcSchedule schedule : ycScheduleMapper.selectScheduleForExpert(activityId))
        {
            addNames(tasks, schedule.getSpeakerNames(), "speaker", null, schedule, guestByName);
            addNames(tasks, schedule.getHostNames(), "host", null, schedule, guestByName);
            addNames(tasks, schedule.getDiscussNames(), "discuss", null, schedule, guestByName);
        }
        if (StringUtils.isNotEmpty(expertName))
        {
            String key = expertName.trim();
            tasks.removeIf(t -> !String.valueOf(t.get("expertName")).contains(key));
        }
        if (StringUtils.isNotEmpty(role))
        {
            tasks.removeIf(t -> !role.equals(String.valueOf(t.get("role"))));
        }
        Map<String, List<Map<String, Object>>> byExpert = new LinkedHashMap<>();
        for (Map<String, Object> t : tasks)
        {
            String name = String.valueOf(t.get("expertName"));
            byExpert.computeIfAbsent(name, k -> new ArrayList<>()).add(t);
        }
        for (List<Map<String, Object>> list : byExpert.values())
        {
            list.sort(Comparator.comparing(a -> (Date) a.getOrDefault("startTime", new Date(0))));
            for (int i = 0; i < list.size(); i++)
            {
                boolean conflict = false;
                Map<String, Object> a = list.get(i);
                Date aStart = (Date) a.get("startTime");
                Date aEnd = (Date) a.get("endTime");
                for (int j = 0; j < list.size(); j++)
                {
                    if (i == j)
                    {
                        continue;
                    }
                    Map<String, Object> b = list.get(j);
                    Date bStart = (Date) b.get("startTime");
                    Date bEnd = (Date) b.get("endTime");
                    if (aStart != null && aEnd != null && bStart != null && bEnd != null
                        && aStart.before(bEnd) && bStart.before(aEnd))
                    {
                        conflict = true;
                        break;
                    }
                }
                a.put("conflict", conflict ? "1" : "0");
                a.put("taskCount", list.size());
            }
        }
        if ("1".equals(onlyConflict))
        {
            tasks.removeIf(t -> !"1".equals(String.valueOf(t.get("conflict"))));
        }
        tasks.sort(Comparator
            .comparing((Map<String, Object> t) -> String.valueOf(t.get("expertName")))
            .thenComparing(t -> (Date) t.getOrDefault("startTime", new Date(0))));
        return tasks;
    }

    private void addNames(List<Map<String, Object>> tasks, String names, String role, YcTopic topic,
        YcSchedule schedule, Map<String, YcGuest> guestByName)
    {
        if (StringUtils.isEmpty(names))
        {
            return;
        }
        for (String raw : names.split("[,，、;/；]"))
        {
            String name = raw.trim();
            if (name.isEmpty())
            {
                continue;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("expertName", name);
            row.put("role", role);
            YcGuest guest = guestByName.get(name);
            if (guest != null)
            {
                row.put("guestId", guest.getGuestId());
                row.put("guestMatched", "1");
                row.put("guestPhone", guest.getPhone());
                row.put("guestOrg", guest.getOrgName());
            }
            else
            {
                row.put("guestMatched", "0");
            }
            if (schedule != null)
            {
                row.put("source", "schedule");
                row.put("title", schedule.getScheduleName());
                row.put("venueName", schedule.getVenueName());
                row.put("topicName", schedule.getTopicName());
                row.put("startTime", schedule.getStartTime());
                row.put("endTime", schedule.getEndTime());
                row.put("scheduleId", schedule.getScheduleId());
            }
            else if (topic != null)
            {
                row.put("source", "topic");
                row.put("title", topic.getTopicName());
                row.put("venueName", topic.getVenueName());
                row.put("topicName", topic.getTopicName());
                row.put("startTime", topic.getStartTime());
                row.put("endTime", topic.getEndTime());
                row.put("topicId", topic.getTopicId());
            }
            tasks.add(row);
        }
    }
}
