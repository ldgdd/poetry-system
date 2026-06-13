<template>
  <div class="carousel-wrapper">
    <el-carousel :interval="5000" arrow="hover" height="440px" indicator-position="outside">
      <el-carousel-item v-for="item in poems" :key="item.id">
        <div class="carousel-slide" :style="{ background: item.bgColor }">
          <div class="slide-pattern"></div>
          <div class="slide-content">
            <div class="slide-badge">{{ item.dynasty }} · {{ item.author }}</div>
            <h2 class="slide-title">{{ item.title }}</h2>
            <div class="slide-poem">{{ item.preview }}</div>
            <router-link :to="`/poem/${item.id}`" class="slide-link">
              品读全诗 <span class="link-arrow">→</span>
            </router-link>
          </div>
          <div class="slide-art">
            <div class="art-circle"></div>
            <div class="art-line art-line-1"></div>
            <div class="art-line art-line-2"></div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
const poems = [
  { id: 1, title: '静夜思', author: '李白', dynasty: '唐', preview: '床前明月光，疑是地上霜。\n举头望明月，低头思故乡。', bgColor: 'linear-gradient(135deg, #1a2a3a 0%, #2c3e50 40%, #1a3540 100%)' },
  { id: 4, title: '水调歌头', author: '苏轼', dynasty: '宋', preview: '明月几时有？把酒问青天。\n但愿人长久，千里共婵娟。', bgColor: 'linear-gradient(135deg, #3a2010 0%, #4a3728 50%, #3a2a18 100%)' },
  { id: 8, title: '望岳', author: '杜甫', dynasty: '唐', preview: '岱宗夫如何？齐鲁青未了。\n会当凌绝顶，一览众山小。', bgColor: 'linear-gradient(135deg, #0a2a1a 0%, #1a3a2a 50%, #0d2d1d 100%)' },
  { id: 10, title: '念奴娇·赤壁怀古', author: '苏轼', dynasty: '宋', preview: '大江东去，浪淘尽，千古风流人物。\n江山如画，一时多少豪杰。', bgColor: 'linear-gradient(135deg, #2a1508 0%, #3d2b1f 50%, #2d180a 100%)' },
  { id: 5, title: '满江红', author: '岳飞', dynasty: '宋', preview: '怒发冲冠，凭栏处、潇潇雨歇。\n莫等闲、白了少年头，空悲切。', bgColor: 'linear-gradient(135deg, #3a1010 0%, #5c1a1a 50%, #3a1015 100%)' }
]
</script>

<style scoped>
.carousel-wrapper { margin-bottom: 32px; border-radius: 14px; overflow: hidden; box-shadow: 0 6px 30px rgba(61,50,38,0.2); }
.carousel-slide { height: 100%; display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden; }
.slide-pattern { position: absolute; inset: 0; background: radial-gradient(circle at 70% 30%, rgba(201,169,110,0.06) 0%, transparent 50%), radial-gradient(circle at 30% 70%, rgba(255,255,255,0.03) 0%, transparent 40%); pointer-events: none; }
.slide-content { text-align: center; color: #f5e6d3; z-index: 2; padding: 40px 60px; max-width: 620px; animation: ink-spread 0.6s cubic-bezier(0.25,0.8,0.25,1); }
@keyframes ink-spread { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
.slide-badge { display: inline-block; padding: 4px 16px; border: 1px solid rgba(201,169,110,0.4); border-radius: 20px; font-size: 0.85rem; color: #c9a96e; margin-bottom: 14px; letter-spacing: 2px; }
.slide-title { font-size: 2.6rem; font-family: '楷体','KaiTi',serif; margin-bottom: 18px; letter-spacing: 10px; text-shadow: 0 2px 12px rgba(0,0,0,0.4); }
.slide-poem { white-space: pre-line; line-height: 2.3; font-size: 1.1rem; margin-bottom: 24px; color: rgba(245,230,211,0.85); }
.slide-link { display: inline-flex; align-items: center; gap: 6px; color: #c9a96e; text-decoration: none; font-size: 1rem; padding: 8px 24px; border: 1px solid rgba(201,169,110,0.5); border-radius: 8px; transition: all 0.3s; }
.slide-link:hover { background: rgba(201,169,110,0.12); }
.link-arrow { transition: transform 0.3s; }
.slide-link:hover .link-arrow { transform: translateX(4px); }
.slide-art { position: absolute; right: -60px; top: -60px; z-index: 1; pointer-events: none; }
.art-circle { width: 320px; height: 320px; border-radius: 50%; border: 2px solid rgba(201,169,110,0.1); animation: float 6s ease-in-out infinite; }
@keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-6px); } }
.art-line { position: absolute; top: 60px; right: 150px; width: 2px; height: 220px; background: linear-gradient(180deg, rgba(201,169,110,0.2), transparent); }
.art-line-1 { transform: rotate(25deg); }
.art-line-2 { transform: rotate(-15deg); top: 100px; right: 170px; height: 180px; opacity: 0.6; }
</style>
