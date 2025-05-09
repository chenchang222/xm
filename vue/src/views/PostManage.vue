<template>
  <div class="container">
    <div class="header">
      <h2>论坛帖子管理</h2>
      <div class="filter-container">
        <el-input 
          v-model="queryParams.title" 
          placeholder="请输入标题查询" 
          clearable 
          @keyup.enter="getList"
          @clear="getList"
          style="width: 240px; margin-right: 10px;">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="getList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      style="width: 100%">
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip/>
      <el-table-column prop="volunteerName" label="发布者" width="120" v-if="false"/>
      <el-table-column prop="viewCount" label="浏览量" width="100"/>
      <el-table-column prop="likeCount" label="点赞数" width="100"/>
      <el-table-column prop="commentCount" label="评论数" width="100"/>
      <el-table-column prop="createTime" label="发布时间" width="160" v-if="false">
        <template #default="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="220">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="viewPostDetail(scope.row)">
            查看
          </el-button>
          <el-button 
            type="success" 
            size="small" 
            @click="showCommentDialog(scope.row)">
            评论
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

    <!-- 帖子详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="帖子详情"
      width="800px"
      top="5vh"
      :destroy-on-close="true"
      :close-on-click-modal="false">
      <div v-if="currentPost" class="post-detail">
        <h3 class="post-title">{{ currentPost.title }}</h3>
        <div class="post-meta">
          <span v-if="false">作者: {{ currentPost.volunteerName }}</span>
          <span v-if="false">发布时间: {{ formatDateTime(currentPost.createTime) }}</span>
          <span>浏览量: {{ currentPost.viewCount }}</span>
          <span>点赞数: {{ currentPost.likeCount }}</span>
          <span>评论数: {{ currentPost.commentCount }}</span>
        </div>
        <div class="post-content" v-html="formatContent(currentPost.content)"></div>

        <!-- 评论列表 -->
        <div class="post-comments" v-if="comments.length > 0">
          <h4>评论列表 ({{ comments.length }}条)</h4>
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="commenter-name">{{ comment.commenterName }}</span>
              <span class="comment-time">{{ formatDateTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content" v-html="comment.content"></div>
            <div class="comment-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click="handleReply(comment)">
                回复
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDeleteComment(comment.id)">
                删除评论
              </el-button>
            </div>
            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="reply-header">
                  <span class="replyer-name">{{ reply.commenterName }}</span>
                  <span class="reply-time">{{ formatDateTime(reply.createTime) }}</span>
                </div>
                <div class="reply-content" v-html="reply.content"></div>
                <div class="reply-actions">
                  <el-button 
                    type="danger" 
                    size="small" 
                    @click="handleDeleteComment(reply.id)">
                    删除回复
                  </el-button>
                </div>
              </div>
            </div>
            <!-- 回复表单 -->
            <div v-if="replyingTo === comment.id" class="reply-form">
              <div class="reply-to">
                回复 <span class="reply-to-name">{{ comment.commenterName }}</span>：
              </div>
              <div class="editor-container">
                <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="replyEditorRef"
                  :default-config="toolbarConfig"
                  :mode="mode"
                />
                <Editor
                  style="height: 150px; overflow-y: hidden;"
                  v-model="replyHtml"
                  :default-config="editorConfig"
                  :mode="mode"
                  @on-created="handleReplyCreated"
                />
              </div>
              <div class="reply-submit">
                <el-button @click="cancelReply">取消</el-button>
                <el-button type="primary" @click="submitReply(comment.id)">发表回复</el-button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-comments">
          暂无评论
        </div>

        <!-- 发表评论 -->
        <div class="comment-form">
          <h4>发表评论</h4>
          <div class="editor-container">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :default-config="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 200px; overflow-y: hidden;"
              v-model="commentHtml"
              :default-config="editorConfig"
              :mode="mode"
              @on-created="handleCreated"
            />
          </div>
          <div class="comment-submit">
            <el-button type="primary" @click="submitComment">发表评论</el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="danger" @click="handleDeleteFromDialog">删除帖子</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog
      v-model="commentDialogVisible"
      title="发表评论"
      width="600px"
      :destroy-on-close="true"
      :close-on-click-modal="false">
      <div v-if="currentPost" class="comment-dialog-content">
        <div class="post-info">
          <h4>{{ currentPost.title }}</h4>
          <div class="post-meta">
            <span>浏览量: {{ currentPost.viewCount }}</span>
            <span>点赞数: {{ currentPost.likeCount }}</span>
            <span>评论数: {{ currentPost.commentCount }}</span>
          </div>
        </div>
        <div class="editor-container">
          <Toolbar
            style="border-bottom: 1px solid #ccc"
            :editor="commentDialogEditorRef"
            :default-config="toolbarConfig"
            :mode="mode"
          />
          <Editor
            style="height: 200px; overflow-y: hidden;"
            v-model="commentDialogHtml"
            :default-config="editorConfig"
            :mode="mode"
            @on-created="handleCommentDialogCreated"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="commentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCommentFromDialog">发表评论</el-button>
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
const currentPost = ref(null);
const comments = ref([]);
const commentHtml = ref('');
const replyingTo = ref(null);
const replyHtml = ref('');
const commentDialogVisible = ref(false);
const commentDialogHtml = ref('');
const commentDialogEditorRef = shallowRef();

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();
const replyEditorRef = shallowRef();

// 工具栏配置
const toolbarConfig = {
  excludeKeys: []
};

// 编辑器配置
const editorConfig = {
  placeholder: '请输入评论内容...',
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

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: ''
});

// 获取帖子列表
const getList = async () => {
  loading.value = true;
  try {
    const res = await request.get('/post/selectPage', { 
      params: queryParams 
    });
    if (res.code === '200') {
      tableData.value = res.data.list || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || '获取帖子列表失败');
    }
  } catch (error) {
    console.error('获取帖子列表失败', error);
    ElMessage.error('获取帖子列表失败');
  } finally {
    loading.value = false;
  }
};

