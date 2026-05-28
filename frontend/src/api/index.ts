import axios from 'axios'

// 打包后前端走 file:// 协议，不能再用 Vite proxy
// 通过 location.protocol 判断运行环境
const isDev = location.protocol === 'http:' || location.protocol === 'https:'
const baseURL = isDev ? '/api' : 'http://localhost:18080/api'
const BACKEND_BASE = isDev ? '' : 'http://localhost:18080'

const api = axios.create({
  baseURL,
  timeout: 10000
})

/** 将后端相对路径转为完整 URL（打包后 file:// 协议需要） */
export function resolveBackendUrl(path: string): string {
  if (!path) return path
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  if (isDev) return path // dev 模式 Vite proxy 处理
  return BACKEND_BASE + path
}

api.interceptors.response.use(
  (res) => res.data,
  (err) => Promise.reject(err.response?.data || err.message)
)

export default api
