<template>
  <div>
    <h2>活动提醒通知</h2>
    
    <!-- 查询表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="活动名称">
          <el-input v-model="queryParams.activityName" placeholder="请输入活动名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadActivityData">查询</el-button>
          <el-button @click="resetActivityQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 活动列表 -->
    <el-table :data="activityData" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="活动名称" show-overflow-tooltip></el-table-column>
      <el-table-column prop="type" label="活动类型" width="120"></el-table-column>
      <el-table-column prop="location" label="活动地点" width="120"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="100"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleSelectActivity(scope.row)">发送提醒</el-button>
          <el-button type="info" size="small" @click="handleViewParticipants(scope.row)">查看参与者</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>
    
    <!-- 发送活动提醒对话框 -->
    <el-dialog
      v-model="sendDialogVisible"
      title="发送活动提醒"
      width="650px"
    >
      <div v-if="selectedActivity.id" class="selected-activity-info">
        <span>已选活动: {{ selectedActivity.title }}</span>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="margin-top: 15px;"
      >
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题"></el-input>
        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择通知类型" style="width: 100%">
            <el-option label="信息变更" value="信息变更"></el-option>
            <el-option label="注意事项" value="注意事项"></el-option>
            <el-option label="开始提醒" value="开始提醒"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入通知内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="sendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">发送</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看活动参与者对话框 -->
    <el-dialog
      v-model="participantsDialogVisible"
      title="活动参与者"
      width="800px"
    >
      <div v-if="selectedActivity.id" class="selected-activity-info">
        <span>活动: {{ selectedActivity.title }}</span>
      </div>
      
      <el-table :data="participantsData" style="width: 100%; margin-top: 15px;">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="volunteerName" label="志愿者姓名"></el-table-column>
        <el-table-column prop="qq" label="联系方式"></el-table-column>
        <el-table-column prop="num" label="报名人数"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  activityName: ''
});

// 表单数据
const form = reactive({
  title: '',
  content: '',
  type: '',
  activityId: null,
  activityName: '',
  creatorId: null,
  creatorName: ''
});

// 已选活动
const selectedActivity = reactive({
  id: null,
  title: ''
});

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
};

const formRef = ref(null);
const sendDialogVisible = ref(false);
const participantsDialogVisible = ref(false);
const activityData = ref([]);
const participantsData = ref([]);
const total = ref(0);
const loading = ref(false);

// 获取用户信息
const userInfo = JSON.parse(localStorage.getItem('xm-user')) || {};

// 加载活动数据
const loadActivityData = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    };
    
    if (queryParams.activityName) {
      params.title = queryParams.activityName;
    }
    
    const res = await request.get('/activity/selectPage', { params });
    if (res.code === '200') {
      activityData.value = res.data.list;
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '加载数据失败');
    }
  } catch (error) {
    console.error('加载数据错误', error);
    ElMessage.error('加载数据出错');
  } finally {
    loading.value = false;
  }
};

// 加载活动参与者数据
const loadParticipantsData = async (activityId) => {
  try {
    const info = new ActivityInfo();
    info.activityId = activityId;
    const res = await request.get('/activityInfo/selectAll', { 
      params: { activityId }
    });
    if (res.code === '200') {
      participantsData.value = res.data || [];
    } else {
      ElMessage.error(res.message || '加载参与者数据失败');
    }
  } catch (error) {
    console.error('加载参与者数据错误', error);
    ElMessage.error('加载参与者数据出错');
  }
};

// 重置查询表单
const resetActivityQuery = () => {
  queryParams.activityName = '';
  queryParams.pageNum = 1;
  loadActivityData();
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  loadActivityData();
};

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.pageNum = page;
  loadActivityData();
};

// 选择活动并发送提醒
const handleSelectActivity = (row) => {
  selectedActivity.id = row.id;
  selectedActivity.title = row.title;
  
  // 重置表单
  resetForm();
  
  // 设置表单初始值
  form.activityId = row.id;
  form.activityName = row.title;
  form.creatorId = userInfo.id;
  form.creatorName = userInfo.name;
  
  // 打开发送对话框
  sendDialogVisible.value = true;
};

// 查看活动参与者
const handleViewParticipants = (row) => {
  selectedActivity.id = row.id;
  selectedActivity.title = row.title;
  
  // 加载参与者数据
  loadParticipantsData(row.id);
  
  // 打开参与者对话框
  participantsDialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.title = '';
  form.content = '';
  form.type = '';
};

// 提交表单发送提醒
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.post('/notification/sendToActivityParticipants', form);
        
        if (res.code === '200') {
          ElMessage.success('提醒通知发送成功');
          sendDialogVisible.value = false;
        } else {
          ElMessage.error(res.message || '发送失败');
        }
      } catch (error) {
        console.error('发送提醒出错', error);
        ElMessage.error('发送提醒出错');
      }
    }
  });
};

// 活动信息对象（仅用于类型导入）
class ActivityInfo {
  constructor() {
    this.activityId = null;
  }
}

onMounted(() => {
  loadActivityData();
});
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.selected-activity-info {
  background-color: #f0f9eb;
  padding: 10px;
  border-radius: 4px;
  font-weight: bold;
  margin-bottom: 15px;
}
</style> 