// 重置查询条件
const resetQuery = () => {
  queryParams.title = '';
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

// 格式化帖子内容，将换行符转换为HTML
const formatContent = (content) => {
  if (!content) return '';
  return content.replace(/\n/g, '<br>');
};

const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  getList();
};

const handleCurrentChange = (page) => {
  queryParams.pageNum = page;
  getList();
};

// 查看帖子详情
const viewPostDetail = async (row) => {
  currentPost.value = { ...row };
  detailDialogVisible.value = true;
  
  // 加载评论 - 修正API路径
  try {
    const res = await request.get(`/postComment/selectByPostId/${row.id}`);
    if (res.code === '200') {
      // 处理评论和回复的关系
      const allComments = res.data || [];
      const commentsMap = new Map();
      const rootComments = [];

      // 首先将所有评论放入Map中
      allComments.forEach(comment => {
        commentsMap.set(comment.id, { ...comment, replies: [] });
      });

      // 然后处理评论的层级关系
      allComments.forEach(comment => {
        if (comment.parentId) {
          // 这是一个回复
          const parentComment = commentsMap.get(comment.parentId);
          if (parentComment) {
            parentComment.replies.push(commentsMap.get(comment.id));
          }
        } else {
          // 这是一个根评论
          rootComments.push(commentsMap.get(comment.id));
        }
      });

      comments.value = rootComments;
    } else {
      comments.value = [];
      ElMessage.warning('获取评论失败');
    }
  } catch (error) {
    console.error('获取评论失败', error);
    comments.value = [];
  }
};

// 删除帖子
const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该帖子吗？此操作将同时删除所有相关评论，不可逆',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await request.delete(`/post/deleteById/${id}`);
      if (res.code === '200') {
        ElMessage.success('删除成功');
        getList();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    } catch (error) {
      console.error('删除失败', error);
      ElMessage.error('删除失败');
    }
  }).catch(() => {});
};

