<template>
  <div>
    <h2>系统公告管理</h2>
    
    <!-- 查询表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="公告标题">
          <el-input v-model="queryParams.title" placeholder="请输入公告标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="queryParams.type" placeholder="请选择公告类型" clearable style="width: 180px;">
            <el-option label="政策变更" value="政策变更"></el-option>
            <el-option label="重要通知" value="重要通知"></el-option>
            <el-option label="活动信息" value="活动信息"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 工具栏 -->
    <div class="tool-bar">
      <el-button type="primary" @click="handleAdd">新增公告</el-button>
    </div>
    
    <!-- 公告列表 -->
    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="公告标题" show-overflow-tooltip></el-table-column>
      <el-table-column prop="type" label="公告类型" width="120"></el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180"></el-table-column>
      <el-table-column prop="creatorName" label="发布人" width="120"></el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          <el-button type="info" size="small" @click="handleView(scope.row)">查看</el-button>
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
    
    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增公告' : '编辑公告'"
      width="650px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型" style="width: 180px;">
            <el-option label="更新维护" value="更新维护"></el-option>
            <el-option label="其他公告" value="其他公告"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入公告内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看公告详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="650px"
    >
      <div class="announcement-detail">
        <h2 class="announcement-title">{{ viewForm.title }}</h2>
        <div class="announcement-meta">
          <span>类型: {{ viewForm.type }}</span>
          <span>发布时间: {{ viewForm.createTime }}</span>
          <span>发布人: {{ viewForm.creatorName }}</span>
        </div>
        <div class="announcement-content">
          {{ viewForm.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';
import dayjs from 'dayjs';

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  type: ''
});

// 表单数据
const form = reactive({
  id: null,
  title: '',
  content: '',
  type: '',
  creatorId: null,
  creatorName: ''
});

// 查看表单数据
const viewForm = reactive({
  id: null,
  title: '',
  content: '',
  type: '',
  createTime: '',
  creatorName: ''
});

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
};

const formRef = ref(null);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'
const tableData = ref([]);
const total = ref(0);
const loading = ref(false);

// 获取用户信息
const userInfo = JSON.parse(localStorage.getItem('xm-user')) || {};

// 加载公告数据
const loadData = async () => {
  loading.value = true;
  try {
    const res = await request.get('/announcement/selectPage', {
      params: queryParams
    });
    if (res.code === '200') {
      tableData.value = res.data.list.map(item => ({
        ...item,
        createTime: item.createTime ? dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss') : ''
      }));
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

// 重置查询表单
const resetQuery = () => {
  queryParams.title = '';
  queryParams.type = '';
  queryParams.pageNum = 1;
  loadData();
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  loadData();
};

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.pageNum = page;
  loadData();
};

// 新增公告
const handleAdd = () => {
  dialogType.value = 'add';
  resetForm();
  dialogVisible.value = true;
  
  // 设置创建者信息
  form.creatorId = userInfo.id;
  form.creatorName = userInfo.name;
};

// 编辑公告
const handleEdit = (row) => {
  dialogType.value = 'edit';
  resetForm();
  Object.assign(form, row);
  dialogVisible.value = true;
};

// 查看公告详情
const handleView = (row) => {
  const formattedRow = {
    ...row,
    createTime: row.createTime ? dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : ''
  };
  Object.assign(viewForm, formattedRow);
  viewDialogVisible.value = true;
};

// 删除公告
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/announcement/deleteById/${row.id}`);
      if (res.code === '200') {
        ElMessage.success('删除成功');
        loadData();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除出错', error);
      ElMessage.error('删除出错');
    }
  }).catch(() => {});
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.id = null;
  form.title = '';
  form.content = '';
  form.type = '';
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = dialogType.value === 'add' ? '/announcement/add' : '/announcement/update';
        const res = await request[dialogType.value === 'add' ? 'post' : 'put'](url, form);
        
        if (res.code === '200') {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功');
          dialogVisible.value = false;
          loadData();
        } else {
          ElMessage.error(res.message || (dialogType.value === 'add' ? '新增失败' : '更新失败'));
        }
      } catch (error) {
        console.error('提交表单出错', error);
        ElMessage.error('提交表单出错');
      }
    }
  });
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.tool-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.announcement-detail {
  padding: 10px;
}

.announcement-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 15px;
}

.announcement-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.announcement-content {
  line-height: 1.6;
  white-space: pre-wrap;
}
</style> 