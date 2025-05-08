package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Activity;
import org.example.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;

    // 新增活动
    @PostMapping("/add")
    public Result add(@RequestBody Activity activity) {
        activityService.add(activity);
        return Result.success();
    }

    // 更新活动
    @PutMapping("/update")
    public Result update(@RequestBody Activity activity) {
        activityService.update(activity);
        return Result.success();
    }

    // 删除活动
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        activityService.deleteById(id);
        return Result.success();
    }

    // 批量删除活动
    @DeleteMapping("/deleteBatchDelete")
    public Result deleteBatchDelete(@RequestBody List<Integer> ids) {
        activityService.deleteBatchDelete(ids);
        return Result.success();
    }

    // 查询所有活动（带条件）
    @GetMapping("/selectAll")
    public Result selectAll(Activity activity) {
        List<Activity> list = activityService.selectAll(activity);
        return Result.success(list);
    }

    // 查询单个活动
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Activity activity = activityService.selectById(id);
        return Result.success(activity);
    }

    // 分页查询活动（带条件）
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Activity activity
    ) {
        PageInfo<Activity> pageInfo = activityService.selectPage(activity, pageNum, pageSize);
        return Result.success(pageInfo);
    }
} 