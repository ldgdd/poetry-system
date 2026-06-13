<template>
  <div class="poem-card" @click="$emit('click')">
    <div class="card-ink-bg"></div>
    <div class="card-content">
      <div class="poem-title">{{ poem.title }}</div>
      <div class="poem-meta">
        <span class="meta-dynasty">{{ poem.dynasty }}</span>
        <span class="meta-divider" v-if="poem.author">·</span>
        <span class="meta-author" v-if="poem.author">{{ poem.author }}</span>
        <span class="meta-divider" v-if="poem.category">·</span>
        <span class="meta-category" v-if="poem.category">{{ poem.category }}</span>
      </div>
      <div class="poem-content">{{ truncatedContent }}</div>
      <div class="card-footer">
        <span class="footer-stat"><IconEye class="footer-icon" /> {{ poem.viewCount || 0 }}</span>
        <span class="footer-stat"><IconHeart class="footer-icon" /> {{ poem.likeCount || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import IconEye from '~icons/mdi/eye-outline'
import IconHeart from '~icons/mdi/heart-outline'

const props = defineProps({ poem: { type: Object, required: true } })
defineEmits(['click'])

const truncatedContent = computed(() => {
  const lines = (props.poem.content || '').split('\n')
  return lines.slice(0, 3).join('\n') + (lines.length > 3 ? '\n…' : '')
})
</script>

<style scoped>
.poem-card {
  position: relative;
  background: linear-gradient(135deg, #fffef9 0%, #fcf9f2 100%);
  border: 1px solid #e0d5c7; border-radius: 12px;
  padding: 22px 26px; margin: 10px 0; cursor: pointer;
  overflow: hidden; transition: all 0.35s cubic-bezier(0.25,0.8,0.25,1);
  box-shadow: 0 1px 6px rgba(61,50,38,0.04);
}
.poem-card:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(61,50,38,0.12); border-color: #c9a96e; }
.card-ink-bg {
  position: absolute; top: -20px; right: -20px;
  width: 120px; height: 120px; border-radius: 50%;
  background: radial-gradient(circle, rgba(201,169,110,0.06) 0%, transparent 70%);
  transition: all 0.5s cubic-bezier(0.25,0.8,0.25,1); pointer-events: none;
}
.poem-card:hover .card-ink-bg { width: 160px; height: 160px; background: radial-gradient(circle, rgba(201,169,110,0.1) 0%, transparent 70%); }
.card-content { position: relative; z-index: 1; }
.poem-title { font-size: 1.15rem; color: #3d3226; font-weight: bold; letter-spacing: 2px; margin-bottom: 6px; }
.poem-meta { margin-bottom: 12px; display: flex; align-items: center; gap: 4px; flex-wrap: wrap; }
.meta-dynasty { font-size: 0.8rem; color: #b5443c; font-weight: bold; }
.meta-divider { color: #d4c5b2; }
.meta-author { font-size: 0.85rem; color: #8d6e63; }
.meta-category { font-size: 0.8rem; color: #6b8e7b; padding: 1px 8px; background: rgba(107,142,123,0.08); border-radius: 4px; }
.poem-content { white-space: pre-line; line-height: 2; font-size: 0.98rem; color: #3d3226; }
.card-footer { display: flex; gap: 16px; margin-top: 14px; padding-top: 12px; border-top: 1px solid #f0ebe3; }
.footer-stat { font-size: 0.8rem; color: #8d7b6e; display: flex; align-items: center; gap: 3px; }
.footer-icon { font-size: 0.95rem; }
</style>
