package org.example.controller;

import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.ActivityReviewReply;
import org.example.service.ActivityReviewReplyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activityReviewReply")
public class ActivityReviewReplyController {

    @Resource
    private ActivityReviewReplyService activityReviewReplyService;

    /**
     * 添加回复
     */
    @PostMapping("/add")
    public Result addReply(@RequestBody ActivityReviewReply reply) {
        if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
            return Result.error("400", "回复内容不能为空");
        }
        
        activityReviewReplyService.addReply(reply);
        return Result.success();
    }

    /**
     * 根据评价ID查询回复列表
     */
    @GetMapping("/listByReviewId/{reviewId}")
    public Result listByReviewId(@PathVariable Integer reviewId) {
        List<ActivityReviewReply> list = activityReviewReplyService.getRepliesByReviewId(reviewId);
        return Result.success(list);
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteReply(@PathVariable Integer id) {
        activityReviewReplyService.deleteReply(id);
        return Result.success();
    }
} 