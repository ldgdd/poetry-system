<template>
  <div class="admin-page" v-if="auth.user?.role === 'ADMIN'">
    <div class="back-bar">
      <span class="back-link" @click="$router.push('/')"><IconArrowLeft class="b-icon" /> 返回首页</span>
      <span class="back-home" @click="$router.push('/')"><IconHome class="b-icon" /> 首页</span>
    </div>
    <div class="page-header">
      <h1><IconShield class="header-icon" /> 管理后台</h1>
      <p>诗词录入 · 评论管理 · 作品审核</p>
    </div>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- Tab 1: 录入诗词 -->
      <el-tab-pane label="📖 录入诗词" name="add">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" size="large" style="max-width:700px;margin:0 auto;">
          <el-row :gutter="16">
            <el-col :span="14">
              <el-form-item label="诗名" prop="title">
                <el-input v-model="form.title" placeholder="如：静夜思" />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="朝代" prop="dynasty">
                <el-select v-model="form.dynasty" style="width:100%">
                  <el-option v-for="d in dynasties" :key="d" :label="d" :value="d" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="分类" prop="category">
                <el-select v-model="form.category" style="width:100%">
                  <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="作者" prop="author">
            <el-input v-model="form.author" placeholder="如：李白" />
          </el-form-item>
          <el-form-item label="标签（逗号分隔）">
            <el-input v-model="form.tags" placeholder="如：思乡,月夜,唐诗" />
          </el-form-item>
          <el-form-item label="诗词原文" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="6" placeholder="输入诗词正文，换行分隔" />
          </el-form-item>
          <el-form-item label="白话译文">
            <el-input v-model="form.translation" type="textarea" :rows="3" placeholder="现代汉语翻译..." />
          </el-form-item>
          <el-form-item label="赏析点评">
            <el-input v-model="form.appreciation" type="textarea" :rows="3" placeholder="文学赏析..." />
          </el-form-item>
          <el-form-item label="创作背景">
            <el-input v-model="form.background" type="textarea" :rows="2" placeholder="历史背景..." />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="adding" @click="handleAdd" round>
              <IconPlus class="btn-icon" /> 录入诗词
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- Tab 2: 评论管理 -->
      <el-tab-pane label="💬 评论管理" name="comments">
        <div v-if="allComments.length" class="manage-list">
          <div v-for="c in allComments" :key="c.id" class="manage-item">
            <div class="mi-header">
              <span class="mi-user">{{ c.username || '用户'+c.userId }}</span>
              <span class="mi-time">{{ formatTime(c.createdAt) }}</span>
            </div>
            <div class="mi-content">{{ c.content }}</div>
            <div class="mi-target">目标: {{ c.targetType }} #{{ c.targetId }}</div>
            <el-button size="small" type="danger" @click="deleteComment(c.id)" round>删除</el-button>
          </div>
        </div>
        <el-empty v-else description="暂无评论" />
        <el-button @click="loadComments" :loading="loadingComments" round>刷新评论列表</el-button>
      </el-tab-pane>

      <!-- Tab 3: 用户作品管理 -->
      <el-tab-pane label="✍️ 作品管理" name="works">
        <div v-if="allUserPoems.length" class="manage-list">
          <div v-for="p in allUserPoems" :key="p.id" class="manage-item">
            <div class="mi-header">
              <span class="mi-title">{{ p.title }}</span>
              <span class="mi-user">作者ID: {{ p.userId }}</span>
            </div>
            <div class="mi-content">{{ (p.content||'').substring(0, 150) }}...</div>
            <el-button size="small" type="danger" @click="deleteUserPoem(p.id)" round>删除</el-button>
          </div>
        </div>
        <el-empty v-else description="暂无用户作品" />
        <el-button @click="loadUserPoems" :loading="loadingPoems" round>刷新作品列表</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
  <div v-else class="no-permission">
    <el-empty description="需要管理员权限" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import IconArrowLeft from '~icons/mdi/arrow-left'
import IconHome from '~icons/mdi/home-outline'
import IconShield from '~icons/mdi/shield-account'
import IconPlus from '~icons/mdi/plus'

