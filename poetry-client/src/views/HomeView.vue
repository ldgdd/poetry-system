<template>
  <div class="home-page">
    <PoemCarousel />

    <!-- 每日一诗 -->
    <section class="daily-section" v-if="dailyPoem">
      <div class="section-title"><span class="title-line"></span><h3><IconScript class="section-icon" /> 每日一诗</h3><span class="title-line"></span></div>
      <div class="daily-card card">
        <div class="daily-main">
          <h2 class="daily-title">{{ dailyPoem.title }}</h2>
          <span class="daily-meta">{{ dailyPoem.dynasty }} · {{ dailyPoem.author }}</span>
          <div class="daily-content">{{ dailyPoem.content }}</div>
        </div>
        <div class="daily-appreciation" v-if="dailyPoem.appreciation">
          <span class="app-label">赏 析</span>
          <p>{{ dailyPoem.appreciation }}</p>
        </div>
      </div>
    </section>

    <!-- 功能入口 -->
    <section class="feature-section">
      <div class="section-title"><span class="title-line"></span><h3><IconGrid class="section-icon" /> 功能模块</h3><span class="title-line"></span></div>
      <div class="feature-grid">
        <router-link v-for="(f, i) in features" :key="f.path" :to="f.path"
          class="feature-card" :style="{ transitionDelay: `${i * 0.08}s` }">
          <component :is="f.icon" class="feature-icon" />
          <h4>{{ f.title }}</h4>
          <p>{{ f.desc }}</p>
        </router-link>
      </div>
    </section>

    <!-- 热门诗词 -->
    <section class="hot-section" v-if="hotPoems.length">
      <div class="section-title"><span class="title-line"></span><h3><IconFire class="section-icon" /> 热门诗词</h3><span class="title-line"></span></div>
      <div class="hot-grid">
        <div v-for="p in hotPoems" :key="p.id" class="hot-card" @click="$router.push(`/poem/${p.id}`)">
          <div class="hot-title">{{ p.title }}</div>
          <div class="hot-meta">{{ p.dynasty }} · {{ p.author }}</div>
          <div class="hot-preview">{{ getPreview(p.content) }}</div>
          <div class="hot-stats">
            <span><IconEye class="stat-icon-sm" /> {{ p.viewCount || 0 }}</span>
            <span><IconHeart class="stat-icon-sm" /> {{ p.likeCount || 0 }}</span>
          </div>
        </div>
      </div>
      <div class="section-more">
        <router-link to="/appreciation">浏览更多诗词 <span class="more-arrow">→</span></router-link>
      </div>
    </section>

    <DynastyZone />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPoems } from '../api'
import api from '../api'
import PoemCarousel from '../components/PoemCarousel.vue'
import DynastyZone from '../components/DynastyZone.vue'
import IconScript from '~icons/mdi/script-text-outline'
import IconGrid from '~icons/mdi/view-grid-outline'
import IconFire from '~icons/mdi/fire'
import IconEye from '~icons/mdi/eye-outline'
import IconHeart from '~icons/mdi/heart-outline'
import IconBookshelf from '~icons/mdi/bookshelf'
import IconFountainPen from '~icons/mdi/fountain-pen'
import IconSignature from '~icons/mdi/signature-freehand'
import IconPodium from '~icons/mdi/podium-gold'

const dailyPoem = ref(null)
const hotPoems = ref([])

const features = [
  { icon: IconBookshelf, title: '诗词大全', desc: '品读经典，穿越千年', path: '/appreciation' },
  { icon: IconFountainPen, title: '主题创作', desc: '挥毫泼墨，抒发胸臆', path: '/creation' },
  { icon: IconSignature, title: '藏头诗', desc: '妙笔生花，一字千金', path: '/acrostic' },
  { icon: IconPodium, title: '诗词排行', desc: '佳作共赏，人气之选', path: '/appreciation' }
]

function getPreview(content) {
  if (!content) return ''
  const lines = content.split('\n')
  return lines.slice(0, 2).join('\n') + (lines.length > 2 ? '\n…' : '')
}

