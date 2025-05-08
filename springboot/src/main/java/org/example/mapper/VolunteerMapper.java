package org.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Volunteer;

import java.util.List;

public interface VolunteerMapper {
    List<Volunteer> selectAll(Volunteer volunteer);

    @Select("select * from volunteer where id =#{id}")
    Volunteer selectById(Integer id);

    void insert(Volunteer volunteer);

    void updateById(Volunteer volunteer);

    @Delete("delete from volunteer where id = #{id}")
    void deleteById(Integer id);
    void deleteByIds(List<Integer> ids);

    @Select("select * from volunteer where username =#{username}")
    Volunteer selectByUsername(String username);
}
