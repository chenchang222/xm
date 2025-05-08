package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.ActivityReview;
import org.example.mapper.ActivityReviewMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityReviewService {

    @Resource
    private ActivityReviewMapper activityReviewMapper;

    /**
     * 添加评价
     */
    public void addReview(ActivityReview activityReview) {
        activityReviewMapper.insert(activityReview);
    }

    /**
     * 查询用户是否已评价某活动
     */
    public boolean hasReviewed(Integer activityId, Integer volunteerId) {
        return activityReviewMapper.countByActivityIdAndVolunteerId(activityId, volunteerId) > 0;
    }

    /**
     * 查询用户的评价列表
     */
    public List<ActivityReview> getReviewsByVolunteerId(Integer volunteerId) {
        return activityReviewMapper.selectByVolunteerId(volunteerId);
    }

    /**
     * 查询活动的评价列表
     */
    public List<ActivityReview> getReviewsByActivityId(Integer activityId) {
        return activityReviewMapper.selectByActivityId(activityId);
    }
    
    /**
     * 查询活动的平均评分
     */
    public double getAverageScoreByActivityId(Integer activityId) {
        return activityReviewMapper.getAverageScoreByActivityId(activityId);
    }
} 