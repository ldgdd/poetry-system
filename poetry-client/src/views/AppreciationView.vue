<template>
  <div class="appreciation-page">
    <div class="page-header">
      <h1><IconBookshelf class="header-icon" /> 诗词大全</h1>
      <p>品读经典，穿越千年文脉</p>
    </div>
    <div class="search-bar">
      <el-input v-model="search.keyword" placeholder="搜索标题、作者、诗句..." clearable style="width:240px" @clear="fetchPoems" @keyup.enter="fetchPoems" size="large" :prefix-icon="Search" />
      <el-select v-model="search.dynasty" placeholder="朝代" clearable style="width:110px" size="large">
        <el-option label="唐" value="唐" /><el-option label="宋" value="宋" /><el-option label="元" value="元" /><el-option label="明" value="明" /><el-option label="清" value="清" />
      </el-select>
      <el-select v-model="search.category" placeholder="分类" clearable style="width:120px" size="large">
        <el-option label="唐诗" value="唐诗" /><el-option label="宋词" value="宋词" /><el-option label="元曲" value="元曲" /><el-option label="古诗" value="古诗" />
      </el-select>
      <el-button type="primary" size="large" @click="fetchPoems" round><el-icon><Search /></el-icon> 搜索</el-button>
      <el-switch v-model="showFavoritesOnly" active-text="收藏" inactive-text="全部" style="margin-left:auto" @change="toggleFavorites" />
    </div>
    <div v-loading="loading" class="poem-list">
      <template v-if="showFavoritesOnly">
        <PoemCard v-for="poem in favorites" :key="'fav-'+poem.id" :poem="poem" @click="$router.push(`/poem/${poem.id}`)" />
      </template>
      <template v-else>
        <PoemCard v-for="poem in poems" :key="poem.id" :poem="poem" @click="$router.push(`/poem/${poem.id}`)" />
      </template>
      <el-empty v-if="!loading && poems.length === 0 && !showFavoritesOnly" description="未找到诗词" />
      <el-empty v-if="!loading && favorites.length === 0 && showFavoritesOnly" description="还没有收藏诗词" />
    </div>
    <div v-if="!showFavoritesOnly && totalPages > 1" class="pagination-wrap">
      <el-pagination background :current-page="currentPage+1" :total="totalElements" :page-size="12" layout="prev, pager, next" @current-change="p => { currentPage=p-1; fetchPoems() }" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPoems, getFavorites } from '../api'
import PoemCard from '../components/PoemCard.vue'
import IconBookshelf from '~icons/mdi/bookshelf'
import { Search } from '@element-plus/icons-vue'

const poems = ref([])
const favorites = ref([])
const loading = ref(false)
const showFavoritesOnly = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const search = reactive({ keyword: '', dynasty: '', category: '' })

async function fetchPoems() {
  loading.value = true
  try {
    const data = await getPoems({ keyword: search.keyword || undefined, dynasty: search.dynasty || undefined, category: search.category || undefined, page: currentPage.value, size: 12 })
    poems.value = data.data.content; totalElements.value = data.data.totalElements; totalPages.value = data.data.totalPages
  } catch { /* */ } finally { loading.value = false }
}
async function loadFavorites() { try { favorites.value = (await getFavorites()).data?.content || [] } catch { /* */ } }
function toggleFavorites(val) { if (val) loadFavorites() }
onMounted(() => { fetchPoems(); loadFavorites() })
</script>

<style scoped>
.appreciation-page { max-width: 900px; margin: 0 auto; }
.page-header { text-align: center; padding: 24px 0 10px; }
.page-header h1 { font-family: '楷体','KaiTi',serif; font-size: 2rem; color: #3d3226; letter-spacing: 4px; }
.header-icon { font-size: 2rem; vertical-align: -0.3em; color: #8d6e63; margin-right: 4px; }
.page-header p { color: #8d7b6e; margin-top: 6px; }
.search-bar { display: flex; gap: 10px; align-items: center; flex-wrap: wrap; background: #fffef9; padding: 16px 20px; border-radius: 12px; border: 1px solid #e0d5c7; box-shadow: 0 2px 8px rgba(61,50,38,0.06); margin-bottom: 24px; }
.poem-list { min-height: 200px; }
.pagination-wrap { text-align: center; margin-top: 24px; }
</style>
