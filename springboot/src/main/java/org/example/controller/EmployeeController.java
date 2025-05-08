//package org.example.controller;
//
//import com.github.pagehelper.PageInfo;
//import jakarta.annotation.Resource;
//import org.example.common.Result;
//import org.example.entity.Employee;
//import org.example.service.EmployeeService;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/employee")
//public class EmployeeController {
//    @Resource
//    private EmployeeService employeeService;
//
////    新增数据
//    @PostMapping("/add")
//    public Result add(@RequestBody Employee employee) {
//        employeeService.add(employee);
//        return Result.success();
//    }
//
//    //    更新数据
//    @PutMapping("/update")
//    public Result update(@RequestBody Employee employee) {
//        employeeService.update(employee);
//        return Result.success();
//    }
//    //    删除数据
//    @DeleteMapping("/deleteById/{id}")
//    public Result deleteById(@PathVariable Integer id) {
//        employeeService.deleteById(id);
//        return Result.success();
//    }
////查询所有数据
//    @GetMapping("/selectAll")
//    public Result selectAll(Employee employee) {
//        List<Employee> list=employeeService.selectAll(employee);
//        return Result.success(list);
//    }
////    查询单个数据
//    @GetMapping("/selectById/{id}")
//    public Result selectById(@PathVariable Integer id) {
//        Employee employee=employeeService.selectById(id);
//        return Result.success(employee);
//    }
//    //    分页查询
//    @GetMapping("/selectPage")
//    public Result selectPage(Employee employee,
//            @RequestParam(defaultValue = "1") Integer pageNum,
//                             @RequestParam(defaultValue = "10") Integer pageSize) {
//        PageInfo<Employee>pageInfo=employeeService.selectPage(employee,pageNum,pageSize);
//        return Result.success(pageInfo);
//    }
//}
package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    //     新增数据
    @PostMapping("/add")
    public Result add(@RequestBody Employee employee) {
        employeeService.add(employee);
        return Result.success();
    }



    // 更新数据
    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
        employeeService.update(employee);
        return Result.success();
    }

    // 单个删除（保留接口）
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        employeeService.deleteById(id);
        return Result.success();
    }

    //批量删除
    @DeleteMapping("/deleteBatchDelete")
    public Result deleteBatchDelete(@RequestBody List<Integer> ids) {
        employeeService.deleteBatchDelete(ids);
        return Result.success();
    }

    // 查询所有数据（带条件）
    @GetMapping("/selectAll")
    public Result selectAll(Employee employee) {  // 支持条件查询
        List<Employee> list = employeeService.selectAll(employee);
        return Result.success(list);
    }

    // 查询单个数据
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }

    // 分页查询（带条件）
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Employee employee  // 接收查询条件对象
    ) {
        PageInfo<Employee> pageInfo = employeeService.selectPage(employee, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}