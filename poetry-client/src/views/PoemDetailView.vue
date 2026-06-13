<template>
  <div class="detail-page" v-if="poem">
    <div class="back-bar">
      <span class="back-link" @click="goBack"><IconArrowLeft class="back-icon" /> 返回诗词列表</span>
      <span class="back-home" @click="$router.push('/')"><IconHome class="back-icon" /> 首页</span>
    </div>
    <div class="detail-layout">
      <div class="detail-left">
        <div class="vertical-poem">
          <div class="vertical-title">{{ poem.title }}</div>
          <div class="vertical-body">{{ poem.content }}</div>
          <div class="vertical-author">—— {{ poem.dynasty }} · {{ poem.author }}</div>
        </div>
      </div>
      <div class="detail-right">
        <h1 class="detail-title">{{ poem.title }}</h1>
        <div class="detail-meta">
          <el-tag size="large" effect="plain">{{ poem.dynasty }}</el-tag>
          <el-tag size="large" type="info" effect="plain">{{ poem.author }}</el-tag>
          <el-tag size="large" effect="plain" class="cat-tag">{{ poem.category }}</el-tag>
        </div>
        <div class="detail-tags" v-if="poem.tags">
          <el-tag v-for="t in poem.tags.split(',')" :key="t" size="small" effect="plain" class="tag-item">{{ t.trim() }}</el-tag>
        </div>
        <div class="detail-section" v-if="poem.translation">
          <h3><IconBookOpen class="sec-icon" /> 译文</h3>
          <p>{{ poem.translation }}</p>
        </div>
        <div class="detail-section" v-if="poem.appreciation">
          <h3><IconPen class="sec-icon" /> 赏析</h3>
          <p>{{ poem.appreciation }}</p>
        </div>
        <div class="detail-section" v-if="poem.background">
          <h3><IconHistory class="sec-icon" /> 创作背景</h3>
          <p>{{ poem.background }}</p>
        </div>
        <div class="detail-actions">
          <el-button :type="liked ? 'danger' : ''" @click="toggleLike" round>
            <IconHeart :class="liked ? 'icon-active' : ''" class="btn-icon" /> {{ liked ? '已点赞' : '点赞' }} {{ poem.likeCount || 0 }}
          </el-button>
          <el-button :type="collected ? 'warning' : ''" @click="toggleCollect" round>
            <IconStar :class="collected ? 'icon-active' : ''" class="btn-icon" /> {{ collected ? '已收藏' : '收藏' }}
          </el-button>
          <el-button @click="scrollToComments" round>
            <IconComment class="btn-icon" /> 评论
          </el-button>
        </div>
        <div class="detail-stats">
          <span><IconEye class="stat-icon" /> {{ poem.viewCount || 0 }} 次浏览</span>
          <span><IconHeart class="stat-icon" /> {{ poem.likeCount || 0 }} 次点赞</span>
        </div>
      </div>
    </div>
    <!-- 评论区 -->
    <div class="comment-section" ref="commentSection">
      <h3><IconCommentText class="sec-icon" /> 评论 ({{ commentTotal }})</h3>
      <div class="comment-input" v-if="auth.isLoggedIn">
        <el-input v-model="commentText" type="textarea" :rows="3" placeholder="写下你的评论..." maxlength="500" show-word-limit />
        <el-button type="primary" @click="submitComment" round style="margin-top:10px">发表评论</el-button>
      </div>
      <div v-if="!auth.isLoggedIn" class="comment-login-hint"><router-link to="/login">登录</router-link> 后发表评论</div>
      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-header"><span class="comment-user">{{ c.username || '用户'+c.userId }}</span><span class="comment-time">{{ formatTime(c.createdAt) }}</span></div>
        <div class="comment-content">{{ c.content }}</div>
        <div class="comment-footer"><span class="comment-like" @click="c.likeCount=(c.likeCount||0)+1"><IconThumbUp class="cm-icon" /> {{ c.likeCount || 0 }}</span></div>
      </div>
    </div>
  </div>
  <div v-else class="loading-wrap"><IconLoading class="spinner" /><p>加载中...</p></div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPoemDetail, addFavorite, removeFavorite, checkFavorite } from '../api'
import { useAuthStore } from '../stores/auth'
import api from '../api'
import { ElMessage } from 'element-plus'
import IconBookOpen from '~icons/mdi/book-open-outline'
import IconPen from '~icons/mdi/pen'
import IconHistory from '~icons/mdi/clock-outline'
import IconHeart from '~icons/mdi/heart-outline'
import IconStar from '~icons/mdi/star-outline'
import IconComment from '~icons/mdi/comment-outline'
import IconCommentText from '~icons/mdi/comment-text-outline'
import IconEye from '~icons/mdi/eye-outline'
import IconThumbUp from '~icons/mdi/thumb-up-outline'
import IconLoading from '~icons/mdi/loading'
import IconArrowLeft from '~icons/mdi/arrow-left'
import IconHome from '~icons/mdi/home-outline'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const poem = ref(null)
const liked = ref(false)
const collected = ref(false)
const comments = ref([])
const commentTotal = ref(0)
const commentText = ref('')
const commentSection = ref(null)

function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/appreciation')
  }
}

onMounted(async () => {
  const id = route.params.id
  if (!id) { router.push('/appreciation'); return }
  try {
    const res = await getPoemDetail(id)
    if (res.data) poem.value = res.data
    else { router.push('/appreciation'); return }
  } catch {
    router.push('/appreciation')
    return
  }
  try {
    const [l, c] = await Promise.all([api.get('/likes/check',{params:{poemId:id}}), checkFavorite(id)])
    liked.value = l.data?.liked || false; collected.value = c.data?.favorited || false
  } catch { /* guest */ }
  loadComments()
})

