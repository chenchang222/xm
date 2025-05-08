package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.Activity;
import org.example.mapper.ActivityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    @Resource
    private ActivityMapper activityMapper;

    public List<Activity> selectAll(Activity activity) {
        return activityMapper.selectAll(activity);
    }

    public Activity selectById(Integer id) {
        return activityMapper.selectById(id);
    }

    public PageInfo<Activity> selectPage(Activity activity, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Activity> list = activityMapper.selectAll(activity);
        return new PageInfo<>(list);
    }

    public void add(Activity activity) {
        activityMapper.insert(activity);
    }

    public void update(Activity activity) {
        activityMapper.updateById(activity);
    }

    public void deleteById(Integer id) {
        activityMapper.deleteById(id);
    }

    public void deleteBatchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }
}
