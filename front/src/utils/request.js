import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 响应拦截
request.interceptors.response.use(res => {
  if (res.data.code === 200) {
    return res.data
  } else {
    ElMessage.error(res.data.msg || '请求失败')
    return Promise.reject(res.data)
  }
}, err => {
  ElMessage.error('网络异常')
  return Promise.reject(err)
})

export default request