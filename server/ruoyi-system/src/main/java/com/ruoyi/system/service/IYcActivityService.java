package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YcActivity;

/**
 * 会议活动 服务层
 *
 * @author ruoyi
 */
public interface IYcActivityService
{
    public YcActivity selectYcActivityById(Long activityId);

    public List<YcActivity> selectYcActivityList(YcActivity ycActivity);

    public boolean checkActivityCodeUnique(YcActivity ycActivity);

    public String generateActivityCode();

    public int insertYcActivity(YcActivity ycActivity);

    public int updateYcActivity(YcActivity ycActivity);

    public int deleteYcActivityByIds(Long[] activityIds);
}