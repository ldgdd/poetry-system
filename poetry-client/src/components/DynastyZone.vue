<template>
  <div class="dynasty-zone">
    <div class="section-title">
      <span class="title-line"></span>
      <h3><IconPillar class="section-icon" /> 朝代专区</h3>
      <span class="title-line"></span>
    </div>
    <div class="dynasty-grid">
      <router-link v-for="d in dynasties" :key="d.name"
        :to="`/appreciation?dynasty=${d.name}`"
        class="dynasty-card" :style="{ '--dynasty-color': d.color }">
        <div class="dynasty-seal">{{ d.name }}</div>
        <span class="dynasty-desc">{{ d.desc }}</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import IconPillar from '~icons/mdi/pillar'

const dynasties = [
  { name: '唐', desc: '诗之巅峰', color: '#b5443c' },
  { name: '宋', desc: '词之盛世', color: '#5d4037' },
  { name: '元', desc: '曲之流芳', color: '#6b8e7b' },
  { name: '明', desc: '诗文复兴', color: '#3a5a8c' },
  { name: '清', desc: '集大成者', color: '#6b4d7a' }
]
</script>

<style scoped>
.dynasty-zone { margin: 40px 0; }
.section-icon { font-size: 1.4rem; vertical-align: -0.2em; color: #c9a96e; }
.dynasty-grid { display: flex; gap: 16px; justify-content: center; flex-wrap: wrap; }
.dynasty-card {
  width: 150px; height: 110px; display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  background: linear-gradient(135deg, #fffef9, #fcf9f2);
  border: 2px solid #e0d5c7; border-radius: 14px;
  text-decoration: none; transition: all 0.4s cubic-bezier(0.25,0.8,0.25,1);
  position: relative; overflow: hidden;
}
.dynasty-card::before {
  content: ''; position: absolute; inset: 0;
  background: radial-gradient(circle at center, var(--dynasty-color) 0%, transparent 70%);
  opacity: 0; transition: opacity 0.4s;
}
.dynasty-card:hover { transform: translateY(-5px); box-shadow: 0 10px 28px rgba(61,50,38,0.14); border-color: var(--dynasty-color); }
.dynasty-card:hover::before { opacity: 0.04; }
.dynasty-seal {
  width: 56px; height: 56px; display: flex; align-items: center; justify-content: center;
  border: 2px solid var(--dynasty-color); border-radius: 50%;
  font-family: '楷体','KaiTi',serif; font-size: 1.8rem; font-weight: bold;
  color: var(--dynasty-color); position: relative; z-index: 1;
  transition: all 0.4s;
}
.dynasty-card:hover .dynasty-seal { background: var(--dynasty-color); color: #fff; }
.dynasty-desc { font-size: 0.8rem; color: #8d7b6e; margin-top: 8px; position: relative; z-index: 1; }
</style>