// 在对话框中删除
const handleDeleteFromDialog = () => {
  if (currentPost.value) {
    handleDelete(currentPost.value.id);
    detailDialogVisible.value = false;
  }
};

// 删除评论
const handleDeleteComment = (id) => {
  ElMessageBox.confirm(
    '确定要删除该评论吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // 修正删除评论的API路径
      const res = await request.delete(`/postComment/deleteById/${id}`);
      if (res.code === '200') {
        ElMessage.success('删除评论成功');
        // 刷新评论列表
        if (currentPost.value) {
          const commentsRes = await request.get(`/postComment/selectByPostId/${currentPost.value.id}`);
          if (commentsRes.code === '200') {
            comments.value = commentsRes.data || [];
            // 更新当前帖子的评论数
            currentPost.value.commentCount = comments.value.length;
          }
        }
      } else {
        ElMessage.error(res.msg || '删除评论失败');
      }
    } catch (error) {
      console.error('删除评论失败', error);
      ElMessage.error('删除评论失败');
    }
  }).catch(() => {});
};

// 提交评论
const submitComment = () => {
  if (!commentHtml.value.trim()) {
    ElMessage.warning('评论内容不能为空');
    return;
  }
  
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  if (!user.id) {
    ElMessage.warning('请先登录后再评论');
    return;
  }
  
  const commentData = {
    postId: currentPost.value.id,
    content: commentHtml.value,
    commenterId: user.id,
    commenterName: user.name
  };
  
  request.post('/postComment/add', commentData).then(res => {
    if (res.code === '200') {
      ElMessage.success('评论成功');
      commentHtml.value = '';
      // 重置编辑器内容
      const editor = editorRef.value;
      if (editor) {
        editor.setHtml('');
      }
      // 重新加载评论
      if (currentPost.value) {
        const commentsRes = request.get(`/postComment/selectByPostId/${currentPost.value.id}`);
        if (commentsRes.code === '200') {
          comments.value = commentsRes.data || [];
          // 更新当前帖子的评论数
          currentPost.value.commentCount = comments.value.length;
        }
      }
    } else {
      ElMessage.error(res.message || '评论失败');
    }
  });
};

// 处理回复评论
const handleReply = (comment) => {
  replyingTo.value = comment.id;
  replyHtml.value = '';
};

// 取消回复
const cancelReply = () => {
  replyingTo.value = null;
  replyHtml.value = '';
  const editor = replyEditorRef.value;
  if (editor) {
    editor.setHtml('');
  }
};

// 提交回复
const submitReply = (parentCommentId) => {
  if (!replyHtml.value.trim()) {
    ElMessage.warning('回复内容不能为空');
    return;
  }
  
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  if (!user.id) {
    ElMessage.warning('请先登录后再回复');
    return;
  }
  
  const replyData = {
    postId: currentPost.value.id,
    content: replyHtml.value,
    commenterId: user.id,
    commenterName: user.name,
    parentId: parentCommentId
  };
  
  request.post('/postComment/add', replyData).then(res => {
    if (res.code === '200') {
      ElMessage.success('回复成功');
      replyHtml.value = '';
      // 重置编辑器内容
      const editor = replyEditorRef.value;
      if (editor) {
        editor.setHtml('');
      }
      // 重新加载评论
      if (currentPost.value) {
        const commentsRes = request.get(`/postComment/selectByPostId/${currentPost.value.id}`);
        if (commentsRes.code === '200') {
          comments.value = commentsRes.data || [];
          // 更新当前帖子的评论数
          currentPost.value.commentCount = comments.value.length;
        }
      }
      replyingTo.value = null;
    } else {
      ElMessage.error(res.message || '回复失败');
    }
  });
};

// 编辑器创建完毕时的回调
const handleReplyCreated = (editor) => {
  replyEditorRef.value = editor;
};