onMounted(async () => {
  try {
    const [poemRes, hotRes] = await Promise.all([
      api.get('/poems/daily'),
      getPoems({ size: 6 })
    ])
    // 每日一诗返回 Result<Poem>，data 即诗歌对象
    if (poemRes.data && poemRes.data.id) dailyPoem.value = poemRes.data
    if (hotRes.data?.content) hotPoems.value = hotRes.data.content
  } catch { /* */ }
})
</script>

<style scoped>
.home-page { max-width: 1100px; margin: 0 auto; }
.section-icon { font-size: 1.4rem; vertical-align: -0.2em; color: #c9a96e; }
.stat-icon-sm { font-size: 0.85rem; vertical-align: -0.12em; }
.daily-card { display: flex; gap: 32px; max-width: 900px; margin: 0 auto; padding: 32px; }
.daily-main { flex: 1; }
.daily-title { font-size: 1.4rem; color: #3d3226; margin-bottom: 4px; letter-spacing: 2px; }
.daily-meta { font-size: 0.85rem; color: #c9a96e; }
.daily-content { white-space: pre-line; line-height: 2.1; font-size: 1.05rem; margin-top: 12px; }
.daily-appreciation { flex: 1; border-left: 1px solid #e0d5c7; padding-left: 24px; display: flex; flex-direction: column; justify-content: center; }
.app-label { color: #c9a96e; font-size: 0.8rem; letter-spacing: 8px; margin-bottom: 8px; }
.daily-appreciation p { line-height: 1.9; color: #3d3226; font-size: 0.95rem; }
.feature-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; max-width: 900px; margin: 0 auto; }
.feature-card {
  background: linear-gradient(135deg, #fffef9, #fcf9f2);
  border: 1px solid #e0d5c7; border-radius: 14px;
  text-align: center; padding: 34px 18px; text-decoration: none;
  transition: all 0.4s cubic-bezier(0.25,0.8,0.25,1);
  position: relative; overflow: hidden;
}
.feature-card::before {
  content: ''; position: absolute; inset: 0;
  background: radial-gradient(circle at center, rgba(201,169,110,0.06) 0%, transparent 70%);
  opacity: 0; transition: opacity 0.4s;
}
.feature-card:hover::before { opacity: 1; }
.feature-card:hover { transform: translateY(-6px); box-shadow: 0 10px 30px rgba(61,50,38,0.12); border-color: #c9a96e; }
.feature-icon { font-size: 2.6rem; display: block; margin: 0 auto 14px; color: #8d6e63; position: relative; z-index: 1; }
.feature-card:hover .feature-icon { color: #5d4037; }
.feature-card h4 { color: #3d3226; font-size: 1.1rem; letter-spacing: 2px; margin-bottom: 6px; position: relative; z-index: 1; }
.feature-card p { color: #8d7b6e; font-size: 0.85rem; position: relative; z-index: 1; }
.hot-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; max-width: 1000px; margin: 0 auto; }
.hot-card {
  background: linear-gradient(135deg, #fffef9, #fcf9f2);
  border: 1px solid #e0d5c7; border-radius: 12px; padding: 20px; cursor: pointer;
  transition: all 0.35s cubic-bezier(0.25,0.8,0.25,1);
}
.hot-card:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(61,50,38,0.1); border-color: #c9a96e; }
.hot-title { font-size: 1.05rem; color: #3d3226; font-weight: bold; margin-bottom: 4px; }
.hot-meta { font-size: 0.8rem; color: #c9a96e; margin-bottom: 10px; }
.hot-preview { white-space: pre-line; line-height: 1.8; font-size: 0.9rem; margin-bottom: 10px; }
.hot-stats { display: flex; gap: 14px; font-size: 0.8rem; color: #8d7b6e; }
.section-more { text-align: center; margin-top: 18px; }
.section-more a { color: #8d6e63; text-decoration: none; font-size: 0.95rem; transition: all 0.3s; }
.section-more a:hover { color: #3d3226; }
.more-arrow { transition: transform 0.3s; }
.section-more a:hover .more-arrow { transform: translateX(4px); }
@media (max-width: 768px) {
  .feature-grid { grid-template-columns: repeat(2, 1fr); }
  .hot-grid { grid-template-columns: 1fr 1fr; }
  .daily-card { flex-direction: column; }
}
</style>
