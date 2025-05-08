<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
        </div>
      </template>
      
      <div v-if="favorites.length === 0" class="empty-state">
        <el-empty description="暂无收藏的活动" />
      </div>
      
      <div v-else class="favorite-list">
        <el-card v-for="item in favorites" :key="item.id" class="favorite-item">
          <div class="favorite-content">
            <div class="activity-info">
              <h3>{{ item.activityName }}</h3>
              <div class="activity-meta">
                <span class="activity-type">{{ item.activityType }}</span>
                <span class="activity-location">{{ item.activityLocation }}</span>
              </div>
              <div class="activity-time">
                <span>开始时间: {{ item.activityStartTime }}</span>
                <span>结束时间: {{ item.activityEndTime }}</span>
              </div>
            </div>
            <div class="actions">
              <el-button type="danger" size="small" @click="handleCancelFavorite(item)">取消收藏</el-button>
              <el-button type="primary" size="small" @click="handleViewActivity(item)">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const favorites = ref([])
const user = JSON.parse(localStorage.getItem('xm-user') || '{}')

// 获取收藏列表
const loadFavorites = async () => {
  if (!user.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    const res = await request.get(`/activityFavorite/listByVolunteerId/${user.id}`)
    if (res.code === '200') {
      favorites.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取收藏列表失败')
    }
  } catch (error) {
    console.error('获取收藏列表失败', error)
    ElMessage.error('获取收藏列表失败')
  }
}

// 取消收藏
const handleCancelFavorite = (item) => {
  ElMessageBox.confirm('确定取消收藏该活动?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete('/activityFavorite/remove', {
        params: {
          activityId: item.activityId,
          volunteerId: user.id
        }
      })
      
      if (res.code === '200') {
        ElMessage.success('取消收藏成功')
        loadFavorites() // 重新加载收藏列表
      } else {
        ElMessage.error(res.message || '取消收藏失败')
      }
    } catch (error) {
      console.error('取消收藏失败', error)
      ElMessage.error('操作失败')
    }
  }).catch(() => {})
}

// 查看活动详情
const handleViewActivity = (item) => {
  router.push({
    path: '/manager/activityDetail',
    query: { id: item.activityId }
  })
}

// 初始化
onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  margin: 40px 0;
}

.favorite-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.favorite-item {
  border-radius: 8px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.favorite-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.favorite-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-info {
  flex: 1;
}

.activity-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.activity-meta {
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.activity-type {
  display: inline-block;
  padding: 2px 8px;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  margin-right: 10px;
}

.activity-location {
  display: inline-block;
  padding: 2px 8px;
  background-color: #f6ffed;
  color: #52c41a;
  border-radius: 4px;
}

.activity-time {
  display: flex;
  flex-direction: column;
  gap: 5px;
  color: #999;
  font-size: 14px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style> 