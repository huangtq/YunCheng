package com.ruoyi.system.mapper;
import com.ruoyi.system.domain.YcAttendeePass;
public interface YcAttendeePassMapper { YcAttendeePass selectById(Long passId); YcAttendeePass selectByOrderId(Long applyOrderId); int insert(YcAttendeePass pass); int update(YcAttendeePass pass); }
