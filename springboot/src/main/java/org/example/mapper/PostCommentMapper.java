package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.example.entity.PostComment;

import java.util.List;

public interface PostCommentMapper {
    @Select("SELECT * FROM post_comment WHERE post_id = #{postId} ORDER BY created_time ASC")
    List<PostComment> selectByPostId(Integer postId);

    @Select("SELECT * FROM post_comment WHERE id = #{id}")
    PostComment selectById(Integer id);

    void insert(PostComment comment);

    @Delete("DELETE FROM post_comment WHERE id = #{id}")
    void deleteById(Integer id);
} 