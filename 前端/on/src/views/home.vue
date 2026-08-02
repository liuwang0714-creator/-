<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="site-header">
      <div class="header-inner">
        <div class="header-left">
          <BrandLogo :size="36" :showName="true" nameSize="20px" />
        </div>
        <nav class="main-nav">
          <a v-for="item in navItems" :key="item.label" href="#" class="nav-item" :class="{ active: activeNav === item.label }" @click.prevent="handleNav(item.label)">{{ item.label }}</a>
        </nav>
        <div class="header-right">
          <div class="search-box">
            <el-input v-model="searchKeyword" placeholder="搜索商品" clearable @keyup.enter="handleSearch">
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
          </div>
          <el-button text @click="goLogin">登录</el-button>
          <el-button type="primary" plain @click="goLogin">注册</el-button>
        </div>
      </div>
    </header>

    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="section-inner">
        <div class="hero-content">
          <h1 class="hero-title">{{ brand.slogan }}</h1>
          <p class="hero-desc">{{ brand.description }}</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="goLogin">开始购物</el-button>
            <el-button size="large" plain @click="goLogin">了解更多</el-button>
          </div>
        </div>
        <div class="hero-stats">
          <div v-for="stat in heroStats" :key="stat.label" class="hero-stat">
            <div class="hero-stat-value">{{ stat.value }}</div>
            <div class="hero-stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品分类 -->
    <section class="categories-section">
      <div class="section-inner">
        <h2 class="section-title">商品分类</h2>
        <div class="categories-grid">
          <div v-for="cat in categories" :key="cat.id" class="category-card" @click="handleCategoryClick(cat)">
            <div class="category-icon">{{ cat.icon }}</div>
            <div class="category-name">{{ cat.name }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 限时秒杀 -->
    <section class="flash-section">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title">限时秒杀</h2>
          <div class="countdown">
            <span class="countdown-label">距结束</span>
            <span class="time-block">{{ countdown.hours }}</span>
            <span class="time-sep">:</span>
            <span class="time-block">{{ countdown.minutes }}</span>
            <span class="time-sep">:</span>
            <span class="time-block">{{ countdown.seconds }}</span>
          </div>
        </div>
        <div class="flash-grid">
          <div v-for="product in flashProducts" :key="product.id" class="flash-card">
            <div class="flash-image">{{ product.emoji }}</div>
            <div class="flash-info">
              <h4 class="flash-name">{{ product.name }}</h4>
              <div class="flash-price">
                <span class="flash-current">¥{{ product.price }}</span>
                <span class="flash-original">¥{{ product.originalPrice }}</span>
              </div>
              <el-progress :percentage="product.soldPercent" :stroke-width="4" :show-text="false" color="#06b6d4" />
              <span class="flash-sold">已抢 {{ product.soldPercent }}%</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 精选商品 -->
    <section class="featured-section">
      <div class="section-inner">
        <div class="section-header">
          <h2 class="section-title">精选好物</h2>
          <div class="featured-tabs">
            <button v-for="tab in featuredTabs" :key="tab.key" class="featured-tab" :class="{ active: activeTab === tab.key }" @click="activeTab = tab.key">{{ tab.label }}</button>
          </div>
        </div>
        <div class="products-grid">
          <div v-for="product in filteredProducts" :key="product.id" class="product-card" @click="viewProduct(product)">
            <div class="product-image">
              <span class="product-emoji">{{ getProductEmoji(product) }}</span>
              <span class="product-tag" :style="{ background: product.tagColor }">{{ product.tag }}</span>
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.name }}</h4>
              <div class="product-shop">{{ product.shop }}</div>
              <div class="product-bottom">
                <div class="product-price">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ product.price }}</span>
                  <span class="price-original">¥{{ product.originalPrice }}</span>
                </div>
                <div class="product-meta">
                  <span>{{ product.sales.toLocaleString() }}人付款</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 平台特色 -->
    <section class="features-section">
      <div class="section-inner">
        <h2 class="section-title">平台服务</h2>
        <div class="features-grid">
          <div v-for="feat in serviceGuarantees" :key="feat.title" class="feature-card">
            <div class="feature-icon">{{ feat.icon }}</div>
            <div class="feature-title">{{ feat.title }}</div>
            <div class="feature-desc">{{ feat.desc }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 商家入驻 -->
    <section class="merchant-section">
      <div class="section-inner">
        <div class="merchant-card">
          <div class="merchant-content">
            <h2 class="merchant-title">加入瞻途商家计划</h2>
            <p class="merchant-desc">0元入驻 · 免保证金 · 流量扶持 · 运营指导</p>
            <div class="merchant-benefits">
              <span v-for="benefit in merchantBenefits" :key="benefit" class="benefit-item">{{ benefit }}</span>
            </div>
            <el-button type="primary" size="large" @click="goLogin">立即入驻</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <BrandLogo :size="32" :showName="true" nameSize="16px" />
          <p class="footer-slogan">{{ brand.slogan }}</p>
        </div>
        <div class="footer-links">
          <div v-for="(links, key) in footerLinks" :key="key" class="footer-column">
            <h4>{{ footerTitles[key] }}</h4>
            <a v-for="link in links" :key="link.label" href="#" class="footer-link">{{ link.label }}</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <span>{{ brand.icp }}</span>
      </div>
    </footer>

    <!-- 返回顶部 -->
    <transition name="fade">
      <div v-if="showBackTop" class="back-to-top" @click="scrollToTop">
        <el-icon :size="20"><Top /></el-icon>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Top } from '@element-plus/icons-vue'
