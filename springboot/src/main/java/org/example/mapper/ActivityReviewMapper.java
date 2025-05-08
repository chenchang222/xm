package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.ActivityReview;

import java.util.List;

public interface ActivityReviewMapper {

    /**
     * 添加评价
     */
    @Insert("INSERT INTO activity_review(activity_id, activity_name, volunteer_id, volunteer_name, score, content) " +
            "VALUES(#{activityId}, #{activityName}, #{volunteerId}, #{volunteerName}, #{score}, #{content})")
    void insert(ActivityReview activityReview);

    /**
     * 查询用户是否已评价某活动
     */
    @Select("SELECT COUNT(*) FROM activity_review WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId}")
    int countByActivityIdAndVolunteerId(@Param("activityId") Integer activityId, @Param("volunteerId") Integer volunteerId);

    /**
     * 查询用户的评价列表
     */
    @Select("SELECT * FROM activity_review WHERE volunteer_id = #{volunteerId} ORDER BY create_time DESC")
    List<ActivityReview> selectByVolunteerId(Integer volunteerId);

    /**
     * 查询活动的评价列表
     */
    @Select("SELECT * FROM activity_review WHERE activity_id = #{activityId} ORDER BY create_time DESC")
    List<ActivityReview> selectByActivityId(Integer activityId);
    
    /**
     * 查询活动的平均评分
     */
    @Select("SELECT IFNULL(AVG(score), 0) FROM activity_review WHERE activity_id = #{activityId}")
    double getAverageScoreByActivityId(Integer activityId);
} 