<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-seal">墨</div>
        <h1>诗词雅集</h1>
        <p>注册账号，开启诗词之旅</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" size="large">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="3-50 个字符" maxlength="50" clearable :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 6 个字符" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-form-item label="邮箱（选填）" prop="email">
          <el-input v-model="form.email" placeholder="example@mail.com" :prefix-icon="Message" />
        </el-form-item>
        <el-form-item label="手机号（选填）" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" maxlength="11" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="auth-btn" :loading="loading" @click="handleRegister" round>
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">已有账号？<router-link to="/login">立即登录</router-link></div>
    </div>
    <div class="bg-ink bg-ink-1"></div><div class="bg-ink bg-ink-2"></div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'

const router = useRouter(); const auth = useAuthStore()
const form = reactive({ username: '', password: '', confirmPassword: '', email: '', phone: '' })
const loading = ref(false); const formRef = ref(null)
const validateConfirm = (rule, value, callback) => { if (value !== form.password) callback(new Error('两次密码输入不一致')); else callback() }
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' },{ min: 3, max: 50, message: '用户名长度 3-50 个字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' },{ min: 6, message: '密码至少 6 个字符', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' },{ validator: validateConfirm, trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}
async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false); if (!valid) return
  loading.value = true
  try { await auth.register({ username: form.username, password: form.password, email: form.email || undefined, phone: form.phone || undefined }); ElMessage.success('注册成功！请登录'); router.push('/login') }
  catch { /* handled */ } finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { display: flex; justify-content: center; align-items: center; min-height: calc(100vh - 140px); position: relative; overflow: hidden; }
.auth-card { width: 450px; padding: 40px; position: relative; z-index: 2; background: rgba(255,254,249,0.97); border: 2px solid #d4c5b2; border-radius: 16px; box-shadow: 0 12px 40px rgba(61,50,38,0.15); }
.auth-header { text-align: center; margin-bottom: 24px; }
.auth-seal { display: inline-flex; align-items: center; justify-content: center; width: 56px; height: 56px; margin-bottom: 14px; border: 2px solid #3d3226; border-radius: 50%; color: #3d3226; font-family: '楷体','KaiTi',serif; font-size: 1.6rem; font-weight: bold; }
.auth-header h1 { font-family: '楷体','KaiTi',serif; font-size: 1.8rem; color: #3d3226; letter-spacing: 6px; }
.auth-header p { color: #8d7b6e; font-size: 0.9rem; margin-top: 6px; }
.auth-btn { width: 100%; font-size: 1.1rem; letter-spacing: 10px; height: 44px; }
.auth-footer { text-align: center; margin-top: 18px; color: #8d7b6e; }
.auth-footer a { color: #8d6e63; font-weight: bold; }
.bg-ink { position: absolute; border-radius: 50%; pointer-events: none; background: radial-gradient(circle, rgba(107,142,123,0.05) 0%, transparent 70%); }
.bg-ink-1 { width: 500px; height: 500px; top: -200px; left: -150px; }
.bg-ink-2 { width: 400px; height: 400px; bottom: -150px; right: -100px; }
</style>
