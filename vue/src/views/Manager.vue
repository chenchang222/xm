<template>
  <!-- 头部 -->
  <div style="height: 60px; background-color: #46aeda; display: flex; align-items: center">
    <div style="width: 200px; display: flex; align-items: center; padding-left: 15px">
      <img style="width: 40px" src="@/assets/help.png" alt="logo">
      <span style="font-size: 20px; color: white; margin-left: 5px">志愿者服务系统</span>
    </div>
    <div style="flex: 1"></div>
    <div style="width: fit-content; display: flex; align-items: center; padding-right: 10px">
      <div class="message-icon" @click="goToMessageCenter">
        <el-badge :value="unreadCount" :max="99" class="message-badge">
          <el-icon :size="22"><Bell /></el-icon>
        </el-badge>
      </div>
      <img
          src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
          style="width: 40px; height: 40px; margin-left: 15px;"
          alt="avatar"
      >
      <span style="color: white; margin-left: 5px">{{ data.user.name }}</span>
    </div>
  </div>

  <!-- 主体 -->
  <div style="display: flex; height: calc(100vh - 60px)">
    <!-- 侧边栏 -->
    <div style="width: 200px; border-right: 1px solid #ddd">
      <el-menu
          router
          :default-active="$route.path"
          :default-openeds="['1']"
          style="border: 0"
      >
        <el-menu-item index="/manager/home">
          <el-icon><House /></el-icon>
          系统首页
        </el-menu-item>

        <el-menu-item index="/manager/data">
          <el-icon><DataAnalysis /></el-icon>
          数据统计
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon><List /></el-icon>
            <span>活动管理</span>
          </template>
          <el-menu-item index="/manager/activity" v-if="data.user.role === 'EMP'">
            <el-icon><Calendar /></el-icon>
            活动管理
          </el-menu-item>
          <el-menu-item index="/manager/activitySignUp" v-if="data.user.role === 'VOL'">
            <el-icon><Edit /></el-icon>
            活动报名
          </el-menu-item>
          <el-menu-item index="/manager/activityApproval" v-if="data.user.role === 'EMP'">
            <el-icon><Check /></el-icon>
            活动报名审核
          </el-menu-item>
          <el-menu-item index="/manager/activityReviewManage" v-if="data.user.role === 'EMP'">
            <el-icon><Comment /></el-icon>
            活动评价管理
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/manager/postList" v-if="data.user.role === 'VOL'">
          <el-icon><ChatLineRound /></el-icon>
          志愿者论坛
        </el-menu-item>
        
        <el-menu-item index="/manager/postManage" v-if="data.user.role === 'EMP'">
          <el-icon><Document /></el-icon>
          论坛帖子管理
        </el-menu-item>

        <el-sub-menu index="2" v-if="data.user.role === 'EMP'">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/manager/employee">
            <el-icon><Avatar /></el-icon>
            管理员信息
          </el-menu-item>
          <el-menu-item index="/manager/volunteer">
            <el-icon><Avatar /></el-icon>
            志愿者信息
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title>
            <el-icon><Message /></el-icon>
            <span>消息通知</span>
          </template>
          <el-menu-item index="/manager/announcement" v-if="data.user.role === 'EMP'">
            <el-icon><Document /></el-icon>
            系统公告管理
          </el-menu-item>
          <el-menu-item index="/manager/activityNotification" v-if="data.user.role === 'EMP'">
            <el-icon><Bell /></el-icon>
            活动提醒通知
          </el-menu-item>
          <el-menu-item index="/manager/messageCenter">
            <el-icon><ChatDotRound /></el-icon>
            消息中心
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="4" v-if="data.user.role === 'VOL'">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>个人中心</span>
          </template>
          <el-menu-item index="/manager/person">
            <el-icon><UserFilled /></el-icon>
            个人信息
          </el-menu-item>
          <el-menu-item index="/manager/mySignUps">
            <el-icon><Calendar /></el-icon>
            我的报名
          </el-menu-item>
          <el-menu-item index="/manager/activityFavorite">
            <el-icon><Star /></el-icon>
            我的收藏
          </el-menu-item>
          <el-menu-item index="/manager/activityReview">
            <el-icon><Comment /></el-icon>
            我的评价
          </el-menu-item>
          <el-menu-item index="/manager/myPosts">
            <el-icon><Document /></el-icon>
            我的发帖
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/manager/person" v-if="data.user.role === 'EMP'">
          <el-icon><UserFilled /></el-icon>
          个人信息
        </el-menu-item>

        <el-menu-item @click="logout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧内容区 -->
    <div style="flex: 1; padding: 20px; background-color: #f5f7fa; overflow: auto">
      <RouterView @updateUser="updateUser"/>
    </div>
  </div>
</template>

<script setup>
import {reactive, onMounted} from "vue";
import router from "@/router/index.js";
import request from "@/utils/request";

const data = reactive({
  user:JSON.parse(localStorage.getItem("xm-user")),
  unreadCount: 0
})
import {
  House,
  DataAnalysis,
  User,
  UserFilled,
  SwitchButton,
  Calendar,
  Edit,
  List,
  Avatar,
  Bell,
  Document,
  ChatDotRound,
  Message,
  Check,
  Star,
  Comment,
  ChatLineRound
} from '@element-plus/icons-vue'
const logout = () => {
  localStorage.removeItem("xm-user");
  location.href = "/login";
}

const goToMessageCenter = () => {
  router.push('/manager/messageCenter');
}

const getUnreadCount = async () => {
  if (!data.user || !data.user.id) return;
  
  try {
    const res = await request.get(`/notification/countUnread/${data.user.id}`);
    if (res.code === '200' && res.data) {
      data.unreadCount = res.data.count || 0;
    }
  } catch (error) {
    console.error('获取未读消息数量失败', error);
  }
}

const updateUser =  () => {
  data.user = JSON.parse(localStorage.getItem("xm-user"));
  getUnreadCount();
}

onMounted(() => {
  getUnreadCount();
});
</script>

<style scoped>
.el-menu {
  user-select: none;
}

.el-menu .is-active {
  background-color: #e6f7ff !important;
}

.el-menu-item.is-active .el-icon {
  color: #1890ff !important;
}

.el-menu-item:hover {
  background-color: #e6f7ff !important;
}

.message-icon {
  cursor: pointer;
  padding: 5px;
}

.message-badge {
  color: white;
}
</style>