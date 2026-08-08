package com.ruoyi.framework.web.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.YcMpUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class MpTokenService
{
    @Value("${token.secret}")
    private String secret;

    @Value("${wechat.mp.token-expire-minutes:10080}")
    private int expireMinutes;

    @Autowired
    private RedisCache redisCache;

    public String createToken(YcMpUser user)
    {
        String uuid = IdUtils.fastUUID();
        MpLoginUser loginUser = toLoginUser(user);
        loginUser.setToken(uuid);
        redisCache.setCacheObject(getTokenKey(uuid), loginUser, expireMinutes, TimeUnit.MINUTES);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.MP_LOGIN_USER_KEY, uuid);
        claims.put("openid", user.getOpenid());
        return Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public MpLoginUser getLoginUser(HttpServletRequest request)
    {
        String token = resolveToken(request);
        if (StringUtils.isEmpty(token))
        {
            return null;
        }
        try
        {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            String uuid = (String) claims.get(Constants.MP_LOGIN_USER_KEY);
            if (StringUtils.isEmpty(uuid))
            {
                return null;
            }
            return redisCache.getCacheObject(getTokenKey(uuid));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public MpLoginUser requireLoginUser(HttpServletRequest request)
    {
        MpLoginUser user = getLoginUser(request);
        if (user == null || user.getUserId() == null)
        {
            throw new ServiceException("please login via wechat", 401);
        }
        return user;
    }

    public void refreshLoginUser(MpLoginUser loginUser, YcMpUser dbUser)
    {
        if (loginUser == null || StringUtils.isEmpty(loginUser.getToken()))
        {
            return;
        }
        MpLoginUser latest = toLoginUser(dbUser);
        latest.setToken(loginUser.getToken());
        redisCache.setCacheObject(getTokenKey(loginUser.getToken()), latest, expireMinutes, TimeUnit.MINUTES);
    }

    private MpLoginUser toLoginUser(YcMpUser user)
    {
        MpLoginUser loginUser = new MpLoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setOpenid(user.getOpenid());
        loginUser.setPhone(user.getPhone());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());
        return loginUser;
    }

    private String resolveToken(HttpServletRequest request)
    {
        String mpHeader = request.getHeader("Mp-Authorization");
        if (StringUtils.isNotEmpty(mpHeader) && mpHeader.startsWith(Constants.TOKEN_PREFIX))
        {
            return mpHeader.substring(Constants.TOKEN_PREFIX.length());
        }
        if (StringUtils.isNotEmpty(mpHeader))
        {
            return mpHeader;
        }
        String auth = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(auth) && auth.startsWith(Constants.TOKEN_PREFIX))
        {
            return auth.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }

    private String getTokenKey(String uuid)
    {
        return CacheConstants.MP_LOGIN_TOKEN_KEY + uuid;
    }
}