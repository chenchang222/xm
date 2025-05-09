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
      <el-table-column label="回复内容" min-width="200">
        <template #default="scope">
          <div v-if="scope.row.replies && scope.row.replies.length > 0">
            <div v-for="reply in scope.row.replies" :key="reply.id" class="table-reply-item">
              <div class="table-reply-header">
                <span class="table-replyer-name">{{ reply.replyerName }}</span>
                <span class="table-reply-time">{{ formatDateTime(reply.createTime) }}</span>
              </div>
              <div class="table-reply-content" v-html="reply.content"></div>
            </div>
          </div>
          <span v-else class="no-reply">暂无回复</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="viewReviewDetail(scope.row)">
            查看
          </el-button>
          <el-button 
            type="success" 
            size="small" 
            @click="showReplyDialog(scope.row)">
            回复
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

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="回复评价"
      width="600px"
      :destroy-on-close="true"
      :close-on-click-modal="false">
      <div v-if="currentReview" class="reply-dialog-content">
        <div class="review-info">
          <h4>{{ currentReview.activityName }}</h4>
          <div class="review-meta">
            <span>志愿者: {{ currentReview.volunteerName }}</span>
            <span>评分: 
              <el-rate 
                v-model="currentReview.score" 
                disabled 
                show-score
                text-color="#ff9900"
                score-template="{value}" />
            </span>
          </div>
          <div class="review-content">{{ currentReview.content }}</div>
        </div>
        <div class="editor-container">
          <Toolbar
            style="border-bottom: 1px solid #ccc"
            :editor="replyDialogEditorRef"
            :default-config="toolbarConfig"
            :mode="mode"
          />
          <Editor
            style="height: 200px; overflow-y: hidden;"
            v-model="replyDialogContent"
            :default-config="editorConfig"
            :mode="mode"
            @on-created="handleReplyDialogCreated"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReplyFromDialog">发表回复</el-button>
        </span>
      </template>
    </el-dialog>

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

        <!-- 回复列表 -->
        <div v-if="replies.length > 0" class="replies-section">
          <h4>回复列表 ({{ replies.length }}条)</h4>
          <div v-for="reply in replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <span class="replyer-name">{{ reply.replyerName }}</span>
              <span class="reply-time">{{ formatDateTime(reply.createTime) }}</span>
            </div>
            <div class="reply-content" v-html="reply.content"></div>
            <div class="reply-actions">
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDeleteReply(reply.id)">
                删除回复
              </el-button>
            </div>
          </div>
        </div>

        <!-- 回复表单 -->
        <div class="reply-form">
          <h4>发表回复</h4>
          <div class="editor-container">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :default-config="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 200px; overflow-y: hidden;"
              v-model="replyContent"
              :default-config="editorConfig"
              :mode="mode"
              @on-created="handleCreated"
            />
          </div>
          <div class="reply-submit">
            <el-button type="primary" @click="submitReply">发表回复</el-button>
          </div>
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
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const detailDialogVisible = ref(false);
const currentReview = ref(null);
const replies = ref([]);
const replyContent = ref('');
const replyDialogVisible = ref(false);
const replyDialogContent = ref('');
const replyDialogEditorRef = shallowRef();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  score: null
});

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 工具栏配置
const toolbarConfig = {
  excludeKeys: []
};

// 编辑器配置
const editorConfig = {
  placeholder: '请输入回复内容...',
  MENU_CONF: {
    uploadImage: {
      // 使用base64格式存储图片，不发送到服务器
      customUpload(file, insertFn) {
        // 检查文件大小
        if (file.size > 5 * 1024 * 1024) {
          ElMessage.error('图片大小不能超过5MB');
          return;
        }
        
        // 读取文件为base64
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          // 插入base64格式图片
          insertFn(reader.result);
        };
        reader.onerror = () => {
          ElMessage.error('图片读取失败');
        };
      }
    }
  }
};

// 编辑器模式
const mode = ref('default');

// 编辑器创建完毕时的回调
const handleCreated = (editor) => {
  editorRef.value = editor;
};

// 组件卸载时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor != null) {
    editor.destroy();
  }
  const replyDialogEditor = replyDialogEditorRef.value;
  if (replyDialogEditor != null) {
    replyDialogEditor.destroy();
  }
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
          // 获取每个评价的回复
          for (const review of reviewRes.data) {
            try {
              const repliesRes = await request.get(`/activityReviewReply/listByReviewId/${review.id}`);
              if (repliesRes.code === '200') {
                review.replies = repliesRes.data || [];
              }
            } catch (error) {
              console.error(`获取评价${review.id}的回复失败`, error);
              review.replies = [];
            }
          }
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
const viewReviewDetail = async (row) => {
  currentReview.value = { ...row };
  detailDialogVisible.value = true;
  replyContent.value = '';
  
  // 加载回复列表
  try {
    const res = await request.get(`/activityReviewReply/listByReviewId/${row.id}`);
    if (res.code === '200') {
      replies.value = res.data || [];
    } else {
      replies.value = [];
      ElMessage.warning('获取回复列表失败');
    }
  } catch (error) {
    console.error('获取回复列表失败', error);
    replies.value = [];
  }
};

