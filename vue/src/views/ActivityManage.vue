<template>
  <div class="manage-container">
    <h2>{{ activityTitle }} - 管理</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="活动详情" name="detail">
        <el-form :model="activityForm" label-width="120px">
          <el-form-item label="活动标题">
            <el-input v-model="activityForm.title" />
          </el-form-item>
          <el-form-item label="活动描述">
            <el-input type="textarea" v-model="activityForm.description" />
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker v-model="activityForm.startTime" type="date" />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker v-model="activityForm.endTime" type="date" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveActivity">保存</el-button>
            <el-button @click="goBack">返回</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="报名列表" name="signupList">
        <el-table :data="signupList" style="width: 100%">
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="contact" label="联系方式" />
          <el-table-column prop="signupTime" label="报名时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="text" @click="handleSignup(scope.row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const activityTitle = ref('')
const activityId = ref('')
const activeTab = ref('detail')
const signupList = ref([])

const activityForm = ref({
  title: '',
  description: '',
  startTime: '',
  endTime: ''
})

onMounted(() => {
  activityTitle.value = route.query.title || ''
  activityId.value = route.query.id || ''
  loadActivityDetail()
  loadSignupList()
})

const loadActivityDetail = async () => {
  try {
    const res = await request.get(`/activity/${activityId.value}`)
    activityForm.value = res.data
  } catch (error) {
    ElMessage.error('加载活动详情失败')
  }
}

const loadSignupList = async () => {
  try {
    const res = await request.get(`/activity/${activityId.value}/signups`)
    signupList.value = res.data
  } catch (error) {
    ElMessage.error('加载报名列表失败')
  }
}

const saveActivity = async () => {
  try {
    await request.put(`/activity/${activityId.value}`, activityForm.value)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleSignup = (row) => {
  // 处理报名信息
}

const goBack = () => {
  router.push('/manager/home')
}
</script>

<style scoped>
.manage-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style> 