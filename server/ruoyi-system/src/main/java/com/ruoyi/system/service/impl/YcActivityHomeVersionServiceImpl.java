package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivityConfig;
import com.ruoyi.system.domain.YcActivityHomeDraft;
import com.ruoyi.system.domain.YcActivityHomeVersion;
import com.ruoyi.system.mapper.YcActivityHomeVersionMapper;
import com.ruoyi.system.mapper.YcActivityMapper;
import com.ruoyi.system.mapper.YcMeetingContentMapper;
import com.ruoyi.system.service.IYcActivityConfigService;
import com.ruoyi.system.service.IYcActivityHomeVersionService;

@Service
public class YcActivityHomeVersionServiceImpl implements IYcActivityHomeVersionService
{
    private static final String DEFAULT_SCHEMA_VERSION = "1";

    @Autowired
    private YcActivityHomeVersionMapper homeVersionMapper;

    @Autowired
    private YcActivityMapper activityMapper;

    @Autowired
    private YcMeetingContentMapper contentMapper;

    @Autowired
    private IYcActivityConfigService activityConfigService;

    @Override
    public YcActivityHomeVersion selectYcActivityHomeVersionById(Long versionId)
    {
        return homeVersionMapper.selectYcActivityHomeVersionById(versionId);
    }

    @Override
    public List<YcActivityHomeVersion> selectYcActivityHomeVersionList(YcActivityHomeVersion version)
    {
        return homeVersionMapper.selectYcActivityHomeVersionList(version);
    }

