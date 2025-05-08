package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.ActivityInfo;
import org.example.entity.Notification;
import org.example.mapper.ActivityInfoMapper;
import org.example.mapper.NotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    
    @Resource
    private NotificationMapper notificationMapper;
    
    @Resource
    private ActivityInfoMapper activityInfoMapper;
    
    /**
     * 添加单个通知
     */
    public void add(Notification notification) {
        notification.setCreateTime(new Date());
        notification.setIsRead(false);
        notificationMapper.insert(notification);
    }
    
    /**
     * 发送通知给指定活动的所有参与者
     * @param notification 通知信息（包含活动ID、标题、内容、类型等）
     * @param creatorId 创建者ID
     * @param creatorName 创建者名称
     */
    @Transactional
    public void sendToActivityParticipants(Notification notification, Integer creatorId, String creatorName) {
        Integer activityId = notification.getActivityId();
        String activityName = notification.getActivityName();
        
        // 查询该活动的所有参与者
        ActivityInfo query = new ActivityInfo();
        query.setActivityId(activityId);
        List<ActivityInfo> participants = activityInfoMapper.selectAll(query);
        
        // 为每个参与者创建一条通知
        for (ActivityInfo participant : participants) {
            Notification newNotification = new Notification();
            newNotification.setTitle(notification.getTitle());
            newNotification.setContent(notification.getContent());
            newNotification.setType(notification.getType());
            newNotification.setActivityId(activityId);
            newNotification.setActivityName(activityName);
            newNotification.setReceiverId(participant.getVolunteerId());
            newNotification.setReceiverName(participant.getVolunteerName());
            newNotification.setCreatorId(creatorId);
            newNotification.setCreatorName(creatorName);
            newNotification.setCreateTime(new Date());
            newNotification.setIsRead(false);
            
            notificationMapper.insert(newNotification);
        }
    }
    
    /**
     * 将通知标记为已读
     */
    public void markAsRead(Integer id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setIsRead(true);
        notification.setReadTime(new Date());
        notificationMapper.updateReadStatus(notification);
    }
    
    /**
     * 根据ID删除通知
     */
    public void deleteById(Integer id) {
        notificationMapper.deleteById(id);
    }
    
    /**
     * 根据ID查询通知
     */
    public Notification selectById(Integer id) {
        return notificationMapper.selectById(id);
    }
    
    /**
     * 查询用户的所有通知
     */
    public List<Notification> selectByReceiverId(Integer receiverId) {
        return notificationMapper.selectByReceiverId(receiverId);
    }
    
    /**
     * 查询活动的所有通知
     */
    public List<Notification> selectByActivityId(Integer activityId) {
        return notificationMapper.selectByActivityId(activityId);
    }
    
    /**
     * 查询所有通知（带条件）
     */
    public List<Notification> selectAll(Notification notification) {
        return notificationMapper.selectAll(notification);
    }
    
    /**
     * 分页查询通知（带条件）
     */
    public PageInfo<Notification> selectPage(Notification notification, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notification> list = notificationMapper.selectAll(notification);
        return new PageInfo<>(list);
    }
    
    /**
     * 获取用户未读通知数量
     */
    public Integer countUnreadByReceiverId(Integer receiverId) {
        return notificationMapper.countUnreadByReceiverId(receiverId);
    }
    
    public List<Notification> selectDistinctByCreatorId(Integer creatorId) {
        return notificationMapper.selectDistinctByCreatorId(creatorId);
    }
    
    public List<Map<String, Object>> selectReceiversForNotification(String title, String content, Integer activityId, Integer creatorId) {
        return notificationMapper.selectReceiversForNotification(title, content, activityId, creatorId);
    }
    
    public void update(Notification notification) {
        notificationMapper.update(notification);
    }
} 