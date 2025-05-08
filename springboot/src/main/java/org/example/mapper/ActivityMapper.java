package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Activity;

import java.util.List;

public interface ActivityMapper {
    List<Activity> selectAll(Activity activity);

    @Select("select * from activity where id = #{id}")
    Activity selectById(Integer id);

    void insert(Activity activity);

    void updateById(Activity activity);

    @Delete("delete from activity where id = #{id}")
    void deleteById(Integer id);
} 