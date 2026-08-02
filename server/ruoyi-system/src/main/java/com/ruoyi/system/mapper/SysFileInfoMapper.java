package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysFileInfo;

/**
 * 文件管理 数据层
 *
 * @author ruoyi
 */
public interface SysFileInfoMapper
{
    /**
     * 查询文件
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    public SysFileInfo selectSysFileInfoById(Long fileId);

    /**
     * 查询文件列表
     *
     * @param sysFileInfo 文件信息
     * @return 文件集合
     */
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo);

    /**
     * 根据ID集合查询文件
     *
     * @param fileIds 文件ID数组
     * @return 文件集合
     */
    public List<SysFileInfo> selectSysFileInfoByIds(Long[] fileIds);

    /**
     * 新增文件
     *
     * @param sysFileInfo 文件信息
     * @return 结果
     */
    public int insertSysFileInfo(SysFileInfo sysFileInfo);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 结果
     */
    public int deleteSysFileInfoById(Long fileId);

    /**
     * 批量删除文件
     *
     * @param fileIds 需要删除的文件ID
     * @return 结果
     */
    public int deleteSysFileInfoByIds(Long[] fileIds);
}