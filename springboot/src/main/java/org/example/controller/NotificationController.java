package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Notification;
import org.example.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    
    @Resource
    private NotificationService notificationService;
    
    /**
     * 添加单个通知
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notification notification) {
        notificationService.add(notification);
        return Result.success();
    }
    
    /**
     * 向活动参与者发送提醒通知
     */
    @PostMapping("/sendToActivityParticipants")
    public Result sendToActivityParticipants(@RequestBody Notification notification) {
        Integer creatorId = notification.getCreatorId();
        String creatorName = notification.getCreatorName();
        
        if (creatorId == null || creatorName == null) {
            return Result.error("400", "创建者信息不能为空");
        }
        
        if (notification.getActivityId() == null) {
            return Result.error("400", "活动ID不能为空");
        }
        
        notificationService.sendToActivityParticipants(notification, creatorId, creatorName);
        return Result.success();
    }
    
    /**
     * 将通知标记为已读
     */
    @PutMapping("/markAsRead/{id}")
    public Result markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return Result.success();
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        notificationService.deleteById(id);
        return Result.success();
    }
    
    /**
     * 根据ID查询通知
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notification notification = notificationService.selectById(id);
        return Result.success(notification);
    }
    
    /**
     * 查询用户的所有通知
     */
    @GetMapping("/selectByReceiverId/{receiverId}")
    public Result selectByReceiverId(@PathVariable Integer receiverId) {
        List<Notification> list = notificationService.selectByReceiverId(receiverId);
        return Result.success(list);
    }
    
    /**
     * 查询用户未读通知数量
     */
    @GetMapping("/countUnread/{receiverId}")
    public Result countUnread(@PathVariable Integer receiverId) {
        Integer count = notificationService.countUnreadByReceiverId(receiverId);
        return Result.success(Map.of("count", count));
    }
    
    /**
     * 分页查询通知（带条件）
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Notification notification
    ) {
        PageInfo<Notification> pageInfo = notificationService.selectPage(notification, pageNum, pageSize);
        return Result.success(pageInfo);
    }
} 