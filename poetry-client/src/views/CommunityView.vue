<template>
  <div class="community-page">
    <div class="back-bar">
      <span class="back-link" @click="$router.push('/')"><IconArrowLeft class="b-icon" /> 返回首页</span>
    </div>
    <div class="page-header">
      <h1><IconForum class="header-icon" /> 诗友论坛</h1>
      <p>赏佳句，会诗友，共品风雅</p>
      <el-button type="primary" @click="$router.push('/creation')" round style="margin-top:12px">
        <IconPen class="btn-icon" /> 发表作品
      </el-button>
    </div>

    <div v-loading="loading">
      <div v-if="poems.length" class="forum-list">
        <div v-for="p in poems" :key="p.id" class="forum-card card" @click="openDetail(p)">
          <div class="forum-card-header">
            <span class="forum-author"><IconAccount class="ficon" /> {{ p.authorName }}</span>
            <span class="forum-time">{{ formatTime(p.createdAt) }}</span>
          </div>
          <h3 class="forum-title">{{ p.title }}</h3>
          <div class="forum-content">{{ getPreview(p.content) }}</div>
          <div class="forum-footer">
            <span><IconEye class="ficon" /> {{ p.viewCount || 0 }}</span>
            <span><IconHeart class="ficon" /> {{ p.likeCount || 0 }}</span>
            <span><IconCommentMini class="ficon" /> 评论</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="还没有诗友发表作品，来做第一个吧！" />
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination-wrap">
      <el-pagination background :current-page="currentPage+1" :total="totalElements"
        :page-size="20" layout="prev, pager, next"
        @current-change="p => { currentPage=p-1; fetchPoems() }" />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="selectedPoem?.title" width="680px" destroy-on-close>
      <template v-if="selectedPoem">
        <div class="detail-meta">
          <span class="d-author">✍️ {{ selectedPoem.authorName }}</span>
          <span class="d-time">{{ formatTime(selectedPoem.createdAt) }}</span>
        </div>
        <div class="d-content">{{ selectedPoem.content }}</div>
        <div v-if="selectedPoem.description" class="d-desc">
          <strong>创作说明：</strong>{{ selectedPoem.description }}
        </div>
        <div class="d-actions">
          <el-button :type="liked ? 'danger' : ''" @click="toggleLike" round size="small">
            <IconHeart class="ficon" /> {{ liked ? '已点赞' : '点赞' }} {{ selectedPoem.likeCount || 0 }}
          </el-button>
          <el-button @click="scrollToComments" round size="small">
            <IconCommentMini class="ficon" /> 评论
          </el-button>
        </div>
        <!-- 评论区 -->
        <div class="comment-box" ref="commentBox">
          <div v-for="c in comments" :key="c.id" class="c-item">
            <span class="c-user">{{ c.username }}</span>
            <span class="c-text">{{ c.content }}</span>
            <span class="c-time">{{ formatTime(c.createdAt) }}</span>
          </div>
          <div v-if="auth.isLoggedIn" style="margin-top:12px;display:flex;gap:8px;">
            <el-input v-model="commentText" placeholder="写下评论..." size="small" />
            <el-button type="primary" size="small" @click="submitComment">发送</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import IconArrowLeft from '~icons/mdi/arrow-left'
import IconForum from '~icons/mdi/forum-outline'
import IconPen from '~icons/mdi/fountain-pen'
import IconAccount from '~icons/mdi/account-circle-outline'
import IconEye from '~icons/mdi/eye-outline'
import IconHeart from '~icons/mdi/heart-outline'
import IconCommentMini from '~icons/mdi/comment-outline'

