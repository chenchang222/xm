package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Announcement;
import org.example.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    
    @Resource
    private AnnouncementService announcementService;
    
    /**
     * 添加系统公告
     */
    @PostMapping("/add")
    public Result add(@RequestBody Announcement announcement) {
        announcementService.add(announcement);
        return Result.success();
    }
    
    /**
     * 更新系统公告
     */
    @PutMapping("/update")
    public Result update(@RequestBody Announcement announcement) {
        announcementService.update(announcement);
        return Result.success();
    }
    
    /**
     * 删除系统公告
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        announcementService.deleteById(id);
        return Result.success();
    }
    
    /**
     * 根据ID查询系统公告
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Announcement announcement = announcementService.selectById(id);
        return Result.success(announcement);
    }
    
    /**
     * 查询所有系统公告（带条件）
     */
    @GetMapping("/selectAll")
    public Result selectAll(Announcement announcement) {
        List<Announcement> list = announcementService.selectAll(announcement);
        return Result.success(list);
    }
    
    /**
     * 分页查询系统公告（带条件）
     */
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Announcement announcement
    ) {
        PageInfo<Announcement> pageInfo = announcementService.selectPage(announcement, pageNum, pageSize);
        return Result.success(pageInfo);
    }
} 