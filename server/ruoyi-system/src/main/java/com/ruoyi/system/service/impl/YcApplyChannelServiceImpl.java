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

@Service
public class YcApplyChannelServiceImpl implements IYcApplyChannelService
{
    @Autowired
    private YcApplyChannelMapper ycApplyChannelMapper;

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
        if (StringUtils.isEmpty(channel.getPriceType()))
        {
            channel.setPriceType("free");
        }
        if (channel.getPrice() == null)
        {
            channel.setPrice(BigDecimal.ZERO);
        }
        if (channel.getQuota() == null)
        {
            channel.setQuota(0);
        }
        if (StringUtils.isEmpty(channel.getNeedInvite()))
        {
            channel.setNeedInvite("0");
        }
        if (StringUtils.isEmpty(channel.getNeedAudit()))
        {
            channel.setNeedAudit("0");
        }
        if (StringUtils.isEmpty(channel.getNeedInvoice()))
        {
            channel.setNeedInvoice("0");
        }
        if (StringUtils.isEmpty(channel.getVisible()))
        {
            channel.setVisible("1");
        }
        if (StringUtils.isEmpty(channel.getSmsNotify()))
        {
            channel.setSmsNotify("0");
        }
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
        return ycApplyChannelMapper.insertYcApplyChannel(channel);
    }

    @Override
    @Transactional
    public int updateYcApplyChannel(YcApplyChannel channel)
    {
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