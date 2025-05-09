package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.ActivityReviewReply;

import java.util.List;

public interface ActivityReviewReplyMapper {

    /**
     * 添加回复
     */
    @Insert("INSERT INTO activity_review_reply(review_id, content, replyer_id, replyer_name, create_time) " +
            "VALUES(#{reviewId}, #{content}, #{replyerId}, #{replyerName}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ActivityReviewReply reply);

    /**
     * 根据评价ID查询回复列表
     */
    @Select("SELECT * FROM activity_review_reply WHERE review_id = #{reviewId} ORDER BY create_time DESC")
    List<ActivityReviewReply> selectByReviewId(Integer reviewId);

    /**
     * 删除回复
     */
    @Delete("DELETE FROM activity_review_reply WHERE id = #{id}")
    void deleteById(Integer id);

    /**
     * 根据评价ID删除所有回复
     */
    @Delete("DELETE FROM activity_review_reply WHERE review_id = #{reviewId}")
    void deleteByReviewId(Integer reviewId);
} 