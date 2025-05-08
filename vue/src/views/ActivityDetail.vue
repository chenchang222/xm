<template>
  <div class="activity-detail">
    <!-- 活动基本信息 -->
    <el-card class="activity-info">
      <template #header>
        <div class="card-header">
          <h2>{{ activity.name }}</h2>
          <el-tag :type="getStatusType(activity.status)">{{ getStatusText(activity.status) }}</el-tag>
        </div>
      </template>
      
      <el-descriptions>
        <el-descriptions-item label="活动时间">
          {{ formatDateTime(activity.start_time) }} - {{ formatDateTime(activity.end_time) }}
        </el-descriptions-item>
        <el-descriptions-item label="活动地点">{{ activity.location }}</el-descriptions-item>
        <el-descriptions-item label="参与人数">{{ activity.participant_count || 0 }}人</el-descriptions-item>
        <el-descriptions-item label="活动描述">{{ activity.description }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const activityId = route.params.id
const activity = ref({})

// 获取活动详情
const fetchActivityDetail = async () => {
  try {
    const response = await request.get(`/activity/${activityId}`)
    activity.value = response.data
  } catch (error) {
    console.error('获取活动详情失败:', error)
    ElMessage.error('获取活动详情失败')
  }
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString()
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'pending': 'info',
    'ongoing': 'success',
    'completed': '',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待开始',
    'ongoing': '进行中',
    'completed': '已结束',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  fetchActivityDetail()
})
</script>

<style scoped>
.activity-detail {
  padding: 20px;
}

.activity-info {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}
</style> 