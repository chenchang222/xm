//package org.example.controller;
//
//import com.github.pagehelper.PageInfo;
//import jakarta.annotation.Resource;
//import org.example.common.Result;
//import org.example.entity.Volunteer;
//import org.example.service.VolunteerService;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/volunteer")
//public class VolunteerController {
//    @Resource
//    private VolunteerService volunteerService;
//
////    新增数据
//    @PostMapping("/add")
//    public Result add(@RequestBody Volunteer volunteer) {
//        volunteerService.add(volunteer);
//        return Result.success();
//    }
//
//    //    更新数据
//    @PutMapping("/update")
//    public Result update(@RequestBody Volunteer volunteer) {
//        volunteerService.update(volunteer);
//        return Result.success();
//    }
//    //    删除数据
//    @DeleteMapping("/deleteById/{id}")
//    public Result deleteById(@PathVariable Integer id) {
//        volunteerService.deleteById(id);
//        return Result.success();
//    }
////查询所有数据
//    @GetMapping("/selectAll")
//    public Result selectAll(Volunteer volunteer) {
//        List<Volunteer> list=volunteerService.selectAll(volunteer);
//        return Result.success(list);
//    }
////    查询单个数据
//    @GetMapping("/selectById/{id}")
//    public Result selectById(@PathVariable Integer id) {
//        Volunteer volunteer=volunteerService.selectById(id);
//        return Result.success(volunteer);
//    }
//    //    分页查询
//    @GetMapping("/selectPage")
//    public Result selectPage(Volunteer volunteer,
//            @RequestParam(defaultValue = "1") Integer pageNum,
//                             @RequestParam(defaultValue = "10") Integer pageSize) {
//        PageInfo<Volunteer>pageInfo=volunteerService.selectPage(volunteer,pageNum,pageSize);
//        return Result.success(pageInfo);
//    }
//}
package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Volunteer;
import org.example.service.VolunteerService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {
    @Resource
    private VolunteerService volunteerService;

    //     新增数据
    @PostMapping("/add")
    public Result add(@RequestBody Volunteer volunteer) {
        volunteerService.add(volunteer);
        return Result.success();
    }



    // 更新数据
    @PutMapping("/update")
    public Result update(@RequestBody Volunteer volunteer) {
        volunteerService.update(volunteer);
        return Result.success();
    }

    // 单个删除（保留接口）
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        volunteerService.deleteById(id);
        return Result.success();
    }

    //批量删除
    @DeleteMapping("/deleteBatchDelete")
    public Result deleteBatchDelete(@RequestBody List<Integer> ids) {
        volunteerService.deleteBatchDelete(ids);
        return Result.success();
    }

    // 查询所有数据（带条件）
    @GetMapping("/selectAll")
    public Result selectAll(Volunteer volunteer) {  // 支持条件查询
        List<Volunteer> list = volunteerService.selectAll(volunteer);
        return Result.success(list);
    }

    // 查询单个数据
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Volunteer volunteer = volunteerService.selectById(id);
        return Result.success(volunteer);
    }

    // 分页查询（带条件）
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Volunteer volunteer  // 接收查询条件对象
    ) {
        PageInfo<Volunteer> pageInfo = volunteerService.selectPage(volunteer, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}