async function loadComments() {
  if (!poem.value) return
  try {
    const res = await api.get('/comments', { params: { targetId: poem.value.id, targetType: 'poem' } })
    comments.value = res.data?.content || []; commentTotal.value = res.data?.totalElements || 0
  } catch { /* */ }
}
async function toggleLike() {
  if (!auth.isLoggedIn) return ElMessage.warning('请先登录')
  try {
    const res = await api.post('/likes/toggle', { poemId: poem.value.id })
    liked.value = res.data?.liked
    poem.value.likeCount += liked.value ? 1 : -1
    if (poem.value.likeCount < 0) poem.value.likeCount = 0
  } catch { /* */ }
}
async function toggleCollect() {
  if (!auth.isLoggedIn) return ElMessage.warning('请先登录')
  try {
    collected.value ? await removeFavorite(poem.value.id) : await addFavorite(poem.value.id)
    collected.value = !collected.value
    ElMessage.success(collected.value ? '已收藏' : '已取消收藏')
  } catch { /* */ }
}
async function submitComment() {
  if (!commentText.value.trim()) return
  try {
    await api.post('/comments', { targetId: poem.value.id, targetType: 'poem', content: commentText.value })
    ElMessage.success('评论成功'); commentText.value = ''; loadComments()
  } catch { /* */ }
}
function scrollToComments() { commentSection.value?.scrollIntoView({ behavior: 'smooth' }) }
function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }
</script>

<style scoped>
.detail-page { max-width: 1000px; margin: 0 auto; padding: 20px; }
.back-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #e0d5c7; }
.back-link, .back-home { display: inline-flex; align-items: center; gap: 4px; color: #8d6e63; cursor: pointer; font-size: 0.95rem; transition: all 0.2s; user-select: none; }
.back-link:hover, .back-home:hover { color: #3d3226; }
.back-icon { font-size: 1.1rem; }
.detail-layout { display: flex; gap: 44px; margin-top: 8px; }
.detail-left { flex: 1; display: flex; justify-content: center; background: #fffef9; border: 2px solid #d4c5b2; border-radius: 12px; padding: 30px 20px; box-shadow: 0 2px 8px rgba(61,50,38,0.06); }
.vertical-poem { writing-mode: vertical-rl; text-align: start; font-family: '楷体','KaiTi',serif; max-height: 650px; }
.vertical-title { font-size: 1.5rem; letter-spacing: 8px; color: #3d3226; margin-bottom: 30px; font-weight: bold; }
.vertical-body { font-size: 1.3rem; line-height: 2.8; letter-spacing: 6px; color: #3d3226; }
.vertical-author { margin-top: 30px; font-size: 1rem; color: #8d7b6e; }
.detail-right { flex: 1.5; }
.detail-title { font-size: 2rem; font-family: '楷体','KaiTi',serif; color: #3d3226; letter-spacing: 4px; margin-bottom: 12px; }
.detail-meta { display: flex; gap: 8px; margin-bottom: 12px; }
.cat-tag { background: rgba(201,169,110,0.1) !important; border-color: rgba(201,169,110,0.3) !important; color: #c9a96e !important; }
.detail-tags { margin-bottom: 16px; } .tag-item { margin-right: 6px; }
.detail-section { margin: 20px 0; padding: 16px 20px; background: #fffef9; border: 1px solid #e0d5c7; border-radius: 10px; box-shadow: 0 2px 8px rgba(61,50,38,0.06); }
.detail-section h3 { color: #3d3226; font-size: 1.05rem; margin-bottom: 8px; display: flex; align-items: center; gap: 8px; }
.sec-icon { font-size: 1.2rem; color: #c9a96e; }
.detail-section p { line-height: 1.9; color: #3d3226; }
.detail-actions { display: flex; gap: 10px; margin-top: 24px; }
.btn-icon { margin-right: 4px; font-size: 1rem; }
.icon-active { color: inherit; }
.detail-stats { display: flex; gap: 20px; margin-top: 16px; color: #8d7b6e; font-size: 0.85rem; }
.stat-icon { font-size: 0.9rem; vertical-align: -0.12em; margin-right: 3px; }
.comment-section { margin-top: 44px; padding-top: 24px; border-top: 2px solid #e0d5c7; }
.comment-input { margin: 16px 0; }
.comment-login-hint { text-align: center; padding: 20px; color: #8d7b6e; }
.comment-login-hint a { color: #8d6e63; font-weight: bold; }
.comment-item { padding: 16px 0; border-bottom: 1px solid #f0ebe3; }
.comment-header { display: flex; justify-content: space-between; margin-bottom: 6px; }
.comment-user { font-weight: bold; color: #3d3226; font-size: 0.9rem; }
.comment-time { color: #8d7b6e; font-size: 0.8rem; }
.comment-content { line-height: 1.7; }
.comment-footer { margin-top: 8px; }
.comment-like { cursor: pointer; color: #8d7b6e; font-size: 0.85rem; transition: color 0.2s; }
.comment-like:hover { color: #3d3226; }
.cm-icon { font-size: 0.85rem; vertical-align: -0.12em; }
.loading-wrap { text-align: center; padding: 80px; color: #8d7b6e; }
.spinner { font-size: 2rem; animation: spin 1s linear infinite; color: #c9a96e; }
@keyframes spin { to { transform: rotate(360deg); } }
@media (max-width: 768px) { .detail-layout { flex-direction: column; } .vertical-poem { max-height: 400px; } }
</style>
