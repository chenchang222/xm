package org.example.controller;

import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.ActivityReview;
import org.example.service.ActivityReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activityReview")
public class ActivityReviewController {

    @Resource
    private ActivityReviewService activityReviewService;

    /**
     * 添加评价
     */
    @PostMapping("/add")
    public Result addReview(@RequestBody ActivityReview activityReview) {
        // 验证评分范围
        if (activityReview.getScore() < 1 || activityReview.getScore() > 5) {
            return Result.error("400", "评分必须在1-5之间");
        }
        
        // 验证用户是否已评价
        if (activityReviewService.hasReviewed(activityReview.getActivityId(), activityReview.getVolunteerId())) {
            return Result.error("400", "您已经评价过该活动了");
        }
        
        activityReviewService.addReview(activityReview);
        return Result.success();
    }

    /**
     * 查询用户是否已评价某活动
     */
    @GetMapping("/hasReviewed")
    public Result hasReviewed(@RequestParam Integer activityId, @RequestParam Integer volunteerId) {
        boolean hasReviewed = activityReviewService.hasReviewed(activityId, volunteerId);
        return Result.success(hasReviewed);
    }

    /**
     * 查询用户的评价列表
     */
    @GetMapping("/listByVolunteerId/{volunteerId}")
    public Result listByVolunteerId(@PathVariable Integer volunteerId) {
        List<ActivityReview> list = activityReviewService.getReviewsByVolunteerId(volunteerId);
        return Result.success(list);
    }

    /**
     * 查询活动的评价列表
     */
    @GetMapping("/listByActivityId/{activityId}")
    public Result listByActivityId(@PathVariable Integer activityId) {
        List<ActivityReview> list = activityReviewService.getReviewsByActivityId(activityId);
        return Result.success(list);
    }
    
    /**
     * 查询活动的平均评分
     */
    @GetMapping("/averageScore/{activityId}")
    public Result getAverageScore(@PathVariable Integer activityId) {
        double score = activityReviewService.getAverageScoreByActivityId(activityId);
        return Result.success(score);
    }
} 