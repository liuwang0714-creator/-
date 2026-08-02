<script setup>
import { RouterView,useRouter,useRoute } from 'vue-router';
import { ref, onMounted, nextTick,watch,watchEffect, computed, onUnmounted  } from 'vue'
import axios from 'axios'
import { HomeFilled,Headset,ChatDotRound,Search,Document,VideoPlay,CaretLeft,CaretRight,VideoPause } from '@element-plus/icons-vue'
import { useUserStore } from '../src/store/user'
import { useImStore } from '../src/store/im'
import ChatDrawer from '../src/components/ChatDrawer.vue'

const userStore = useUserStore()
const imStore = useImStore()

const route = useRoute()
const role = ref()
const router = useRouter()

// 是否在 index 页面
const isIndexPage = computed(() => route.path === '/index' || route.path === '/')

function is_login(){
  axios.get("/api/user/isLogin").then((res) => {
    if(!res.data){
      localStorage.removeItem('satoken')
      localStorage.removeItem('role')
      router.push('/login')
    }
  })
}

watchEffect(() => {
  role.value = localStorage.getItem('role')
})

// 检测登录状态 → 初始化 IM（在 onMounted 中确保执行）
onMounted(async () => {
  applyTheme()
  const savedRole = localStorage.getItem('role')
  const savedToken = localStorage.getItem('satoken')
  if (savedRole && savedToken) {
    try {
      const res = await axios.get('/api/user/info')
      if (res.data.code === 0) {
        const user = res.data.data
        console.log('[IM] 初始化聊天系统:', user.username, savedRole)
        imStore.init({
          id: user.id,
          username: user.username,
          nickname: user.nickname,
          avatar: user.file_url,
          roleCode: savedRole
        })
      }
    } catch (e) {
      console.log('[IM] 初始化失败:', e)
    }
  }
})

const songname = ref('邓紫棋')
const nowid = ref('')
const drawer = ref(false)
const segintable = ref([])

// 音频核心变量
const status = ref("未播放")
let audio = null
// 进度条配套响应式
const currentTime = ref(0)
const duration = ref(0)
const progressPercent = ref(0)

document.documentElement.setAttribute('referrerpolicy', 'origin')

