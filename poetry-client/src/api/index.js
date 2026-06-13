import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 60000   // AI 接口响应时间较长
})

// ============ 请求拦截器：自动带 JWT Token ============
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ============ 响应拦截器：统一处理 Result<T> 格式 ============
api.interceptors.response.use(
  response => {
    const body = response.data
    // 后端统一返回 { code, message, data }
    if (body && typeof body.code !== 'undefined') {
      if (body.code === 200) {
        return body  // 成功，返回整个 body（含 data）
      } else {
        ElMessage.error(body.message || '请求失败')
        return Promise.reject(new Error(body.message))
      }
    }
    return body
  },
  error => {
    const body = error.response?.data
    const msg = body?.message || error.message || '请求失败'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default api

// ============ 用户认证 ============
export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  getMe: () => api.get('/auth/me')
}

// ============ 藏头诗 ============
export const generateAcrostic = (headWord, style) =>
  api.post('/acrostic/generate', { headWord, style })

// ============ 诗词创作 ============
export const createPoem = (theme, type, mood) =>
  api.post('/creation/generate', { theme, type, mood })

// ============ 诗词欣赏 ============
export const getPoems = (params) =>
  api.get('/poems', { params })

export const getPoemDetail = (id) =>
  api.get(`/poems/${id}`)

export const addFavorite = (poemId) =>
  api.post('/favorites', { poemId })

export const removeFavorite = (poemId) =>
  api.delete(`/favorites/${poemId}`)

export const getFavorites = () =>
  api.get('/favorites')

export const checkFavorite = (poemId) =>
  api.get('/favorites/check', { params: { poemId } })
