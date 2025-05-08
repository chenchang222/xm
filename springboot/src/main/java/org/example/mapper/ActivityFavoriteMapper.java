package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.ActivityFavorite;

import java.util.List;

public interface ActivityFavoriteMapper {

    /**
     * 添加收藏
     */
    @Insert("INSERT INTO activity_favorite(activity_id, volunteer_id) VALUES(#{activityId}, #{volunteerId})")
    void insert(ActivityFavorite activityFavorite);

    /**
     * 取消收藏
     */
    @Delete("DELETE FROM activity_favorite WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId}")
    void delete(@Param("activityId") Integer activityId, @Param("volunteerId") Integer volunteerId);

    /**
     * 查询用户是否已收藏某活动
     */
    @Select("SELECT COUNT(*) FROM activity_favorite WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId}")
    int countByActivityIdAndVolunteerId(@Param("activityId") Integer activityId, @Param("volunteerId") Integer volunteerId);

    /**
     * 查询用户的收藏列表
     */
    @Select("SELECT f.*, a.title as activity_name, a.type as activity_type, a.location as activity_location, " +
            "a.start_time as activity_start_time, a.end_time as activity_end_time " +
            "FROM activity_favorite f " +
            "LEFT JOIN activity a ON f.activity_id = a.id " +
            "WHERE f.volunteer_id = #{volunteerId} " +
            "ORDER BY f.create_time DESC")
    List<ActivityFavorite> selectByVolunteerId(Integer volunteerId);
} 