import BrandLogo from '../components/BrandLogo.vue'
import {
  BRAND, NAV_ITEMS, FOOTER_LINKS, CATEGORIES, FEATURED_PRODUCTS,
  SERVICE_GUARANTEES
} from '../config/brand.js'

const router = useRouter()
const brand = BRAND
const navItems = NAV_ITEMS
const footerLinks = FOOTER_LINKS
const categories = CATEGORIES
const featuredProducts = FEATURED_PRODUCTS
const serviceGuarantees = SERVICE_GUARANTEES

const footerTitles = {
  help: '帮助中心',
  about: '关于瞻途',
  policy: '法律信息',
  follow: '关注我们'
}

const heroStats = [
  { label: '注册用户', value: '580万+' },
  { label: '入驻商家', value: '2.8万+' },
  { label: '在售商品', value: '860万+' },
  { label: '好评率', value: '99.6%' }
]

const featuredTabs = [
  { key: 'all', label: '全部' },
  { key: 'digital', label: '数码3C' },
  { key: 'beauty', label: '美妆个护' },
  { key: 'food', label: '食品生鲜' },
  { key: 'home', label: '家居生活' }
]

const flashProducts = [
  { id: 1, emoji: '🎧', name: '瞻途智能降噪耳机 Pro', price: 599, originalPrice: 899, soldPercent: 89 },
  { id: 2, emoji: '💻', name: '瞻途轻薄笔记本 14寸', price: 4999, originalPrice: 5999, soldPercent: 65 },
  { id: 3, emoji: '🧴', name: '瞻途蚕丝面膜30片', price: 199, originalPrice: 299, soldPercent: 95 },
  { id: 4, emoji: '🤖', name: '瞻途扫地机器人', price: 1299, originalPrice: 1999, soldPercent: 42 },
  { id: 5, emoji: '🥜', name: '瞻途每日坚果大礼包', price: 89, originalPrice: 139, soldPercent: 98 }
]

const merchantBenefits = ['新商家首年免技术服务费', '专属流量扶持', '专业运营指导', '数据分析工具']

const activeNav = ref('首页')
const activeTab = ref('all')
const searchKeyword = ref('')
const showBackTop = ref(false)
const countdown = reactive({ hours: '02', minutes: '15', seconds: '30' })
let countdownTimer = null

const tabProductMap = {
  all: [1, 2, 3, 4, 5, 6, 7, 8],
  digital: [1, 2, 4],
  beauty: [3],
  food: [5],
  home: [7]
}

const filteredProducts = computed(() => {
  if (activeTab.value === 'all') return featuredProducts
  const ids = tabProductMap[activeTab.value] || []
  return featuredProducts.filter(p => ids.includes(p.id))
})