const auth = useAuthStore()
const poems = ref([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const detailVisible = ref(false)
const selectedPoem = ref(null)
const liked = ref(false)
const comments = ref([])
const commentText = ref('')
const commentBox = ref(null)

function getPreview(content) {
  if (!content) return ''
  const lines = content.split('\n')
  return lines.slice(0, 4).join('\n') + (lines.length > 4 ? '\n…' : '')
}

async function fetchPoems() {
  loading.value = true
  try {
    const res = await api.get('/user-poems', { params: { page: currentPage.value, size: 20 } })
    poems.value = res.data.content; totalElements.value = res.data.totalElements; totalPages.value = res.data.totalPages
  } catch { /* */ } finally { loading.value = false }
}

async function openDetail(p) {
  selectedPoem.value = p; detailVisible.value = true
  try {
    const [cRes, lRes] = await Promise.all([
      api.get('/comments', { params: { targetId: p.id, targetType: 'user_poem' } }),
      api.get('/likes/check', { params: { userPoemId: p.id } })
    ])
    comments.value = cRes.data?.content || []
    liked.value = lRes.data?.liked || false
  } catch { /* */ }
}

async function toggleLike() {
  if (!auth.isLoggedIn) return ElMessage.warning('请先登录')
  try {
    const res = await api.post('/likes/toggle', { userPoemId: selectedPoem.value.id })
    liked.value = res.data?.liked
    selectedPoem.value.likeCount += liked.value ? 1 : -1
    if (selectedPoem.value.likeCount < 0) selectedPoem.value.likeCount = 0
  } catch { /* */ }
}

async function submitComment() {
  if (!commentText.value.trim()) return
  try {
    await api.post('/comments', { targetId: selectedPoem.value.id, targetType: 'user_poem', content: commentText.value })
    ElMessage.success('评论成功'); commentText.value = ''
    const cRes = await api.get('/comments', { params: { targetId: selectedPoem.value.id, targetType: 'user_poem' } })
    comments.value = cRes.data?.content || []
  } catch { /* */ }
}

function scrollToComments() { commentBox.value?.scrollIntoView({ behavior: 'smooth' }) }
function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }

onMounted(() => fetchPoems())
</script>

<style scoped>
.community-page { max-width: 800px; margin: 0 auto; padding: 20px; }
.back-bar { margin-bottom: 10px; }
.back-link { display: inline-flex; align-items: center; gap: 4px; color: #8d6e63; cursor: pointer; font-size: 0.95rem; transition: all 0.2s; }
.back-link:hover { color: #3d3226; }
.b-icon { font-size: 1.1rem; }
.page-header { text-align: center; padding: 20px 0 16px; }
.page-header h1 { font-family: '楷体','KaiTi',serif; font-size: 2rem; color: #3d3226; letter-spacing: 4px; }
.header-icon { font-size: 2rem; vertical-align: -0.3em; color: #8d6e63; }
.page-header p { color: #8d7b6e; margin-top: 6px; }
.btn-icon { margin-right: 4px; font-size: 1rem; }
.forum-card {
  padding: 20px 24px; margin: 10px 0; cursor: pointer;
  transition: all 0.35s cubic-bezier(0.25,0.8,0.25,1);
}
.forum-card:hover { border-color: #c9a96e; }
.forum-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.forum-author { font-size: 0.9rem; color: #8d6e63; font-weight: bold; display: flex; align-items: center; gap: 4px; }
.forum-time { font-size: 0.8rem; color: #bcaa9d; }
.forum-title { font-size: 1.15rem; color: #3d3226; margin-bottom: 8px; }
.forum-content { white-space: pre-line; line-height: 1.9; font-size: 0.95rem; color: #3d3226; }
.forum-footer { display: flex; gap: 16px; margin-top: 12px; font-size: 0.8rem; color: #8d7b6e; }
.ficon { font-size: 0.9rem; vertical-align: -0.12em; margin-right: 2px; }
.pagination-wrap { text-align: center; margin-top: 24px; }
.detail-meta { display: flex; justify-content: space-between; margin-bottom: 16px; }
.d-author { color: #5d4037; font-weight: bold; }
.d-time { color: #bcaa9d; font-size: 0.85rem; }
.d-content { white-space: pre-line; line-height: 2.2; font-size: 1.05rem; margin-bottom: 12px; }
.d-desc { background: #faf6ef; padding: 12px 16px; border-radius: 8px; margin-bottom: 12px; font-size: 0.9rem; line-height: 1.7; }
.d-actions { display: flex; gap: 10px; margin-bottom: 20px; }
.comment-box { border-top: 1px solid #e0d5c7; padding-top: 12px; }
.c-item { padding: 8px 0; display: flex; gap: 10px; align-items: baseline; flex-wrap: wrap; font-size: 0.9rem; }
.c-user { color: #5d4037; font-weight: bold; white-space: nowrap; }
.c-text { color: #3d3226; flex: 1; }
.c-time { color: #bcaa9d; font-size: 0.78rem; white-space: nowrap; }
</style>
