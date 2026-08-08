package com.ruoyi.web.controller.meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcHotelRoom;
import com.ruoyi.system.service.IYcHotelRoomService;
@RestController
@RequestMapping("/meeting/hotel/room")
public class YcHotelRoomController extends BaseController {
    @Autowired private IYcHotelRoomService service;
    @PreAuthorize("@ss.hasPermi('meeting:room:list')") @GetMapping("/list")
    public TableDataInfo list(YcHotelRoom q){ startPage(); return getDataTable(service.selectYcHotelRoomList(q)); }
    @PreAuthorize("@ss.hasPermi('meeting:room:list')") @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId){ return success(service.selectRoomStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:room:list')") @GetMapping("/{roomId}")
    public AjaxResult getInfo(@PathVariable Long roomId){ return success(service.selectYcHotelRoomById(roomId)); }
    @PreAuthorize("@ss.hasPermi('meeting:room:add')") @Log(title="HotelRoom", businessType=BusinessType.INSERT) @PostMapping
    public AjaxResult add(@RequestBody YcHotelRoom e){ e.setCreateBy(getUsername()); return toAjax(service.insertYcHotelRoom(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:room:edit')") @Log(title="HotelRoom", businessType=BusinessType.UPDATE) @PutMapping
    public AjaxResult edit(@RequestBody YcHotelRoom e){ e.setUpdateBy(getUsername()); return toAjax(service.updateYcHotelRoom(e)); }
    @PreAuthorize("@ss.hasPermi('meeting:room:remove')") @Log(title="HotelRoom", businessType=BusinessType.DELETE) @DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable Long[] roomIds){ return toAjax(service.deleteYcHotelRoomByIds(roomIds)); }
}