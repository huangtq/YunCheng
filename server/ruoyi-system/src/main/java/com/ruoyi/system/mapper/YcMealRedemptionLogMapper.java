package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.YcMealRedemptionLog;

public interface YcMealRedemptionLogMapper
{
    YcMealRedemptionLog selectByIdempotencyKey(String key);

    int insert(YcMealRedemptionLog log);
}
