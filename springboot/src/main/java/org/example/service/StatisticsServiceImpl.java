package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.ActivityInfo;
import org.example.mapper.ActivityInfoMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Resource
    private ActivityInfoMapper activityInfoMapper;
    
    /**
     * 获取热门活动TOP N
     * @param limit 获取的数量
     * @return 活动列表，包含活动名称和报名人数
     */
    @Override
    public List<Map<String, Object>> getTopActivities(int limit) {
        // 查询所有活动报名信息
        List<ActivityInfo> allActivityInfos = activityInfoMapper.selectAll(new ActivityInfo());
        
        // 按活动ID分组，统计每个活动的报名人数
        Map<Integer, Integer> activitySignupCount = new HashMap<>();
        Map<Integer, String> activityNames = new HashMap<>();
        
        for (ActivityInfo info : allActivityInfos) {
            Integer activityId = info.getActivityId();
            String activityName = info.getActivityName();
            
            // 保存活动名称
            activityNames.put(activityId, activityName);
            
            // 累加报名人数
            int count = activitySignupCount.getOrDefault(activityId, 0);
            activitySignupCount.put(activityId, count + (info.getNum() != null ? info.getNum() : 1));
        }
        
        // 转换为列表并按报名人数排序
        List<Map<String, Object>> result = new ArrayList<>();
        activitySignupCount.forEach((activityId, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("activityId", activityId);
            item.put("activityName", activityNames.get(activityId));
            item.put("signupCount", count);
            result.add(item);
        });
        
        // 排序取前N个
        return result.stream()
                .sorted((a, b) -> ((Integer) b.get("signupCount")).compareTo((Integer) a.get("signupCount")))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取志愿者参与活动次数排行榜
     * @return 志愿者排行榜，包含志愿者名称和参与次数
     */
    @Override
    public List<Map<String, Object>> getVolunteerRanking() {
        // 查询所有活动报名信息
        List<ActivityInfo> allActivityInfos = activityInfoMapper.selectAll(new ActivityInfo());
        
        // 按志愿者ID分组，统计每个志愿者参与的活动次数
        Map<Integer, Integer> volunteerActivityCount = new HashMap<>();
        Map<Integer, String> volunteerNames = new HashMap<>();
        
        for (ActivityInfo info : allActivityInfos) {
            Integer volunteerId = info.getVolunteerId();
            String volunteerName = info.getVolunteerName();
            
            // 跳过无效的志愿者ID
            if (volunteerId == null) continue;
            
            // 保存志愿者名称
            volunteerNames.put(volunteerId, volunteerName);
            
            // 累加参与次数
            int count = volunteerActivityCount.getOrDefault(volunteerId, 0);
            volunteerActivityCount.put(volunteerId, count + 1);
        }
        
        // 转换为列表并按参与次数排序
        List<Map<String, Object>> result = new ArrayList<>();
        volunteerActivityCount.forEach((volunteerId, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("volunteerId", volunteerId);
            item.put("volunteerName", volunteerNames.get(volunteerId));
            item.put("activityCount", count);
            result.add(item);
        });
        
        // 排序
        return result.stream()
                .sorted((a, b) -> ((Integer) b.get("activityCount")).compareTo((Integer) a.get("activityCount")))
                .collect(Collectors.toList());
    }
} 