package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.ActivityInfo;
import org.example.service.ActivityInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activityInfo")
public class ActivityInfoController {
    @Resource
    private ActivityInfoService activityInfoService;

    // 新增报名信息
    @PostMapping("/add")
    public Result add(@RequestBody ActivityInfo activityInfo) {
        activityInfoService.add(activityInfo);
        return Result.success();
    }

    // 更新报名信息
    @PutMapping("/update")
    public Result update(@RequestBody ActivityInfo activityInfo) {
        activityInfoService.update(activityInfo);
        return Result.success();
    }

    // 删除报名信息
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        activityInfoService.deleteById(id);
        return Result.success();
    }

    // 查询所有报名信息（带条件）
    @GetMapping("/selectAll")
    public Result selectAll(ActivityInfo activityInfo) {
        List<ActivityInfo> list = activityInfoService.selectAll(activityInfo);
        return Result.success(list);
    }

    // 查询单个报名信息
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ActivityInfo activityInfo = activityInfoService.selectById(id);
        return Result.success(activityInfo);
    }

    // 分页查询报名信息（带条件）
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            ActivityInfo activityInfo
    ) {
        PageInfo<ActivityInfo> pageInfo = activityInfoService.selectPage(activityInfo, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    // 查询用户的报名记录
    @GetMapping("/selectByVolunteerId/{volunteerId}")
    public Result selectByVolunteerId(@PathVariable Integer volunteerId) {
        List<ActivityInfo> list = activityInfoService.selectByVolunteerId(volunteerId);
        return Result.success(list);
    }

    // 查询活动的报名记录
    @GetMapping("/selectByActivityId/{activityId}")
    public Result selectByActivityId(@PathVariable Integer activityId) {
        List<ActivityInfo> list = activityInfoService.selectByActivityId(activityId);
        return Result.success(list);
    }

    // 批量通过报名申请
    @PostMapping("/batchApprove")
    public Result batchApprove(@RequestBody Map<String, List<Integer>> params) {
        List<Integer> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("400", "请选择要审核的报名记录");
        }
        activityInfoService.batchApprove(ids);
        return Result.success();
    }

    // 批量拒绝报名申请
    @PostMapping("/batchReject")
    public Result batchReject(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) params.get("ids");
        String rejectReason = (String) params.get("rejectReason");
        
        if (ids == null || ids.isEmpty()) {
            return Result.error("400", "请选择要审核的报名记录");
        }
        
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            return Result.error("400", "请填写拒绝理由");
        }
        
        activityInfoService.batchReject(ids, rejectReason);
        return Result.success();
    }

    /**
     * 活动签到
     */
    @PostMapping("/signIn/{id}")
    public Result signIn(@PathVariable Integer id) {
        activityInfoService.signIn(id);
        return Result.success();
    }

    /**
     * 查询签到状态 by id
     */
    @GetMapping("/signInStatus/{id}")
    public Result getSignInStatusById(@PathVariable Integer id) {
        Integer status = activityInfoService.getSignInStatusById(id);
        return Result.success(status);
    }

    /**
     * 查询签到状态 by activityId+volunteerId
     */
    @GetMapping("/signInStatus")
    public Result getSignInStatus(@RequestParam Integer activityId, @RequestParam Integer volunteerId) {
        Integer status = activityInfoService.getSignInStatus(activityId, volunteerId);
        return Result.success(status);
    }
} 