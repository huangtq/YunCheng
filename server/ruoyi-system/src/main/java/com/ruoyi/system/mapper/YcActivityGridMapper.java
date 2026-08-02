package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcActivityGrid;

public interface YcActivityGridMapper
{
    public YcActivityGrid selectYcActivityGridById(Long gridId);

    public List<YcActivityGrid> selectYcActivityGridList(YcActivityGrid grid);

    public int insertYcActivityGrid(YcActivityGrid grid);

    public int updateYcActivityGrid(YcActivityGrid grid);

    public int deleteYcActivityGridByIds(Long[] gridIds);
}