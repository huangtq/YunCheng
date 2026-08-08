package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivityGrid;
import com.ruoyi.system.mapper.YcActivityGridMapper;
import com.ruoyi.system.service.IYcActivityGridService;

@Service
public class YcActivityGridServiceImpl implements IYcActivityGridService
{
    @Autowired
    private YcActivityGridMapper ycActivityGridMapper;

    @Override
    public YcActivityGrid selectYcActivityGridById(Long gridId)
    {
        return ycActivityGridMapper.selectYcActivityGridById(gridId);
    }

    @Override
    public List<YcActivityGrid> selectYcActivityGridList(YcActivityGrid grid)
    {
        return ycActivityGridMapper.selectYcActivityGridList(grid);
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
        return ycActivityGridMapper.deleteYcActivityGridByIds(gridIds);
    }
}