function getProductEmoji(product) {
  const emojis = { 1: '🎧', 2: '💻', 3: '🧴', 4: '🤖', 5: '🥜', 6: '👟', 7: '🪑', 8: '🍼' }
  return emojis[product.id] || '📦'
}
function handleNav(label) { activeNav.value = label }
function handleSearch() { if (!searchKeyword.value.trim()) return }
function handleCategoryClick(cat) {}
function viewProduct(product) {}
function goLogin() { router.push('/login') }
function scrollToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
function handleScroll() { showBackTop.value = window.scrollY > 400 }
function updateCountdown() {
  let totalSeconds = 2 * 3600 + 15 * 60 + 30
  totalSeconds--
  if (totalSeconds < 0) totalSeconds = 24 * 3600
  const h = Math.floor(totalSeconds / 3600)
  const m = Math.floor((totalSeconds % 3600) / 60)
  const s = totalSeconds % 60
  countdown.hours = String(h).padStart(2, '0')
  countdown.minutes = String(m).padStart(2, '0')
  countdown.seconds = String(s).padStart(2, '0')
}

onMounted(() => { window.addEventListener('scroll', handleScroll); countdownTimer = setInterval(updateCountdown, 1000) })
onUnmounted(() => { window.removeEventListener('scroll', handleScroll); if (countdownTimer) clearInterval(countdownTimer) })
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f8f9fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  color: #333;
}

.section-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* ========== 顶部导航 ========== */
.site-header {
  background: #fff;
  border-bottom: 1px solid #eaeaea;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 40px;
}

.header-left {
  flex-shrink: 0;
}

.main-nav {
  display: flex;
  gap: 32px;
  flex: 1;
}

.nav-item {
  color: #555;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
  position: relative;
}

.nav-item:hover,
.nav-item.active {
  color: #06b6d4;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 2px;
  background: #06b6d4;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.search-box {
  width: 240px;
}

.search-box :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: #f5f5f5;
  box-shadow: none;
}

.search-box :deep(.el-input__wrapper:hover) {
  background: #efefef;
}

/* ========== Hero 区域 ========== */
.hero-section {
  background: #fff;
  padding: 80px 0;
  border-bottom: 1px solid #eaeaea;
}

.hero-content {
  text-align: center;
  margin-bottom: 60px;
}

.hero-title {
  font-size: 42px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 20px 0;
  letter-spacing: 2px;
}

.hero-desc {
  font-size: 16px;
  color: #888;
  line-height: 1.8;
  margin: 0 0 32px 0;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.hero-actions :deep(.el-button--primary) {
  background: #06b6d4;
  border-color: #06b6d4;
}

.hero-actions :deep(.el-button--primary:hover) {
  background: #0891b2;
  border-color: #0891b2;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 80px;
}

.hero-stat {
  text-align: center;
}

.hero-stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #06b6d4;
}

.hero-stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 8px;
}

/* ========== 通用标题 ========== */
.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 32px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-header .section-title {
  margin: 0;
}

