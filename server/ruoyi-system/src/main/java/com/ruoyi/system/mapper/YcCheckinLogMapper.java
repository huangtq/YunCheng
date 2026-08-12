package com.ruoyi.system.mapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YcCheckinLog;
public interface YcCheckinLogMapper { YcCheckinLog selectByIdempotencyKey(String idempotencyKey); int insert(YcCheckinLog log); List<Map<String,Object>> selectCheckpointStats(@Param("activityId") Long activityId); }
