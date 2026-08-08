package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YcTopic;
public interface YcTopicMapper {
    YcTopic selectYcTopicById(Long topicId);
    List<YcTopic> selectYcTopicList(YcTopic topic);
    Map<String, Object> selectTopicStats(Long activityId);
    int insertYcTopic(YcTopic topic);
    int updateYcTopic(YcTopic topic);
    int deleteYcTopicByIds(Long[] topicIds);
}