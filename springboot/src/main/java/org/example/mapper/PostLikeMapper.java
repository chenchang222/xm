package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.PostLike;

import java.util.List;

public interface PostLikeMapper {
    void insert(PostLike postLike);

    @Delete("DELETE FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    void deleteByPostIdAndUserId(@Param("postId") Integer postId, @Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int checkLiked(@Param("postId") Integer postId, @Param("userId") Integer userId);
    
    @Select("SELECT * FROM post_like WHERE post_id = #{postId}")
    List<PostLike> selectByPostId(Integer postId);
} 