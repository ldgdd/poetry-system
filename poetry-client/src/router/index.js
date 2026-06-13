import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
    meta: { title: '诗词雅集' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '登录 - 诗词雅集', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { title: '注册 - 诗词雅集', guest: true }
  },
  {
    path: '/acrostic',
    name: 'Acrostic',
    component: () => import('../views/AcrosticView.vue'),
    meta: { title: '藏头诗生成' }
  },
  {
    path: '/creation',
    name: 'Creation',
    component: () => import('../views/CreationView.vue'),
    meta: { title: '诗词创作', requireAuth: true }
  },
  {
    path: '/appreciation',
    name: 'Appreciation',
    component: () => import('../views/AppreciationView.vue'),
    meta: { title: '诗词欣赏' }
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('../views/CommunityView.vue'),
    meta: { title: '诗友论坛 - 诗词雅集' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/AdminView.vue'),
    meta: { title: '管理后台 - 诗词雅集', requireAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { title: '个人主页', requireAuth: true }
  },
  {
    path: '/poem/:id',
    name: 'PoemDetail',
    component: () => import('../views/PoemDetailView.vue'),
    meta: { title: '诗词详情' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from) => {
  document.title = to.meta.title || '诗词雅集'

  // 从 localStorage 检查登录状态
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')

  // 需要登录的页面
  if (to.meta.requireAuth && !token) {
    return { name: 'Login', query: { redirect: to.fullPath } }
  }

  // 登录/注册页面：已登录用户重定向到首页
  if (to.meta.guest && token) {
    return { name: 'Home' }
  }
})

export default router
