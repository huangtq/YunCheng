package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.YcMpUser;

public interface YcMpUserMapper
{
    YcMpUser selectYcMpUserById(Long userId);

    YcMpUser selectYcMpUserByOpenid(String openid);

    int insertYcMpUser(YcMpUser user);

    int updateYcMpUser(YcMpUser user);
}
