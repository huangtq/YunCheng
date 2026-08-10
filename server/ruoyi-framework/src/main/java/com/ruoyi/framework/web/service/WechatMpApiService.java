package com.ruoyi.framework.web.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;

/**
 * 微信公众号服务端能力：access_token / jsapi_ticket / JS-SDK 签名。
 */
@Service
public class WechatMpApiService
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpApiService.class);

    @Autowired
    private RedisCache redisCache;

    @Value("${wechat.mp.app-id:}")
    private String appId;

    @Value("${wechat.mp.app-secret:}")
    private String appSecret;

    public String getAppId()
    {
        return appId;
    }

    public boolean isConfigured()
    {
        return StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(appSecret);
    }

    public String getAccessToken()
    {
        if (!isConfigured())
        {
            throw new ServiceException("微信公众号未配置 app-id / app-secret");
        }
        String cacheKey = CacheConstants.WX_MP_ACCESS_TOKEN_KEY + appId;
        String cached = redisCache.getCacheObject(cacheKey);
        if (StringUtils.isNotEmpty(cached))
        {
            return cached;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
            + appId + "&secret=" + appSecret;
        String resp = HttpUtils.sendGet(url);
        JSONObject json = JSON.parseObject(resp);
        String token = json == null ? null : json.getString("access_token");
        Integer expiresIn = json == null ? null : json.getInteger("expires_in");
        if (StringUtils.isEmpty(token))
        {
            log.error("获取微信 access_token 失败: {}", resp);
            throw new ServiceException("获取微信 access_token 失败");
        }
        int ttl = expiresIn == null ? 7000 : Math.max(60, expiresIn - 200);
        redisCache.setCacheObject(cacheKey, token, ttl, TimeUnit.SECONDS);
        return token;
    }

    public String getJsapiTicket()
    {
        if (!isConfigured())
        {
            throw new ServiceException("微信公众号未配置 app-id / app-secret");
        }
        String cacheKey = CacheConstants.WX_MP_JSAPI_TICKET_KEY + appId;
        String cached = redisCache.getCacheObject(cacheKey);
        if (StringUtils.isNotEmpty(cached))
        {
            return cached;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
            + getAccessToken() + "&type=jsapi";
        String resp = HttpUtils.sendGet(url);
        JSONObject json = JSON.parseObject(resp);
        String ticket = json == null ? null : json.getString("ticket");
        Integer expiresIn = json == null ? null : json.getInteger("expires_in");
        if (StringUtils.isEmpty(ticket))
        {
            log.error("获取微信 jsapi_ticket 失败: {}", resp);
            throw new ServiceException("获取微信 jsapi_ticket 失败");
        }
        int ttl = expiresIn == null ? 7000 : Math.max(60, expiresIn - 200);
        redisCache.setCacheObject(cacheKey, ticket, ttl, TimeUnit.SECONDS);
        return ticket;
    }

    public JsSdkSignature createJsSdkSignature(String url)
    {
        if (StringUtils.isEmpty(url))
        {
            throw new ServiceException("签名 url 不能为空");
        }
        // 微信要求签名用当前页完整 URL（不含 # 及其后部分）
        String signUrl = url;
        int hashIndex = signUrl.indexOf('#');
        if (hashIndex >= 0)
        {
            signUrl = signUrl.substring(0, hashIndex);
        }
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String raw = "jsapi_ticket=" + getJsapiTicket()
            + "&noncestr=" + nonceStr
            + "&timestamp=" + timestamp
            + "&url=" + signUrl;
        String signature = sha1(raw);
        return new JsSdkSignature(appId, timestamp, nonceStr, signature, signUrl);
    }

    private static String sha1(String source)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest)
            {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1)
                {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new ServiceException("微信签名计算失败");
        }
    }

    public static class JsSdkSignature
    {
        private final String appId;
        private final String timestamp;
        private final String nonceStr;
        private final String signature;
        private final String url;

        public JsSdkSignature(String appId, String timestamp, String nonceStr, String signature, String url)
        {
            this.appId = appId;
            this.timestamp = timestamp;
            this.nonceStr = nonceStr;
            this.signature = signature;
            this.url = url;
        }

        public String getAppId()
        {
            return appId;
        }

        public String getTimestamp()
        {
            return timestamp;
        }

        public String getNonceStr()
        {
            return nonceStr;
        }

        public String getSignature()
        {
            return signature;
        }

        public String getUrl()
        {
            return url;
        }
    }
}
