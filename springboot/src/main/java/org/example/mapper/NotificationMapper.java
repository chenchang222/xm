package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationMapper {
    
    @Insert("INSERT INTO notification(title, content, type, activity_id, activity_name, " +
            "receiver_id, receiver_name, creator_id, creator_name) " +
            "VALUES(#{title}, #{content}, #{type}, #{activityId}, #{activityName}, " +
            "#{receiverId}, #{receiverName}, #{creatorId}, #{creatorName})")
    void insert(Notification notification);
    
    @Update("UPDATE notification SET is_read=#{isRead}, read_time=#{readTime} WHERE id=#{id}")
    void updateReadStatus(Notification notification);
    
    @Delete("DELETE FROM notification WHERE id=#{id}")
    void deleteById(Integer id);
    
    @Select("SELECT * FROM notification WHERE id=#{id}")
    Notification selectById(Integer id);
    
    @Select("SELECT * FROM notification WHERE receiver_id=#{receiverId} ORDER BY create_time DESC")
    List<Notification> selectByReceiverId(Integer receiverId);
    
    @Select("SELECT * FROM notification WHERE activity_id=#{activityId} ORDER BY create_time DESC")
    List<Notification> selectByActivityId(Integer activityId);
    
    @Select("<script>" +
            "SELECT * FROM notification " +
            "<where>" +
            "  <if test='receiverId != null'>AND receiver_id = #{receiverId} </if>" +
            "  <if test='activityId != null'>AND activity_id = #{activityId} </if>" +
            "  <if test='isRead != null'>AND is_read = #{isRead} </if>" +
            "  <if test='type != null and type != \"\"'>AND type = #{type} </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Notification> selectAll(Notification notification);
    
    @Select("SELECT COUNT(*) FROM notification WHERE receiver_id=#{receiverId} AND is_read=0")
    Integer countUnreadByReceiverId(Integer receiverId);
    
    @Select("SELECT MIN(id) as id, title, content, type, activity_id, activity_name, creator_id, creator_name, create_time " +
            "FROM notification WHERE creator_id=#{creatorId} " +
            "GROUP BY title, content, type, activity_id, activity_name, creator_id, creator_name, create_time " +
            "ORDER BY create_time DESC")
    List<Notification> selectDistinctByCreatorId(Integer creatorId);
    
    @Select("SELECT receiver_id, receiver_name FROM notification WHERE title=#{title} AND content=#{content} AND activity_id=#{activityId} AND creator_id=#{creatorId}")
    List<Map<String, Object>> selectReceiversForNotification(String title, String content, Integer activityId, Integer creatorId);
    
    @Update("UPDATE notification SET title=#{title}, content=#{content}, type=#{type} WHERE id=#{id}")
    void update(Notification notification);
} 