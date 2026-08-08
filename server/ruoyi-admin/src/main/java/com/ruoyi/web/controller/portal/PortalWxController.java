package com.ruoyi.web.controller.portal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.web.service.MpTokenService;
import com.ruoyi.system.domain.YcMpUser;
import com.ruoyi.system.service.IYcMpUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Anonymous
@RestController
@RequestMapping("/portal/wx")
public class PortalWxController extends BaseController
{
    @Autowired
    private IYcMpUserService mpUserService;
    @Autowired
    private MpTokenService mpTokenService;

    @Value("${wechat.mp.app-id:}")
    private String appId;
    @Value("${wechat.mp.app-secret:}")
    private String appSecret;
    @Value("${wechat.mp.oauth-redirect-uri:}")
    private String oauthRedirectUri;
    @Value("${wechat.mp.h5-home-url:}")
    private String h5HomeUrl;
    @Value("${wechat.mp.mock-enabled:true}")
    private boolean mockEnabled;

    @GetMapping("/oauth/url")
    public AjaxResult oauthUrl(@RequestParam(required = false) Long activityId,
        @RequestParam(required = false) String redirect)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("mockEnabled", mockEnabled || StringUtils.isEmpty(appId));
        if (mockEnabled || StringUtils.isEmpty(appId))
        {
            String mock = "/portal/wx/oauth/mock?activityId=" + (activityId == null ? "" : activityId)
                + (StringUtils.isNotEmpty(redirect) ? "&redirect=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8) : "");
            data.put("url", mock);
            data.put("mode", "mock");
            return success(data);
        }
        String state = (activityId == null ? "0" : String.valueOf(activityId));
        if (StringUtils.isNotEmpty(redirect))
        {
            state = state + "|" + URLEncoder.encode(redirect, StandardCharsets.UTF_8);
        }
        String redirectUri = URLEncoder.encode(oauthRedirectUri, StandardCharsets.UTF_8);
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId
            + "&redirect_uri=" + redirectUri
            + "&response_type=code&scope=snsapi_userinfo&state=" + state
            + "#wechat_redirect";
        data.put("url", url);
        data.put("mode", "wechat");
        return success(data);
    }

    @GetMapping("/oauth/mock")
    public void oauthMock(@RequestParam(required = false) Long activityId,
        @RequestParam(required = false) String redirect,
        HttpServletResponse response) throws Exception
    {
        if (!mockEnabled && StringUtils.isNotEmpty(appId))
        {
            response.sendError(403, "mock disabled");
            return;
        }
        String openid = "mock_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        YcMpUser user = mpUserService.loginByOpenid(openid, "", "Guest", "");
        String token = mpTokenService.createToken(user);
        response.sendRedirect(buildH5Redirect(activityId, redirect, token));
    }

    @GetMapping("/oauth/callback")
    public void oauthCallback(@RequestParam(required = false) String code,
        @RequestParam(required = false) String state,
        HttpServletResponse response) throws Exception
    {
        Long activityId = null;
        String redirect = null;
        if (StringUtils.isNotEmpty(state))
        {
            String[] parts = state.split("\\|", 2);
            if (parts.length > 0 && StringUtils.isNotEmpty(parts[0]) && !"0".equals(parts[0]))
            {
                activityId = Long.valueOf(parts[0]);
            }
            if (parts.length > 1)
            {
                redirect = java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        if (StringUtils.isEmpty(code))
        {
            response.sendRedirect(buildH5Redirect(activityId, redirect, null) + "&error=missing_code");
            return;
        }
        String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId
            + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
        String tokenResp = HttpUtils.sendGet(tokenUrl);
        JSONObject tokenJson = JSON.parseObject(tokenResp);
        String openid = tokenJson.getString("openid");
        String accessToken = tokenJson.getString("access_token");
        if (StringUtils.isEmpty(openid))
        {
            response.sendRedirect(buildH5Redirect(activityId, redirect, null) + "&error=oauth_failed");
            return;
        }
        String nickname = "";
        String avatar = "";
        String unionid = tokenJson.getString("unionid");
        if (StringUtils.isNotEmpty(accessToken))
        {
            String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken
                + "&openid=" + openid + "&lang=zh_CN";
            String infoResp = HttpUtils.sendGet(infoUrl);
            JSONObject info = JSON.parseObject(infoResp);
            if (info != null)
            {
                nickname = info.getString("nickname");
                avatar = info.getString("headimgurl");
                if (StringUtils.isEmpty(unionid))
                {
                    unionid = info.getString("unionid");
                }
            }
        }
        YcMpUser user = mpUserService.loginByOpenid(openid, unionid, nickname, avatar);
        String token = mpTokenService.createToken(user);
        response.sendRedirect(buildH5Redirect(activityId, redirect, token));
    }

    @GetMapping("/me")
    public AjaxResult me(HttpServletRequest request)
    {
        MpLoginUser user = mpTokenService.requireLoginUser(request);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("openid", user.getOpenid());
        data.put("phone", user.getPhone());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("phoneBound", StringUtils.isNotEmpty(user.getPhone()));
        return success(data);
    }

    @PutMapping("/bindPhone")
    public AjaxResult bindPhone(@RequestBody Map<String, String> body, HttpServletRequest request)
    {
        MpLoginUser loginUser = mpTokenService.requireLoginUser(request);
        String phone = body == null ? null : body.get("phone");
        mpUserService.bindPhone(loginUser.getUserId(), phone);
        YcMpUser db = mpUserService.selectYcMpUserById(loginUser.getUserId());
        mpTokenService.refreshLoginUser(loginUser, db);
        return success(db);
    }

    private String buildH5Redirect(Long activityId, String redirect, String token)
    {
        String base;
        if (StringUtils.isNotEmpty(redirect))
        {
            base = redirect;
        }
        else if (StringUtils.isNotEmpty(h5HomeUrl))
        {
            base = h5HomeUrl.replace("{activityId}", activityId == null ? "" : String.valueOf(activityId));
        }
        else
        {
            base = "/#/pages/meeting/home?activityId=" + (activityId == null ? "" : activityId);
        }
        String join = base.contains("?") ? "&" : "?";
        if (StringUtils.isNotEmpty(token))
        {
            return base + join + "mpToken=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        }
        return base;
    }
}