package com.ruoyi.system.mapper;
import java.util.List;import org.apache.ibatis.annotations.Param;import com.ruoyi.system.domain.YcActivityHomeVersion;
public interface YcActivityHomeVersionMapperExtra {List<YcActivityHomeVersion> selectDueScheduled();int publishScheduled(@Param("versionId")Long versionId);}
