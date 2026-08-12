package com.ruoyi.system.mapper;
import java.util.List; import java.util.Map; import org.apache.ibatis.annotations.Param;
public interface YcMeetingEntryEventMapper { int insert(@Param("activityId") Long activityId,@Param("entryId") String entryId,@Param("eventType") String eventType,@Param("mpUserId") Long mpUserId,@Param("contextJson") String contextJson); List<Map<String,Object>> topEntries(@Param("activityId") Long activityId); }
