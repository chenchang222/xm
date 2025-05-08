<template>
  <div class="home-container">
    <!-- 欢迎标题 -->
    <div class="welcome-header">
      <h1>欢迎使用志愿服务系统</h1>
      <p>让爱心传递，让温暖延续</p>
    </div>

    <!-- 活动轮播图 -->
    <div class="carousel-container">
      <el-carousel :interval="3000" height="400px" :autoplay="true">
        <el-carousel-item 
          v-for="(activity, index) in activities" 
          :key="index"
          @click="handleActivityClick(activity)"
          style="cursor: pointer;"
        >
          <div class="carousel-item">
            <img :src="activity.imageUrl" class="carousel-image" />
            <div class="carousel-caption">
              <p>{{ activity.description }}</p>
              <p>活动时间：{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 导入默认图片
import activity1 from '@/assets/activity6.jpg'
import activity2 from '@/assets/activity2.jpg'
import activity3 from '@/assets/activity3.jpg'
import activity4 from '@/assets/activity4.jpg'
import activity5 from '@/assets/activity5.jpg'
import activity6 from '@/assets/activity1.jpg'

const router = useRouter()
const userStore = useUserStore()

// 活动数据
const activities = ref([
  {
    id: 1,
    imageUrl: activity1,
    title: '志愿服务活动1',
    description: '关爱老人，传递温暖',
    startTime: '2024-03-01',
    endTime: '2024-03-31'
  },
  {
    id: 2,
    imageUrl: activity2,
    title: '志愿服务活动2',
    description: '环保行动，守护地球',
    startTime: '2024-04-01',
    endTime: '2024-04-30'
  },
  {
    id: 3,
    imageUrl: activity3,
    title: '志愿服务活动3',
    description: '爱心助学，点亮未来',
    startTime: '2024-05-01',
    endTime: '2024-05-31'
  },
  {
    id: 4,
    imageUrl: activity4,
    title: '志愿服务活动4',
    description: '社区服务，共建家园',
    startTime: '2024-06-01',
    endTime: '2024-06-30'
  },
  {
    id: 5,
    imageUrl: activity5,
    title: '志愿服务活动5',
    description: '文化传承，弘扬美德',
    startTime: '2024-07-01',
    endTime: '2024-07-31'
  },
  {
    id: 6,
    imageUrl: activity6,
    title: '志愿服务活动6',
    description: '健康义诊，关爱生命',
    startTime: '2024-08-01',
    endTime: '2024-08-31'
  }
])

// 获取活动数据
const loadActivities = async () => {
  try {
    const res = await request.get('/activity/carousel')
    if (res.data && res.data.length > 0) {
      activities.value = res.data
    }
  } catch (error) {
    ElMessage.error('活动加载失败')
  }
}

// 处理活动点击事件
const handleActivityClick = (activity) => {
  // 根据用户角色跳转到不同页面
  if (userStore.role === 'VOL') {
    // 志愿者跳转到活动报名页面
    router.push({
      path: '/manager/activitySignUp',
      query: {
        id: activity.id,
        title: activity.title
      }
    })
  } else {
    // 管理员跳转到活动管理页面
    router.push({
      path: '/manager/activity',
      query: {
        id: activity.id,
        title: activity.title
      }
    })
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString()
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-header {
  text-align: center;
  margin: 40px 0;
}

.welcome-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 15px;
}

.welcome-header p {
  font-size: 1.2rem;
  color: #7f8c8d;
}

.carousel-container {
  margin: 0 auto;
  max-width: 1200px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.carousel-item {
  position: relative;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
}

.carousel-caption p {
  font-size: 1rem;
  margin: 5px 0;
}
</style>