const auth = useAuthStore()
const activeTab = ref('add')
const adding = ref(false)
const formRef = ref(null)
const form = reactive({
  title: '', author: '', dynasty: '唐', category: '唐诗',
  tags: '', content: '', translation: '', appreciation: '', background: ''
})
const rules = {
  title: [{ required: true, message: '必填' }],
  author: [{ required: true, message: '必填' }],
  content: [{ required: true, message: '必填' }],
  dynasty: [{ required: true }],
  category: [{ required: true }]
}
const dynasties = ['先秦','两汉','魏晋','南北朝','隋','唐','五代','宋','元','明','清','近现代']
const categories = ['诗经','楚辞','乐府','古诗','唐诗','宋词','元曲','其他']

// 评论管理
const allComments = ref([])
const loadingComments = ref(false)

// 作品管理
const allUserPoems = ref([])
const loadingPoems = ref(false)

async function handleAdd() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  adding.value = true
  try {
    const res = await api.post('/admin/poems', { ...form })
    ElMessage.success(`录入成功！诗词ID: ${res.data.id}`)
    // 清空表单
    Object.keys(form).forEach(k => form[k] = k === 'dynasty' ? '唐' : k === 'category' ? '唐诗' : '')
  } catch { /* */ } finally { adding.value = false }
}

async function loadComments() {
  loadingComments.value = true
  try {
    // 通过诗词列表逐个获取评论（简化处理）
    const poemsRes = await api.get('/poems', { params: { size: 100 } })
    const poems = poemsRes.data?.content || []
    const all = []
    for (const p of poems.slice(0, 20)) {
      try {
        const cRes = await api.get('/comments', { params: { targetId: p.id, targetType: 'poem' } })
        if (cRes.data?.content) all.push(...cRes.data.content)
      } catch { /* */ }
    }
    allComments.value = all.sort((a,b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 50)
  } catch { /* */ } finally { loadingComments.value = false }
}

async function deleteComment(id) {
  try {
    await ElMessageBox.confirm('确定删除此评论？', '确认', { type: 'warning' })
    await api.delete(`/admin/comments/${id}`)
    ElMessage.success('已删除')
    allComments.value = allComments.value.filter(c => c.id !== id)
  } catch { /* */ }
}

async function loadUserPoems() {
  loadingPoems.value = true
  try {
    const res = await api.get('/admin/user-poems')
    allUserPoems.value = (res.data || []).sort((a,b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch { /* */ } finally { loadingPoems.value = false }
}

async function deleteUserPoem(id) {
  try {
    await ElMessageBox.confirm('确定删除此作品？', '确认', { type: 'warning' })
    await api.delete(`/admin/user-poems/${id}`)
    ElMessage.success('已删除')
    allUserPoems.value = allUserPoems.value.filter(p => p.id !== id)
  } catch { /* */ }
}

function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }

onMounted(() => {
  loadComments()
  loadUserPoems()
})
</script>

<style scoped>
.admin-page { max-width: 900px; margin: 0 auto; padding: 20px; }
.back-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; padding-bottom: 12px; border-bottom: 1px solid #e0d5c7; }
.back-link, .back-home { display: inline-flex; align-items: center; gap: 4px; color: #8d6e63; cursor: pointer; font-size: 0.95rem; transition: all 0.2s; user-select: none; }
.back-link:hover, .back-home:hover { color: #3d3226; }
.b-icon { font-size: 1.1rem; }
.page-header { text-align: center; padding: 20px 0 16px; }
.page-header h1 { font-family: '楷体','KaiTi',serif; font-size: 2rem; color: #3d3226; letter-spacing: 4px; }
.header-icon { font-size: 2rem; vertical-align: -0.3em; color: #b5443c; }
.page-header p { color: #8d7b6e; margin-top: 6px; }
.btn-icon { margin-right: 4px; font-size: 1.1rem; }
.manage-list { max-height: 500px; overflow-y: auto; }
.manage-item { padding: 14px 16px; border-bottom: 1px solid #f0ebe3; display: flex; flex-wrap: wrap; align-items: center; gap: 10px; }
.mi-header { display: flex; gap: 12px; align-items: center; width: 100%; }
.mi-title { font-weight: bold; color: #3d3226; }
.mi-user { font-size: 0.85rem; color: #8d6e63; }
.mi-time { font-size: 0.8rem; color: #bcaa9d; margin-left: auto; }
.mi-content { width: 100%; font-size: 0.9rem; line-height: 1.6; color: #3d3226; }
.mi-target { font-size: 0.78rem; color: #bcaa9d; }
.no-permission { text-align: center; padding: 80px; }
</style>
