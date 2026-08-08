package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcMpUser;
import com.ruoyi.system.mapper.YcMpUserMapper;
import com.ruoyi.system.service.IYcMpUserService;

@Service
public class YcMpUserServiceImpl implements IYcMpUserService
{
    @Autowired
    private YcMpUserMapper ycMpUserMapper;

    @Override
    public YcMpUser selectYcMpUserById(Long userId)
    {
        return ycMpUserMapper.selectYcMpUserById(userId);
    }

    @Override
    public YcMpUser selectYcMpUserByOpenid(String openid)
    {
        return ycMpUserMapper.selectYcMpUserByOpenid(openid);
    }

    @Override
    public YcMpUser loginByOpenid(String openid, String unionid, String nickname, String avatar)
    {
        if (StringUtils.isEmpty(openid))
        {
            throw new ServiceException("openid required");
        }
        YcMpUser user = ycMpUserMapper.selectYcMpUserByOpenid(openid);
        if (user == null)
        {
            user = new YcMpUser();
            user.setOpenid(openid);
            user.setUnionid(unionid == null ? "" : unionid);
            user.setPhone("");
            user.setNickname(nickname == null ? "" : nickname);
            user.setAvatar(avatar == null ? "" : avatar);
            user.setCreateBy("mp");
            ycMpUserMapper.insertYcMpUser(user);
            return ycMpUserMapper.selectYcMpUserById(user.getUserId());
        }
        boolean changed = false;
        if (StringUtils.isNotEmpty(unionid) && !unionid.equals(user.getUnionid()))
        {
            user.setUnionid(unionid);
            changed = true;
        }
        if (StringUtils.isNotEmpty(nickname) && !nickname.equals(user.getNickname()))
        {
            user.setNickname(nickname);
            changed = true;
        }
        if (StringUtils.isNotEmpty(avatar) && !avatar.equals(user.getAvatar()))
        {
            user.setAvatar(avatar);
            changed = true;
        }
        if (changed)
        {
            user.setUpdateBy("mp");
            ycMpUserMapper.updateYcMpUser(user);
        }
        return user;
    }

    @Override
    public int bindPhone(Long userId, String phone)
    {
        if (userId == null)
        {
            throw new ServiceException("not logged in");
        }
        if (StringUtils.isEmpty(phone) || !phone.matches("^1\\d{10}$"))
        {
            throw new ServiceException("invalid phone");
        }
        YcMpUser user = ycMpUserMapper.selectYcMpUserById(userId);
        if (user == null)
        {
            throw new ServiceException("user not found");
        }
        user.setPhone(phone);
        user.setUpdateBy("mp");
        return ycMpUserMapper.updateYcMpUser(user);
    }
}