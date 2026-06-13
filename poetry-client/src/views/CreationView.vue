<template>
  <div class="feature-page">
    <div class="back-bar">
      <span class="back-link" @click="$router.push('/')"><IconArrowLeft class="b-icon" /> 返回首页</span>
    </div>
    <div class="page-header">
      <h1><IconPen class="header-icon" /> 诗词创作</h1>
      <p>AI 灵感辅助 + 手动挥毫，两种方式任选</p>
    </div>

    <el-tabs v-model="mode" type="border-card" class="mode-tabs">
      <!-- Tab 1: AI 辅助创作 -->
      <el-tab-pane label="🤖 AI 辅助创作" name="ai">
        <div class="form-card card">
          <el-form :model="aiForm" label-position="top" size="large">
            <el-form-item label="主题 / 关键词">
              <el-input v-model="aiForm.theme" placeholder="例如：春日田园、大漠孤烟、离别相思..." clearable class="theme-input" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="诗词类型">
                  <el-select v-model="aiForm.type" style="width:100%">
                    <el-option label="七言绝句" value="七言绝句" /><el-option label="五言绝句" value="五言绝句" />
                    <el-option label="七言律诗" value="七言律诗" /><el-option label="五言律诗" value="五言律诗" />
                    <el-option label="宋词" value="宋词" /><el-option label="元曲" value="元曲" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="风格意境">
                  <el-select v-model="aiForm.mood" style="width:100%">
                    <el-option label="豪放" value="豪放" /><el-option label="婉约" value="婉约" />
                    <el-option label="田园" value="田园" /><el-option label="边塞" value="边塞" />
                    <el-option label="送别" value="送别" /><el-option label="咏物" value="咏物" /><el-option label="怀古" value="怀古" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" size="large" :loading="aiLoading" @click="aiGenerate" :disabled="!aiForm.theme.trim()" round>
                <IconWand class="btn-icon" /> {{ aiLoading ? '创作中...' : 'AI 生成' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        <div v-if="aiResult" class="result-card card">
          <div class="result-seal">词</div>
          <div class="result-content">{{ aiResult }}</div>
          <div class="result-actions">
            <el-button @click="copyResult" round><IconCopy class="btn-icon" /> 复制</el-button>
            <el-button type="primary" @click="publishAiResult" round><IconSend class="btn-icon" /> 一键发布</el-button>
          </div>
        </div>
      </el-tab-pane>

      <!-- Tab 2: 手动创作 -->
      <el-tab-pane label="✍️ 手动创作" name="manual">
        <div class="form-card card">
          <el-form :model="manualForm" :rules="manualRules" ref="manualFormRef" label-position="top" size="large">
            <el-form-item label="作品标题" prop="title">
              <el-input v-model="manualForm.title" placeholder="给你的作品取个名字" maxlength="100" clearable />
            </el-form-item>
            <el-form-item label="诗词正文" prop="content">
              <el-input v-model="manualForm.content" type="textarea" :rows="8" placeholder="在此挥毫泼墨，写下你的诗句..." />
            </el-form-item>
            <el-form-item label="创作说明（选填）">
              <el-input v-model="manualForm.description" type="textarea" :rows="2" placeholder="分享创作灵感和背后的故事..." />
            </el-form-item>
            <el-form-item label="标签（选填，逗号分隔）">
              <el-input v-model="manualForm.tags" placeholder="如：山水,思乡,秋日" maxlength="200" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" :loading="manualLoading" @click="publishManual" round>
                <IconSend class="btn-icon" /> 发布作品
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { createPoem } from '../api'
import api from '../api'
import { ElMessage } from 'element-plus'
import IconArrowLeft from '~icons/mdi/arrow-left'
import IconPen from '~icons/mdi/fountain-pen'
import IconWand from '~icons/mdi/auto-fix'
import IconCopy from '~icons/mdi/content-copy'
import IconSend from '~icons/mdi/send-outline'

const router = useRouter()
const auth = useAuthStore()
const mode = ref('ai')

// ===== AI 模式 =====
const aiForm = reactive({ theme: '', type: '七言绝句', mood: '婉约' })
const aiResult = ref('')
const aiLoading = ref(false)

async function aiGenerate() {
  if (!aiForm.theme.trim()) return
  aiLoading.value = true; aiResult.value = ''
  try { const data = await createPoem(aiForm.theme.trim(), aiForm.type, aiForm.mood); aiResult.value = data.data?.poem || '生成失败' }
  catch { /* handled */ } finally { aiLoading.value = false }
}

async function copyResult() {
  await navigator.clipboard.writeText(aiResult.value); ElMessage.success('已复制')
}

async function publishAiResult() {
  if (!auth.isLoggedIn) return ElMessage.warning('请先登录')
  try {
    await api.post('/user-poems', { title: aiForm.theme, content: aiResult.value, tags: aiForm.mood })
    ElMessage.success('作品已发布到论坛！')
    router.push('/community')
  } catch { /* handled */ }
}

// ===== 手动模式 =====
const manualForm = reactive({ title: '', content: '', description: '', tags: '' })
const manualLoading = ref(false)
const manualFormRef = ref(null)
const manualRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入诗词内容', trigger: 'blur' }]
}

async function publishManual() {
  if (!auth.isLoggedIn) return ElMessage.warning('请先登录')
  const valid = await manualFormRef.value.validate().catch(() => false)
  if (!valid) return
  manualLoading.value = true
  try {
    await api.post('/user-poems', { ...manualForm })
    ElMessage.success('作品已发布！')
    router.push('/community')
  } catch { /* handled */ } finally { manualLoading.value = false }
}
</script>

<style scoped>
.feature-page { max-width: 700px; margin: 0 auto; }
.back-bar { margin-bottom: 10px; }
.back-link { display: inline-flex; align-items: center; gap: 4px; color: #8d6e63; cursor: pointer; font-size: 0.95rem; transition: all 0.2s; }
.back-link:hover { color: #3d3226; }
.b-icon { font-size: 1.1rem; }
.page-header { text-align: center; padding: 20px 0 10px; }
.page-header h1 { font-family: '楷体','KaiTi',serif; font-size: 2rem; color: #3d3226; letter-spacing: 4px; }
.header-icon { font-size: 2rem; vertical-align: -0.3em; color: #8d6e63; }
.page-header p { color: #8d7b6e; margin-top: 6px; }
.mode-tabs { border-radius: 12px; overflow: hidden; }
.form-card { padding: 24px; margin: 0; }
.theme-input :deep(input) { font-family: '楷体','KaiTi',serif; font-size: 1.05rem; }
.result-card { margin-top: 20px; padding: 28px; position: relative; overflow: hidden; }
.result-seal { position: absolute; top: 14px; right: 18px; width: 44px; height: 44px; display: flex; align-items: center; justify-content: center; border: 2px solid #3d3226; border-radius: 50%; color: #3d3226; font-family: '楷体','KaiTi',serif; font-size: 1.2rem; font-weight: bold; opacity: 0.4; }
.result-content { white-space: pre-line; line-height: 2.3; font-size: 1.08rem; }
.result-actions { display: flex; gap: 10px; margin-top: 18px; justify-content: flex-end; }
.btn-icon { margin-right: 4px; font-size: 1.1rem; }
</style>
