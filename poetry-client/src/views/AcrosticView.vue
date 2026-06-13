<template>
  <div class="feature-page">
    <div class="back-bar">
      <span class="back-link" @click="$router.push('/')"><IconArrowLeft class="b-icon" /> 返回首页</span>
    </div>
    <div class="page-header">
      <h1><IconSignature class="header-icon" /> 藏头诗生成</h1>
      <p>输入几个字，AI 为您创作一首藏头诗</p>
    </div>
    <div class="form-card card">
      <el-form :model="form" label-position="top" size="large">
        <el-form-item label="藏头字（每句首字，2-8字）">
          <el-input v-model="form.headWord" placeholder="例如：春风化雨" maxlength="8" show-word-limit clearable class="headword-input" />
        </el-form-item>
        <el-form-item label="格式风格">
          <el-radio-group v-model="form.style">
            <el-radio-button value="七言绝句">七言</el-radio-button><el-radio-button value="五言绝句">五言</el-radio-button>
            <el-radio-button value="七言律诗">七律</el-radio-button><el-radio-button value="五言律诗">五律</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleGenerate" :disabled="!form.headWord.trim()" round>
            <IconWand class="btn-icon" /> {{ loading ? '挥毫中...' : '生成藏头诗' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-if="result" class="result-card card">
      <div class="result-seal">诗</div>
      <div class="result-content">{{ result }}</div>
      <div class="result-actions">
        <el-button @click="copyResult" round><IconCopy class="btn-icon" /> 复制</el-button>
        <el-button type="primary" @click="publishResult" round><IconSend class="btn-icon" /> 发布作品</el-button>
      </div>
    </div>
    <div v-if="!result && !loading" class="empty-hint">
      <div class="hint-card"><IconLightbulb class="hint-icon" /><p>试试输入「春风化雨」「龙马精神」「江山如画」</p></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { generateAcrostic } from '../api'
import api from '../api'
import { ElMessage } from 'element-plus'
import IconSignature from '~icons/mdi/signature-freehand'
import IconWand from '~icons/mdi/auto-fix'
import IconCopy from '~icons/mdi/content-copy'
import IconSend from '~icons/mdi/send-outline'
import IconLightbulb from '~icons/mdi/lightbulb-on-outline'
import IconArrowLeft from '~icons/mdi/arrow-left'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ headWord: '', style: '七言绝句' })
const result = ref('')
const loading = ref(false)
async function handleGenerate() {
  if (!form.headWord.trim()) return
  loading.value = true; result.value = ''
  try { const data = await generateAcrostic(form.headWord.trim(), form.style); result.value = data.data?.poem || '生成失败' }
  catch { /* handled */ } finally { loading.value = false }
}
async function copyResult() { await navigator.clipboard.writeText(result.value); ElMessage.success('已复制到剪贴板') }
async function publishResult() {
  if (!auth.isLoggedIn) return ElMessage.warning('请先登录')
  if (!result.value) return
  try {
    await api.post('/user-poems', { title: form.headWord, content: result.value, tags: '藏头诗,' + form.style })
    ElMessage.success('作品已发布到论坛！')
    router.push('/community')
  } catch { /* handled */ }
}
</script>

<style scoped>
.feature-page { max-width: 650px; margin: 0 auto; }
.back-bar { margin-bottom: 10px; }
.back-link { display: inline-flex; align-items: center; gap: 4px; color: #8d6e63; cursor: pointer; font-size: 0.95rem; transition: all 0.2s; user-select: none; }
.back-link:hover { color: #3d3226; }
.b-icon { font-size: 1.1rem; }
.page-header { text-align: center; padding: 24px 0 16px; }
.page-header h1 { font-family: '楷体','KaiTi',serif; font-size: 2rem; color: #3d3226; letter-spacing: 4px; }
.header-icon { font-size: 2rem; vertical-align: -0.3em; color: #8d6e63; }
.page-header p { color: #8d7b6e; margin-top: 6px; }
.form-card { padding: 30px; }
.headword-input :deep(input) { font-size: 1.3rem; letter-spacing: 8px; text-align: center; font-family: '楷体','KaiTi',serif; }
.result-card { margin-top: 24px; padding: 30px; position: relative; overflow: hidden; }
.result-seal { position: absolute; top: 16px; right: 20px; width: 48px; height: 48px; display: flex; align-items: center; justify-content: center; border: 2px solid #b5443c; border-radius: 50%; color: #b5443c; font-family: '楷体','KaiTi',serif; font-size: 1.3rem; font-weight: bold; opacity: 0.5; }
.result-content { white-space: pre-line; line-height: 2.3; font-size: 1.1rem; }
.result-actions { display: flex; gap: 10px; margin-top: 20px; justify-content: flex-end; }
.btn-icon { margin-right: 4px; font-size: 1.1rem; }
.empty-hint { text-align: center; margin-top: 40px; }
.hint-card { display: inline-block; padding: 16px 28px; background: #fffef9; border: 1px dashed #d4c5b2; border-radius: 12px; color: #8d7b6e; }
.hint-icon { font-size: 1.6rem; display: block; margin: 0 auto 8px; color: #c9a96e; }
</style>
