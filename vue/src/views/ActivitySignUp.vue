<template>
  <div>
    <div>
      <el-card>
        <el-input style="width: 240px;margin-right: 10px" v-model="data.title" placeholder="请输入活动标题查询" :prefix-icon="Search"></el-input>
        <el-select v-model="data.type" placeholder="请选择活动类型" style="width: 240px;margin-right: 10px">
          <el-option label="全部" value=""></el-option>
          <el-option v-for="item in data.activityTypes" :key="item.type" :label="item.type" :value="item.type"/>
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button type="primary" @click="reset">重置</el-button>
      </el-card>
      <div class="card" style="margin-bottom: 5px">
        <el-table :data="data.tableData" stripe>
          <el-table-column label="活动类型" prop="type"/>
          <el-table-column label="活动地点" prop="location"/>
          <el-table-column label="活动标题" prop="title"/>
          <el-table-column label="活动描述" prop="description"/>
          <el-table-column label="开始时间" prop="startTime"/>
          <el-table-column label="结束时间" prop="endTime"/>
          <el-table-column label="状态" prop="status"/>
          <el-table-column label="操作" width="220" align="center">
            <template #default="scope">
              <div class="button-group">
                <el-button 
                  @click="handleSignUp(scope.row)" 
                  type="primary" 
                  :icon="isAlreadySignedUp(scope.row.id) ? Check : Plus"
                  size="small"
                  :disabled="isAlreadySignedUp(scope.row.id)">
                  {{ isAlreadySignedUp(scope.row.id) ? '已报名' : '报名' }}
                </el-button>
                <el-button 
                  @click="handleToggleFavorite(scope.row)" 
                  :type="isFavorite(scope.row.id) ? 'danger' : 'info'" 
                  :icon="isFavorite(scope.row.id) ? Star : StarFilled"
                  size="small">
                  {{ isFavorite(scope.row.id) ? '取消收藏' : '收藏' }}
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:10px">
          <el-pagination
              @current-change="load"
              @size-change="load"
              v-model:current-page="data.pageNum"
              v-model:page-size="data.pageSize"
              :page-sizes="[5,10,15,20]"
              background
              layout="total, prev, pager, next, jumper"
              :total="data.total"
          />
        </div>
      </div>
    </div>

    <!-- 我的报名记录 -->
    <div class="card">
      <h3>我的报名记录</h3>
      <el-table :data="data.mySignUpList" stripe>
        <el-table-column label="活动名称" prop="activityName"/>
        <el-table-column label="联系方式" prop="qq"/>
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
        <el-table-column label="操作">
          <template #default="scope">
            <el-button @click="handleCancelSignUp(scope.row.id)" type="danger" :icon="Delete">取消报名</el-button>
            <el-button 
              v-if="canSignIn(scope.row)" 
              @click="handleSignIn(scope.row)" 
              type="primary"
            >签到</el-button>
            <el-button 
              @click="handleReview(scope.row)" 
              type="primary" 
              :disabled="!canReview(scope.row) || hasReviewed(scope.row.activityId)"
              :icon="hasReviewed(scope.row.activityId) ? 'Check' : 'EditPen'">
              {{ hasReviewed(scope.row.activityId) ? '已评价' : '评价' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 报名弹窗 -->
    <el-dialog v-model="data.signUpVisible" title="活动报名" width="30%">
      <el-form :model="data.form" label-width="80px">
        <el-form-item label="联系方式" required>
          <el-input v-model="data.form.qq" placeholder="请输入联系方号"/>
        </el-form-item>
        <el-form-item label="报名人数" required>
          <el-input-number v-model="data.form.num" :min="1" :max="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.signUpVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSignUp">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="data.reviewVisible" title="活动评价" width="40%">
      <el-form :model="data.reviewForm" label-width="80px">
        <el-form-item label="活动名称">
          <span>{{ data.reviewForm.activityName }}</span>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="data.reviewForm.score" show-score :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input 
            v-model="data.reviewForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入您对活动的评价（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.reviewVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {reactive, onMounted} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {Search, Plus, Delete, Star, StarFilled, EditPen, Check} from '@element-plus/icons-vue';

const data = reactive({
  title: null,
  type: null,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  signUpVisible: false,
  reviewVisible: false,
  form: {},
  reviewForm: {
    score: 5,
    content: ''
  },
  mySignUpList: [],
  activityTypes: [],
  user: JSON.parse(localStorage.getItem("xm-user")),
  favoriteActivityIds: [], // 已收藏的活动ID列表
  reviewedActivityIds: [], // 已评价的活动ID列表
  currentActivity: null,
  signedUpActivityIds: [] // 已报名的活动ID列表
});

// 加载活动类型
const loadActivityTypes = () => {
  request.get('activityType/selectAll').then(res => {
    data.activityTypes = res.data || [];
  });
};

// 加载活动列表
const load = () => {
  request.get('activity/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title,
      type: data.type
    }
  }).then(res => {
    data.tableData = res.data?.list || [];
    data.total = res.data?.total || 0;
  });
};

// 加载我的报名记录
const loadMySignUp = () => {
  request.get('activityInfo/selectByVolunteerId/' + data.user.id).then(res => {
    data.mySignUpList = res.data || [];
    // 更新已报名活动ID列表
    data.signedUpActivityIds = data.mySignUpList.map(item => item.activityId);
  });
};

// 加载我的收藏记录
const loadMyFavorites = () => {
  request.get('activityFavorite/listByVolunteerId/' + data.user.id).then(res => {
    if (res.code === '200') {
      data.favoriteActivityIds = (res.data || []).map(item => item.activityId);
    }
  });
};

