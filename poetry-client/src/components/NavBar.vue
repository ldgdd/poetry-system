<template>
  <nav class="nav-bar">
    <div class="nav-inner">
      <router-link to="/" class="nav-brand">
        <span class="brand-icon">篆</span>
        <span class="brand-text">诗词雅集</span>
      </router-link>

      <div class="nav-links">
        <router-link to="/acrostic" class="nav-link">
          <IconSignature class="nav-link-icon" />藏头诗
        </router-link>
        <router-link to="/creation" class="nav-link">
          <IconPen class="nav-link-icon" />主题创作
        </router-link>
        <router-link to="/appreciation" class="nav-link">
          <IconBookshelf class="nav-link-icon" />诗词大全
        </router-link>
        <router-link to="/community" class="nav-link">
          <IconForum class="nav-link-icon" />论坛
        </router-link>
      </div>

      <div class="nav-user">
        <template v-if="auth.isLoggedIn">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <el-avatar size="32" icon="UserFilled" />
              <span class="username">{{ auth.username }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="admin" v-if="auth.user?.role === 'ADMIN'">
                  <IconShield class="dropdown-icon" /> 管理后台
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <IconAccount class="dropdown-icon" /> 个人主页
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <IconCog class="dropdown-icon" /> 账号设置
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <IconLogout class="dropdown-icon" /> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-btn nav-btn-text">登录</router-link>
          <router-link to="/register" class="nav-btn nav-btn-fill">注册</router-link>
        </template>
      </div>
    </div>
    <div class="nav-ink-border"></div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import IconSignature from '~icons/mdi/signature-freehand'
import IconPen from '~icons/mdi/fountain-pen'
import IconBookshelf from '~icons/mdi/bookshelf'
import IconForum from '~icons/mdi/forum-outline'
import IconAccount from '~icons/mdi/account-circle-outline'
import IconCog from '~icons/mdi/cog-outline'
import IconLogout from '~icons/mdi/logout-variant'
import IconShield from '~icons/mdi/shield-account'

const auth = useAuthStore()
const router = useRouter()

function handleCommand(cmd) {
  if (cmd === 'logout') { auth.logout(); ElMessage.success('已退出登录'); router.push('/') }
  else if (cmd === 'admin') router.push('/admin')
  else if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'settings') router.push('/profile')
}
</script>

<style scoped>
.nav-bar {
  position: sticky; top: 0; z-index: 100;
  background: linear-gradient(180deg, rgba(62,44,34,0.97) 0%, rgba(78,52,46,0.96) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0,0,0,0.25);
}
.nav-ink-border {
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #c9a96e 20%, #d4b97e 50%, #c9a96e 80%, transparent 100%);
  opacity: 0.8;
}
.nav-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between;
  height: 58px; padding: 0 12px;
}
.nav-brand {
  display: flex; align-items: center; gap: 8px; text-decoration: none;
}
.brand-icon {
  display: inline-flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border: 2px solid #c9a96e; border-radius: 8px;
  font-family: '楷体','KaiTi',serif; font-size: 1.2rem; color: #c9a96e;
  font-weight: bold;
}
.brand-text {
  font-family: '楷体','KaiTi',serif; font-size: 1.3rem; font-weight: bold;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #f5e6d3, #c9a96e);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
}
.nav-links { display: flex; gap: 4px; }
.nav-link {
  color: #d7ccc8; text-decoration: none; padding: 8px 16px;
  border-radius: 8px; font-size: 0.9rem; letter-spacing: 1px;
  transition: all 0.25s cubic-bezier(0.25,0.8,0.25,1);
  display: flex; align-items: center; gap: 5px;
}
.nav-link:hover { background: rgba(255,255,255,0.1); color: #fff; }
.nav-link.router-link-exact-active { background: rgba(201,169,110,0.18); color: #c9a96e; }
.nav-link-icon { font-size: 1.05rem; }
.nav-user { display: flex; align-items: center; gap: 10px; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; color: #f5e6d3; padding: 4px 8px; border-radius: 8px; transition: all 0.2s; }
.user-info:hover { background: rgba(255,255,255,0.08); }
.username { font-size: 0.9rem; max-width: 100px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.arrow { font-size: 0.75rem; color: #c9a96e; }
.dropdown-icon { margin-right: 6px; font-size: 1rem; color: #8d6e63; }
.nav-btn { text-decoration: none; padding: 7px 18px; border-radius: 8px; font-size: 0.9rem; transition: all 0.25s; }
.nav-btn-text { color: #f5e6d3; }
.nav-btn-text:hover { background: rgba(255,255,255,0.1); }
.nav-btn-fill { background: linear-gradient(135deg, #c9a96e, #b8944f); color: #3d3226; font-weight: bold; box-shadow: 0 2px 8px rgba(0,0,0,0.2); }
.nav-btn-fill:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0,0,0,0.3); }
</style>
