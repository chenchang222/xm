package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.Account;
import org.example.entity.Volunteer;
import org.example.exception.CustomException;
import org.example.mapper.VolunteerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerService {
    @Resource
    private VolunteerService volunteerService;
    @Autowired
    private VolunteerMapper volunteerMapper;

    public List<Volunteer> selectAll(Volunteer volunteer) {
        return volunteerMapper.selectAll(volunteer);
    }

    public Volunteer selectById(Integer id) {
        return volunteerMapper.selectById(id);
    }

    public PageInfo<Volunteer> selectPage(Volunteer volunteer,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Volunteer> list = volunteerMapper.selectAll(volunteer);
        return new PageInfo<>(list);
    }

    public void add(Volunteer volunteer) {
        volunteerMapper.insert(volunteer);
    }


    public void update(Volunteer volunteer) {
        volunteerMapper.updateById(volunteer);
    }

    public void deleteById(Integer id) {
        volunteerMapper.deleteById(id);
    }

    public void deleteByIds(List<Integer> ids) {
        volunteerMapper.deleteByIds(ids);
    }

    public void deleteBatchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public Volunteer login(Account account) {
        String username = account.getUsername();
        Volunteer dbVolunteer= volunteerMapper.selectByUsername(username);
        if (dbVolunteer == null) {
            //没查询到任何用户
            throw new CustomException("500","账号不存在");
        }
        String password = account.getPassword();
        if (!dbVolunteer.getPassword().equals(password)) {
            throw new CustomException("500","账号或密码错误");
        }
        return dbVolunteer;

    }
}
