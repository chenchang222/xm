package org.example.controller;

import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.ActivityType;
import org.example.service.ActivityTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activityType")
public class ActivityTypeController {
    @Resource
    private ActivityTypeService activityTypeService;

    // 查询所有活动类型
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<ActivityType> list = activityTypeService.selectAll();
        return Result.success(list);
    }

    // 根据ID查询活动类型
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ActivityType activityType = activityTypeService.selectById(id);
        return Result.success(activityType);
    }
} 