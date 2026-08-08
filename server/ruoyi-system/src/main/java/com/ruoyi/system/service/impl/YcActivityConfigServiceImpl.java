package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivityConfig;
import com.ruoyi.system.mapper.YcActivityConfigMapper;
import com.ruoyi.system.service.IYcActivityConfigService;

@Service
public class YcActivityConfigServiceImpl implements IYcActivityConfigService
{
    @Autowired
    private YcActivityConfigMapper ycActivityConfigMapper;

    @Override
    public YcActivityConfig selectYcActivityConfigById(Long activityId)
    {
        return ycActivityConfigMapper.selectYcActivityConfigById(activityId);
    }

    @Override
    public YcActivityConfig getOrCreate(Long activityId, String username)
    {
        YcActivityConfig config = ycActivityConfigMapper.selectYcActivityConfigById(activityId);
        if (config != null)
        {
            return config;
        }
        config = new YcActivityConfig();
        config.setActivityId(activityId);
        config.setMpShow("1");
        config.setHomeBanner("0");
        config.setHotShow("0");
        config.setShowCountdown("1");
        config.setCountdownStyle("classic");
        config.setShowRegisterCount("0");
        config.setHotelNeedRegister("1");
        config.setLiveNeedRegister("1");
        config.setRegisterShowLive("1");
        config.setRegisterShowHotel("1");
        config.setHotelOnce("1");
        config.setCancelRegisterCancelHotel("0");
        config.setLoginSms("0");
        config.setRegisterForceMobile("0");
        config.setGridTemplate("grid3x3");
        config.setQrUrl("");
        config.setMobileTemplate("standard");
        config.setMobileThemeColor("#1f6feb");
        config.setMobileBackgroundUrl("");
        config.setMobileBlocksJson("[]");
        config.setMobileNotice("");
        config.setCreateBy(username);
        ycActivityConfigMapper.insertYcActivityConfig(config);
        return ycActivityConfigMapper.selectYcActivityConfigById(activityId);
    }

    @Override
    public int updateYcActivityConfig(YcActivityConfig config)
    {
        YcActivityConfig exist = ycActivityConfigMapper.selectYcActivityConfigById(config.getActivityId());
        if (StringUtils.isNull(exist))
        {
            config.setCreateBy(config.getUpdateBy());
            return ycActivityConfigMapper.insertYcActivityConfig(config);
        }
        return ycActivityConfigMapper.updateYcActivityConfig(config);
    }
}