<template>
  <div class="brand-logo" :class="{ 'brand-logo--inline': inline }">
    <svg
      :width="size"
      :height="size"
      viewBox="0 0 64 64"
      xmlns="http://www.w3.org/2000/svg"
      :style="{ display: 'block' }"
    >
      <defs>
        <!-- 瞻途品牌主色渐变：蓝绿色系，寓意科技感与未来感 -->
        <linearGradient :id="'grad-' + gradId" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#06b6d4" />
          <stop offset="50%" stop-color="#3b82f6" />
          <stop offset="100%" stop-color="#8b5cf6" />
        </linearGradient>
        <!-- 内部高光渐变 -->
        <linearGradient :id="'grad2-' + gradId" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#22d3ee" />
          <stop offset="100%" stop-color="#0ea5e9" />
        </linearGradient>
        <!-- 发光滤镜 -->
        <filter :id="'glow-' + gradId" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="1.5" result="coloredBlur"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
      
      <!-- 外层圆角方框 - 寓意稳健前进 -->
      <rect x="2" y="2" width="60" height="60" rx="12" ry="12"
            :fill="'url(#grad-' + gradId + ')'" />
      <rect x="4" y="4" width="56" height="56" rx="10" ry="10"
            fill="none" stroke="rgba(255,255,255,0.35)" stroke-width="1.5" />
      
      <!-- 内部圆形装饰 -->
      <circle cx="32" cy="32" r="20" fill="none" stroke="rgba(255,255,255,0.12)" stroke-width="1" />
      <circle cx="32" cy="32" r="14" fill="none" stroke="rgba(255,255,255,0.10)" stroke-width="1" />
      
      <!-- "瞻途" 核心图形：Z（瞻）+ T（途）融合 + 箭头（前行方向） -->
      <!-- Z 字母：左边笔画 -->
      <path
        d="M18 20 L18 24 L26 40 L26 44"
        fill="none"
        stroke="white"
        stroke-width="4"
        stroke-linecap="round"
        stroke-linejoin="round"
        :filter="'url(#glow-' + gradId + ')'"
      />
      <!-- Z 字母：中间对角线 -->
      <path
        d="M18 24 L26 40"
        fill="none"
        stroke="white"
        stroke-width="4"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <!-- T 字母：顶部横线（与箭头融合） -->
      <path
        d="M26 34 L48 34"
        fill="none"
        stroke="white"
        stroke-width="4"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <!-- 箭头：前行方向 寓意"途" -->
      <path
        d="M36 26 L48 34 L36 42"
        fill="none"
        stroke="white"
        stroke-width="4"
        stroke-linecap="round"
        stroke-linejoin="round"
        :filter="'url(#glow-' + gradId + ')'"
      />
      
      <!-- 中心小圆点 - 寓意精准目标 -->
      <circle cx="32" cy="32" r="2.5" fill="white" opacity="0.9" />
      
      <!-- 底部品牌文字：途 -->
      <text
        x="32"
        y="59"
        text-anchor="middle"
        fill="white"
        font-size="9"
        font-weight="bold"
        font-family="'Microsoft YaHei', sans-serif"
        opacity="0.95"
      >
        途
      </text>
    </svg>
    <span v-if="showName" class="brand-name" :style="{ fontSize: nameSize }">
      {{ brandName }}
    </span>
  </div>
</template>

<script setup>
import { BRAND } from '../config/brand.js'

defineProps({
  size: { type: [Number, String], default: 40 },
  nameSize: { type: String, default: '18px' },
  showName: { type: Boolean, default: true },
  inline: { type: Boolean, default: false },
  brandName: { type: String, default: BRAND.name }
})

const gradId = Math.random().toString(36).slice(2, 8)
</script>

<style scoped>
.brand-logo {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  user-select: none;
}
.brand-logo--inline {
  gap: 6px;
}
.brand-name {
  font-weight: 700;
  color: var(--el-text-color-primary);
  letter-spacing: 1.5px;
  background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 50%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>
