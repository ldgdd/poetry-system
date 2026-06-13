import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'

export const useAuthStore = defineStore('auth', () => {
  // ======== State ========
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // ======== Getters ========
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => user.value?.nickname || user.value?.username || '')
  const avatar = computed(() => user.value?.avatar || '')

  // ======== Actions ========

  // 注册
  async function register(form) {
    await api.post('/auth/register', form)
  }

  // 登录
  async function login(account, password, rememberMe = false) {
    const res = await api.post('/auth/login', { account, password, rememberMe })
    const data = res.data
    token.value = data.token
    user.value = data.user
    // 记住密码时存 localStorage
    if (rememberMe) {
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    } else {
      sessionStorage.setItem('token', data.token)
      sessionStorage.setItem('user', JSON.stringify(data.user))
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    }
    return data
  }

  // 退出登录
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
  }

  // 从存储恢复登录状态
  function restoreLogin() {
    const t = localStorage.getItem('token') || sessionStorage.getItem('token')
    const u = localStorage.getItem('user') || sessionStorage.getItem('user')
    if (t && u) {
      token.value = t
      user.value = JSON.parse(u)
    }
  }

  return {
    token, user, isLoggedIn, username, avatar,
    register, login, logout, restoreLogin
  }
})
