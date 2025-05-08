<template>
  <div>
    <h2>消息中心</h2>
    
    <!-- 消息分类选项卡 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="系统公告" name="announcement">
        <el-empty v-if="announcements.length === 0" description="暂无系统公告"></el-empty>
        <div v-else class="message-list">
          <div 
            v-for="item in announcements" 
            :key="item.id" 
            class="message-item"
            @click="handleViewAnnouncement(item)"
          >
            <div class="message-header">
              <span class="message-title">{{ item.title }}</span>
              <div class="header-actions">
                <el-tag size="small" :type="getTagType(item.type)">{{ item.type }}</el-tag>
                <el-button 
                  v-if="userInfo.role === 'EMP'"
                  type="danger" 
                  size="small" 
                  @click.stop="handleDeleteAnnouncement(item.id)"
                >删除</el-button>
              </div>
            </div>
            <div class="message-content">{{ truncateContent(item.content) }}</div>
            <div class="message-footer">
              <span>{{ formatDateTime(item.createTime) }}</span>
              <span>发布人: {{ item.creatorName }}</span>
            </div>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="活动提醒" name="notification">
        <el-empty v-if="notifications.length === 0" description="暂无活动提醒"></el-empty>
        <div v-else class="message-list">
          <div 
            v-for="item in notifications" 
            :key="item.id" 
            :class="['message-item', { 'unread': !item.isRead }]"
            @click="handleViewNotification(item)"
          >
            <div class="message-header">
              <span class="message-title">{{ item.title }}</span>
              <div class="header-actions">
                <el-tag size="small" :type="getTagType(item.type)">{{ item.type }}</el-tag>
                <el-button 
                  v-if="userInfo.role === 'EMP'"
                  type="primary" 
                  size="small" 
                  @click.stop="handleViewReceivers(item)"
                >查看接收者</el-button>
                <el-button 
                  v-if="userInfo.role === 'EMP'"
                  type="warning" 
                  size="small" 
                  @click.stop="handleEditNotification(item)"
                >编辑</el-button>
                <el-button 
                  v-if="userInfo.role === 'EMP'"
                  type="danger" 
                  size="small" 
                  @click.stop="handleDeleteNotification(item.id)"
                >删除</el-button>
              </div>
            </div>
            <div class="message-content">{{ truncateContent(item.content) }}</div>
            <div class="message-footer">
              <span>{{ formatDateTime(item.createTime) }}</span>
              <span>活动: {{ item.activityName }}</span>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 查看公告详情对话框 -->
    <el-dialog
      v-model="announcementDialogVisible"
      title="公告详情"
      width="650px"
    >
      <div class="announcement-detail">
        <h2 class="announcement-title">{{ selectedAnnouncement.title }}</h2>
        <div class="announcement-meta">
          <span>类型: {{ selectedAnnouncement.type }}</span>
          <span>发布时间: {{ formatDateTime(selectedAnnouncement.createTime) }}</span>
          <span>发布人: {{ selectedAnnouncement.creatorName }}</span>
        </div>
        <div class="announcement-content">
          {{ selectedAnnouncement.content }}
        </div>
      </div>
    </el-dialog>
    
    <!-- 查看通知详情对话框 -->
    <el-dialog
      v-model="notificationDialogVisible"
      title="活动提醒详情"
      width="650px"
    >
      <div class="notification-detail">
        <h2 class="notification-title">{{ selectedNotification.title }}</h2>
        <div class="notification-meta">
          <span>类型: {{ selectedNotification.type }}</span>
          <span>活动: {{ selectedNotification.activityName }}</span>
          <span>发布时间: {{ formatDateTime(selectedNotification.createTime) }}</span>
        </div>
        <div class="notification-content">
          {{ selectedNotification.content }}
        </div>
      </div>
    </el-dialog>
    
    <!-- 查看接收者对话框 -->
    <el-dialog
      v-model="receiversDialogVisible"
      title="通知接收者"
      width="650px"
    >
      <div class="receivers-detail">
        <h2 class="receivers-title">{{ selectedNotification.title }}</h2>
        <div class="receivers-meta">
          <span>活动: {{ selectedNotification.activityName }}</span>
        </div>
        <div class="receivers-list">
          <div v-for="receiver in receivers" :key="receiver.receiver_id" class="receiver-item">
            {{ receiver.receiver_name }}
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 编辑通知对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑活动提醒"
      width="650px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="rules"
        label-width="100px"
        style="margin-top: 15px;"
      >
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入通知标题"></el-input>
        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="editForm.type" placeholder="请选择通知类型" style="width: 100%">
            <el-option label="信息变更" value="信息变更"></el-option>
            <el-option label="注意事项" value="注意事项"></el-option>
            <el-option label="开始提醒" value="开始提醒"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="editForm.content" type="textarea" :rows="8" placeholder="请输入通知内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const activeTab = ref('announcement');
const announcements = ref([]);
const notifications = ref([]);
const announcementDialogVisible = ref(false);
const notificationDialogVisible = ref(false);
const selectedAnnouncement = reactive({
  id: null,
  title: '',
  content: '',
  type: '',
  createTime: '',
  creatorName: ''
});
const selectedNotification = reactive({
  id: null,
  title: '',
  content: '',
  type: '',
  activityId: null,
  activityName: '',
  createTime: '',
  isRead: false
});
const receiversDialogVisible = ref(false);
const receivers = ref([]);
const editDialogVisible = ref(false);
const editFormRef = ref(null);
const editForm = reactive({
  id: null,
  title: '',
  content: '',
  type: '',
  activityId: null,
  activityName: '',
  creatorId: null,
  creatorName: ''
});