// 显示回复对话框
const showReplyDialog = (row) => {
  currentReview.value = { ...row };
  replyDialogVisible.value = true;
  replyDialogContent.value = '';
};

// 编辑器创建完毕时的回调
const handleReplyDialogCreated = (editor) => {
  replyDialogEditorRef.value = editor;
};

// 从对话框提交回复
const submitReplyFromDialog = async () => {
  if (!replyDialogContent.value.trim()) {
    ElMessage.warning('回复内容不能为空');
    return;
  }
  
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  if (!user.id) {
    ElMessage.warning('请先登录后再回复');
    return;
  }
  
  const replyData = {
    reviewId: currentReview.value.id,
    content: replyDialogContent.value,
    replyerId: user.id,
    replyerName: user.name
  };
  
  try {
    const res = await request.post('/activityReviewReply/add', replyData);
    if (res.code === '200') {
      ElMessage.success('回复成功');
      replyDialogContent.value = '';
      // 重置编辑器内容
      const editor = replyDialogEditorRef.value;
      if (editor) {
        editor.setHtml('');
      }
      replyDialogVisible.value = false;
      // 重新加载评价列表
      getList();
    } else {
      ElMessage.error(res.message || '回复失败');
    }
  } catch (error) {
    console.error('回复失败', error);
    ElMessage.error('回复失败');
  }
};

// 提交回复（用于详情对话框）
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空');
    return;
  }
  
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  if (!user.id) {
    ElMessage.warning('请先登录后再回复');
    return;
  }
  
  const replyData = {
    reviewId: currentReview.value.id,
    content: replyContent.value,
    replyerId: user.id,
    replyerName: user.name
  };
  
  try {
    const res = await request.post('/activityReviewReply/add', replyData);
    if (res.code === '200') {
      ElMessage.success('回复成功');
      replyContent.value = '';
      // 重置编辑器内容
      const editor = editorRef.value;
      if (editor) {
        editor.setHtml('');
      }
      // 重新加载回复列表
      const repliesRes = await request.get(`/activityReviewReply/listByReviewId/${currentReview.value.id}`);
      if (repliesRes.code === '200') {
        replies.value = repliesRes.data || [];
      }
      // 重新加载评价列表以更新回复显示
      getList();
    } else {
      ElMessage.error(res.message || '回复失败');
    }
  } catch (error) {
    console.error('回复失败', error);
    ElMessage.error('回复失败');
  }
};

// 删除回复
const handleDeleteReply = (id) => {
  ElMessageBox.confirm(
    '确定要删除该回复吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete(`/activityReviewReply/deleteById/${id}`);
      if (res.code === '200') {
        ElMessage.success('删除回复成功');
        // 重新加载回复列表
        const repliesRes = await request.get(`/activityReviewReply/listByReviewId/${currentReview.value.id}`);
        if (repliesRes.code === '200') {
          replies.value = repliesRes.data || [];
        }
      } else {
        ElMessage.error(res.message || '删除回复失败');
      }
    } catch (error) {
      console.error('删除回复失败', error);
      ElMessage.error('删除回复失败');
    }
  }).catch(() => {});
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

.replies-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.replies-section h4 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #303133;
}

.reply-item {
  padding: 12px;
  margin-bottom: 12px;
  background-color: #fafafa;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.replyer-name {
  font-weight: bold;
  color: #409EFF;
}

.reply-time {
  color: #909399;
}

.reply-content {
  margin: 10px 0;
  padding: 8px;
  border-radius: 4px;
  background-color: #f7f7f7;
  line-height: 1.6;
}

.reply-actions {
  margin-top: 8px;
  text-align: right;
}

.reply-form {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.reply-form h4 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #303133;
}

.reply-submit {
  margin-top: 10px;
  text-align: right;
}

.editor-container {
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 10px;
}

.reply-content :deep(img) {
  max-width: 100%;
  height: auto;
}

.reply-content :deep(p) {
  margin: 0.5em 0;
}

.reply-dialog-content {
  padding: 0 10px;
}

.review-info {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.review-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.review-meta {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.review-content {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.5;
}

.table-reply-item {
  padding: 8px;
  margin-bottom: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.table-reply-item:last-child {
  margin-bottom: 0;
}

.table-reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 12px;
}

.table-replyer-name {
  font-weight: bold;
  color: #409EFF;
}

.table-reply-time {
  color: #909399;
}

.table-reply-content {
  font-size: 13px;
  line-height: 1.4;
  color: #606266;
}

.table-reply-content :deep(img) {
  max-width: 100%;
  height: auto;
  max-height: 100px;
  object-fit: contain;
}

.no-reply {
  color: #909399;
  font-size: 13px;
}
</style> 