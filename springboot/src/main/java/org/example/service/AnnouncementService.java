package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.Announcement;
import org.example.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnnouncementService {
    
    @Resource
    private AnnouncementMapper announcementMapper;
    
    /**
     * 添加系统公告
     */
    public void add(Announcement announcement) {
        announcement.setCreateTime(new Date());
        announcementMapper.insert(announcement);
    }
    
    /**
     * 更新系统公告
     */
    public void update(Announcement announcement) {
        announcementMapper.update(announcement);
    }
    
    /**
     * 根据ID删除系统公告
     */
    public void deleteById(Integer id) {
        announcementMapper.deleteById(id);
    }
    
    /**
     * 根据ID查询系统公告
     */
    public Announcement selectById(Integer id) {
        return announcementMapper.selectById(id);
    }
    
    /**
     * 查询所有系统公告（带条件）
     */
    public List<Announcement> selectAll(Announcement announcement) {
        return announcementMapper.selectAll(announcement);
    }
    
    /**
     * 分页查询系统公告（带条件）
     */
    public PageInfo<Announcement> selectPage(Announcement announcement, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Announcement> list = announcementMapper.selectAll(announcement);
        return new PageInfo<>(list);
    }
} 