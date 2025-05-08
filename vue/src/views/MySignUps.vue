<template>
  <div class="container">
    <div class="header">
      <h2>我的报名</h2>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      style="width: 100%">
      <el-table-column prop="activityName" label="活动名称" min-width="180">
        <template #default="scope">
          <el-link type="primary" @click="viewActivityDetail(scope.row.activityId)">{{ scope.row.activityName }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="联系方式" prop="qq" width="120"/>
      <el-table-column label="报名人数" prop="num" width="120"/>
      <el-table-column prop="createTime" label="报名时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status && scope.row.status.toUpperCase() === 'PENDING'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status && scope.row.status.toUpperCase() === 'APPROVED'" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.status && scope.row.status.toUpperCase() === 'REJECTED'" type="danger">已拒绝</el-tag>
          <el-tag v-else type="info">{{ scope.row.status || '未知' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="签到状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.signedIn === 1" type="success">已签到</el-tag>
          <el-tag v-else type="info">未签到</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="scope">
          <el-button 
            v-if="canCancel(scope.row)" 
            type="danger" 
            size="small" 
            @click="handleCancelSignUp(scope.row.id)">
            取消报名
          </el-button>
          <el-button 
            v-if="canSignIn(scope.row)" 
            type="primary"
            size="small" 
            @click="handleSignIn(scope.row)">
            签到
          </el-button>
          <el-button 
            v-if="canReview(scope.row)" 
            type="primary" 
            size="small" 
            @click="openReviewDialog(scope.row)">
            评价
          </el-button>
          <el-button 
            v-if="hasReviewed(scope.row.activityId)" 
            type="info" 
            size="small" 
            @click="viewReview(scope.row)">
            查看评价
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

    <!-- 评价对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="活动评价"
      width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate
            v-model="reviewForm.rating"
            :texts="['很差', '较差', '一般', '较好', '很好']"
            show-text
          />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您对活动的评价..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Search, Delete, EditPen, Check } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';
import router from '@/router';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const reviewDialogVisible = ref(false);
const currentSignUp = ref(null);
const reviewedActivityIds = ref([]);

const queryParams = reactive({
  volunteerId: JSON.parse(localStorage.getItem('xm-user')).id,
  pageNum: 1,
  pageSize: 10
});

const reviewForm = reactive({
  signUpId: null,
  activityId: null,
  rating: 5,
  content: ''
});

// 加载我的报名记录
const loadMySignUp = async () => {
  loading.value = true;
  try {
    const res = await request.get('activityInfo/selectByVolunteerId/' + queryParams.volunteerId);
    if (res.code === '200') {
      tableData.value = res.data || [];
      total.value = res.data.length || 0;
    } else {
      ElMessage.error(res.msg || '获取报名列表失败');
    }
  } catch (error) {
    console.error('获取报名列表失败', error);
    ElMessage.error('获取报名列表失败');
  } finally {
    loading.value = false;
  }
};

// 加载我的评价记录
const loadMyReviews = () => {
  request.get('activityReview/listByVolunteerId/' + queryParams.volunteerId).then(res => {
    if (res.code === '200') {
      reviewedActivityIds.value = (res.data || []).map(item => item.activityId);
    }
  });
};

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
  loadMySignUp();
};

const handleCurrentChange = (page) => {
  queryParams.pageNum = page;
  loadMySignUp();
};

const viewActivityDetail = (activityId) => {
  router.push(`/manager/activitySignUp?activityId=${activityId}`);
};

// 判断是否可以取消报名
const canCancel = (row) => {
  // 只有待审核或已通过且未开始的活动才能取消
  return (row.status === 'PENDING' || row.status === 'APPROVED') && 
         (!row.activityStartTime || new Date(row.activityStartTime) > new Date());
};

// 判断是否可以签到
const canSignIn = (row) => {
  // 审核通过且未签到的活动可以签到
  return row.status === 'APPROVED' && row.signedIn !== 1;
};

// 判断是否可以评价
const canReview = (row) => {
  // 审核通过且活动已结束的可以评价
  return row.status === 'APPROVED' && row.signedIn === 1 && !hasReviewed(row.activityId);
};

// 判断是否已评价
const hasReviewed = (activityId) => {
  return reviewedActivityIds.value.includes(activityId);
};

// 取消报名
const handleCancelSignUp = (id) => {
  ElMessageBox.confirm(
    '确定要取消该活动报名吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete('activityInfo/deleteById/' + id);
      if (res) {
        ElMessage.success('取消报名成功');
        loadMySignUp();
      } else {
        ElMessage.error(res.message || '取消报名失败');
      }
    } catch (error) {
      console.error('取消报名失败', error);
      ElMessage.error('取消报名失败');
    }
  }).catch(() => {});
};

// 签到
const handleSignIn = async (row) => {
  try {
    const res = await request.put(`activityInfo/signIn/${row.id}`);
    if (res.code === '200') {
      ElMessage.success('签到成功');
      loadMySignUp();
    } else {
      ElMessage.error(res.message || '签到失败');
    }
  } catch (error) {
    ElMessage.error('签到失败');
  }
};

// 打开评价对话框
const openReviewDialog = (row) => {
  currentSignUp.value = row;
  reviewForm.signUpId = row.id;
  reviewForm.activityId = row.activityId;
  reviewForm.rating = 5;
  reviewForm.content = '';
  reviewDialogVisible.value = true;
};

// 提交评价
const submitReview = async () => {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评价内容');
    return;
  }
  
  try {
    const res = await request.post('/activityReview/add', reviewForm);
    if (res.code === '200') {
      ElMessage.success('评价提交成功');
      reviewDialogVisible.value = false;
      loadMyReviews();
      loadMySignUp();
    } else {
      ElMessage.error(res.msg || '评价提交失败');
    }
  } catch (error) {
    console.error('评价提交失败', error);
    ElMessage.error('评价提交失败');
  }
};

// 查看评价
const viewReview = (row) => {
  router.push(`/manager/activityReview?activityId=${row.activityId}`);
};

onMounted(() => {
  loadMySignUp();
  loadMyReviews();
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
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 