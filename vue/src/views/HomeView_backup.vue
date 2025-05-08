<!--<template>-->
<!--  <div style="border: 1px solid white; min-height: 100vh;">-->
<!--    <div >-->
<!--      <RouterLink to="/text">通过router跳转到text.vue</RouterLink>-->
<!--      <a href="/text">通过a标签跳转text.vue</a>-->
<!--    </div>-->
<!--    <div>-->
<!--      <el-button type="primary" @click="router.push('/Manager/text')">push跳转新页面可返回</el-button>-->
<!--      <el-button type="primary" @click="router.replace('/Manager/text')">replace跳转新页面不可返回</el-button>-->
<!--    </div>-->
<!--    <div class="flex gap-4 mb-4">-->
<!--      <el-input v-model="data.input" style="width: 240px" placeholder="请输入内容" :prefix-icon="Search" /> {{ data.input }}-->
<!--      <span>Using attributes</span>-->
<!--      <el-date-picker-->
<!--          v-model="dateValue"-->
<!--          type="date"-->
<!--          placeholder="选择日期"-->
<!--          format="yyyy-MM-dd"-->
<!--          value-format="yyyy-MM-dd"-->
<!--      ></el-date-picker>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup lang="ts">-->
<!--import { reactive, ref } from 'vue';-->
<!--import { Calendar, Search } from '@element-plus/icons-vue';-->
<!--import router from '@/router';-->
<!--import request from '@/utils/request.js';-->

<!--const data = reactive({-->
<!--  input: null,-->
<!--  employeesList: []-->
<!--});-->
<!--const dateValue = ref('');-->
<!--request.get('/employee/selectAll').then(res => {-->
<!--  console.log(res);-->
<!--  data.employeesList = res.data;-->
<!--  console.log(data.employeesList);-->
<!--})-->
<!--</script>-->

<template>
  <div class="home-container">
    <!-- 欢迎标题 -->
    <div class="welcome-header">
      <h1>欢迎使用志愿服务系统</h1>
      <p>让爱心传递，让温暖延续</p>
    </div>

    <!-- 活动轮播图 -->
    <div class="carousel-container">
      <el-carousel :interval="5000" height="400px">
        <el-carousel-item v-for="(activity, index) in activities" :key="index">
          <div class="carousel-item">
            <img :src="activity.imageUrl" class="carousel-image" />
            <div class="carousel-caption">
              <h3>{{ activity.title }}</h3>
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
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 活动数据
const activities = ref([])

// 获取活动数据
const loadActivities = async () => {
  try {
    const res = await request.get('/activity/carousel')
    activities.value = res.data
  } catch (error) {
    ElMessage.error('活动加载失败')
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

.carousel-caption h3 {
  font-size: 1.8rem;
  margin-bottom: 10px;
}

.carousel-caption p {
  font-size: 1rem;
  margin: 5px 0;
}
</style> 