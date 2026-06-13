<template>
  <div class="profile-page">
    <div class="back-bar">
      <span class="back-link" @click="$router.back()"><IconArrowLeft class="b-icon" /> 返回</span>
      <span class="back-home" @click="$router.push('/')"><IconHome class="b-icon" /> 首页</span>
    </div>
    <div class="profile-header card">
      <el-avatar size="88" icon="UserFilled" class="profile-avatar" />
      <h2>{{ auth.username }}</h2>
      <p class="bio">{{ auth.user?.bio || '这个人很懒，什么都没写...' }}</p>
      <div class="stats">
        <div class="stat-item"><strong>{{ creations.length }}</strong><span>作品</span></div>
        <div class="stat-item"><strong>{{ totalLikes }}</strong><span>获赞</span></div>
        <div class="stat-item"><strong>0</strong><span>关注</span></div>
        <div class="stat-item"><strong>0</strong><span>粉丝</span></div>
      </div>
    </div>

    <div class="profile-tabs card" style="margin-top:20px; padding:8px 0;">
      <el-tabs v-model="activeTab" class="profile-tabs-inner">
        <el-tab-pane name="favorites">
          <template #label><IconStar class="tab-icon" /> 我的收藏</template>
          <div v-for="p in favorites" :key="'fav-'+p.id" class="poem-item" @click="$router.push(`/poem/${p.id}`)">
            <div class="pi-title">{{ p.title }}</div>
            <div class="pi-meta">{{ p.dynasty }} · {{ p.author }}</div>
            <div class="pi-content">{{ (p.content||'').substring(0, 120) }}{{ (p.content||'').length > 120 ? '...' : '' }}</div>
          </div>
          <el-empty v-if="!favorites.length" description="还没有收藏诗词" />
        </el-tab-pane>

        <el-tab-pane name="creations">
          <template #label><IconDraw class="tab-icon" /> 我的创作</template>
          <div v-for="p in creations" :key="'up-'+p.id" class="poem-item" @click="$router.push('/community')">
            <div class="pi-title">{{ p.title }}</div>
            <div class="pi-meta">{{ formatTime(p.createdAt) }}</div>
            <div class="pi-content">{{ (p.content||'').substring(0, 120) }}{{ (p.content||'').length > 120 ? '...' : '' }}</div>
            <div class="pi-footer">
              <span>👁 {{ p.viewCount || 0 }}</span>
              <span>❤️ {{ p.likeCount || 0 }}</span>
            </div>
          </div>
          <el-empty v-if="!creations.length" description="还没有发布作品">
            <el-button type="primary" @click="$router.push('/creation')" round>去创作</el-button>
          </el-empty>
        </el-tab-pane>

        <el-tab-pane name="settings">
          <template #label><IconCog class="tab-icon" /> 账号设置</template>
          <el-form :model="form" label-position="top" style="max-width:420px; padding:10px;">
            <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="设置昵称" /></el-form-item>
            <el-form-item label="个人简介"><el-input v-model="form.bio" type="textarea" :rows="3" placeholder="写一句签名..." /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="绑定邮箱" /></el-form-item>
            <el-form-item><el-button type="primary" @click="saveSettings" round>保存设置</el-button></el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { getFavorites } from '../api'
import api from '../api'
import { ElMessage } from 'element-plus'
import IconStar from '~icons/mdi/star-outline'
import IconDraw from '~icons/mdi/draw'
import IconCog from '~icons/mdi/cog-outline'
import IconArrowLeft from '~icons/mdi/arrow-left'
import IconHome from '~icons/mdi/home-outline'

const auth = useAuthStore()
const activeTab = ref('favorites')
const favorites = ref([])
const creations = ref([])
const form = ref({ nickname: '', bio: '', email: '' })

const totalLikes = computed(() => creations.value.reduce((sum, p) => sum + (p.likeCount || 0), 0))

onMounted(async () => {
  try { favorites.value = (await getFavorites()).data?.content || [] } catch { /* */ }
  try { creations.value = (await api.get('/user-poems/my')).data || [] } catch { /* */ }
})

function saveSettings() { ElMessage.success('设置已保存') }
function formatTime(t) { return t ? new Date(t).toLocaleString('zh-CN') : '' }
</script>

<style scoped>
.profile-page { max-width: 780px; margin: 0 auto; padding: 20px; }
.back-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #e0d5c7; }
.back-link, .back-home { display: inline-flex; align-items: center; gap: 4px; color: #8d6e63; cursor: pointer; font-size: 0.95rem; transition: all 0.2s; user-select: none; }
.back-link:hover, .back-home:hover { color: #3d3226; }
.b-icon { font-size: 1.1rem; }
.profile-header { text-align: center; padding: 36px; }
.profile-avatar { margin-bottom: 14px; }
.profile-header h2 { font-family: '楷体','KaiTi',serif; color: #3d3226; margin: 8px 0 4px; font-size: 1.5rem; letter-spacing: 3px; }
.bio { color: #8d7b6e; font-size: 0.9rem; }
.stats { display: flex; justify-content: center; gap: 36px; margin-top: 20px; padding-top: 18px; border-top: 1px solid #e0d5c7; }
.stat-item { text-align: center; } .stat-item strong { display: block; font-size: 1.4rem; color: #3d3226; } .stat-item span { font-size: 0.8rem; color: #8d7b6e; }
.profile-tabs-inner { padding: 0 20px; }
.tab-icon { font-size: 1.1rem; vertical-align: -0.15em; margin-right: 4px; }
.poem-item { background: #faf6ef; border: 1px solid #e0d5c7; border-radius: 10px; padding: 16px 20px; margin: 8px 0; cursor: pointer; transition: all 0.3s; }
.poem-item:hover { border-color: #c9a96e; box-shadow: 0 2px 8px rgba(61,50,38,0.06); }
.pi-title { font-size: 1.05rem; color: #3d3226; font-weight: bold; } .pi-meta { font-size: 0.8rem; color: #c9a96e; margin: 4px 0; }
.pi-content { font-size: 0.9rem; color: #3d3226; white-space: pre-line; margin-bottom: 6px; }
.pi-footer { display: flex; gap: 14px; font-size: 0.8rem; color: #8d7b6e; }
</style>
