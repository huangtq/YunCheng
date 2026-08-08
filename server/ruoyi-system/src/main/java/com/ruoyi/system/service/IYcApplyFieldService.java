package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YcApplyField;

public interface IYcApplyFieldService
{
    public YcApplyField selectYcApplyFieldById(Long fieldId);

    public List<YcApplyField> selectYcApplyFieldList(YcApplyField field);

    public int insertYcApplyField(YcApplyField field);

    public int updateYcApplyField(YcApplyField field);

    public int deleteYcApplyFieldByIds(Long[] fieldIds);

    public int updateEnabledByIds(Long[] fieldIds, String enabledFlag, String updateBy);

    public void ensureStandardFields(Long channelId, Long activityId, String createBy);
}
