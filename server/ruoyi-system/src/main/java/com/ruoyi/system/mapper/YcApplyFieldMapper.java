package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcApplyField;

public interface YcApplyFieldMapper
{
    public YcApplyField selectYcApplyFieldById(Long fieldId);

    public List<YcApplyField> selectYcApplyFieldList(YcApplyField field);

    public int countByChannelId(Long channelId);

    public int insertYcApplyField(YcApplyField field);

    public int batchInsertYcApplyField(List<YcApplyField> list);

    public int updateYcApplyField(YcApplyField field);

    public int updateEnabledByIds(YcApplyField field);

    public int deleteYcApplyFieldByIds(Long[] fieldIds);
}
