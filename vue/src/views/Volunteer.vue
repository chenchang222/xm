<template>
  <div>
    <div>
      <el-card>
        <el-input style="width: 240px;margin-right: 10px" v-model="data.name" placeholder="请输入名称查询" :prefix-icon="Search"></el-input>
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
          <el-table-column label="账号" prop="username"/>
          <el-table-column label="密码" prop="password"/>
          <el-table-column label="姓名" prop="name"/>
          <el-table-column label="性别" prop="sex"/>
          <el-table-column label="志愿者编号" prop="no"/>
          <el-table-column label="年龄" prop="age"/>
          <el-table-column label="联系方式" prop="contact"/>
          <el-table-column label="操作" width="180" align="center">
            <template #default="scope">
              <div style="display: flex; justify-content: space-evenly; align-items: center">
                <el-button @click="handleUpdate(scope.row)" type="primary" :icon="Edit" size="small">编辑</el-button>
                <el-button @click="handleDelete(scope.row.id)" type="danger" :icon="Delete" size="small">删除</el-button>
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

    <el-dialog title="志愿者信息" v-model="data.formVisible" width="500">
      <el-form :model="data.form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="data.form.username" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="data.form.password" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="data.form.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="data.form.sex">
            <el-radio value="男" label="男"></el-radio>
            <el-radio value="女" label="女"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="志愿者编号">
          <el-input v-model="data.form.no" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="data.form.age" autocomplete="off"/>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="data.form.contact" autocomplete="off"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import {Delete, Edit, Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  name: null,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {}
});

const load = () => {
  request.get('volunteer/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
    }
  }).then(res => {
    data.tableData = res.data?.list || [];
    data.total = res.data?.total || 0;
  }).catch(error => {
    ElMessage.error('数据加载失败: ' + error.message);
  });
};

const reset = () => {
  data.name = null;
  load();
};

const handleAdd = () => {
  data.formVisible = true;
  data.form = {};
};

const save = () => {
  if (data.form.id) {
    // 编辑操作
    request.put('volunteer/update', data.form)
        .then(res => {
          if (res.code === 200) {
            ElMessage.success('更新成功');
            data.formVisible = false;
            load();
          } else {
            ElMessage.error(res.message || '更新失败');
          }
        })
        .catch(error => {
          ElMessage.error('请求失败: ' + error.message);
        });
  } else {
    // 添加操作
    request.post('volunteer/add', data.form)
        .then(res => {
          if (res.code === 200) {
            ElMessage.success('保存成功');
            data.formVisible = false;
            load();
          } else {
            ElMessage.error(res.message || '保存失败');
          }
        })
        .catch(error => {
          ElMessage.error('请求失败: ' + error.message);
        });
  }
};

const handleUpdate = (row) => {
  data.form=JSON.parse(JSON.stringify(row));
  data.formVisible = true;
}
load();

const handleDelete = (id) => {
  ElMessageBox.confirm('是否确认删除？','删除确认',{type:'warning'}).then(() => {
    request.delete(`/volunteer/deleteById/${id}`).then(res => {
      if (res.code === 200) {
        ElMessage.success('删除成功');
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    }).catch(error => {
      ElMessage.error('请求失败: ' + error.message);
    });
  })
}

const handleSelectChange = (rows) => {
data.ids = rows.map(row => row.id)
}
const handleBatchDelete = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请至少选择一条数据');
    return;
  }
  ElMessageBox.confirm('是否确认删除选中的数据？', '批量删除确认', { type: 'warning' }).then(() => {
    request.delete('volunteer/deleteBatchDelete', {data: data.ids}).then(res => {
      if (res.code === 200) {
        ElMessage.success('批量删除成功');
        load();
      } else {
        ElMessage.error(res.message || '批量删除失败');
      }
    }).catch(error => {
      ElMessage.error('请求失败: ' + error.message);
    });
  });
}

</script>

<style scoped>
.el-button {
  margin: 0 5px;
}
</style>