package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.ActivityFavorite;
import org.example.mapper.ActivityFavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityFavoriteService {

    @Resource
    private ActivityFavoriteMapper activityFavoriteMapper;

    /**
     * 添加收藏
     */
    public void addFavorite(ActivityFavorite activityFavorite) {
        activityFavoriteMapper.insert(activityFavorite);
    }

    /**
     * 取消收藏
     */
    public void removeFavorite(Integer activityId, Integer volunteerId) {
        activityFavoriteMapper.delete(activityId, volunteerId);
    }

    /**
     * 查询用户是否已收藏某活动
     */
    public boolean isFavorite(Integer activityId, Integer volunteerId) {
        return activityFavoriteMapper.countByActivityIdAndVolunteerId(activityId, volunteerId) > 0;
    }

    /**
     * 查询用户的收藏列表
     */
    public List<ActivityFavorite> getFavoritesByVolunteerId(Integer volunteerId) {
        return activityFavoriteMapper.selectByVolunteerId(volunteerId);
    }
} 