package org.example.controller;

import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.ActivityFavorite;
import org.example.service.ActivityFavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activityFavorite")
public class ActivityFavoriteController {

    @Resource
    private ActivityFavoriteService activityFavoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result addFavorite(@RequestBody ActivityFavorite activityFavorite) {
        activityFavoriteService.addFavorite(activityFavorite);
        return Result.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result removeFavorite(@RequestParam Integer activityId, @RequestParam Integer volunteerId) {
        activityFavoriteService.removeFavorite(activityId, volunteerId);
        return Result.success();
    }

    /**
     * 查询用户是否已收藏某活动
     */
    @GetMapping("/isFavorite")
    public Result isFavorite(@RequestParam Integer activityId, @RequestParam Integer volunteerId) {
        boolean isFavorite = activityFavoriteService.isFavorite(activityId, volunteerId);
        return Result.success(isFavorite);
    }

    /**
     * 查询用户的收藏列表
     */
    @GetMapping("/listByVolunteerId/{volunteerId}")
    public Result listByVolunteerId(@PathVariable Integer volunteerId) {
        List<ActivityFavorite> list = activityFavoriteService.getFavoritesByVolunteerId(volunteerId);
        return Result.success(list);
    }
} 