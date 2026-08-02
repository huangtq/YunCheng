package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.service.ISysFileInfoService;

/**
 * 文件管理 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/file")
public class SysFileController extends BaseController
{
    @Autowired
    private ISysFileInfoService sysFileInfoService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 查询文件列表
     */
    @PreAuthorize("@ss.hasPermi('system:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFileInfo sysFileInfo)
    {
        startPage();
        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
        return getDataTable(list);
    }

    /**
     * 获取文件详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:file:query')")
    @GetMapping(value = "/{fileId}")
    public AjaxResult getInfo(@PathVariable Long fileId)
    {
        return success(sysFileInfoService.selectSysFileInfoById(fileId));
    }

    /**
     * 上传文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:upload')")
    @Log(title = "文件管理", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file) throws Exception
    {
        SysFileInfo sysFileInfo = sysFileInfoService.uploadFile(file, getUsername());
        // 返回绝对地址，便于前端直接展示
        sysFileInfo.setUrl(serverConfig.getUrl() + sysFileInfo.getFileName());
        return success(sysFileInfo);
    }

    /**
     * 删除文件
     */
    @PreAuthorize("@ss.hasPermi('system:file:remove')")
    @Log(title = "文件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable Long[] fileIds)
    {
        return toAjax(sysFileInfoService.deleteSysFileInfoByIds(fileIds));
    }
}