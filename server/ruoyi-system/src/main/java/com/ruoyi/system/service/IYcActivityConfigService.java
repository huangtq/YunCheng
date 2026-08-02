package com.ruoyi.system.service;

import com.ruoyi.system.domain.YcActivityConfig;

public interface IYcActivityConfigService
{
    public YcActivityConfig selectYcActivityConfigById(Long activityId);

    public YcActivityConfig getOrCreate(Long activityId, String username);

    public int updateYcActivityConfig(YcActivityConfig config);
}