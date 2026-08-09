package com.ruoyi.web.controller.meeting;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.service.IYcActivityService;
import com.ruoyi.system.service.ISysFileInfoService;

/**
 * 会议文件管理。
 */
@RestController
@RequestMapping("/meeting/activity/{activityId}/file")
public class YcActivityFileController extends BaseController
{
    @Autowired
    private ISysFileInfoService sysFileInfoService;

    @Autowired
    private IYcActivityService ycActivityService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 查询指定会议的文件列表。
     */
    @PreAuthorize("@ss.hasAnyPermi('system:file:list,meeting:activity:query')")
    @GetMapping("/list")
    public TableDataInfo list(@PathVariable Long activityId, SysFileInfo sysFileInfo)
    {
        ensureActivityExists(activityId);
        sysFileInfo.setActivityId(activityId);
        startPage();
        List<SysFileInfo> list = sysFileInfoService.selectSysFileInfoList(sysFileInfo);
        return getDataTable(list);
    }

    /**
     * 上传指定会议的文件。
     */
    @PreAuthorize("@ss.hasAnyPermi('system:file:upload,meeting:activity:edit')")
    @Log(title = "会议文件管理", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(@PathVariable Long activityId, @RequestParam("file") MultipartFile file) throws Exception
    {
        ensureActivityExists(activityId);
        SysFileInfo sysFileInfo = sysFileInfoService.uploadFile(file, getUsername(), activityId);
        sysFileInfo.setUrl(serverConfig.getUrl() + sysFileInfo.getFileName());
        return success(sysFileInfo);
    }

    /**
     * 删除指定会议的文件。
     */
    @PreAuthorize("@ss.hasAnyPermi('system:file:remove,meeting:activity:edit')")
    @Log(title = "会议文件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable Long activityId, @PathVariable Long[] fileIds)
    {
        ensureActivityExists(activityId);
        return toAjax(sysFileInfoService.deleteSysFileInfoByIds(fileIds, activityId));
    }

    private void ensureActivityExists(Long activityId)
    {
        if (activityId == null || ycActivityService.selectYcActivityById(activityId) == null)
        {
            throw new ServiceException("会议不存在");
        }
    }
}
