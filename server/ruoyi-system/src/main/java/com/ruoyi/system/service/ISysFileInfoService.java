package com.ruoyi.system.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.system.domain.SysFileInfo;

/**
 * 文件管理 服务层
 *
 * @author ruoyi
 */
public interface ISysFileInfoService
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
     * 上传文件并保存元数据
     *
     * @param file 上传文件
     * @param createBy 创建者
     * @return 文件信息
     * @throws Exception 上传异常
     */
    public SysFileInfo uploadFile(MultipartFile file, String createBy) throws Exception;

    /**
     * 批量删除文件（数据库 + 本地磁盘）
     *
     * @param fileIds 需要删除的文件ID
     * @return 结果
     */
    public int deleteSysFileInfoByIds(Long[] fileIds);
}