// 加载我的评价记录
const loadMyReviews = () => {
  request.get('activityReview/listByVolunteerId/' + data.user.id).then(res => {
    if (res.code === '200') {
      data.reviewedActivityIds = (res.data || []).map(item => item.activityId);
    }
  });
};

// 重置查询条件
const reset = () => {
  data.title = null;
  data.type = null;
  load();
};

// 检查是否可以报名（只有未开始的活动才能报名，兼容英文和中文状态）
const canSignUp = (activity) => {
  return activity && (activity.status === 'pending' || activity.status === '未开始');
};

// 检查用户是否已报名该活动
const isAlreadySignedUp = (activityId) => {
  return data.mySignUpList.some(item => item.activityId === activityId);
};

// 报名活动
const handleSignUp = (activity) => {
  // 如果已经报名，不进行任何操作
  if (isAlreadySignedUp(activity.id)) {
    return;
  }

  if (!canSignUp(activity)) {
    ElMessage.warning('只能报名未开始的活动');
    return;
  }
  
  // 打开报名对话框
  data.currentActivity = activity;
  data.form = {
    qq: data.user.qq || '',
    num: 1
  };
  data.signUpVisible = true;
};

// 提交报名
const submitSignUp = () => {
  if (!data.form.qq) {
    ElMessage.warning('请输入QQ号');
    return;
  }
  
  if (!data.form.num || data.form.num < 1) {
    ElMessage.warning('请输入有效的报名人数');
    return;
  }
  
  request.post('activityInfo/add', {
    activityId: data.currentActivity.id,
    activityName: data.currentActivity.title,
    volunteerId: data.user.id,
    volunteerName: data.user.name,
    qq: data.form.qq,
    num: data.form.num
  }).then(res => {
    if (res.code === '200') {
      ElMessage.success('报名成功，请等待审核');
      data.signUpVisible = false;
      loadMySignUp();
    } else {
      ElMessage.error(res.message || '报名失败，请勿重复报名');
    }
  });
};

// 取消报名
const handleCancelSignUp = (id) => {
  request.delete('activityInfo/deleteById/' + id).then(res => {
    if (res) {
      ElMessage.success('取消报名成功');
      loadMySignUp();
    } else {
      ElMessage.error(res.message || '取消报名失败');
    }
  });
};

// 检查活动是否已收藏
const isFavorite = (activityId) => {
  return data.favoriteActivityIds.includes(activityId);
};

// 切换收藏状态
const handleToggleFavorite = (activity) => {
  if (isFavorite(activity.id)) {
    // 取消收藏
    request.delete('activityFavorite/remove', {
      params: {
        activityId: activity.id,
        volunteerId: data.user.id
      }
    }).then(res => {
      if (res.code === '200') {
        ElMessage.success('取消收藏成功');
        loadMyFavorites();
      } else {
        ElMessage.error(res.message || '操作失败');
      }
    });
  } else {
    // 添加收藏
    request.post('activityFavorite/add', {
      activityId: activity.id,
      volunteerId: data.user.id
    }).then(res => {
      if (res.code === '200') {
        ElMessage.success('收藏成功');
        loadMyFavorites();
      } else {
        ElMessage.error(res.message || '操作失败');
      }
    });
  }
};

// 检查活动是否已评价
const hasReviewed = (activityId) => {
  return data.reviewedActivityIds.includes(activityId);
};

// 检查是否可以评价（已审核通过且已签到的活动才能评价）
const canReview = (record) => {
  return record && record.status && record.status.toUpperCase() === 'APPROVED' && record.signedIn === 1;
};

// 打开评价弹窗
const handleReview = (record) => {
  if (!canReview(record)) {
    ElMessage.warning('只能评价审核通过且已签到的活动');
    return;
  }
  
  if (hasReviewed(record.activityId)) {
    ElMessage.warning('您已经评价过该活动了');
    return;
  }
  
  data.reviewVisible = true;
  data.reviewForm = {
    activityId: record.activityId,
    activityName: record.activityName,
    volunteerId: data.user.id,
    volunteerName: data.user.name,
    score: 5,
    content: ''
  };
};

// 提交评价
const submitReview = () => {
  if (data.reviewForm.score < 1 || data.reviewForm.score > 5) {
    ElMessage.warning('评分必须在1-5之间');
    return;
  }
  
  request.post('activityReview/add', data.reviewForm).then(res => {
    if (res.code === '200') {
      ElMessage.success('评价提交成功');
      data.reviewVisible = false;
      loadMyReviews();
    } else {
      ElMessage.error(res.message || '评价提交失败');
    }
  });
};

// 检查是否可以签到（审核通过且未签到）
const canSignIn = (record) => {
  return record && record.status && record.status.toUpperCase() === 'APPROVED' && record.signedIn !== 1;
};

// 签到
const handleSignIn = (record) => {
  request.post('activityInfo/signIn/' + record.id).then(res => {
    if (res.code === '200') {
      ElMessage.success('签到成功');
      loadMySignUp();
    } else {
      ElMessage.error(res.message || '签到失败');
    }
  });
};

// 初始化
onMounted(() => {
  loadActivityTypes();
  load();
  loadMySignUp();
  loadMyFavorites();
  loadMyReviews();
});
</script>

<style scoped>
.card {
  margin: 10px 0;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
}

.button-group {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
  padding: 0 4px;
}

.button-group .el-button {
  margin: 0;
  flex: 1;
  min-width: 80px;
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 