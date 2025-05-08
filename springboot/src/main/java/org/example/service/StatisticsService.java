package org.example.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    
    /**
     * 获取热门活动TOP N
     * @param limit 获取的数量
     * @return 活动列表，包含活动名称和报名人数
     */
    List<Map<String, Object>> getTopActivities(int limit);
    
    /**
     * 获取志愿者参与活动次数排行榜
     * @return 志愿者排行榜，包含志愿者名称和参与次数
     */
    List<Map<String, Object>> getVolunteerRanking();
} 