package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivityGrid;
import com.ruoyi.system.domain.YcActivityGridAttachment;
import com.ruoyi.system.mapper.YcActivityGridMapper;
import com.ruoyi.system.mapper.YcActivityGridAttachmentMapper;
import com.ruoyi.system.service.IYcActivityGridService;

@Service
public class YcActivityGridServiceImpl implements IYcActivityGridService
{
    @Autowired
    private YcActivityGridMapper ycActivityGridMapper;

    @Autowired
    private YcActivityGridAttachmentMapper attachmentMapper;

    @Override
    public YcActivityGrid selectYcActivityGridById(Long gridId)
    {
        YcActivityGrid grid = ycActivityGridMapper.selectYcActivityGridById(gridId);
        if (grid != null) grid.setAttachments(loadAttachments(gridId, true));
        return grid;
    }

    @Override
    public List<YcActivityGrid> selectYcActivityGridList(YcActivityGrid grid)
    {
        List<YcActivityGrid> list = ycActivityGridMapper.selectYcActivityGridList(grid);
        for (YcActivityGrid item : list) item.setAttachments(loadAttachments(item.getGridId(), false));
        return list;
    }

    @Override
    public int insertYcActivityGrid(YcActivityGrid grid)
    {
        if (StringUtils.isEmpty(grid.getLinkType()))
        {
            grid.setLinkType("none");
        }
        if (StringUtils.isEmpty(grid.getIconType()))
        {
            grid.setIconType(StringUtils.isNotEmpty(grid.getIconKey()) ? "icon" : "image");
        }
        if (StringUtils.isEmpty(grid.getIconUrl()))
        {
            grid.setIconUrl("");
        }
        if (StringUtils.isEmpty(grid.getIconKey()))
        {
            grid.setIconKey("");
        }
        if (StringUtils.isEmpty(grid.getModuleKey()))
        {
            grid.setModuleKey("none");
        }
        if (StringUtils.isEmpty(grid.getStatus()))
        {
            grid.setStatus("1");
        }
        if (grid.getSortOrder() == null)
        {
            grid.setSortOrder(0);
        }
        return ycActivityGridMapper.insertYcActivityGrid(grid);
    }

    @Override
    public int updateYcActivityGrid(YcActivityGrid grid)
    {
        return ycActivityGridMapper.updateYcActivityGrid(grid);
    }

    @Override
    public int deleteYcActivityGridByIds(Long[] gridIds)
    {
        for (Long gridId : gridIds) safeDeleteAttachments(gridId);
        return ycActivityGridMapper.deleteYcActivityGridByIds(gridIds);
    }

    @Override
    public List<YcActivityGridAttachment> selectAttachments(Long gridId)
    {
        return loadAttachments(gridId, true);
    }

    @Override
    @Transactional
    public int syncAttachments(Long gridId, List<YcActivityGridAttachment> attachments, String updateBy)
    {
        YcActivityGrid grid = ycActivityGridMapper.selectYcActivityGridById(gridId);
        if (grid == null) throw new IllegalArgumentException("九宫格项不存在");
        try
        {
            attachmentMapper.deleteByGridId(gridId);
        }
        catch (DataAccessException e)
        {
            if (isMissingAttachmentTable(e)) throw new ServiceException("请先执行 meeting_grid_attachment_phase.sql");
            throw e;
        }
        if (attachments == null || attachments.isEmpty()) return 1;
        int sortOrder = 0;
        for (YcActivityGridAttachment attachment : attachments)
        {
            if (attachment == null || StringUtils.isEmpty(attachment.getFileUrl())) continue;
            if (!attachment.getFileUrl().startsWith("/")
                && !attachment.getFileUrl().startsWith("https://")
                && !attachment.getFileUrl().startsWith("http://"))
            {
                throw new IllegalArgumentException("附件地址必须是本地路径或 http/https 地址");
            }
            String fallbackName = fileName(attachment.getFileUrl());
            String displayName = StringUtils.isEmpty(attachment.getDisplayName()) ? fallbackName : attachment.getDisplayName().trim();
            String downloadName = StringUtils.isEmpty(attachment.getDownloadName()) ? displayName : attachment.getDownloadName().trim();
            if (StringUtils.isEmpty(displayName) || StringUtils.isEmpty(downloadName))
            {
                throw new IllegalArgumentException("附件展示名称和下载名称不能为空");
            }
            attachment.setAttachmentId(null);
            attachment.setActivityId(grid.getActivityId());
            attachment.setGridId(gridId);
            attachment.setDisplayName(displayName);
            attachment.setDownloadName(downloadName);
            attachment.setSortOrder(sortOrder++);
            attachment.setStatus("1");
            attachment.setCreateBy(updateBy);
            attachmentMapper.insertAttachment(attachment);
        }
        return 1;
    }

    private String fileName(String url)
    {
        String value = StringUtils.substringBefore(StringUtils.substringBefore(url, "?"), "#");
        int slash = value.lastIndexOf('/');
        return slash >= 0 ? value.substring(slash + 1) : value;
    }

    private List<YcActivityGridAttachment> loadAttachments(Long gridId, boolean includeDisabled)
    {
        try
        {
            return includeDisabled ? attachmentMapper.selectAllByGridId(gridId) : attachmentMapper.selectByGridId(gridId);
        }
        catch (DataAccessException e)
        {
            if (isMissingAttachmentTable(e)) return new java.util.ArrayList<>();
            throw e;
        }
    }

    private void safeDeleteAttachments(Long gridId)
    {
        try
        {
            attachmentMapper.deleteByGridId(gridId);
        }
        catch (DataAccessException e)
        {
            if (!isMissingAttachmentTable(e)) throw e;
        }
    }

    private boolean isMissingAttachmentTable(DataAccessException e)
    {
        String message = e.getMessage();
        return message != null && message.contains("yc_activity_grid_attachment")
            && (message.contains("doesn't exist") || message.contains("does not exist") || message.contains("不存在"));
    }
}
