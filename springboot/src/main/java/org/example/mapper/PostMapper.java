package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Post;

import java.util.List;

public interface PostMapper {
    List<Post> selectAll(Post post);

    @Select("SELECT * FROM post WHERE id = #{id}")
    Post selectById(Integer id);

    void insert(Post post);

    void updateById(Post post);

    @Delete("DELETE FROM post WHERE id = #{id}")
    void deleteById(Integer id);
    
    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);
    
    @Update("UPDATE post SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Integer id);
    
    @Update("UPDATE post SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    void decrementLikeCount(Integer id);
    
    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(Integer id);
    
    @Select("SELECT * FROM post WHERE author_id = #{authorId} ORDER BY created_time DESC")
    List<Post> selectByAuthorId(Integer authorId);
} 