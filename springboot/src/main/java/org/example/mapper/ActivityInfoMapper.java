package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.ActivityInfo;

import java.util.List;

public interface ActivityInfoMapper {
    List<ActivityInfo> selectAll(ActivityInfo activityInfo);

    @Select("select * from infomation where id = #{id}")
    ActivityInfo selectById(Integer id);

    void insert(ActivityInfo activityInfo);

    void updateById(ActivityInfo activityInfo);

    @Delete("delete from infomation where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from infomation where volunteer_id = #{volunteerId}")
    List<ActivityInfo> selectByVolunteerId(Integer volunteerId);
    
    /**
     * 批量更新报名审核状态
     * @param ids ID列表
     * @param status 审核状态
     * @param rejectReason 拒绝理由（如果状态为REJECTED）
     */
    @Update("<script>" +
            "UPDATE infomation SET status = #{status}" +
            "<if test='status == \"REJECTED\" and rejectReason != null'>, reject_reason = #{rejectReason}</if>" +
            " WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status, @Param("rejectReason") String rejectReason);
    
    /**
     * 根据活动ID查询报名记录
     */
    @Select("select * from infomation where activity_id = #{activityId}")
    List<ActivityInfo> selectByActivityId(Integer activityId);

    /**
     * 签到
     */
    @Update("UPDATE infomation SET signed_in = 1 WHERE id = #{id}")
    void signInById(@Param("id") Integer id);

    /**
     * 根据报名id查询签到状态
     */
    @Select("SELECT signed_in FROM infomation WHERE id = #{id}")
    Integer getSignInStatusById(@Param("id") Integer id);

    /**
     * 根据活动和志愿者查询签到状态
     */
    @Select("SELECT signed_in FROM infomation WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId}")
    Integer getSignInStatus(@Param("activityId") Integer activityId, @Param("volunteerId") Integer volunteerId);

    /**
     * 查询某个志愿者是否已报名某个活动
     */
    @org.apache.ibatis.annotations.Select("SELECT COUNT(*) FROM infomation WHERE activity_id = #{activityId} AND volunteer_id = #{volunteerId}")
    int countByActivityAndVolunteer(@Param("activityId") Integer activityId, @Param("volunteerId") Integer volunteerId);
} 