<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>活动报名审核</span>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-input
          v-model="searchParams.activityName"
          placeholder="活动名称"
          style="width: 200px; margin-right: 10px"
          clearable
        />
        <el-input
          v-model="searchParams.volunteerName"
          placeholder="志愿者姓名"
          style="width: 200px; margin-right: 10px"
          clearable
        />
        <el-select v-model="searchParams.status" placeholder="审核状态" style="width: 200px; margin-right: 10px" clearable>
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      
      <!-- 批量操作按钮 -->
      <div class="batch-actions" v-if="selectedIds.length > 0">
        <el-button type="success" @click="handleBatchApprove">批量通过</el-button>
        <el-button type="danger" @click="openRejectDialog">批量拒绝</el-button>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="activityName" label="活动名称" min-width="120" />
        <el-table-column prop="volunteerName" label="志愿者姓名" width="120" />
        <el-table-column prop="qq" label="联系方式" width="120" />
        <el-table-column prop="num" label="参与人数" width="80" />
        <el-table-column prop="status" label="审核状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'PENDING'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rejectReason" label="拒绝理由" min-width="150" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button 
              type="success" 
              size="small" 
              :disabled="scope.row.status === 'APPROVED'"
              @click="handleApprove(scope.row)"
            >通过</el-button>
            <el-button 
              type="danger" 
              size="small" 
              :disabled="scope.row.status === 'REJECTED'"
              @click="handleReject(scope.row)"
            >拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 拒绝理由弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="500px">
      <el-form>
        <el-form-item label="拒绝理由" :label-width="'80px'">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝理由"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 分页和数据相关
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref([])
const selectedRow = ref(null)
const isBatchOperation = ref(false)

// 搜索参数
const searchParams = reactive({
  activityName: '',
  volunteerName: '',
  status: ''
})

// 拒绝理由弹窗
const rejectDialogVisible = ref(false)
const rejectReason = ref('')

// 加载数据
const loadData = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchParams
    }
    
    const res = await request.get('/activityInfo/selectPage', { params })
    if (res.code === '200') {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('加载数据失败', error)
    ElMessage.error('加载数据失败')
  }
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchParams).forEach(key => {
    searchParams[key] = ''
  })
  loadData()
}

// 表格选择事件
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页事件
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  loadData()
}

// 批量通过
const handleBatchApprove = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要审核的记录')
    return
  }
  
  try {
    ElMessageBox.confirm('确认通过所选报名申请?', '确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const res = await request.post('/activityInfo/batchApprove', { ids: selectedIds.value })
      if (res.code === '200') {
        ElMessage.success('批量通过成功')
        loadData()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    }).catch(() => {})
  } catch (error) {
    console.error('批量通过失败', error)
    ElMessage.error('操作失败')
  }
}

// 单个通过
const handleApprove = async (row) => {
  try {
    ElMessageBox.confirm('确认通过该报名申请?', '确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const res = await request.post('/activityInfo/batchApprove', { ids: [row.id] })
      if (res.code === '200') {
        ElMessage.success('审核通过成功')
        loadData()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    }).catch(() => {})
  } catch (error) {
    console.error('审核通过失败', error)
    ElMessage.error('操作失败')
  }
}

// 打开拒绝弹窗
const openRejectDialog = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要审核的记录')
    return
  }
  
  isBatchOperation.value = true
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

// 单个拒绝
const handleReject = (row) => {
  selectedRow.value = row
  isBatchOperation.value = false
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入拒绝理由')
    return
  }
  
  try {
    let ids = isBatchOperation.value ? selectedIds.value : [selectedRow.value.id]
    const res = await request.post('/activityInfo/batchReject', { 
      ids, 
      rejectReason: rejectReason.value 
    })
    
    if (res.code === '200') {
      ElMessage.success(isBatchOperation.value ? '批量拒绝成功' : '拒绝成功')
      rejectDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('拒绝操作失败', error)
    ElMessage.error('操作失败')
  }
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.batch-actions {
  margin-top: 15px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 