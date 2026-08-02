package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YcActivity;
import com.ruoyi.system.mapper.YcActivityMapper;
import com.ruoyi.system.service.IYcActivityService;

/**
 * 会议活动 服务层实现
 *
 * @author ruoyi
 */
@Service
public class YcActivityServiceImpl implements IYcActivityService
{
    @Autowired
    private YcActivityMapper ycActivityMapper;

    @Override
    public YcActivity selectYcActivityById(Long activityId)
    {
        return ycActivityMapper.selectYcActivityById(activityId);
    }

    @Override
    public List<YcActivity> selectYcActivityList(YcActivity ycActivity)
    {
        return ycActivityMapper.selectYcActivityList(ycActivity);
    }

    @Override
    public boolean checkActivityCodeUnique(YcActivity ycActivity)
    {
        Long activityId = StringUtils.isNull(ycActivity.getActivityId()) ? -1L : ycActivity.getActivityId();
        YcActivity info = ycActivityMapper.checkActivityCodeUnique(ycActivity.getActivityCode());
        if (StringUtils.isNotNull(info) && info.getActivityId().longValue() != activityId.longValue())
        {
            return false;
        }
        return true;
    }

    @Override
    public String generateActivityCode()
    {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        for (int i = 0; i < 20; i++)
        {
            int random = ThreadLocalRandom.current().nextInt(10, 100);
            String code = datePart + random;
            YcActivity exist = ycActivityMapper.checkActivityCodeUnique(code);
            if (StringUtils.isNull(exist))
            {
                return code;
            }
        }
        return datePart + System.currentTimeMillis() % 10000;
    }

    @Override
    public int insertYcActivity(YcActivity ycActivity)
    {
        if (StringUtils.isEmpty(ycActivity.getActivityCode()))
        {
            ycActivity.setActivityCode(generateActivityCode());
        }
        if (StringUtils.isEmpty(ycActivity.getIsShow()))
        {
            ycActivity.setIsShow("1");
        }
        if (StringUtils.isEmpty(ycActivity.getIsHot()))
        {
            ycActivity.setIsHot("0");
        }
        if (StringUtils.isEmpty(ycActivity.getIsHome()))
        {
            ycActivity.setIsHome("0");
        }
        if (ycActivity.getRegisterCount() == null)
        {
            ycActivity.setRegisterCount(0);
        }
        if (ycActivity.getVisitCount() == null)
        {
            ycActivity.setVisitCount(0);
        }
        if (ycActivity.getViewCount() == null)
        {
            ycActivity.setViewCount(0);
        }
        return ycActivityMapper.insertYcActivity(ycActivity);
    }

    @Override
    public int updateYcActivity(YcActivity ycActivity)
    {
        return ycActivityMapper.updateYcActivity(ycActivity);
    }

    @Override
    public int deleteYcActivityByIds(Long[] activityIds)
    {
        return ycActivityMapper.deleteYcActivityByIds(activityIds);
    }
}