// 格式化秒数为 MM:SS
const formatTime = (sec) => {
  if (isNaN(sec)) return '00:00'
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// 拖动进度条跳转播放位置
function seekAudio(e) {
  if (!audio || !audio.duration) return
  const val = Number(e.target.value)
  audio.currentTime = val
  currentTime.value = val
}

// 核心播放方法（示例原版逻辑不变）
async function playSong(id) {
  status.value = "获取音频地址中..."
  try {
    const res = await fetch(`http://127.0.0.1:3158/play/${id}`, {
      headers: {
        "Accept": "application/json"
      }
    })
    const data = await res.json()

    status.value = "开始播放"

    if (!audio) {
      audio = new Audio()
      // 缓冲/播放/失败事件
      audio.addEventListener("playing", () => {
        status.value = "播放中"
      })
      audio.addEventListener("waiting", () => {
        status.value = "缓冲中"
      })
      audio.addEventListener("error", () => {
        status.value = "播放失败"
      })
      // 播放结束自动下一首
      audio.addEventListener("ended", nextSong)
      // 音频加载完成，获取总时长
      audio.addEventListener('loadedmetadata', () => {
        duration.value = audio.duration
      })
      // 实时更新进度
      audio.addEventListener('timeupdate', () => {
        currentTime.value = audio.currentTime
        progressPercent.value = (audio.currentTime / audio.duration) * 100 || 0
      })
    }

    audio.src = data.url
    await audio.play()
  } catch (err)
    {console.log('播放请求异常', err)
    status.value = "播放失败"}
  }


// 播放选中歌曲（页面点击列表调用）
function songt(id, index) {
  nowid.value = index
  playSong(id)
}

// 切歌通用方法（上下首调用）
function qiewsongt(id) {
  playSong(id)
}

// 搜索歌曲
function search() {
  if (!songname.value.trim()) return
  axios.post("http://127.0.0.1:3158/encrypt", {
    "name": songname.value
  }).then(res => {
    segintable.value = res.data
  }).catch(err => {
    console.error("请求失败：", err)
  })
}

// 上一首
function prevSong() {
  if (!segintable.value.length) return
  nowid.value--
  if (nowid.value < 0) nowid.value = segintable.value.length - 1
  qiewsongt(segintable.value[nowid.value]["songid"])
}

// 下一首
function nextSong() {
  if (!segintable.value.length) return
  nowid.value++
  if (nowid.value >= segintable.value.length) nowid.value = 0
  qiewsongt(segintable.value[nowid.value]["songid"])
}

// 读取是否开启深色模式（全局生效）
const isDark = ref(localStorage.getItem('theme') === 'dark')

watch(isDark, applyTheme)

function applyTheme() {
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}

async function musicbtn() {
  drawer.value = !drawer.value
  await nextTick()
  // 无正在播放歌曲则默认播放并搜索
  if (!audio || !audio.src) {
    songt(2083785152,1)
    search()
  }
}

function toHome() {
  is_login()
  if(role.value=='admin'){
    router.push('/admin')
  }else if(role.value=='user'){
    router.push('/user/goodslist')
  }else if(role.value=='merchant'){
    router.push('/merchant')
  }else{
    router.push('/login')
  }
  console.log(role.value)
}

// 组件销毁移除音频监听，防止内存泄漏
onUnmounted(() => {
  if(audio) audio.pause()
})
</script>

<template>
  <div id="app">
    <RouterView />
  </div>

  <!-- 去主页：仅在 index 页面显示 -->
  <div v-if="isIndexPage" class="go-home-btn">
    <el-button
      type="primary"
      @click="toHome"
      round
      size="large"
    >
      <el-icon><HomeFilled /></el-icon>
      {{userStore.isLogin ? '去主页' : '去登录'}}
    </el-button>
  </div>

  <!-- 音乐控制台：右下角浮动按钮 -->
  <div class="music-fab" @click="musicbtn">
    <el-icon :size="22"><Headset /></el-icon>
    <div>音乐</div>
  </div>

  <!-- 聊天浮动按钮：登录后显示 -->
  <div v-if="role" class="chat-fab" @click="imStore.openDrawer()">
    <el-badge :value="imStore.unreadCount" :hidden="imStore.unreadCount === 0" :max="99" class="chat-badge">
      <el-icon :size="22"><ChatDotRound /></el-icon>
    </el-badge>
    <div>消息</div>
  </div>

  <!-- 聊天抽屉 -->
  <ChatDrawer />

  <el-drawer v-model="drawer" title="音乐播放器" :with-header="true" direction="rtl" size="80vh">
    <div class="drawer-content">
      <!-- 搜索栏 -->
      <div class="search-wrapper">
        <input
          type="text"
          placeholder="请输入要搜索的音乐"
          @keyup.enter="search"
          v-model="songname"
          class="search-input"
        >
        <button @click="search" class="search-btn">
          <el-icon><Search /></el-icon>
          搜索
        </button>
      </div>

      <!-- 歌曲列表 -->
      <div class="song-list">
        <ul>
          <li v-for="item,index in segintable" :key="item.songid || index" class="song-item" @click="songt(item.songid,index)">
            <img :src="item?.picUrl" alt="封面" class="fmimg">
            <div class="song-info">
              <span class="song-name">{{ item?.sname }}</span>
              <span class="singer-name">{{ item?.rname }}</span>
            </div>
            <el-button type="primary" @click.stop="songt(item.songid,index)" size="large" circle>
              <el-icon><VideoPlay /></el-icon>
            </el-button>
          </li>
        </ul>

        <!-- 空状态 -->
        <div v-if="segintable.length === 0" class="empty-tip">
          <el-icon size="48" color="#ccc"><Document /></el-icon>
          <p>暂无歌曲，搜索后显示</p>
        </div>
      </div>
      <!-- 播放状态展示 -->
      <div style="margin:12px 0;color:#666">播放状态：{{ status }}</div>
    </div>

    <!-- 底部播放器（新增自定义进度条） -->
    <div class="player-wrapper">
  <div class="player-controls">
    
    <!-- 歌曲信息 -->
    <div class="player-info">
      <div class="player-title">
        {{ segintable[nowid]?.sname || '暂无播放' }}
      </div>
      <div class="player-artist">
        {{ segintable[nowid]?.rname || '未知歌手' }}
      </div>
    </div>

    <!-- 控制按钮 -->
    <div class="player-btns">

      <!-- 上一首 -->
      <el-button @click="prevSong" circle type="primary" size="large">
        <el-icon>
          <CaretLeft />
        </el-icon>
      </el-button>

      <!-- 下一首 -->
      <el-button @click="nextSong" circle type="primary" size="large">
        <el-icon>
          <CaretRight />
        </el-icon>
      </el-button>

      <!-- 播放 / 暂停（已修复图标 bug） -->
      <el-button
        v-if="audio"
        @click="audio.paused ? audio.play() : audio.pause()"
        circle
        type="success"
        size="large"
      >
        <el-icon>
          <component :is="audio?.paused ? VideoPlay : VideoPause" />
        </el-icon>
      </el-button>

    </div>

    <!-- 进度条 -->
    <div class="progress-wrap">
      
      <!-- 当前时间 -->
      <span class="time-text">
        {{ formatTime(currentTime) }}
      </span>

      <!-- 进度条 -->
      <input
        v-if="audio && duration"
        type="range"
        class="progress-slider"
        min="0"
        :max="duration"
        :value="currentTime"
        @input="seekAudio"
      />

      <!-- 总时长 -->
      <span class="time-text">
        {{ formatTime(duration) }}
      </span>

    </div>

  </div>
</div>
  </el-drawer>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  
}