// 获取用户信息
const userInfo = JSON.parse(localStorage.getItem('xm-user')) || {};

// 加载系统公告
const loadAnnouncements = async () => {
  try {
    const res = await request.get('/announcement/selectAll');
    if (res.code === '200') {
      announcements.value = res.data || [];
    } else {
      ElMessage.error(res.message || '加载公告失败');
    }
  } catch (error) {
    console.error('加载公告错误', error);
    ElMessage.error('加载公告出错');
  }
};

// 加载活动提醒
const loadNotifications = async () => {
  if (!userInfo.id) return;
  
  try {
    let res;
    if (userInfo.role === 'EMP') {
      // 管理员只查看自己发送的通知
      res = await request.get(`/notification/sentByMe/${userInfo.id}`);
      if (res.code === '200') {
        notifications.value = res.data || [];
      }
    } else {
      // 志愿者只查看自己的通知
      res = await request.get(`/notification/selectByReceiverId/${userInfo.id}`);
      if (res.code === '200') {
        notifications.value = res.data || [];
      }
    }
    
    if (res.code !== '200') {
      ElMessage.error(res.message || '加载提醒失败');
    }
  } catch (error) {
    console.error('加载提醒错误', error);
    ElMessage.error('加载提醒出错');
  }
};

// 处理选项卡切换
const handleTabChange = (tab) => {
  if (tab === 'announcement') {
    loadAnnouncements();
  } else if (tab === 'notification') {
    loadNotifications();
  }
};

// 查看公告详情
const handleViewAnnouncement = (announcement) => {
  Object.assign(selectedAnnouncement, announcement);
  announcementDialogVisible.value = true;
};

// 查看通知详情并标记为已读
const handleViewNotification = async (notification) => {
  Object.assign(selectedNotification, notification);
  notificationDialogVisible.value = true;
  
  // 如果未读，则标记为已读
  if (!notification.isRead) {
    try {
      await request.put(`/notification/markAsRead/${notification.id}`);
      // 更新本地状态
      notification.isRead = true;
    } catch (error) {
      console.error('标记已读错误', error);
    }
  }
};

// 查看接收者
const handleViewReceivers = async (notification) => {
  Object.assign(selectedNotification, notification);
  receiversDialogVisible.value = true;
  
  try {
    const res = await request.get('/notification/receivers', {
      params: {
        title: notification.title,
        content: notification.content,
        activityId: notification.activityId,
        creatorId: notification.creatorId
      }
    });
    if (res.code === '200') {
      receivers.value = res.data || [];
    } else {
      ElMessage.error(res.message || '加载接收者失败');
    }
  } catch (error) {
    console.error('加载接收者错误', error);
    ElMessage.error('加载接收者出错');
  }
};

// 删除通知
const handleDeleteNotification = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条通知吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete(`/notification/deleteById/${id}`);
      if (res.code === '200') {
        ElMessage.success('删除成功');
        // 刷新通知列表
        loadNotifications();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除通知错误', error);
      ElMessage.error('删除通知出错');
    }
  }).catch(() => {
    // 用户取消操作
  });
};

// 删除公告
const handleDeleteAnnouncement = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条公告吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete(`/announcement/deleteById/${id}`);
      if (res.code === '200') {
        ElMessage.success('删除成功');
        // 刷新公告列表
        loadAnnouncements();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除公告错误', error);
      ElMessage.error('删除公告出错');
    }
  }).catch(() => {
    // 用户取消操作
  });
};

// 获取标签类型
const getTagType = (type) => {
  const typeMap = {
    '政策变更': 'danger',
    '重要通知': 'warning',
    '活动信息': 'success',
    '活动时间变更': 'danger',
    '注意事项': 'info'
  };
  return typeMap[type] || '';
};

// 内容截断
const truncateContent = (content, length = 100) => {
  if (!content) return '';
  return content.length > length ? content.substring(0, length) + '...' : content;
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  const date = new Date(dateTime);
  return date.toLocaleString();
};

// 编辑通知
const handleEditNotification = (notification) => {
  Object.assign(editForm, notification);
  editDialogVisible.value = true;
};

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return;
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.put(`/notification/update/${editForm.id}`, editForm);
        
        if (res.code === '200') {
          ElMessage.success('通知更新成功');
          editDialogVisible.value = false;
          loadNotifications();
        } else {
          ElMessage.error(res.message || '更新失败');
        }
      } catch (error) {
        console.error('更新通知出错', error);
        ElMessage.error('更新通知出错');
      }
    }
  });
};

onMounted(() => {
  loadAnnouncements();
});
</script>

<style scoped>
.message-list {
  margin-top: 15px;
}

.message-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background-color: #f0f9eb;
  border-left: 3px solid #67c23a;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-title {
  font-weight: bold;
  font-size: 16px;
}

.message-content {
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.6;
}

.message-footer {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 12px;
}

.announcement-detail, .notification-detail {
  padding: 10px;
}

.announcement-title, .notification-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 15px;
}

.announcement-meta, .notification-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.announcement-content, .notification-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.receivers-detail {
  padding: 10px;
}
.receivers-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 15px;
}
.receivers-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}
.receivers-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.receiver-item {
  background-color: #f0f9eb;
  padding: 5px 10px;
  border-radius: 4px;
}
</style> 