/* ========== 商品分类 ========== */
.categories-section {
  padding: 60px 0;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.category-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.category-card:hover {
  border-color: #06b6d4;
  transform: translateY(-2px);
}

.category-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.category-name {
  font-size: 14px;
  color: #555;
}

/* ========== 限时秒杀 ========== */
.flash-section {
  padding: 60px 0;
  background: #fff;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.countdown-label {
  margin-right: 8px;
}

.time-block {
  background: #333;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  min-width: 32px;
  text-align: center;
}

.time-sep {
  color: #333;
  font-weight: 600;
}

.flash-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.flash-card {
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.flash-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.flash-image {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.flash-info {
  padding: 16px;
}

.flash-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.flash-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.flash-current {
  font-size: 20px;
  font-weight: 600;
  color: #06b6d4;
}

.flash-original {
  font-size: 12px;
  color: #bbb;
  text-decoration: line-through;
}

.flash-sold {
  font-size: 12px;
  color: #999;
  display: block;
  margin-top: 4px;
}

/* ========== 精选商品 ========== */
.featured-section {
  padding: 60px 0;
}

.featured-tabs {
  display: flex;
  gap: 8px;
}

.featured-tab {
  padding: 6px 16px;
  border: 1px solid #e0e0e0;
  background: #fff;
  border-radius: 16px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.featured-tab:hover {
  border-color: #06b6d4;
  color: #06b6d4;
}

.featured-tab.active {
  background: #06b6d4;
  border-color: #06b6d4;
  color: #fff;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.product-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.product-image {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 56px;
  background: #fafafa;
  position: relative;
}

.product-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  color: #fff;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 40px;
}

.product-shop {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.price-symbol {
  font-size: 12px;
  color: #06b6d4;
  font-weight: 600;
}

.price-value {
  font-size: 20px;
  font-weight: 600;
  color: #06b6d4;
}

.price-original {
  font-size: 12px;
  color: #ccc;
  text-decoration: line-through;
}

.product-meta {
  font-size: 12px;
  color: #bbb;
}

/* ========== 平台服务 ========== */
.features-section {
  padding: 60px 0;
  background: #fff;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  text-align: center;
  padding: 32px 24px;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  transition: all 0.2s;
}

.feature-card:hover {
  border-color: #06b6d4;
}

.feature-icon {
  font-size: 36px;
  margin-bottom: 16px;
}

.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.feature-desc {
  font-size: 13px;
  color: #999;
  line-height: 1.6;
}

/* ========== 商家入驻 ========== */
.merchant-section {
  padding: 60px 0;
}

.merchant-card {
  background: #fff;
  border-radius: 8px;
  padding: 48px;
  text-align: center;
  border: 1px solid #eaeaea;
}

.merchant-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px 0;
}

.merchant-desc {
  font-size: 15px;
  color: #888;
  margin: 0 0 24px 0;
}

.merchant-benefits {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 32px;
}

.benefit-item {
  padding: 6px 16px;
  background: #f0f9ff;
  color: #06b6d4;
  border-radius: 16px;
  font-size: 13px;
}

.merchant-card :deep(.el-button--primary) {
  background: #06b6d4;
  border-color: #06b6d4;
}

/* ========== 页脚 ========== */
.site-footer {
  background: #fff;
  border-top: 1px solid #eaeaea;
  padding: 48px 0 24px 0;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  gap: 60px;
}

.footer-brand {
  flex-shrink: 0;
}

.footer-slogan {
  font-size: 13px;
  color: #999;
  margin: 12px 0 0 0;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 60px;
}

.footer-column h4 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.footer-link {
  display: block;
  font-size: 13px;
  color: #999;
  text-decoration: none;
  margin-bottom: 10px;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #06b6d4;
}

.footer-bottom {
  max-width: 1200px;
  margin: 32px auto 0 auto;
  padding: 24px 24px 0 24px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
  font-size: 13px;
  color: #bbb;
}

/* ========== 返回顶部 ========== */
.back-to-top {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 44px;
  height: 44px;
  background: #06b6d4;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
  z-index: 50;
  transition: all 0.2s;
}

.back-to-top:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(6, 182, 212, 0.4);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ========== 响应式 ========== */
@media (max-width: 992px) {
  .categories-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  .flash-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .hero-stats {
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .header-inner {
    flex-wrap: wrap;
    gap: 16px;
  }
  .main-nav {
    order: 3;
    width: 100%;
    justify-content: center;
    gap: 24px;
  }
  .search-box {
    width: 180px;
  }
  .hero-title {
    font-size: 28px;
  }
  .hero-stats {
    flex-wrap: wrap;
    gap: 24px;
  }
  .hero-stat-value {
    font-size: 24px;
  }
  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .flash-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .features-grid {
    grid-template-columns: 1fr;
  }
  .footer-inner {
    flex-direction: column;
    gap: 32px;
  }
  .footer-links {
    grid-template-columns: repeat(2, 1fr);
    gap: 32px;
  }
}

@media (max-width: 480px) {
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .flash-grid {
    grid-template-columns: 1fr;
  }
  .products-grid {
    grid-template-columns: 1fr;
  }
  .hero-section {
    padding: 48px 0;
  }
  .hero-title {
    font-size: 24px;
  }
}
</style>
