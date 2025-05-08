package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.example.exception.CustomException;
import org.example.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Resource
    private EmployeeService employeeService;
    @Autowired
    private EmployeeMapper employeeMapper;

    public List<Employee> selectAll(Employee employee) {
        return employeeMapper.selectAll(employee);
    }

    public Employee selectById(Integer id) {
        return employeeMapper.selectById(id);
    }

    public PageInfo<Employee> selectPage(Employee employee,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Employee> list = employeeMapper.selectAll(employee);
        return new PageInfo<>(list);
    }

    public void add(Employee employee) {
        employeeMapper.insert(employee);
    }


    public void update(Employee employee) {
        employeeMapper.updateById(employee);
    }

    public void deleteById(Integer id) {
        employeeMapper.deleteById(id);
    }

    public void deleteByIds(List<Integer> ids) {
        employeeMapper.deleteByIds(ids);
    }

    public void deleteBatchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public Employee login(Account account) {
        String username = account.getUsername();
        Employee dbEmployee= employeeMapper.selectByUsername(username);
        if (dbEmployee == null) {
            //没查询到任何用户
            throw new CustomException("500","账号不存在");
        }
        String password = account.getPassword();
        if (!dbEmployee.getPassword().equals(password)) {
            throw new CustomException("500","账号或密码错误");
        }
        return dbEmployee;

    }
}
