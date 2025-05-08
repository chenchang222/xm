<template>
  <div style="border: 1px solid white; min-height: 100vh;">
    <!-- 新增欢迎标题 -->
    <h1 style="text-align: center; padding: 20px">欢迎登录志愿服务管理系统</h1>

    <!-- 新增轮播图 -->
    <el-carousel :interval="3000" height="400px" style="margin: 0 auto; max-width: 800px">
      <el-carousel-item v-for="index in 6" :key="index" @click="handleActivityClick(index)">
        <img
            :src="getImageUrl(index)"
            style="width: 100%; height: 100%; object-fit: cover"
            alt="活动图片"
        >
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
// 新增图片路径处理方法
const getImageUrl = (index: number) => {
  // 使用静态import方式引入图片，确保兼容性
  return `/src/assets/activity${index}.jpg`
}

// 新增点击跳转逻辑
const handleActivityClick = (index: number) => {
  const userRole = localStorage.getItem('userRole') // 假设用户角色存储在localStorage中
  if (userRole === 'VOL') {
    window.location.href = 'http://localhost:5173/manager/activitySignUp'
  } else {
    window.location.href = 'http://localhost:5173/manager/activity'
  }
}
</script>

