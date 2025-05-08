package org.example.controller;

import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Account;
import org.example.entity.Employee;
import org.example.exception.CustomException;
import org.example.service.EmployeeService;
import org.example.service.VolunteerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WebController     {

    @Resource
    private EmployeeService employeeService;
    @Resource
    public VolunteerService volunteerService;


    @GetMapping("/hello")
    public  String hello() {
        return "hello";
    }

    //管理员或志愿者登录
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account result = null;
        if("EMP".equals(account.getRole())) {
             result= employeeService.login(account);
        }else if("VOL".equals(account.getRole())) {
            result= volunteerService.login(account);
        }
        return Result.success(result);
    }
}
