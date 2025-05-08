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
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button type="warning" @click="handleBatchDelete">批量删除</el-button>
      </div>
      <div class="card" style="margin-bottom: 5px">
        <el-table :data="data.tableData" stripe @selection-change="handleSelectChange">
          <el-table-column type="selection" width="55"/>
          <el-table-column label="活动类型" prop="type"/>
          <el-table-column label="活动地点" prop="location"/>
          <el-table-column label="活动标题" prop="title"/>
          <el-table-column label="活动描述" prop="description"/>
          <el-table-column label="开始时间" prop="startTime"/>
          <el-table-column label="结束时间" prop="endTime"/>
          <el-table-column label="状态" prop="status"/>
          <el-table-column label="操作" width="355">
            <template #default="scope">
              <el-button @click="handleUpdate(scope.row)" type="primary" :icon="Edit">编辑</el-button>
              <el-button @click="handleDelete(scope.row.id)" type="danger" :icon="Delete">删除</el-button>
              <el-button @click="handleViewSignUp(scope.row)" type="info" :icon="View">查看报名</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="data.formVisible" title="活动信息" width="50%">
      <el-form :model="data.form" label-width="120px" style="padding-right: 30px">
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="data.form.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option v-for="item in data.activityTypes" :key="item.type" :label="item.type" :value="item.type"/>
          </el-select>
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="data.form.location" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="data.form.title" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input type="textarea" v-model="data.form.description" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="data.form.startTime" type="date" placeholder="选择开始时间" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="data.form.endTime" type="date" placeholder="选择结束时间" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="data.form.status" placeholder="请选择活动状态" style="width: 100%">
            <el-option label="未开始" value="未开始"/>
            <el-option label="进行中" value="进行中"/>
            <el-option label="已结束" value="已结束"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看报名信息弹窗 -->
    <el-dialog v-model="data.signUpVisible" title="报名信息" width="70%">
      <el-table :data="data.signUpList" stripe>
        <el-table-column label="志愿者姓名" prop="volunteerName"/>
        <el-table-column label="联系方式" prop="qq"/>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button @click="handleDeleteSignUp(scope.row.id)" type="danger" :icon="Delete">删除报名</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {Search, Edit, Delete, View} from '@element-plus/icons-vue';

const data = reactive({
  title: null,
  type: null,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  signUpVisible: false,
  form: {},
  signUpList: [],
  activityTypes: [],
  selectedIds: []
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

// 重置查询条件
const reset = () => {
  data.title = null;
  data.type = null;
  load();
};

// 新增活动
const handleAdd = () => {
  data.formVisible = true;
  data.form = {};
};

// 编辑活动
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 删除活动
const handleDelete = (id) => {
  request.delete('activity/deleteById/' + id).then(res => {
    if (res) {
      ElMessage.success('删除成功');
      load();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  });
};

// 批量删除
const handleBatchDelete = () => {
  if (!data.selectedIds.length) {
    ElMessage.warning('请选择要删除的数据');
    return;
  }
  request.delete('activity/deleteBatchDelete', {
    data: data.selectedIds
  }).then(res => {
    if (res) {
      ElMessage.success('批量删除成功');
      load();
    } else {
      ElMessage.error(res.message || '批量删除失败');
    }
  });
};

// 查看报名信息
const handleViewSignUp = (row) => {
  data.signUpVisible = true;
  request.get('activityInfo/selectAll', {
    params: {
      activityId: row.id
    }
  }).then(res => {
    data.signUpList = res.data || [];
  });
};

// 删除报名信息
const handleDeleteSignUp = (id) => {
  request.delete('activityInfo/deleteById/' + id).then(res => {
    if (res) {
      ElMessage.success('删除报名信息成功');
      handleViewSignUp({id: data.form.id});
    } else {
      ElMessage.error(res.message || '删除报名信息失败');
    }
  });
};

// 保存活动信息
const save = () => {
  if (data.form.id) {
    request.put('activity/update', data.form).then(res => {
      if (res) {
        ElMessage.success('更新成功');
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.message || '更新失败');
      }
    });
  } else {
    request.post('activity/add', data.form).then(res => {
      if (res) {
        ElMessage.success('新增成功');
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.message || '新增失败');
      }
    });
  }
};

// 表格多选
const handleSelectChange = (selection) => {
  data.selectedIds = selection.map(item => item.id);
};

// 初始化
loadActivityTypes();
load();
</script>

<style scoped>
.card {
  margin: 10px 0;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
}
</style> 