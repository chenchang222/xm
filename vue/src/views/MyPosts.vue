<template>
  <div>
    <div class="header">
      <h2>我的发帖</h2>
      <el-button type="primary" @click="goToForum">去论坛发帖</el-button>
    </div>

    <div class="post-list">
      <el-empty v-if="posts.length === 0" description="暂无发帖记录"></el-empty>
      <el-card v-for="post in posts" :key="post.id" class="post-card">
        <template #header>
          <div class="post-header">
            <h3 class="post-title" @click="viewPost(post)">{{ post.title }}</h3>
            <div class="post-meta">
              <span>{{ formatTime(post.createdTime) }}</span>
              <div class="post-actions">
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
              :editor="editorRef"
              :default-config="toolbarConfig"
              :mode="mode"
            />
            <Editor
              style="height: 300px; overflow-y: hidden;"
              v-model="editForm.html"
              :default-config="editorConfig"
              :mode="mode"
              @on-created="handleCreated"
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
import { ref, reactive, onMounted, shallowRef, onBeforeUnmount, computed } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';

const router = useRouter();
const allPosts = ref([]); // 存储所有帖子
const posts = computed(() => {
  const startIndex = (pageNum.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return allPosts.value.slice(startIndex, endIndex);
});
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 编辑相关
const editDialogVisible = ref(false);
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

// 加载我的帖子列表
const loadMyPosts = () => {
  if (!user.id) {
    ElMessage.warning('请先登录');
    return;
  }

  request.get(`/post/selectByAuthorId/${user.id}`).then(res => {
    if (res.code === '200') {
      allPosts.value = res.data || [];
      total.value = allPosts.value.length;
    }
  });
};

// 处理分页变化
const handlePageChange = (val) => {
  pageNum.value = val;
  // 不需要重新请求数据，computed属性posts会自动更新
};

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  pageNum.value = 1; // 重置到第一页
  // 不需要重新请求数据，computed属性posts会自动更新
};

// 查看帖子详情
const viewPost = (post) => {
  router.push(`/manager/postDetail/${post.id}`);
};

// 去论坛发帖
const goToForum = () => {
  router.push('/manager/postList');
};

// 处理编辑帖子
const handleEditPost = (post) => {
  editForm.id = post.id;
  editForm.title = post.title;
  editForm.html = post.content;
  editDialogVisible.value = true;
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
    authorId: user.id,
    authorName: user.name
  };
  
  request.put('/post/update', postData).then(res => {
    if (res.code === '200') {
      ElMessage.success('修改成功');
      editDialogVisible.value = false;
      loadMyPosts(); // 重新加载帖子列表
    } else {
      ElMessage.error(res.message || '修改失败');
    }
  });
};

// 处理删除帖子
const handleDeletePost = (postId) => {
  request.delete(`/post/deleteById/${postId}`).then(res => {
    if (res.code === '200') {
      ElMessage.success('删除成功');
      loadMyPosts();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  });
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
  if (editor) {
    editor.destroy();
  }
});

onMounted(() => {
  loadMyPosts();
});
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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