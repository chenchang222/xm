package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.ActivityType;
import org.example.mapper.ActivityTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityTypeService {
    @Resource
    private ActivityTypeMapper activityTypeMapper;

    public List<ActivityType> selectAll() {
        return activityTypeMapper.selectAll();
    }

    public ActivityType selectById(Integer id) {
        return activityTypeMapper.selectById(id);
    }
} 