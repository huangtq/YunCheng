package com.ruoyi.web.controller.portal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.MpLoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.web.service.MpTokenService;
import com.ruoyi.framework.web.service.WechatMpApiService;
import com.ruoyi.system.domain.YcActivity;
import com.ruoyi.system.domain.YcMpUser;
import com.ruoyi.system.mapper.YcActivityMapper;
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
    @Autowired
    private WechatMpApiService wechatMpApiService;
    @Autowired
    private YcActivityMapper activityMapper;

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
    /** 对外可访问的 API 根地址，用于分享封面绝对路径与分享落地页自链 */
    @Value("${wechat.mp.public-base-url:https://yunchengmice.cn/prod-api}")
    private String publicBaseUrl;
    /** 对外分享落地页（建议同域 /h5/share，便于微信生成卡片） */
    @Value("${wechat.mp.share-page-url:https://yunchengmice.cn/h5/share?activityId={activityId}}")
    private String sharePageUrl;

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

    /**
     * 分享卡片文案（标题/描述/封面/落地链接），供 H5 配置微信分享使用。
     */
    @GetMapping("/share-info")
    public AjaxResult shareInfo(@RequestParam Long activityId)
    {
        Map<String, Object> data = buildShareInfo(activityId);
        if (data == null)
        {
            return error("会议不存在或未开放");
        }
        return success(data);
    }

    /**
     * 微信爬虫友好的分享落地页：带中文 title/description/og，展示标题/地点/日期。
     * 不要瞬间跳转，否则微信抓不到卡片。
     */
    @GetMapping(value = "/share", produces = MediaType.TEXT_HTML_VALUE)
    public void sharePage(@RequestParam(required = false) Long activityId,
        HttpServletResponse response) throws Exception
    {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        // 避免中间层/浏览器缓存旧的秒跳页面
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        Map<String, Object> info = activityId == null ? null : buildShareInfo(activityId);
        if (info == null)
        {
            response.setStatus(404);
            response.getWriter().write("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>会议不存在</title></head>"
                + "<body><p>会议不存在或未开放</p></body></html>");
            return;
        }
        String title = HtmlUtils.htmlEscape(String.valueOf(info.get("title")));
        String desc = HtmlUtils.htmlEscape(String.valueOf(info.get("desc")));
        String place = HtmlUtils.htmlEscape(String.valueOf(info.getOrDefault("place", "")));
        String dateText = HtmlUtils.htmlEscape(String.valueOf(info.getOrDefault("dateText", "")));
        String imgUrl = HtmlUtils.htmlEscape(String.valueOf(info.getOrDefault("imgUrl", "")));
        String link = HtmlUtils.htmlEscape(String.valueOf(info.get("link")));
        String h5Url = HtmlUtils.htmlEscape(String.valueOf(info.get("h5Url")));
        StringBuilder html = new StringBuilder(2048);
        html.append("<!DOCTYPE html><html lang=\"zh-CN\"><head>")
            .append("<meta charset=\"UTF-8\"/>")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>")
            .append("<title>").append(title).append("</title>")
            .append("<meta name=\"description\" content=\"").append(desc).append("\"/>")
            .append("<meta property=\"og:type\" content=\"website\"/>")
            .append("<meta property=\"og:title\" content=\"").append(title).append("\"/>")
            .append("<meta property=\"og:description\" content=\"").append(desc).append("\"/>")
            .append("<meta property=\"og:url\" content=\"").append(link).append("\"/>");
        if (StringUtils.isNotEmpty(imgUrl))
        {
            html.append("<meta property=\"og:image\" content=\"").append(imgUrl).append("\"/>")
                .append("<meta itemprop=\"image\" content=\"").append(imgUrl).append("\"/>")
                .append("<link rel=\"image_src\" href=\"").append(imgUrl).append("\"/>");
        }
        html.append("<meta itemprop=\"name\" content=\"").append(title).append("\"/>")
            .append("<meta itemprop=\"description\" content=\"").append(desc).append("\"/>")
            .append("<style>")
            .append("body{margin:0;font-family:-apple-system,BlinkMacSystemFont,\"PingFang SC\",\"Helvetica Neue\",Arial,sans-serif;background:#f5f7fa;color:#1f2329;}")
            .append(".wrap{max-width:640px;margin:0 auto;padding:24px 16px 40px;}")
            .append(".cover{width:100%;border-radius:12px;overflow:hidden;background:#e8eef7;}")
            .append(".cover img{display:block;width:100%;height:auto;}")
            .append("h1{margin:18px 0 12px;font-size:22px;line-height:1.4;}")
            .append(".meta{margin:0;padding:0;list-style:none;color:#5c6570;font-size:15px;line-height:1.7;}")
            .append(".btn{display:block;margin-top:24px;text-align:center;background:#1f6feb;color:#fff;text-decoration:none;")
            .append("padding:14px 18px;border-radius:10px;font-size:16px;font-weight:600;}")
            .append("</style></head><body><div class=\"wrap\">");
        if (StringUtils.isNotEmpty(imgUrl))
        {
            html.append("<div class=\"cover\"><img src=\"").append(imgUrl).append("\" alt=\"").append(title).append("\"/></div>");
        }
        html.append("<h1>").append(title).append("</h1><ul class=\"meta\">");
        if (StringUtils.isNotEmpty(place))
        {
            html.append("<li>地点：").append(place).append("</li>");
        }
        if (StringUtils.isNotEmpty(dateText))
        {
            html.append("<li>时间：").append(dateText).append("</li>");
        }
        html.append("</ul>")
            .append("<a class=\"btn\" id=\"enter\" href=\"").append(h5Url).append("\">进入会议</a>")
            .append("</div>")
            // 延迟跳转：给微信爬虫留时间读取 meta；真实用户约 1.5s 后进入会议
            .append("<script>(function(){")
            .append("var ua=navigator.userAgent||'';")
            .append("var bot=/bot|spider|crawl|slurp|facebookexternalhit|WhatsApp|Twitterbot|LinkedInBot|Preview/i.test(ua);")
            .append("if(bot){return;}")
            .append("setTimeout(function(){location.replace(\"").append(h5Url).append("\");},1500);")
            .append("})();</script>")
            .append("</body></html>");
        response.getWriter().write(html.toString());
    }

    /**
     * 微信 JS-SDK 签名。url 必须是当前页完整地址（不含 # 后片段）。
     */
    @GetMapping("/js-config")
    public AjaxResult jsConfig(@RequestParam String url)
    {
        Map<String, Object> data = new HashMap<>();
        if (!wechatMpApiService.isConfigured())
        {
            data.put("enabled", false);
            return success(data);
        }
        try
        {
            WechatMpApiService.JsSdkSignature sign = wechatMpApiService.createJsSdkSignature(url);
            data.put("enabled", true);
            data.put("appId", sign.getAppId());
            data.put("timestamp", sign.getTimestamp());
            data.put("nonceStr", sign.getNonceStr());
            data.put("signature", sign.getSignature());
            data.put("url", sign.getUrl());
            return success(data);
        }
        catch (Exception e)
        {
            data.put("enabled", false);
            data.put("msg", e.getMessage());
            return success(data);
        }
    }

    private Map<String, Object> buildShareInfo(Long activityId)
    {
        if (activityId == null)
        {
            return null;
        }
        YcActivity activity = activityMapper.selectYcActivityById(activityId);
        if (activity == null || "2".equals(activity.getDelFlag()))
        {
            return null;
        }
        if ("0".equals(activity.getIsShow()))
        {
            return null;
        }
        String title = StringUtils.isNotEmpty(activity.getActivityName()) ? activity.getActivityName() : "会议邀请";
        String place = StringUtils.defaultString(activity.getAddress());
        String dateText = formatMeetingDate(activity.getStartTime(), activity.getEndTime());
        String desc = buildShareDesc(place, dateText, activity.getRemark());
        String imgUrl = toAbsoluteUrl(activity.getCoverUrl());
        String h5Url = buildPlainH5HomeUrl(activityId);
        String link = buildSharePageUrl(activityId);

        Map<String, Object> data = new HashMap<>();
        data.put("activityId", activityId);
        data.put("title", title);
        data.put("desc", desc);
        data.put("place", place);
        data.put("dateText", dateText);
        data.put("imgUrl", imgUrl);
        data.put("link", link);
        data.put("h5Url", h5Url);
        return data;
    }

    private String buildShareDesc(String place, String dateText, String remark)
    {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotEmpty(dateText))
        {
            parts.add("时间：" + dateText);
        }
        if (StringUtils.isNotEmpty(place))
        {
            parts.add("地点：" + place);
        }
        if (parts.isEmpty() && StringUtils.isNotEmpty(remark))
        {
            return trimDesc(remark);
        }
        if (parts.isEmpty())
        {
            return "点击查看会议详情，欢迎报名参加";
        }
        // 标题由微信卡片单独展示，描述按时间、地点的顺序拼接。
        return trimDesc(String.join("　", parts));
    }

    private String buildSharePageUrl(Long activityId)
    {
        String id = activityId == null ? "" : String.valueOf(activityId);
        if (StringUtils.isNotEmpty(sharePageUrl))
        {
            return sharePageUrl.replace("{activityId}", id);
        }
        return "https://yunchengmice.cn/h5/share?activityId=" + id;
    }

    private String formatMeetingDate(Date startTime, Date endTime)
    {
        if (startTime == null) return "";
        String start = DateUtils.parseDateToStr("yyyy年MM月dd日", startTime);
        if (endTime == null) return start;
        String end = DateUtils.parseDateToStr("yyyy年MM月dd日", endTime);
        return start.equals(end) ? start : start + " 至 " + end;
    }

    private static String trimDesc(String text)
    {
        String value = text == null ? "" : text.trim().replaceAll("\\s+", " ");
        if (value.length() <= 80)
        {
            return value;
        }
        return value.substring(0, 80) + "...";
    }

    private String toAbsoluteUrl(String path)
    {
        if (StringUtils.isEmpty(path))
        {
            return "";
        }
        if (path.startsWith("http://") || path.startsWith("https://"))
        {
            return path;
        }
        String base = normalizePublicBase();
        return path.startsWith("/") ? base + path : base + "/" + path;
    }

    private String normalizePublicBase()
    {
        String base = StringUtils.isNotEmpty(publicBaseUrl) ? publicBaseUrl : "https://yunchengmice.cn/prod-api";
        return base.replaceAll("/+$", "");
    }

    private String buildPlainH5HomeUrl(Long activityId)
    {
        if (StringUtils.isNotEmpty(h5HomeUrl))
        {
            return h5HomeUrl.replace("{activityId}", activityId == null ? "" : String.valueOf(activityId));
        }
        return "https://yunchengmice.cn/h5/pages/meeting/home?activityId="
            + (activityId == null ? "" : activityId);
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
            base = "/h5/pages/meeting/home?activityId=" + (activityId == null ? "" : activityId);
        }
        String join = base.contains("?") ? "&" : "?";
        if (StringUtils.isNotEmpty(token))
        {
            return base + join + "mpToken=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        }
        return base;
    }
}
