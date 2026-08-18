package com.ruoyi.web.controller.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.quartz.service.ISysJobService;

/** Lightweight readiness endpoint used by the reverse proxy during blue-green deploys. */
@Anonymous
@RestController
@RequestMapping("/internal/health")
public class ReadinessController
{
    @Value("${yuncheng.release-id:unknown}")
    private String releaseId;

    @Value("${yuncheng.quartz-activation-token:}")
    private String quartzActivationToken;

    @Autowired
    private ISysJobService jobService;

    @GetMapping("/ready")
    public Map<String, String> ready()
    {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("release", releaseId);
        return result;
    }

    @PostMapping("/quartz/activate")
    public Map<String, String> activateQuartz(
            @RequestHeader(name = "X-Yuncheng-Activation-Token", required = false) String token)
            throws Exception
    {
        if (quartzActivationToken.isBlank()
                || token == null
                || !MessageDigest.isEqual(quartzActivationToken.getBytes(StandardCharsets.UTF_8),
                        token.getBytes(StandardCharsets.UTF_8)))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        jobService.activate();
        Map<String, String> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("quartz", "ACTIVE");
        result.put("release", releaseId);
        return result;
    }
}
