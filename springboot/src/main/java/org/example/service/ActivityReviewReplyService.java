package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.ActivityReviewReply;
import org.example.mapper.ActivityReviewReplyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityReviewReplyService {

    @Resource
    private ActivityReviewReplyMapper activityReviewReplyMapper;

    /**
     * 添加回复
     */
    public void addReply(ActivityReviewReply reply) {
        activityReviewReplyMapper.insert(reply);
    }

    /**
     * 根据评价ID查询回复列表
     */
    public List<ActivityReviewReply> getRepliesByReviewId(Integer reviewId) {
        return activityReviewReplyMapper.selectByReviewId(reviewId);
    }

    /**
     * 删除回复
     */
    public void deleteReply(Integer id) {
        activityReviewReplyMapper.deleteById(id);
    }

    /**
     * 根据评价ID删除所有回复
     */
    public void deleteRepliesByReviewId(Integer reviewId) {
        activityReviewReplyMapper.deleteByReviewId(reviewId);
    }
} 