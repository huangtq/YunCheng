package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcApplyChannel;

public interface YcApplyChannelMapper
{
    public YcApplyChannel selectYcApplyChannelById(Long channelId);

    public List<YcApplyChannel> selectYcApplyChannelList(YcApplyChannel channel);

    public int clearMainByActivityId(Long activityId);

    public int insertYcApplyChannel(YcApplyChannel channel);

    public int updateYcApplyChannel(YcApplyChannel channel);

    public int deleteYcApplyChannelByIds(Long[] channelIds);
}