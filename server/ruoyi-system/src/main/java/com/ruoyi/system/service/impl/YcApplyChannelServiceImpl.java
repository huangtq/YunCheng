package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcApplyChannel;
import com.ruoyi.system.mapper.YcApplyChannelMapper;
import com.ruoyi.system.service.IYcApplyChannelService;
import com.ruoyi.system.service.IYcApplyFieldService;

@Service
public class YcApplyChannelServiceImpl implements IYcApplyChannelService
{
    @Autowired
    private YcApplyChannelMapper ycApplyChannelMapper;

    @Autowired
    private IYcApplyFieldService ycApplyFieldService;

    @Override
    public YcApplyChannel selectYcApplyChannelById(Long channelId)
    {
        return ycApplyChannelMapper.selectYcApplyChannelById(channelId);
    }

    @Override
    public List<YcApplyChannel> selectYcApplyChannelList(YcApplyChannel channel)
    {
        return ycApplyChannelMapper.selectYcApplyChannelList(channel);
    }

    private void fillDefaults(YcApplyChannel channel)
    {
        if (StringUtils.isEmpty(channel.getIsMain()))
        {
            channel.setIsMain("0");
        }
        if (channel.getParentId() == null)
        {
            channel.setParentId(0L);
        }
        if (channel.getSortOrder() == null)
        {
            channel.setSortOrder(0);
        }
        // 当前报名配置不启用支付、审核、邀请码和短信能力，统一写入关闭状态。
        channel.setPriceType("free");
        channel.setPrice(BigDecimal.ZERO);
        if (channel.getQuota() == null)
        {
            channel.setQuota(0);
        }
        channel.setNeedInvite("0");
        channel.setNeedAudit("0");
        channel.setNeedInvoice("0");
        if (StringUtils.isEmpty(channel.getVisible()))
        {
            channel.setVisible("1");
        }
        channel.setSmsNotify("0");
    }

    @Override
    @Transactional
    public int insertYcApplyChannel(YcApplyChannel channel)
    {
        fillDefaults(channel);
        if ("1".equals(channel.getIsMain()))
        {
            ycApplyChannelMapper.clearMainByActivityId(channel.getActivityId());
        }
        int rows = ycApplyChannelMapper.insertYcApplyChannel(channel);
        if (rows > 0)
        {
            ycApplyFieldService.ensureStandardFields(channel.getChannelId(), channel.getActivityId(), channel.getCreateBy());
        }
        return rows;
    }

    @Override
    @Transactional
    public int updateYcApplyChannel(YcApplyChannel channel)
    {
        fillDefaults(channel);
        if ("1".equals(channel.getIsMain()) && channel.getActivityId() != null)
        {
            ycApplyChannelMapper.clearMainByActivityId(channel.getActivityId());
        }
        return ycApplyChannelMapper.updateYcApplyChannel(channel);
    }

    @Override
    public int deleteYcApplyChannelByIds(Long[] channelIds)
    {
        return ycApplyChannelMapper.deleteYcApplyChannelByIds(channelIds);
    }
}