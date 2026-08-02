package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.YcActivityConfig;

public interface YcActivityConfigMapper
{
    public YcActivityConfig selectYcActivityConfigById(Long activityId);

    public int insertYcActivityConfig(YcActivityConfig config);

    public int updateYcActivityConfig(YcActivityConfig config);
}