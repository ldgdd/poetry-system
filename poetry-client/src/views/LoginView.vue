<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-seal">诗</div>
        <h1>诗词雅集</h1>
        <p>登录账号，品味千年诗韵</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" size="large">
        <el-form-item label="用户名 / 邮箱" prop="account">
          <el-input v-model="form.account" placeholder="请输入用户名或邮箱" clearable :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password :prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.rememberMe">记住密码</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="auth-btn" :loading="loading" @click="handleLogin" round>
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">还没有账号？<router-link to="/register">立即注册</router-link></div>
    </div>
    <div class="bg-ink bg-ink-1"></div>
    <div class="bg-ink bg-ink-2"></div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ account: '', password: '', rememberMe: false })
const loading = ref(false)
const formRef = ref(null)
const rules = {
  account: [{ required: true, message: '请输入用户名或邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try { await auth.login(form.account, form.password, form.rememberMe); ElMessage.success('登录成功！欢迎回来'); router.push('/') }
  catch { /* handled */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { display: flex; justify-content: center; align-items: center; min-height: calc(100vh - 140px); position: relative; overflow: hidden; }
.auth-card { width: 420px; padding: 44px 40px; position: relative; z-index: 2; background: rgba(255,254,249,0.97); border: 2px solid #d4c5b2; border-radius: 16px; box-shadow: 0 12px 40px rgba(61,50,38,0.15); }
.auth-header { text-align: center; margin-bottom: 30px; }
.auth-seal { display: inline-flex; align-items: center; justify-content: center; width: 56px; height: 56px; margin-bottom: 14px; border: 2px solid #b5443c; border-radius: 50%; color: #b5443c; font-family: '楷体','KaiTi',serif; font-size: 1.6rem; font-weight: bold; }
.auth-header h1 { font-family: '楷体','KaiTi',serif; font-size: 1.8rem; color: #3d3226; letter-spacing: 6px; }
.auth-header p { color: #8d7b6e; font-size: 0.9rem; margin-top: 6px; }
.auth-btn { width: 100%; font-size: 1.1rem; letter-spacing: 10px; height: 44px; }
.auth-footer { text-align: center; margin-top: 18px; color: #8d7b6e; font-size: 0.9rem; }
.auth-footer a { color: #8d6e63; font-weight: bold; }
.bg-ink { position: absolute; border-radius: 50%; pointer-events: none; background: radial-gradient(circle, rgba(201,169,110,0.06) 0%, transparent 70%); }
.bg-ink-1 { width: 500px; height: 500px; top: -200px; right: -150px; }
.bg-ink-2 { width: 400px; height: 400px; bottom: -150px; left: -100px; }
</style>
