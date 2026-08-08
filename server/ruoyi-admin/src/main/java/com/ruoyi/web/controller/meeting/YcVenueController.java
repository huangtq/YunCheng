package com.ruoyi.web.controller.meeting;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YcVenue;
import com.ruoyi.system.service.IYcVenueService;

@RestController
@RequestMapping("/meeting/venue")
public class YcVenueController extends BaseController {
    @Autowired private IYcVenueService ycVenueService;

    @PreAuthorize("@ss.hasPermi('meeting:venue:list')")
    @GetMapping("/list")
    public TableDataInfo list(YcVenue venue) {
        startPage();
        return getDataTable(ycVenueService.selectYcVenueList(venue));
    }
    @PreAuthorize("@ss.hasPermi('meeting:venue:list')")
    @GetMapping("/stats/{activityId}")
    public AjaxResult stats(@PathVariable Long activityId) { return success(ycVenueService.selectVenueStats(activityId)); }
    @PreAuthorize("@ss.hasPermi('meeting:venue:list')")
    @GetMapping("/{venueId}")
    public AjaxResult getInfo(@PathVariable Long venueId) { return success(ycVenueService.selectYcVenueById(venueId)); }
    @PreAuthorize("@ss.hasPermi('meeting:venue:add')")
    @Log(title="Venue", businessType=BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YcVenue venue) { venue.setCreateBy(getUsername()); return toAjax(ycVenueService.insertYcVenue(venue)); }
    @PreAuthorize("@ss.hasPermi('meeting:venue:edit')")
    @Log(title="Venue", businessType=BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YcVenue venue) { venue.setUpdateBy(getUsername()); return toAjax(ycVenueService.updateYcVenue(venue)); }
    @PreAuthorize("@ss.hasPermi('meeting:venue:remove')")
    @Log(title="Venue", businessType=BusinessType.DELETE)
    @DeleteMapping("/{venueIds}")
    public AjaxResult remove(@PathVariable Long[] venueIds) { return toAjax(ycVenueService.deleteYcVenueByIds(venueIds)); }
}