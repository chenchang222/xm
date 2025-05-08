<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的活动评价</span>
        </div>
      </template>
      
      <div v-if="reviews.length === 0" class="empty-state">
        <el-empty description="暂无评价记录" />
      </div>
      
      <div v-else class="review-list">
        <el-card v-for="item in reviews" :key="item.id" class="review-item">
          <div class="review-header">
            <h3>{{ item.activityName }}</h3>
            <div class="review-time">评价时间: {{ formatDate(item.createTime) }}</div>
          </div>
          <div class="review-content">
            <div class="review-score">
              <span class="score-label">我的评分:</span>
              <el-rate
                v-model="item.score"
                disabled
                show-score
                text-color="#ff9900"
              />
            </div>
            <div class="review-text" v-if="item.content">
              <span class="content-label">评价内容:</span>
              <p>{{ item.content }}</p>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const reviews = ref([])
const user = JSON.parse(localStorage.getItem('xm-user') || '{}')

// 获取评价列表
const loadReviews = async () => {
  if (!user.id) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    const res = await request.get(`/activityReview/listByVolunteerId/${user.id}`)
    if (res.code === '200') {
      reviews.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取评价列表失败')
    }
  } catch (error) {
    console.error('获取评价列表失败', error)
    ElMessage.error('获取评价列表失败')
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  loadReviews()
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

.review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item {
  border-radius: 8px;
  transition: box-shadow 0.3s ease;
}

.review-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.review-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.review-time {
  color: #999;
  font-size: 14px;
}

.review-content {
  padding: 5px 0;
}

.review-score {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.score-label {
  margin-right: 10px;
  font-weight: 500;
  color: #666;
}

.review-text {
  display: flex;
}

.content-label {
  margin-right: 10px;
  font-weight: 500;
  color: #666;
  white-space: nowrap;
}

.review-text p {
  margin: 0;
  color: #333;
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
  flex: 1;
}
</style> 