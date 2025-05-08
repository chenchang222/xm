<template>
  <div>
    <el-button type="primary" @click="router.back()">返回主页</el-button>
  </div>
  <div>
    <div style="background-color: antiquewhite;font-weight: bold;color: black;text-decoration: underline;font-style: italic">
      快点做毕设
    </div>

    <div style="font-size:30px ">
      {{a}} {{b}}
    </div>

    <div>
      {{data.a}}
      {{data.b}}

    </div>

    <div style="margin-bottom: 20px">
      <input v-model="data.b" type="text" />
    </div>
    <!--    //数据在用户输入时实时变化，双向绑定，表单绑定数据-->

    <div style="margin-bottom: 20px">
      <span style="color: aqua" v-if="data.name==='佩奇'">川</span>
      <span style="color: blueviolet" v-else-if="data.name==='乔治'">渝</span>
      <span style="color: brown" v-else-if="data.name==='朵朵'">藏</span>
      <span style="color: brown" v-else>滇</span>
    </div>

    <div style="margin-bottom: 20px;display: flex">
      <div @click="show(item)" style="width: 300px;height: 300px;background-color: burlywood;text-align: center;font-size: 30px;line-height: 300px;margin-left: 10px" v-for="item in data.arr">{{item}}</div>
    </div>
  </div>

  <div style="margin-top: 20px;width: 600px">
    <select style="width: 600px">
      <option v-for="item in data.pictures" >
        {{item}}
      </option>
    </select>
  </div>

  <div style="margin-top: 20px;width: 600px;margin-bottom: 20px">
    <button @click="click">点我加好运</button>
  </div>

  <div :style="data.box"></div>
  <div><img :src="data.img"></div>

  <div style="margin: 30px">
    <el-button>Default</el-button>
    <el-button type="primary">Primary</el-button>
    <el-button type="success">Success</el-button>
    <el-button type="info">Info</el-button>
    <el-button type="warning">Warning</el-button>
    <el-button type="danger">Danger</el-button>
  </div>

  <div>
    <el-icon :size="20">
      <Edit />
    </el-icon>
  </div>

  <div style="font-size: larger">
    <el-icon size="20px" style="top: 4px"><ChatLineSquare /></el-icon>消息
    <el-button type="danger" :icon="Delete" circle />
    <el-input
        v-model="data.a"
        style="width: 240px"
        size="large"
        placeholder="Please Input"
        :prefix-icon="Search"
    />
  </div>

</template>

<script setup>
import {reactive, ref,onMounted} from "vue";
import {Delete, Search} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const a = ref(1)
const b = ref("尽快看视频")

const data=reactive({
  a:123,
  b:666,
  name:"胡佛",
  arr:[7,8,9,10],
  pictures:["风景","人","动物"],
  box:{height:"100px",width:'100',color:'yellow'},
  img:'https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BB1odmwj.img?w=44&h=44'
})//更常用定义数据

const click=()=>{alert("好运+1")};
const show=(value)=>{alert(value)}
onMounted(()=>console.log("页面加载完成"))

</script>
