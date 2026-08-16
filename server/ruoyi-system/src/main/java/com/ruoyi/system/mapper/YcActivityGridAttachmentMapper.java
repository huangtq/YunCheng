package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YcActivityGridAttachment;

public interface YcActivityGridAttachmentMapper
{
    List<YcActivityGridAttachment> selectByGridId(Long gridId);

    List<YcActivityGridAttachment> selectAllByGridId(Long gridId);

    int insertAttachment(YcActivityGridAttachment attachment);

    int deleteByGridId(Long gridId);
}
