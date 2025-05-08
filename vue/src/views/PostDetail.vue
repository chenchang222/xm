<template>
  <div class="post-detail-container">
    <el-page-header @back="goBack" title="返回论坛" />

    <!-- 帖子详情 -->
    <el-card v-if="post" class="post-detail-card">
      <div class="post-title">
        <h1>{{ post.title }}</h1>
        <div class="post-meta">
          <span v-if="false">作者：{{ post.authorName }}</span>
          <span v-if="false">发布于：{{ formatTime(post.createdTime) }}</span>
          <span>浏览：{{ post.viewCount }}</span>
        </div>
      </div>
      <div class="post-content" v-html="post.content"></div>
      <div class="post-actions">
        <!-- 显示修改/删除按钮（仅限帖子作者） -->
        <div v-if="isMyPost(post)" class="author-actions">
          <el-button type="primary" size="small" @click="handleEditPost">修改</el-button>
          <el-popconfirm
            title="确定要删除这个帖子吗?"
            @confirm="handleDeletePost"
          >
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </div>

        <el-button 
          :type="post.hasLiked ? 'danger' : 'default'" 
          :icon="post.hasLiked ? 'Star' : 'StarFilled'"
          @click="toggleLike"
        >
          {{ post.likeCount || 0 }} 点赞
        </el-button>
      </div>
    </el-card>

    <!-- 评论区 -->
    <div class="comment-section">
      <h2>评论区 ({{ comments.length }})</h2>
      
      <!-- 发表评论 -->
      <el-card class="comment-form">
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
      </el-card>

      <!-- 评论列表 -->
      <el-card v-if="comments.length > 0" class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="commenter-name">{{ comment.commenterName }}</span>
            <div class="comment-actions">
              <span class="comment-time">{{ formatTime(comment.createdTime) }}</span>
              <!-- 如果是用户自己的评论，显示删除按钮 -->
              <el-popconfirm
                v-if="isMyComment(comment)"
                title="确定要删除这条评论吗?"
                @confirm="handleDeleteComment(comment.id)"
              >
                <template #reference>
                  <el-button type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
          <div class="comment-content" v-html="comment.content"></div>
        </div>
      </el-card>
      <el-empty v-else description="暂无评论，快来发表你的看法吧！"></el-empty>
    </div>

    <!-- 编辑帖子弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改帖子" width="70%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="editForm.title" placeholder="请输入帖子标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" required>
          <div class="editor-container">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editEditorRef"
              :default-config="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 300px; overflow-y: hidden;"
              v-model="editForm.html"
              :default-config="editorConfig"
              :mode="mode"
              @on-created="handleEditCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditPost">保存修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, shallowRef, onBeforeUnmount, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';

const route = useRoute();
const router = useRouter();
const postId = ref(parseInt(route.params.id));
const post = ref(null);
const comments = ref([]);
const commentHtml = ref('');
const user = JSON.parse(localStorage.getItem('xm-user') || '{}');

// 编辑相关
const editDialogVisible = ref(false);
const editEditorRef = shallowRef();
const editForm = reactive({
  id: null,
  title: '',
  content: '',
  html: ''
});

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 工具栏配置
const toolbarConfig = {
  excludeKeys: []
};

// 编辑器配置
const editorConfig = {
  placeholder: '写下你的评论...',
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

// 编辑器创建完毕时的回调 (编辑帖子)
const handleEditCreated = (editor) => {
  editEditorRef.value = editor;
};

// 判断帖子是否为当前用户发布的
const isMyPost = (post) => {
  return user.id && post.authorId === user.id;
};

// 判断评论是否为当前用户发布的
const isMyComment = (comment) => {
  return user.id && comment.commenterId === user.id;
};

// 加载帖子详情
const loadPostDetail = () => {
  request.get(`/post/selectById/${postId.value}`, {
    params: { userId: user.id }
  }).then(res => {
    if (res.code === '200') {
      post.value = res.data;
    } else {
      ElMessage.error('获取帖子失败');
    }
  });
};

// 加载评论
const loadComments = () => {
  request.get(`/postComment/selectByPostId/${postId.value}`).then(res => {
    if (res.code === '200') {
      comments.value = res.data || [];
    }
  });
};

// 提交评论
const submitComment = () => {
  if (!user.id) {
    ElMessage.warning('请先登录后再评论');
    return;
  }
  
  if (!commentHtml.value.trim()) {
    ElMessage.warning('评论内容不能为空');
    return;
  }
  
  const commentData = {
    postId: postId.value,
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
      loadComments(); // 重新加载评论
      loadPostDetail(); // 更新评论数
    } else {
      ElMessage.error(res.message || '评论失败');
    }
  });
};

