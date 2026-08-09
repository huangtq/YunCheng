package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileTypeUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.mapper.SysFileInfoMapper;
import com.ruoyi.system.service.ISysFileInfoService;

/**
 * 文件管理 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysFileInfoServiceImpl implements ISysFileInfoService
{
    @Autowired
    private SysFileInfoMapper sysFileInfoMapper;

    /**
     * 查询文件
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    @Override
    public SysFileInfo selectSysFileInfoById(Long fileId)
    {
        return sysFileInfoMapper.selectSysFileInfoById(fileId);
    }

    /**
     * 查询文件列表
     *
     * @param sysFileInfo 文件信息
     * @return 文件集合
     */
    @Override
    public List<SysFileInfo> selectSysFileInfoList(SysFileInfo sysFileInfo)
    {
        return sysFileInfoMapper.selectSysFileInfoList(sysFileInfo);
    }

    /**
     * 上传文件并保存元数据
     *
     * @param file 上传文件
     * @param createBy 创建者
     * @return 文件信息
     */
    @Override
    public SysFileInfo uploadFile(MultipartFile file, String createBy) throws Exception
    {
        return uploadFile(file, createBy, null);
    }

    @Override
    public SysFileInfo uploadFile(MultipartFile file, String createBy, Long activityId) throws Exception
    {
        String filePath = RuoYiConfig.getUploadPath();
        String fileName = FileUploadUtils.upload(filePath, file);

        SysFileInfo sysFileInfo = new SysFileInfo();
        String originalName = file.getOriginalFilename();
        sysFileInfo.setOriginalName(originalName);
        sysFileInfo.setFileName(fileName);
        sysFileInfo.setActivityId(activityId);
        // 存相对路径，前端用 baseApi 拼接；Controller 可再补全绝对地址返回
        sysFileInfo.setUrl(fileName);
        sysFileInfo.setFileSuffix(StringUtils.isNotEmpty(originalName) ? FileTypeUtils.getFileType(originalName) : "");
        sysFileInfo.setFileSize(file.getSize());
        sysFileInfo.setCreateBy(createBy);
        sysFileInfo.setCreateTime(DateUtils.getNowDate());
        sysFileInfoMapper.insertSysFileInfo(sysFileInfo);
        return sysFileInfo;
    }

    /**
     * 批量删除文件（数据库 + 本地磁盘）
     *
     * @param fileIds 需要删除的文件ID
     * @return 结果
     */
    @Override
    public int deleteSysFileInfoByIds(Long[] fileIds)
    {
        List<SysFileInfo> fileList = sysFileInfoMapper.selectSysFileInfoByIds(fileIds);
        for (SysFileInfo fileInfo : fileList)
        {
            deleteLocalFile(fileInfo.getFileName());
        }
        return sysFileInfoMapper.deleteSysFileInfoByIds(fileIds);
    }

    @Override
    public int deleteSysFileInfoByIds(Long[] fileIds, Long activityId)
    {
        List<SysFileInfo> fileList = sysFileInfoMapper.selectSysFileInfoByIds(fileIds);
        for (SysFileInfo fileInfo : fileList)
        {
            if (fileInfo.getActivityId() == null || !activityId.equals(fileInfo.getActivityId()))
            {
                throw new ServiceException("不能删除其他范围的文件");
            }
        }
        for (SysFileInfo fileInfo : fileList)
        {
            deleteLocalFile(fileInfo.getFileName());
        }
        return sysFileInfoMapper.deleteSysFileInfoByIds(fileIds);
    }

    /**
     * 删除本地磁盘文件（仅允许 profile 目录下路径）
     *
     * @param fileName 存储相对路径（含 /profile 前缀）
     */
    private void deleteLocalFile(String fileName)
    {
        if (StringUtils.isEmpty(fileName) || !StringUtils.startsWith(fileName, Constants.RESOURCE_PREFIX))
        {
            return;
        }
        String localPath = RuoYiConfig.getProfile() + FileUtils.stripPrefix(fileName);
        FileUtils.deleteFile(localPath);
    }
}