package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcActivity;

/**
 * 会议活动 数据层
 *
 * @author ruoyi
 */
public interface YcActivityMapper
{
    public YcActivity selectYcActivityById(Long activityId);

    public List<YcActivity> selectYcActivityList(YcActivity ycActivity);

    public YcActivity checkActivityCodeUnique(String activityCode);

    public int insertYcActivity(YcActivity ycActivity);

    public int updateYcActivity(YcActivity ycActivity);

    public int deleteYcActivityById(Long activityId);

    public int deleteYcActivityByIds(Long[] activityIds);
}