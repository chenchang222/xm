<template>
  <div class="container">
    <div class="header">
      <h2>活动评价管理</h2>
      <div class="filter-container">
        <el-input 
          v-model="queryParams.keyword" 
          placeholder="搜索活动名称/志愿者" 
          clearable 
          @keyup.enter="getList"
          @clear="getList"
          style="width: 240px; margin-right: 10px;">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select 
          v-model="queryParams.score" 
          placeholder="评分筛选" 
          clearable 
          @change="getList"
          style="width: 120px; margin-right: 10px;">
          <el-option v-for="i in 5" :key="i" :label="`${i}星`" :value="i" />
        </el-select>
        <el-button type="primary" @click="getList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      style="width: 100%">
      <el-table-column prop="activityName" label="活动名称" min-width="150"/>
      <el-table-column prop="volunteerName" label="志愿者" width="120"/>
      <el-table-column prop="score" label="评分" width="100">
        <template #default="scope">
          <el-rate 
            v-model="scope.row.score" 
            disabled 
            show-score
            text-color="#ff9900"
            score-template="{value}" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip/>
      <el-table-column prop="createTime" label="评价时间" width="160">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="viewReviewDetail(scope.row)">
            查看
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 评价详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="评价详情"
      width="600px">
      <div v-if="currentReview">
        <div class="review-detail-item">
          <span class="label">活动名称:</span>
          <span>{{ currentReview.activityName }}</span>
        </div>
        <div class="review-detail-item">
          <span class="label">志愿者:</span>
          <span>{{ currentReview.volunteerName }}</span>
        </div>
        <div class="review-detail-item">
          <span class="label">评分:</span>
          <el-rate 
            v-model="currentReview.score" 
            disabled 
            show-score
            text-color="#ff9900"
            score-template="{value}" />
        </div>
        <div class="review-detail-item">
          <span class="label">评价内容:</span>
          <div class="review-content">{{ currentReview.content }}</div>
        </div>
        <div class="review-detail-item">
          <span class="label">评价时间:</span>
          <span>{{ formatDateTime(currentReview.createTime) }}</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="danger" @click="handleDeleteFromDialog">删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const detailDialogVisible = ref(false);
const currentReview = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  score: null
});

// 获取评价列表
const getList = async () => {
  loading.value = true;
  try {
    // 先获取所有活动
    const activityRes = await request.get('/activity/selectAll');
    if (activityRes.code !== '200') {
      ElMessage.error('获取活动列表失败');
      loading.value = false;
      return;
    }
    
    const activities = activityRes.data || [];
    let allReviews = [];
    
    // 对每个活动获取评价
    for (const activity of activities) {
      if (!activity.id) continue;
      
      try {
        const reviewRes = await request.get(`/activityReview/listByActivityId/${activity.id}`);
        if (reviewRes.code === '200' && reviewRes.data && reviewRes.data.length > 0) {
          allReviews = [...allReviews, ...reviewRes.data];
        }
      } catch (error) {
        console.error(`获取活动${activity.id}的评价失败`, error);
      }
    }
    
    // 在前端进行过滤和分页处理
    let filteredData = allReviews;
    
    // 关键词过滤
    if (queryParams.keyword) {
      const keyword = queryParams.keyword.toLowerCase();
      filteredData = filteredData.filter(item => 
        (item.activityName && item.activityName.toLowerCase().includes(keyword)) ||
        (item.volunteerName && item.volunteerName.toLowerCase().includes(keyword))
      );
    }
    
    // 评分过滤
    if (queryParams.score) {
      filteredData = filteredData.filter(item => 
        item.score === queryParams.score
      );
    }
    
    // 计算总数
    total.value = filteredData.length;
    
    // 分页
    const start = (queryParams.pageNum - 1) * queryParams.pageSize;
    const end = start + queryParams.pageSize;
    tableData.value = filteredData.slice(start, end);
  } catch (error) {
    console.error('获取评价列表失败', error);
    ElMessage.error('获取评价列表失败');
  } finally {
    loading.value = false;
  }
};

// 重置查询条件
const resetQuery = () => {
  queryParams.keyword = '';
  queryParams.score = null;
  getList();
};

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  const date = new Date(dateTimeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-');
};

const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  getList();
};

const handleCurrentChange = (page) => {
  queryParams.pageNum = page;
  getList();
};

// 查看评价详情
const viewReviewDetail = (row) => {
  currentReview.value = { ...row };
  detailDialogVisible.value = true;
};

// 删除评价
const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该评价吗？此操作不可逆',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 由于后端没有提供删除接口，这里需要修改为适当的处理方式
      // 可以提示用户目前不支持删除功能
      ElMessage.warning('抱歉，当前系统暂不支持删除评价功能');
      
      /*
      const res = await request.delete(`/activityReview/delete/${id}`);
      if (res.code === '200') {
        ElMessage.success('删除成功');
        getList();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
      */
    } catch (error) {
      console.error('操作失败', error);
      ElMessage.error('操作失败');
    }
  }).catch(() => {});
};

// 在对话框中删除
const handleDeleteFromDialog = () => {
  if (currentReview.value) {
    handleDelete(currentReview.value.id);
    detailDialogVisible.value = false;
  }
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.review-detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.review-detail-item .label {
  width: 100px;
  font-weight: bold;
  color: #606266;
}

.review-content {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.5;
  flex: 1;
}
</style> 