package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YcApplyChannel;

public interface IYcApplyChannelService
{
    public YcApplyChannel selectYcApplyChannelById(Long channelId);

    public List<YcApplyChannel> selectYcApplyChannelList(YcApplyChannel channel);

    public int insertYcApplyChannel(YcApplyChannel channel);

    public int updateYcApplyChannel(YcApplyChannel channel);

    public int deleteYcApplyChannelByIds(Long[] channelIds);
}