// 显示评论对话框
const showCommentDialog = (row) => {
  currentPost.value = { ...row };
  commentDialogVisible.value = true;
  commentDialogHtml.value = '';
};

// 编辑器创建完毕时的回调
const handleCommentDialogCreated = (editor) => {
  commentDialogEditorRef.value = editor;
};

// 从对话框提交评论
const submitCommentFromDialog = () => {
  if (!commentDialogHtml.value.trim()) {
    ElMessage.warning('评论内容不能为空');
    return;
  }
  
  const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
  if (!user.id) {
    ElMessage.warning('请先登录后再评论');
    return;
  }
  
  const commentData = {
    postId: currentPost.value.id,
    content: commentDialogHtml.value,
    commenterId: user.id,
    commenterName: user.name
  };
  
  request.post('/postComment/add', commentData).then(res => {
    if (res.code === '200') {
      ElMessage.success('评论成功');
      commentDialogHtml.value = '';
      // 重置编辑器内容
      const editor = commentDialogEditorRef.value;
      if (editor) {
        editor.setHtml('');
      }
      // 更新帖子列表中的评论数
      const index = tableData.value.findIndex(item => item.id === currentPost.value.id);
      if (index !== -1) {
        tableData.value[index].commentCount = (tableData.value[index].commentCount || 0) + 1;
      }
      commentDialogVisible.value = false;
    } else {
      ElMessage.error(res.message || '评论失败');
    }
  });
};

// 组件卸载时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor != null) {
    editor.destroy();
  }
  const replyEditor = replyEditorRef.value;
  if (replyEditor != null) {
    replyEditor.destroy();
  }
  const commentDialogEditor = commentDialogEditorRef.value;
  if (commentDialogEditor != null) {
    commentDialogEditor.destroy();
  }
});

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

.post-detail {
  padding: 0 10px;
  max-height: 60vh;
  overflow-y: auto;
}

.post-title {
  font-size: 22px;
  margin-bottom: 15px;
  color: #303133;
  font-weight: bold;
  word-break: break-all;
}

.post-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.post-content {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
  line-height: 1.6;
  min-height: 100px;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-content :deep(img) {
  max-width: 100%;
  height: auto;
}

.post-comments {
  margin-top: 20px;
}

.post-comments h4 {
  font-size: 16px;
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}

.comment-item {
  padding: 12px;
  border-bottom: 1px dashed #ebeef5;
  margin-bottom: 12px;
  background-color: #fafafa;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.commenter-name {
  font-weight: bold;
  color: #409EFF;
}

.comment-time {
  color: #909399;
}

.comment-content {
  margin: 10px 0;
  padding: 8px;
  border-radius: 4px;
  background-color: #f7f7f7;
  line-height: 1.6;
  white-space: normal;
}

.comment-content :deep(img) {
  max-width: 100%;
  height: auto;
}

.comment-content :deep(p) {
  margin: 0.5em 0;
}

.comment-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}

.no-comments {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 20px 0;
}

.comment-form {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.comment-submit {
  margin-top: 10px;
  text-align: right;
}

.reply-list {
  margin-left: 20px;
  margin-top: 10px;
  padding-left: 10px;
  border-left: 2px solid #e0e0e0;
}

.reply-item {
  margin-bottom: 10px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 13px;
}

.replyer-name {
  font-weight: bold;
  color: #409EFF;
}

.reply-time {
  color: #909399;
}

.reply-content {
  margin: 5px 0;
  padding: 5px;
  background-color: #fff;
  border-radius: 4px;
  line-height: 1.5;
}

.reply-actions {
  margin-top: 5px;
  text-align: right;
}

.reply-form {
  margin-top: 10px;
  margin-left: 20px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.reply-submit {
  margin-top: 10px;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.comment-dialog-content {
  padding: 0 10px;
}

.post-info {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.post-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.post-info .post-meta {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #909399;
}

.reply-to {
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.reply-to-name {
  color: #409EFF;
  font-weight: bold;
}
</style> 