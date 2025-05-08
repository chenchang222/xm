<template>
  <div>
    <div class="header">
      <h2>志愿者交流论坛</h2>
      <el-button type="primary" @click="showCreatePostDialog">发布帖子</el-button>
    </div>

    <el-card class="search-bar">
      <el-input
        v-model="search.title"
        placeholder="搜索帖子标题"
        style="width: 240px; margin-right: 10px"
        :prefix-icon="Search"
      />
      <el-button type="primary" @click="loadPosts">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </el-card>

    <div class="post-list">
      <el-empty v-if="posts.length === 0" description="暂无帖子"></el-empty>
      <el-card v-for="post in posts" :key="post.id" class="post-card">
        <template #header>
          <div class="post-header">
            <h3 class="post-title" @click="viewPost(post)">{{ post.title }}</h3>
            <div class="post-meta">
              <span>{{ post.authorName }}</span>
              <span>{{ formatTime(post.createdTime) }}</span>
              <!-- 当帖子是当前用户发的，显示修改和删除按钮 -->
              <div v-if="isMyPost(post)" class="post-actions">
                <el-button size="small" type="primary" @click.stop="handleEditPost(post)">修改</el-button>
                <el-popconfirm
                  title="确定要删除这个帖子吗?"
                  @confirm="handleDeletePost(post.id)"
                >
                  <template #reference>
                    <el-button size="small" type="danger" @click.stop>删除</el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </div>
        </template>
        <!-- <div class="post-content" v-html="truncateContent(post.content)" @click="viewPost(post)"></div> -->
        <div class="post-stats">
          <el-tag size="small" type="info">浏览 {{ post.viewCount }}</el-tag>
          <el-tag size="small" type="success">点赞 {{ post.likeCount }}</el-tag>
          <el-tag size="small" type="primary">评论 {{ post.commentCount }}</el-tag>
        </div>
      </el-card>

      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </div>

    <!-- 发布帖子弹窗 -->
    <el-dialog v-model="createPostVisible" :title="editMode ? '修改帖子' : '发布帖子'" width="70%">
      <el-form :model="newPost" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="newPost.title" placeholder="请输入帖子标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" required>
          <div class="editor-container">
            <Toolbar
              style="border-bottom: 1px solid #ccc"
              :editor="editorRef"
              :default-config="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 300px; overflow-y: hidden;"
              v-model="newPost.html"
              :default-config="editorConfig"
              :mode="mode"
              @on-created="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelCreate">取消</el-button>
          <el-button type="primary" @click="submitPost">{{ editMode ? '保存修改' : '发布' }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';

const router = useRouter();
const posts = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const search = reactive({
  title: ''
});

const createPostVisible = ref(false);
const editMode = ref(false); // 是否为编辑模式
const newPost = reactive({
  id: null,
  title: '',
  content: '',
  html: '',
  authorId: null,
  authorName: ''
});

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 内容 HTML
const valueHtml = ref('');

// 工具栏配置
const toolbarConfig = {
  excludeKeys: []
};

// 编辑器配置
const editorConfig = {
  placeholder: '请输入内容...',
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

const user = JSON.parse(localStorage.getItem('xm-user') || '{}');

// 判断帖子是否为当前用户发布的
const isMyPost = (post) => {
  return user.id && post.authorId === user.id;
};

// 加载帖子列表
const loadPosts = () => {
  request.get('/post/selectPage', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      title: search.title
    }
  }).then(res => {
    if (res.code === '200') {
      posts.value = res.data.list || [];
      total.value = res.data.total || 0;
    }
  });
};

// 重置搜索
const resetSearch = () => {
  search.title = '';
  loadPosts();
};

// 处理分页变化
const handlePageChange = (val) => {
  pageNum.value = val;
  loadPosts();
};

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  loadPosts();
};

// 展示帖子创建弹窗
const showCreatePostDialog = () => {
  if (!user.id) {
    ElMessage.warning('请先登录后再发布帖子');
    return;
  }
  
  editMode.value = false;
  newPost.id = null;
  newPost.title = '';
  newPost.html = '';
  newPost.authorId = user.id;
  newPost.authorName = user.name;
  createPostVisible.value = true;
};

// 处理编辑帖子
const handleEditPost = (post) => {
  if (!user.id) {
    ElMessage.warning('请先登录后再修改帖子');
    return;
  }
  
  if (user.id !== post.authorId) {
    ElMessage.warning('只能修改自己发布的帖子');
    return;
  }
  
  editMode.value = true;
  newPost.id = post.id;
  newPost.title = post.title;
  newPost.html = post.content;
  newPost.authorId = post.authorId;
  newPost.authorName = post.authorName;
  createPostVisible.value = true;
};

// 处理删除帖子
const handleDeletePost = (postId) => {
  request.delete(`/post/deleteById/${postId}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('删除成功');
      loadPosts();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  });
};

// 取消创建/编辑帖子
const cancelCreate = () => {
  createPostVisible.value = false;
  // 延迟清空内容以避免视觉闪烁
  setTimeout(() => {
    newPost.title = '';
    newPost.html = '';
    editMode.value = false;
  }, 300);
};

// 提交帖子（创建或更新）
const submitPost = () => {
  if (!newPost.title) {
    ElMessage.warning('请输入帖子标题');
    return;
  }
  if (!newPost.html) {
    ElMessage.warning('请输入帖子内容');
    return;
  }
  
  // 将富文本内容保存到content字段
  newPost.content = newPost.html;
  
  if (editMode.value) {
    // 更新帖子
    request.put('/post/update', newPost).then(res => {
      if (res.code === '200') {
        ElMessage.success('修改成功');
        createPostVisible.value = false;
        loadPosts();
      } else {
        ElMessage.error(res.message || '修改失败');
      }
    });
  } else {
    // 新建帖子
    request.post('/post/add', newPost).then(res => {
      if (res.code === '200') {
        ElMessage.success('发布成功');
        createPostVisible.value = false;
        loadPosts();
      } else {
        ElMessage.error(res.message || '发布失败');
      }
    });
  }
};

// 查看帖子详情
const viewPost = (post) => {
  router.push(`/manager/postDetail/${post.id}`);
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  return dayjs(time).format('YYYY-MM-DD HH:mm');
};

// 截断内容并保留HTML标签
const truncateContent = (content) => {
  if (!content) return '';
  // 创建一个临时DOM元素来解析HTML并提取纯文本
  const tempElement = document.createElement('div');
  tempElement.innerHTML = content;
  const text = tempElement.textContent || tempElement.innerText || '';
  
  // 如果文本太长，截断它
  if (text.length > 100) {
    // 为了安全，返回截断后的纯文本内容
    const truncatedHtml = content.substring(0, 300); // 保留更多HTML以确保标签闭合
    tempElement.innerHTML = truncatedHtml;
    return tempElement.innerHTML + '...';
  }
  return content;
};

// 组件卸载时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.post-list {
  margin-top: 20px;
}

.post-card {
  margin-bottom: 15px;
}

.post-title {
  cursor: pointer;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-meta {
  display: flex;
  gap: 15px;
  color: #888;
  font-size: 14px;
  align-items: center;
}

.post-content {
  margin: 10px 0;
  color: #555;
  line-height: 1.5;
  cursor: pointer;
}

.post-content :deep(img) {
  max-width: 100%;
  height: auto;
}

.post-stats {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
}

.editor-container {
  border: 1px solid #ccc;
  z-index: 100;
}
</style> 