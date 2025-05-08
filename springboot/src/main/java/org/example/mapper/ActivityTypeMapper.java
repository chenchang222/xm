package org.example.mapper;

import org.apache.ibatis.annotations.Select;
import org.example.entity.ActivityType;

import java.util.List;

public interface ActivityTypeMapper {
    @Select("select * from activity_type")
    List<ActivityType> selectAll();

    @Select("select * from activity_type where id = #{id}")
    ActivityType selectById(Integer id);
} 