<template>
  <div>
    <h2 style="margin-bottom: 20px">数据统计</h2>
    
    <!-- 统计图表区域 -->
    <el-row :gutter="20">
      <!-- 热门活动TOP5条形图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <div class="chart-header">
            <div class="chart-title">活动数据：热门活动TOP5</div>
            <el-button type="primary" size="small" @click="exportTopActivities">
              <el-icon><Download /></el-icon>导出Excel
            </el-button>
          </div>
          <div id="topActivitiesChart" style="width: 100%; height: 400px"></div>
      </el-card>
      </el-col>
      
      <!-- 志愿者参与次数排行榜 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <div class="chart-header">
            <div class="chart-title">志愿者数据：按活动报名参与次数排行榜</div>
            <el-button type="primary" size="small" @click="exportVolunteerRanking">
              <el-icon><Download /></el-icon>导出Excel
            </el-button>
      </div>
          <!-- 将图表替换为表格 -->
          <el-table :data="data.volunteerRanking.slice(0, 10)" stripe style="width: 100%">
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="volunteerName" label="志愿者姓名" />
            <el-table-column prop="activityCount" label="参与次数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import * as echarts from 'echarts';
import request from "@/utils/request";
import { ElMessage } from 'element-plus';
import { Download } from '@element-plus/icons-vue';

const data = reactive({
  topActivities: [],
  volunteerRanking: []
});

// 初始化热门活动TOP5图表
const initTopActivitiesChart = () => {
  const chartDom = document.getElementById('topActivitiesChart');
  const myChart = echarts.init(chartDom);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.topActivities.map(item => item.activityName),
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '报名人数'
    },
    series: [
      {
        name: '报名人数',
        type: 'bar',
        data: data.topActivities.map(item => item.signupCount),
        itemStyle: {
          color: '#46aeda'
        }
      }
    ]
  };

  myChart.setOption(option);
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

// 初始化志愿者参与排行榜图表 - 已不再使用，但保留函数供可能的后续使用
const initVolunteerRankingChart = () => {
  const chartDom = document.getElementById('volunteerRankingChart');
  if (!chartDom) return; // 添加检查以防DOM元素不存在
  
  const myChart = echarts.init(chartDom);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '参与次数'
    },
    yAxis: {
      type: 'category',
      data: data.volunteerRanking.slice(0, 10).map(item => item.volunteerName),
      axisTick: {
        alignWithLabel: true
      }
    },
    series: [
      {
        name: '参与次数',
        type: 'bar',
        data: data.volunteerRanking.slice(0, 10).map(item => item.activityCount),
        itemStyle: {
          color: '#ffa940'
        }
      }
    ]
  };

  myChart.setOption(option);
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', () => {
    myChart.resize();
  });
};

// 加载数据
const loadData = async () => {
  try {
    // 加载热门活动TOP5数据
    const topActivitiesRes = await request.get('/statistics/topActivities');
    if (topActivitiesRes.code === '200') {
      data.topActivities = topActivitiesRes.data || [];
      initTopActivitiesChart();
    }
    
    // 加载志愿者参与排行榜数据
    const volunteerRankingRes = await request.get('/statistics/volunteerRanking');
    if (volunteerRankingRes.code === '200') {
      data.volunteerRanking = volunteerRankingRes.data || [];
      // 不再需要初始化图表，因为使用表格了
    }
  } catch (error) {
    console.error('加载统计数据失败', error);
  }
};

// 导出热门活动数据到Excel
const exportTopActivities = () => {
  try {
    // 使用window.open直接下载文件
    window.open("http://localhost:8533/statistics/exportTopActivities", '_blank');
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出数据失败', error);
    ElMessage.error('导出失败');
  }
};

// 导出志愿者排行榜数据到Excel
const exportVolunteerRanking = () => {
  try {
    // 使用window.open直接下载文件
    window.open("http://localhost:8533/statistics/exportVolunteerRanking", '_blank');
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出数据失败', error);
    ElMessage.error('导出失败');
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
</style>
