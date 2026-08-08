package com.ruoyi.system.service;

import com.ruoyi.system.domain.YcMpUser;

public interface IYcMpUserService
{
    YcMpUser selectYcMpUserById(Long userId);

    YcMpUser selectYcMpUserByOpenid(String openid);

    YcMpUser loginByOpenid(String openid, String unionid, String nickname, String avatar);

    int bindPhone(Long userId, String phone);
}
