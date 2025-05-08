import axios from "axios";
import {ElMessage} from "element-plus";

const request = axios.create({
    baseURL: "http://localhost:8533",
    timeout: 5000,
})

request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=UTF-8';
    return config;
}, error => {
    return Promise.reject(error)
});

request.interceptors.response.use(response => {
    let res = response.data;
    if (typeof res === 'string') {
        res = res ? JSON.parse(res) : res;
    }
    return res;
}, error => {
    if (error.response.status === 404) {
        ElMessage.error('未找到请求接口')
    }else if (error.response.status === 500) {
        ElMessage.error('系统异常，请查看后端控制台报错')
    }else{
        console.log(error.response.message);
    }
    return Promise.reject(error);
})
export default request;