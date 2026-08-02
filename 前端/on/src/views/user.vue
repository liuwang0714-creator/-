<template>
  <div class="user-page">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="header-left">
        <BrandLogo :size="32" :showName="true" nameSize="18px" />
        <h1 class="page-title">瞻途用户中心</h1>
      </div>
      <div class="header-right">
        <div class="user-area">
          <div class="user-meta">
            <span class="greeting-label">你好，</span>
            <span class="nickname">{{ fits[0] }}</span>
          </div>
          <el-avatar shape="circle" :size="42" :src="url" class="user-avatar" />
        </div>
        <el-switch
          v-model="isDark"
          active-icon="Moon"
          inactive-icon="Sunny"
          inline-prompt
          size="large"
          class="dark-switch"
        />
        <el-button type="danger" @click="logout" round size="default">
          <el-icon :size="16"><SwitchButton /></el-icon>
          退出
        </el-button>
      </div>
    </header>

    <div class="main-layout">
      <nav class="sidebar">
        <div class="sidebar-title">导航菜单</div>
        <RouterLink to="/user/userhome" class="link-btn">
          <el-icon :size="16"><Shop /></el-icon>
          <span>首页</span>
        </RouterLink>
        <RouterLink to="/user/goodslist" class="link-btn">
          <el-icon :size="16"><Goods /></el-icon>
          <span>商品列表</span>
        </RouterLink>
        <RouterLink to="/user/userorder" class="link-btn">
          <el-icon :size="16"><Document /></el-icon>
          <span>订单列表</span>
        </RouterLink>
        <RouterLink to="/user/chat" class="link-btn">
          <el-icon :size="16"><ChatDotRound /></el-icon>
          <span>在线消息</span>
        </RouterLink>
      </nav>

      <main class="container">
        <RouterView></RouterView>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { RouterLink, RouterView } from 'vue-router'
import { UserFilled, Goods, Document, ChatDotRound } from '@element-plus/icons-vue'
import BrandLogo from '../components/BrandLogo.vue'
import axios from 'axios'
import { useUserStore } from '../store/user'
import { useImStore } from '../store/im'

const router = useRouter()
const userStore = useUserStore()
const imStore = useImStore()

const file_url = ref('')
const fits = reactive([])
const url = ref('')

watch(file_url, (newVal) => {
  if (newVal) {
    url.value = '/api/images/' + newVal
  }
}, { immediate: true })

onMounted(() => {
  axios.get("/api/user/info").then(res => {
    if (res.data.code === 0) {
      const user = res.data.data
      fits.push(user.nickname)
      file_url.value = user.file_url
    }
  })
})

// 深色模式
const isDark = ref(localStorage.getItem('theme') === 'dark')
watch(isDark, (val) => {
  const html = document.documentElement
  if (val) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
})

// 退出登录
function logout() {
  imStore.reset()
  userStore.logout()
  axios.get("/api/user/logout")
  router.push('/login')
}


</script>

<style scoped>
.user-page {
  max-width: 1260px;
  margin: 0 auto;
  padding: 24px 20px;
}

/* ========== 顶部导航栏 ========== */
.top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--el-bg-color, #fff);
  border-radius: 10px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  padding: 14px 24px;
  margin-bottom: 20px;
  transition: background 0.3s, box-shadow 0.3s;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  color: var(--el-color-primary);
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--el-text-color-primary, #1f2937);
  margin: 0;
  letter-spacing: 0.3px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-meta {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.greeting-label {
  font-size: 13px;
  color: var(--el-text-color-secondary, #6b7280);
}

.nickname {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-color-primary);
}

.user-avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s;
}

.dark-switch {
  margin-left: 4px;
}

/* ========== 主布局 ========== */
.main-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

/* ========== 侧边栏 ========== */
.sidebar {
  display: flex;
  flex-direction: column;
  min-width: 190px;
  background: var(--el-bg-color, #fff);
  border-radius: 10px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  padding: 8px 0;
  flex-shrink: 0;
  overflow: hidden;
  transition: background 0.3s, box-shadow 0.3s;
}

.sidebar-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--el-text-color-placeholder, #9ca3af);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 10px 20px 8px;
}

.link-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 18px;
  color: var(--el-text-color-regular, #4b5563);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.link-btn:hover {
  background: var(--el-color-primary-light-9, #f0f7ff);
  color: var(--el-color-primary, #3b82f6);
  border-left-color: var(--el-color-primary, #3b82f6);
}

/* ========== 内容区 ========== */
.container {
  flex: 1;
  min-width: 0;
  background: var(--el-bg-color, #fff);
  border-radius: 10px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.05);
  padding: 24px;
  transition: background 0.3s, box-shadow 0.3s;
}

/* ========== 深色模式 ========== */
:global(html.dark) .top-header,
:global(html.dark) .sidebar,
:global(html.dark) .container {
  background: #1b1d2a;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.25);
}

:global(html.dark) .link-btn:hover {
  background: rgba(64, 158, 255, 0.1);
}

:global(html.dark) .user-avatar {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
}

:global(html.dark) .greeting-label {
  color: #a0a5b0;
}

:global(html.dark) .sidebar-title {
  color: #6b7080;
}
</style>


