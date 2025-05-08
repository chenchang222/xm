<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-content" style="padding: 20px;background-color: white;margin-left: 20px">
        <h2 class="login-title"style="margin-bottom: 30px;font-size: 20px;color: black;font-weight: bold">志愿服务系统登录</h2>
        <el-form ref="formRef" :rules="data.rules" :model="data.form">
          <!-- 用户名输入 -->
          <el-form-item prop="username">
            <el-input
                v-model="data.form.username"
                placeholder="请输入账号"
                :prefix-icon="User"
                clearable
            />
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item prop="password">
            <el-input
                v-model="data.form.password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
            />
          </el-form-item>

          <el-form-item prop="role">
            <el-select style="width: 100%" v-model="data.form.role">
              <el-option value="VOL" label="志愿者"></el-option>
              <el-option value="EMP" label="管理员"></el-option>
            </el-select>
          </el-form-item>

          <!-- 登录按钮 -->
          <el-form-item>
            <el-button @click="login" type="primary" style="width: 100%">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive ,ref} from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus"; // 正确导入图标

const data = reactive({
  form: {
    username: "",
    password: "",
    role: "EMP",
  },
  rules: {
    username: [{required:true,message:'请输入账号',trigger:"blur"}],
    password: [{required:true,message:'请输入密码',trigger:"blur"}],
  }
});
const formRef = ref()

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post("/login", data.form).then(res => {
        if (res.code === '200') {//登陆成功
          //存储后台返回的用户信息
          localStorage.setItem("xm-user", JSON.stringify(res.data))
          ElMessage.success('登陆成功')
          setTimeout(() =>{location.href = "/manager/home"},500)

        }else{
          ElMessage.error(res.message);
        }
      })
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url("../assets/bg2.jpg");
  background-size: cover;
}

.login-box {
  display: flex;
  align-items: center;
  padding: 40px 30px;
}

.login-content {
  max-width: 320px;
  margin: 0 auto;
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
  color: #333;
  font-size: 24px;
}

.el-form-item {
  margin-bottom: 24px;
}
</style>