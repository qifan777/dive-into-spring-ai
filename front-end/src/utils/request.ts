import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const BASE_URL = import.meta.env.VITE_API_PREFIX
export const request = axios.create({
  baseURL: BASE_URL,
  timeout: 600000
})
request.interceptors.response.use(
  (res) => {
    return res.data.result
  },
  ({ response }) => {
    if (response.data.code !== 1) {
      ElMessage.warning({ message: response.data.msg })
    }
    if (response.data.code === 10012) {
      router.push('/login')
    }
    return Promise.reject(response.data.result)
  }
)
