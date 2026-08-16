package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YcActivityGrid;
import com.ruoyi.system.domain.YcActivityGridAttachment;

public interface IYcActivityGridService
{
    public YcActivityGrid selectYcActivityGridById(Long gridId);

    public List<YcActivityGrid> selectYcActivityGridList(YcActivityGrid grid);

    public int insertYcActivityGrid(YcActivityGrid grid);

    public int updateYcActivityGrid(YcActivityGrid grid);

    public int deleteYcActivityGridByIds(Long[] gridIds);

    public List<YcActivityGridAttachment> selectAttachments(Long gridId);

    public int syncAttachments(Long gridId, List<YcActivityGridAttachment> attachments, String updateBy);
}
