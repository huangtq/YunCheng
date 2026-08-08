package com.ruoyi.system.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcTopic;
import com.ruoyi.system.mapper.YcTopicMapper;
import com.ruoyi.system.service.IYcTopicService;
@Service
public class YcTopicServiceImpl implements IYcTopicService {
    @Autowired private YcTopicMapper ycTopicMapper;
    @Override public YcTopic selectYcTopicById(Long topicId) { return ycTopicMapper.selectYcTopicById(topicId); }
    @Override public List<YcTopic> selectYcTopicList(YcTopic topic) { return ycTopicMapper.selectYcTopicList(topic); }
    @Override public Map<String, Object> selectTopicStats(Long activityId) {
        Map<String, Object> stats = ycTopicMapper.selectTopicStats(activityId);
        if (stats == null) { stats = new HashMap<>(); stats.put("totalCount",0); stats.put("scheduleCount",0); }
        return stats;
    }
    private void fill(YcTopic t) {
        if (t.getChairNames()==null) t.setChairNames("");
        if (t.getHostNames()==null) t.setHostNames("");
        if (t.getDiscussNames()==null) t.setDiscussNames("");
        if (t.getSortOrder()==null) t.setSortOrder(0);
    }
    @Override public int insertYcTopic(YcTopic topic) {
        if (topic.getVenueId()==null) throw new ServiceException("venueId required");
        if (StringUtils.isEmpty(topic.getTopicName())) throw new ServiceException("topicName required");
        fill(topic); return ycTopicMapper.insertYcTopic(topic);
    }
    @Override public int updateYcTopic(YcTopic topic) { fill(topic); return ycTopicMapper.updateYcTopic(topic); }
    @Override public int deleteYcTopicByIds(Long[] topicIds) { return ycTopicMapper.deleteYcTopicByIds(topicIds); }
}