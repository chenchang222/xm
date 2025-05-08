package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.Activity;
import org.example.entity.ActivityInfo;
import org.example.mapper.ActivityInfoMapper;
import org.example.mapper.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityInfoService {
    @Resource
    private ActivityInfoMapper activityInfoMapper;
    
    @Resource
    private ActivityMapper activityMapper;

    public List<ActivityInfo> selectAll(ActivityInfo activityInfo) {
        return activityInfoMapper.selectAll(activityInfo);
    }

    public ActivityInfo selectById(Integer id) {
        return activityInfoMapper.selectById(id);
    }

    public PageInfo<ActivityInfo> selectPage(ActivityInfo activityInfo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ActivityInfo> list = activityInfoMapper.selectAll(activityInfo);
        return new PageInfo<>(list);
    }

    public void add(ActivityInfo activityInfo) {
        // 检查活动状态
        Activity activity = activityMapper.selectById(activityInfo.getActivityId());
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!"未开始".equals(activity.getStatus())) {
            throw new RuntimeException("只能报名未开始的活动");
        }
        // 检查是否重复报名
        if (activityInfoMapper.countByActivityAndVolunteer(activityInfo.getActivityId(), activityInfo.getVolunteerId()) > 0) {
            throw new RuntimeException("您已报名该活动，请勿重复报名");
        }
        // 设置默认状态为待审核
        if (activityInfo.getStatus() == null) {
            activityInfo.setStatus("PENDING");
        }
        activityInfoMapper.insert(activityInfo);
    }

    public void update(ActivityInfo activityInfo) {
        activityInfoMapper.updateById(activityInfo);
    }

    public void deleteById(Integer id) {
        activityInfoMapper.deleteById(id);
    }

    public List<ActivityInfo> selectByVolunteerId(Integer volunteerId) {
        return activityInfoMapper.selectByVolunteerId(volunteerId);
    }
    
    /**
     * 根据活动ID查询报名记录
     */
    public List<ActivityInfo> selectByActivityId(Integer activityId) {
        return activityInfoMapper.selectByActivityId(activityId);
    }
    
    /**
     * 批量通过报名申请
     * @param ids 报名ID列表
     */
    @Transactional
    public void batchApprove(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        activityInfoMapper.batchUpdateStatus(ids, "APPROVED", null);
    }
    
    /**
     * 批量拒绝报名申请
     * @param ids 报名ID列表
     * @param rejectReason 拒绝理由
     */
    @Transactional
    public void batchReject(List<Integer> ids, String rejectReason) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        activityInfoMapper.batchUpdateStatus(ids, "REJECTED", rejectReason);
    }

    /**
     * 签到
     */
    public void signIn(Integer id) {
        activityInfoMapper.signInById(id);
    }

    /**
     * 查询签到状态 by id
     */
    public Integer getSignInStatusById(Integer id) {
        return activityInfoMapper.getSignInStatusById(id);
    }

    /**
     * 查询签到状态 by activityId+volunteerId
     */
    public Integer getSignInStatus(Integer activityId, Integer volunteerId) {
        return activityInfoMapper.getSignInStatus(activityId, volunteerId);
    }
} 