/* ========== 去主页按钮 ========== */
.go-home-btn {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

.go-home-btn .el-button {
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.35);
  transition: all 0.3s ease;
}

.go-home-btn .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.45);
}

/* ========== 音乐控制台浮动按钮 ========== */
.music-fab {
  position: fixed;
  bottom: 28px;
  right: 28px;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  z-index: 999;
  box-shadow: 0 4px 18px rgba(64, 158, 255, 0.40);
  transition: all 0.3s ease;
  font-size: 11px;
  font-weight: 500;
  user-select: none;
}

.music-fab .el-icon {
  line-height: 1;
}

.music-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.55);
}

.music-fab:active {
  transform: scale(0.95);
}

/* ========== 聊天浮动按钮 ========== */
.chat-fab {
  position: fixed;
  bottom: 96px;
  right: 28px;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  z-index: 999;
  box-shadow: 0 4px 18px rgba(103, 194, 58, 0.40);
  transition: all 0.3s ease;
  font-size: 11px;
  font-weight: 500;
  user-select: none;
}
.chat-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(103, 194, 58, 0.55);
}
.chat-fab:active {
  transform: scale(0.95);
}
.chat-badge {
  line-height: 1;
}
.chat-badge :deep(.el-badge__content) {
  top: -4px;
  right: -6px;
}

/* ========== 抽屉内容 ========== */
.drawer-content {
  height: calc(150vh - 150px);
  overflow-y: auto;
  padding: 20px 16px;
}

/* 搜索栏 */
.search-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 24px;
}

.search-input {
  flex: 1;
  height: 44px;
  padding: 0 16px;
  border: 3px solid #ffffff;
  border-radius: 12px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s;
  background: #3bc1ff;
}

.search-input:focus {
  border-color: #ff0000;
  background: #00bfff;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1);
}

.search-btn {
  height: 44px;
  padding: 0 18px;
  background: #168bff;
  color: #fff;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background 0.3s;
}

.search-btn:hover {
  background: #0e82ff;
}

/* 歌曲列表 */
.song-list {
  width: 100%;
}

.song-list ul {
  list-style: none;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #f1f2f3;
  transition: all 0.25s ease;
}

.song-item:hover {
  transform: translateX(-2px);
  border-color: #dcdfe6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  background: #1f8fff;
  color: #fff;
}

.fmimg {
  width: 46px;
  height: 46px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.song-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.song-name {
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
}

.singer-name {
  font-size: 12px;
  color: #7f8c8d;
}

.song-item:hover .song-name,
.song-item:hover .singer-name {
  color: #fff;
}

/* 空状态 */
.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #999;
}

.empty-tip p {
  margin-top: 12px;
  font-size: 14px;
}

.player-wrapper {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #ffffff;
  border-top: 1px solid #f1f2f3;
  padding: 12px 20px;
  box-shadow: 0 -2px 15px rgba(0, 0, 0, 0.04);
}

.player-controls {
  width: 100%;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.player-info {
  min-width: 140px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.player-title {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.player-artist {
  font-size: 12px;
  color: #7f8c8d;
}

.player-btns {
  display: flex;
  gap: 4px;
}

/* 自定义进度条样式 */
.progress-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  margin-top:8px;
}
.time-text {
  font-size:12px;
  color:#666;
  min-width:42px;
  text-align:center;
}
.progress-slider {
  flex:1;
  height:6px;
  cursor:pointer;
}
</style>