// 删除评论
const handleDeleteComment = (commentId) => {
  request.delete(`/postComment/deleteById/${commentId}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('评论删除成功');
      loadComments(); // 重新加载评论
      loadPostDetail(); // 更新评论数
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  });
};

// 编辑帖子
const handleEditPost = () => {
  editForm.id = post.value.id;
  editForm.title = post.value.title;
  editForm.html = post.value.content;
  editDialogVisible.value = true;
};

// 删除帖子
const handleDeletePost = () => {
  request.delete(`/post/deleteById/${postId.value}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('帖子已删除');
      router.push('/manager/postList');
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  });
};

// 提交编辑后的帖子
const submitEditPost = () => {
  if (!editForm.title) {
    ElMessage.warning('请输入帖子标题');
    return;
  }
  if (!editForm.html) {
    ElMessage.warning('请输入帖子内容');
    return;
  }
  
  // 将富文本内容保存到content字段
  const postData = {
    id: editForm.id,
    title: editForm.title,
    content: editForm.html,
    authorId: post.value.authorId,
    authorName: post.value.authorName
  };
  
  request.put('/post/update', postData).then(res => {
    if (res.code === '200') {
      ElMessage.success('修改成功');
      editDialogVisible.value = false;
      loadPostDetail(); // 重新加载帖子详情
    } else {
      ElMessage.error(res.message || '修改失败');
    }
  });
};

// 点赞/取消点赞
const toggleLike = () => {
  if (!user.id) {
    ElMessage.warning('请先登录后再点赞');
    return;
  }
  
  request.post('/post/toggleLike', null, {
    params: {
      postId: postId.value,
      userId: user.id
    }
  }).then(res => {
    if (res.code === '200') {
      // 更新帖子点赞状态和数量
      post.value.hasLiked = !post.value.hasLiked;
      post.value.likeCount = post.value.hasLiked 
        ? (post.value.likeCount || 0) + 1 
        : Math.max((post.value.likeCount || 0) - 1, 0);
      
      ElMessage.success(post.value.hasLiked ? '点赞成功' : '取消点赞成功');
    }
  });
};

// 返回论坛列表
const goBack = () => {
  router.push('/manager/postList');
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return dayjs(time).format('YYYY-MM-DD HH:mm');
};

// 组件卸载时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor != null) {
    editor.destroy();
  }
  
  const editEditor = editEditorRef.value;
  if (editEditor != null) {
    editEditor.destroy();
  }
});

onMounted(() => {
  if (!postId.value) {
    ElMessage.error('参数错误');
    router.push('/postList');
    return;
  }
  
  loadPostDetail();
  loadComments();
});
</script>

<style scoped>
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.post-detail-card {
  margin: 20px 0;
}

.post-title {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.post-title h1 {
  margin-bottom: 10px;
}

.post-meta {
  display: flex;
  gap: 15px;
  color: #888;
  font-size: 14px;
}

.post-content {
  padding: 10px 0;
  line-height: 1.8;
  font-size: 16px;
  white-space: pre-wrap;
}

.post-content :deep(img) {
  max-width: 100%;
  height: auto;
}

.post-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}

.author-actions {
  display: flex;
  gap: 10px;
}

.comment-section {
  margin-top: 30px;
}

.comment-section h2 {
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 20px;
}

.editor-container {
  border: 1px solid #ccc;
  z-index: 100;
}

.comment-submit {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.commenter-name {
  font-weight: bold;
}

.comment-time {
  color: #888;
  font-size: 14px;
}

.comment-content {
  line-height: 1.6;
}

.comment-content :deep(img) {
  max-width: 100%;
  height: auto;
}
</style> 