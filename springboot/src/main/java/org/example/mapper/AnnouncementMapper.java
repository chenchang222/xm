package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Announcement;

import java.util.List;

public interface AnnouncementMapper {
    
    @Insert("INSERT INTO announcement(title, content, type, creator_id, creator_name) " +
            "VALUES(#{title}, #{content}, #{type}, #{creatorId}, #{creatorName})")
    void insert(Announcement announcement);
    
    @Update("UPDATE announcement SET title=#{title}, content=#{content}, type=#{type} " +
            "WHERE id=#{id}")
    void update(Announcement announcement);
    
    @Delete("DELETE FROM announcement WHERE id=#{id}")
    void deleteById(Integer id);
    
    @Select("SELECT * FROM announcement WHERE id=#{id}")
    Announcement selectById(Integer id);
    
    @Select("<script>" +
            "SELECT * FROM announcement " +
            "<where>" +
            "  <if test='title != null and title != \"\"'>AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "  <if test='type != null and type != \"\"'>AND type = #{type} </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Announcement> selectAll(Announcement announcement);
} 