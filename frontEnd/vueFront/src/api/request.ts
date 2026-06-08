import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: import.meta.env.NODE_ENV === 'development' ? 'http://localhost:5173' : 'http://localhost:80',
    timeout: 15000,
})

request.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

request.interceptors.response.use(
    (res) => {
        const data = res.data
        if (data.code !== 200) {
            ElMessage.error(data.msg || '请求失败')
            return Promise.reject(new Error(data.msg))
        }
        return data
    },
    (err) => {
        ElMessage.error('网络异常')
        return Promise.reject(err)
    },
)

export default request