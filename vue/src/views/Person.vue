<template>
  <div class="card">
    <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="80px" style="padding-right:20px;padding-top: 20px">
    <el-form-item label="账号">
      <el-input disabled v-model="data.form.username" autocomplete="off"/>
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
      <el-form-item label="年龄">
      <el-input v-model="data.form.age" autocomplete="off"/>
    </el-form-item>

      <div style="text-align:center">
        <el-button @click="updateUser" type="primary">更新个人信息</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import {reactive,ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const formRef=ref()
const data=reactive({
  user:JSON.parse(localStorage.getItem("xm-user")),
  form:{},
  rules:{
    username:[
      {required:true,message:'请输入账号',trigger:"blur"}
    ],
    name:[
        {required:true,message:'请输入姓名',trigger:"blur"}
    ],
    no:[
        {required:true,message:'请输入工号',trigger:"blur"}
    ],
  }
})

if(data.user.role === 'VOL'){
  request.get('volunteer/selectById/' + data.user.id).then(res=>{
    data.form=res.data;
  })
}else {
  request.get('employee/selectById/' + data.user.id).then(res=>{
    data.form=res.data;
  })
}
const emit=defineEmits(["update:user"]);

const updateUser=()=>{
  if(data.user.role === 'VOL'){
    request.put('volunteer/update',data.form).then(res=>{
      if(res.data.code==200){
        ElMessage.success('更新成功')
        localStorage.setItem("xm-user",JSON.stringify(data.form));
        emit("updateUser");
      }else {
        ElMessage.error(res.message);
      }
    })
  }else{
    request.post('employee/update',data.form).then(res=>{
      if(res.data.code==200){
        ElMessage.success('更新成功')
        localStorage.setItem("xm-user",JSON.stringify(data.form));
        emit("updateUser");
      }
    })
  }
}
</script>