    @Override
    public YcActivityHomeVersion selectLatestPublishedByActivityId(Long activityId)
    {
        return homeVersionMapper.selectLatestPublishedByActivityId(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YcActivityHomeVersion saveDraft(YcActivityHomeVersion version)
    {
        requireActivityAndPage(version);
        // The database column is NOT NULL; new drafts always start in manual mode.
        version.setSchemaVersion(StringUtils.isEmpty(version.getSchemaVersion()) ? DEFAULT_SCHEMA_VERSION : version.getSchemaVersion());
        version.setPublishMode(StringUtils.isEmpty(version.getPublishMode()) ? "manual" : version.getPublishMode());
        if (version.getPublishRemark() == null) version.setPublishRemark("");
        if (version.getVersionId() == null)
        {
            YcActivityHomeVersion existingDraft = homeVersionMapper.selectLatestGridConfigDraftByActivityId(version.getActivityId());
            if (existingDraft != null)
            {
                version.setVersionId(existingDraft.getVersionId());
            }
            else
            {
                version.setVersionNo(homeVersionMapper.selectNextVersionNo(version.getActivityId()));
                version.setStatus("draft");
                homeVersionMapper.insertYcActivityHomeVersion(version);
            }
        }
        if (version.getVersionId() != null)
        {
            YcActivityHomeVersion existing = homeVersionMapper.selectYcActivityHomeVersionById(version.getVersionId());
            if (existing == null || !existing.getActivityId().equals(version.getActivityId()))
            {
                throw new ServiceException("home version not found");
            }
            if (!"draft".equals(existing.getStatus()))
            {
                throw new ServiceException("only the current draft can be edited");
            }
            version.setStatus("draft");
            homeVersionMapper.updateYcActivityHomeVersion(version);
        }
        YcActivityHomeVersion saved = homeVersionMapper.selectYcActivityHomeVersionById(version.getVersionId());
        homeVersionMapper.archiveOtherGridConfigDraftsByActivityId(saved.getActivityId(), saved.getVersionId());
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YcActivityHomeVersion saveDraft(YcActivityHomeDraft draft, String username)
    {
        if (draft == null || draft.getActivityId() == null)
        {
            throw new ServiceException("activityId required");
        }
        YcActivityHomeVersion version = new YcActivityHomeVersion();
        version.setVersionId(draft.getVersionId());
        version.setActivityId(draft.getActivityId());
        version.setPageJson(draft.getPageJson());
        version.setSchemaVersion(draft.getSchemaVersion());
        version.setCreateBy(username);
        version.setUpdateBy(username);

        YcActivityHomeVersion saved = saveDraft(version);
        syncEditorConfig(draft, username);
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YcActivityHomeVersion restoreToDraft(Long versionId, String username)
    {
        YcActivityHomeVersion source = homeVersionMapper.selectYcActivityHomeVersionById(versionId);
        if (source == null)
        {
            throw new ServiceException("home version not found");
        }
        requireActivityAndPage(source);

        YcActivityHomeVersion target = new YcActivityHomeVersion();
        YcActivityHomeVersion currentDraft = homeVersionMapper.selectLatestGridConfigDraftByActivityId(source.getActivityId());
        target.setVersionId(currentDraft == null ? null : currentDraft.getVersionId());
        target.setActivityId(source.getActivityId());
        target.setPageJson(source.getPageJson());
        target.setSchemaVersion(source.getSchemaVersion());
        target.setPublishRemark("");
        target.setPublishMode("manual");
        target.setCreateBy(username);
        target.setUpdateBy(username);

        return saveDraft(target);
    }

    private void syncEditorConfig(YcActivityHomeDraft draft, String username)
    {
        YcActivityConfig config = activityConfigService.getOrCreate(draft.getActivityId(), username);
        config.setGridTemplate(draft.getGridTemplate());
        config.setRemark(draft.getConfigRemark());
        config.setMobileBackgroundUrl(draft.getMobileBackgroundUrl());
        config.setUpdateBy(username);
        activityConfigService.updateYcActivityConfig(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YcActivityHomeVersion publish(Long versionId, String publishedBy, String publishRemark)
    {
        YcActivityHomeVersion version = homeVersionMapper.selectYcActivityHomeVersionById(versionId);
        if (version == null)
        {
            throw new ServiceException("home version not found");
        }
        if (!"draft".equals(version.getStatus()))
        {
            throw new ServiceException("only a draft version can be published");
        }
        validatePage(version.getPageJson(), true, version.getActivityId());
        homeVersionMapper.archivePublishedByActivityId(version.getActivityId());
        if (homeVersionMapper.publishVersion(versionId, publishedBy, publishRemark == null ? "" : publishRemark) != 1)
        {
            throw new ServiceException("home version cannot be published");
        }
        return homeVersionMapper.selectYcActivityHomeVersionById(versionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public YcActivityHomeVersion schedule(Long versionId, Date publishAt, String scheduledBy)
    {
        if (publishAt == null || !publishAt.after(new Date())) throw new ServiceException("publishAt must be in the future");
        YcActivityHomeVersion version = homeVersionMapper.selectYcActivityHomeVersionById(versionId);
        if (version == null) throw new ServiceException("home version not found");
        if (!"draft".equals(version.getStatus())) throw new ServiceException("only draft version can be scheduled");
        validatePage(version.getPageJson(), true, version.getActivityId());
        version.setPublishAt(publishAt);
        version.setPublishMode("scheduled");
        version.setStatus("draft");
        version.setUpdateBy(scheduledBy);
        homeVersionMapper.updateYcActivityHomeVersion(version);
        return homeVersionMapper.selectYcActivityHomeVersionById(versionId);
    }

    private void requireActivityAndPage(YcActivityHomeVersion version)
    {
        if (version == null || version.getActivityId() == null)
        {
            throw new ServiceException("activityId required");
        }
        if (activityMapper.selectYcActivityById(version.getActivityId()) == null)
        {
            throw new ServiceException("activity not found");
        }
        validatePage(version.getPageJson(), false, version.getActivityId());
    }

    /** Validate stable page primitives here; module and content targets are validated by the publish workflow. */
    private void validatePage(String pageJson, boolean publishing, Long activityId)
    {
        if (StringUtils.isEmpty(pageJson))
        {
            throw new ServiceException("pageJson required");
        }
        final JSONObject page;
        try
        {
            page = JSON.parseObject(pageJson);
        }
        catch (Exception e)
        {
            throw new ServiceException("pageJson must be valid JSON");
        }
        if (page == null || StringUtils.isEmpty(page.getString("mode")))
        {
            throw new ServiceException("page mode required");
        }
        if (publishing && !"grid-config".equals(page.getString("source")))
        {
            throw new ServiceException("home page must be published from grid configuration");
        }
        JSONArray sections = page.getJSONArray("sections");
        if (sections == null)
        {
            throw new ServiceException("page sections required");
        }
        if (publishing && visibleSectionCount(sections) == 0)
        {
            throw new ServiceException("cannot publish a page without visible sections");
        }
        JSONArray entryTree = page.getJSONArray("entryTree");
        if (entryTree != null)
        {
            JSONObject layout = page.getJSONObject("layout");
            String pageMode = layout == null || StringUtils.isEmpty(layout.getString("template"))
                ? page.getString("mode") : layout.getString("template");
            validateEntries(entryTree, new ArrayList<String>(), new HashSet<String>(), publishing, pageMode, activityId);
            if (publishing) validateTemplateSections(page, entryTree);
        }
    }

    private int visibleSectionCount(JSONArray sections)
    {
        int count = 0;
        for (int i = 0; i < sections.size(); i++)
        {
            JSONObject section = sections.getJSONObject(i);
            if (section != null && !Boolean.FALSE.equals(section.getBoolean("enabled")))
            {
                JSONArray entries = section.getJSONArray("entries");
                if (entries != null && !entries.isEmpty()) count++;
            }
        }
        return count;
    }

    /** Template sections constrain visual slots, so a malformed draft cannot become a broken published page. */
    private void validateTemplateSections(JSONObject page, JSONArray entries)
    {
        JSONObject layout = page.getJSONObject("layout");
        JSONArray definitions = layout == null ? null : layout.getJSONArray("entrySections");
        if (definitions == null || definitions.isEmpty()) return;
        for (int i = 0; i < definitions.size(); i++)
        {
            JSONObject definition = definitions.getJSONObject(i);
            if (definition == null || StringUtils.isEmpty(definition.getString("key"))) throw new ServiceException("template entry section requires a key");
            String key = definition.getString("key");
            int min = definition.getIntValue("min");
            int max = definition.getIntValue("max");
            int count = 0;
            for (int j = 0; j < entries.size(); j++)
            {
                JSONObject entry = entries.getJSONObject(j);
                if (entry != null && !Boolean.FALSE.equals(entry.getBoolean("enabled")) && key.equals(entry.getString("sectionKey"))) count++;
            }
            if (count < min) throw new ServiceException("template section " + key + " requires at least " + min + " enabled entries");
            if (max > 0 && count > max) throw new ServiceException("template section " + key + " allows at most " + max + " entries");
        }
    }

    private void validateEntries(JSONArray entries, List<String> ancestors, Set<String> ids, boolean publishing, String pageMode, Long activityId)
    {
        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = entries.getJSONObject(i);
            if (entry == null) throw new ServiceException("entryTree contains an invalid entry");
            String id = entry.getString("id");
            String targetType = entry.getString("targetType");
            if (StringUtils.isEmpty(id) || StringUtils.isEmpty(targetType))
            {
                throw new ServiceException("entry id and targetType required");
            }
            if (ancestors.contains(id)) throw new ServiceException("entryTree cannot contain cycles");
            if (!ids.add(id)) throw new ServiceException("entryTree entry id must be unique");
            if (!"group".equals(targetType) && !"content".equals(targetType) && !"module".equals(targetType)
                && !"file".equals(targetType) && !"map".equals(targetType) && !"external".equals(targetType) && !"phone".equals(targetType))
            {
                throw new ServiceException("unsupported entry targetType: " + targetType);
            }
            String target = entry.getString("target");
            JSONObject targetObject = entry.getJSONObject("target");
            if ("external".equals(targetType))
            {
                if (targetObject != null) target = targetObject.getString("url");
                if (StringUtils.isEmpty(target) || !(target.startsWith("https://") || target.startsWith("http://")))
                {
                    throw new ServiceException("external entry requires an http or https target");
                }
            }
            if ("phone".equals(targetType) && StringUtils.isEmpty(target))
            {
                target = targetObject == null ? target : targetObject.getString("phone");
                if (StringUtils.isEmpty(target)) throw new ServiceException("phone entry requires a phone target");
            }
            if ("content".equals(targetType) && publishing)
            {
                Long contentId = targetObject == null ? parseLong(target) : targetObject.getLong("contentId");
                com.ruoyi.system.domain.YcMeetingContent content = contentId == null ? null : contentMapper.selectYcMeetingContentById(contentId);
                boolean legacyGridContent = contentId == null
                    && (StringUtils.isNotEmpty(entry.getString("legacyContent")) || StringUtils.isNotEmpty(entry.getString("contentUrl")));
                if (!legacyGridContent && (content == null || !activityId.equals(content.getActivityId()) || !"published".equals(content.getStatus())))
                {
                    throw new ServiceException("content entry must reference published content");
                }
            }
            if ("module".equals(targetType))
            {
                String moduleKey = targetObject == null ? target : targetObject.getString("moduleKey");
                if (!"apply".equals(moduleKey) && !"schedule".equals(moduleKey) && !"guest".equals(moduleKey)
                    && !"venue".equals(moduleKey) && !"nav".equals(moduleKey) && !"my-attendance".equals(moduleKey)
                    && !"hotel".equals(moduleKey) && !"meal".equals(moduleKey) && !"exhibitor".equals(moduleKey)
                    && !"notice".equals(moduleKey) && !"feedback".equals(moduleKey))
                {
                    throw new ServiceException("unsupported module entry target");
                }
            }
            if ("group".equals(targetType) && (entry.getJSONArray("children") == null || entry.getJSONArray("children").isEmpty()))
            {
                throw new ServiceException("group entry requires children");
            }
            // A poster hotspot can lead to any supported destination (module,
            // content or external URL). Bounds are a property of the page
            // layout, not of the destination type.
            if ("image-map".equals(pageMode) && !"group".equals(targetType)) validateImageMapBounds(entry);
            List<String> next = new ArrayList<>(ancestors);
            next.add(id);
            JSONArray children = entry.getJSONArray("children");
            if (children != null) validateEntries(children, next, ids, publishing, pageMode, activityId);
        }
    }

    private Long parseLong(String value)
    {
        try { return StringUtils.isEmpty(value) ? null : Long.valueOf(value); }
        catch (NumberFormatException ignored) { return null; }
    }

    private void validateImageMapBounds(JSONObject entry)
    {
        JSONObject bounds = entry.getJSONObject("bounds");
        if (bounds == null) throw new ServiceException("image map entry requires bounds");
        Double left = bounds.getDouble("left");
        Double top = bounds.getDouble("top");
        Double width = bounds.getDouble("width");
        Double height = bounds.getDouble("height");
        if (left == null || top == null || width == null || height == null || left < 0 || top < 0 || width <= 0 || height <= 0 || left + width > 100 || top + height > 100)
        {
            throw new ServiceException("image map entry bounds must stay within 0-100%");
        }
    }
}
