package com.ruoyi.common.core.domain.model;

import java.io.Serializable;

/** WeChat meeting attendee login identity */
public class MpLoginUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String openid;
    private String phone;
    private String nickname;
    private String avatar;
    private String token;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}