<template>
  <view class="music-app">
    <!-- 动态背景光晕 -->
    <view class="bg-glow glow-1"></view>
    <view class="bg-glow glow-2"></view>

    <!-- 顶部标题区 -->
    <view class="header">
      <text class="logo-text">Ripple</text>
      <text class="slogan">让旋律泛起涟漪</text>
    </view>

    <!-- 搜索入口 -->
    <view class="search-section">
      <view class="search-bar">
        <!-- 搜索图标用 CSS 绘制 -->
        <view class="icon-search-css"></view>
        <input
          class="search-field"
          type="text"
          v-model="songname"
          confirm-type="search"
          placeholder="搜索歌曲、歌手..."
          @confirm="search"
        />
        <view class="search-trigger" @click="search">
          <view class="icon-search-css small light"></view>
        </view>
      </view>
    </view>

    <!-- 歌曲列表（移除顶部正在播放卡片） -->
    <scroll-view scroll-y class="playlist">
      <view class="list-header">
        <text class="list-title">搜索结果</text>
        <text class="list-count" v-if="segintable.length">{{ segintable.length }} 首</text>
      </view>
      <view
        class="track-row"
        v-for="(item, index) in segintable"
        :key="item.songid"
        :class="{ 'track-active': index === nowid }"
        @click="clickPlay(item.songid, index)"
      >
        <view class="track-cover-wrap">
          <image class="track-cover" :src="item.picUrl" mode="aspectFill" />
          <view class="cover-mask" v-if="index === nowid && isPlaying">
            <!-- 暂停图标 CSS -->
            <view class="icon-pause-css small"></view>
          </view>
        </view>
        <view class="track-desc">
          <text class="track-name">{{ item.sname }}</text>
          <text class="track-singer">{{ item.rname }}</text>
        </view>
        <view class="track-play-btn">
          <!-- 播放/暂停图标 CSS -->
          <view v-if="index === nowid && isPlaying" class="icon-pause-css"></view>
          <view v-else class="icon-play-css"></view>
        </view>
      </view>
      <view class="empty-playlist" v-if="segintable.length === 0">
        <text class="empty-icon">♪</text>
        <text class="empty-main">发现音乐</text>
        <text class="empty-sub">搜索你喜欢的歌曲开始播放</text>
      </view>
    </scroll-view>

    <!-- 底部播放条（整合信息+进度+控制） -->
    <view class="mini-player" v-if="segintable.length && (audioCtx || h5Audio)">
      <!-- 当前歌曲信息行 -->
      <view class="player-info-row">
        <image class="player-cover" :src="segintable[nowid]?.picUrl" mode="aspectFill" />
        <view class="player-text">
          <text class="player-title">{{ segintable[nowid]?.sname }}</text>
          <view class="player-status">
            <view class="status-dot" :class="{ live: isPlaying }"></view>
            <text class="status-label">{{ statusText }}</text>
          </view>
        </view>
      </view>

      <!-- 进度条 -->
      <view class="progress-area">
        <text class="time">{{ formatTime(currentTime) }}</text>
        <slider
          class="slider"
          :value="sliderValue"
          :max="Math.floor(duration || 0)"
          block-size="12"
          activeColor="#A78BFA"
          backgroundColor="rgba(255,255,255,0.12)"
          @change="seekAudio"
        />
        <text class="time">{{ formatTime(duration) }}</text>
      </view>

      <!-- 控制按钮组 -->
      <view class="controls">
        <!-- 上一首 -->
        <view class="ctrl-btn" @click="prevSong">
          <view class="icon-prev-css"></view>
        </view>
        <!-- 播放/暂停 -->
        <view class="play-btn-main" @click="togglePlay">
          <view v-if="isPlaying" class="icon-pause-css large"></view>
          <view v-else class="icon-play-css large"></view>
        </view>
        <!-- 下一首 -->
        <view class="ctrl-btn" @click="nextSong">
          <view class="icon-next-css"></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
// 原有脚本逻辑完全保留，无任何修改
import { ref, computed, onUnmounted } from 'vue'

const songname = ref('')
const segintable = ref([])
const nowid = ref(0)
const isPlaying = ref(false)

const statusText = ref("未播放")
const currentTime = ref(0)
const duration = ref(0)
const sliderValue = computed(() => Math.floor(currentTime.value))

let audioCtx = null
let h5Audio = null

const getPlatform = () => {
  const info = uni.getSystemInfoSync()
  return info.uniPlatform
}

function destroyAllAudio() {
  if (audioCtx) {
    audioCtx.destroy()
    audioCtx = null
  }
  // #ifdef H5
  if (h5Audio) {
    h5Audio.pause()
    h5Audio = null
  }
  // #endif
  isPlaying.value = false
  statusText.value = "未播放"
  currentTime.value = 0
  duration.value = 0
}

function createUniAudio() {
  if (audioCtx) audioCtx.destroy()
  audioCtx = uni.createInnerAudioContext()
  audioCtx.autoplay = false
  audioCtx.obeyMuteSwitch = false
  audioCtx.preload = 'auto'

  audioCtx.onPlay(() => {
    isPlaying.value = true
    statusText.value = "播放中"
  })
  audioCtx.onPause(() => {
    isPlaying.value = false
    statusText.value = "已暂停"
  })
  audioCtx.onStop(() => {
    isPlaying.value = false
    statusText.value = "未播放"
  })
  audioCtx.onWaiting(() => {
    statusText.value = "缓冲中..."
  })
  audioCtx.onCanplay(() => {
    statusText.value = "缓冲就绪"
  })
  audioCtx.onEnded(() => {
    nextSong()
  })
  audioCtx.onError((e) => {
    console.error("uni音频错误", e)
    statusText.value = "播放失败"
    isPlaying.value = false
  })
  audioCtx.onTimeUpdate(() => {
    currentTime.value = audioCtx.currentTime
    duration.value = audioCtx.duration || 0
  })
}

// #ifdef H5
function createH5Audio() {
  if (h5Audio) h5Audio.pause()
  h5Audio = new Audio()
  h5Audio.preload = 'auto'

  h5Audio.addEventListener("playing", () => {
    isPlaying.value = true
    statusText.value = "播放中"
  })
  h5Audio.addEventListener("waiting", () => {
    statusText.value = "缓冲中..."
  })
  h5Audio.addEventListener("error", () => {
    statusText.value = "播放失败"
    isPlaying.value = false
  })
  h5Audio.addEventListener("ended", nextSong)
  h5Audio.addEventListener('loadedmetadata', () => {
    duration.value = h5Audio.duration
  })
  h5Audio.addEventListener('timeupdate', () => {
    currentTime.value = h5Audio.currentTime
  })
}
// #endif

function formatTime(sec) {
  if (isNaN(sec) || sec <= 0) return "00:00"
  const m = Math.floor(sec / 60).toString().padStart(2, "0")
  const s = Math.floor(sec % 60).toString().padStart(2, "0")
  return `${m}:${s}`
}

function seekAudio(e) {
  const target = e.detail.value
  const plat = getPlatform()
  // #ifdef H5
  if (plat === "web" && h5Audio) {
    h5Audio.currentTime = target
    currentTime.value = target
    return
  }
  // #endif
  if (audioCtx && duration.value) {
    audioCtx.seek(target)
    currentTime.value = target
  }
}

async function playMusic(songId) {
  const plat = getPlatform()
  statusText.value = "加载音源..."
  destroyAllAudio()

  // #ifdef H5
  if (plat === "web") {
    try {
      const res = await uni.request({
        url: `http://127.0.0.1:3158/play/${songId}`,
        method: "GET",
        header: {
          "Accept": "application/json"
        }
      })
      const realNetEaseUrl = res.data.url
      if (!realNetEaseUrl) throw new Error("无音源地址")
      createH5Audio()
      h5Audio.src = realNetEaseUrl
      h5Audio.play()
    } catch (err) {
      console.error("H5播放失败", err)
      statusText.value = "音源加载失败"
      uni.showToast({ title: "音源加载失败", icon: "none" })
    }
    return
  }
  // #endif

  createUniAudio()
  audioCtx.src = `http://127.0.0.1:3158/play/${songId}`
  audioCtx.play()
}

function clickPlay(id, index) {
  nowid.value = index
  playMusic(id)
}

function togglePlay() {
  const plat = getPlatform()
  // #ifdef H5
  if (plat === "web" && h5Audio) {
    if (h5Audio.paused) h5Audio.play()
    else h5Audio.pause()
    return
  }
  // #endif
  if (!audioCtx || !audioCtx.src) {
    uni.showToast({ title: "请先点击歌曲播放", icon: "none" })
    return
  }
  if (isPlaying.value) audioCtx.pause()
  else audioCtx.play()
}

function nextSong() {
  if (!segintable.value.length) return
  nowid.value++
  if (nowid.value >= segintable.value.length) nowid.value = 0
  playMusic(segintable.value[nowid.value].songid)
}

function prevSong() {
  if (!segintable.value.length) return
  nowid.value--
  if (nowid.value < 0) nowid.value = segintable.value.length - 1
  playMusic(segintable.value[nowid.value].songid)
}

function search() {
  if (!songname.value.trim()) {
    uni.showToast({ title: '请输入歌曲名称', icon: 'none' })
    return
  }
  uni.showLoading({ title: '搜索中...' })
  uni.request({
    url: 'http://127.0.0.1:3158/encrypt',
    method: 'POST',
    data: { name: songname.value },
    success: (res) => {
      segintable.value = res.data || []
      if (segintable.value.length === 0) {
        uni.showToast({ title: "未搜到歌曲", icon: "none" })
      }
    },
    fail: () => {
      uni.showToast({ title: '搜索失败', icon: 'none' })
    },
    complete: () => {
      uni.hideLoading()
    }
  })
}

onUnmounted(() => {
  destroyAllAudio()
})
</script>

<style scoped>
/* 全局变量与基础 */
.music-app {
  --primary: #A78BFA;
  --primary-dark: #7C3AED;
  min-height: 100vh;
  background: linear-gradient(145deg, #0A0A0F 0%, #1A1025 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding: 0 28rpx 0 28rpx;
  box-sizing: border-box;
}

/* 动态背景光晕 */
.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(120rpx);
  opacity: 0.15;
  pointer-events: none;
  z-index: 0;
}
.glow-1 {
  width: 500rpx;
  height: 500rpx;
  background: #7C3AED;
  top: -100rpx;
  right: -150rpx;
}
.glow-2 {
  width: 400rpx;
  height: 400rpx;
  background: #2563EB;
  bottom: 200rpx;
  left: -100rpx;
}

/* 头部 */
.header {
  position: relative;
  z-index: 2;
  padding: 80rpx 0 30rpx 0;
}
.logo-text {
  font-size: 52rpx;
  font-weight: 800;
  background: linear-gradient(to right, #E2E8F0, #A78BFA);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 4rpx;
}
.slogan {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 8rpx;
  letter-spacing: 2rpx;
}

/* 搜索栏 */
.search-section {
  position: relative;
  z-index: 2;
  margin-bottom: 40rpx;
}
.search-bar {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(30rpx);
  border-radius: 60rpx;
  height: 96rpx;
  padding: 0 20rpx 0 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
  transition: 0.2s;
}
.search-bar:focus-within {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(167, 139, 250, 0.3);
  box-shadow: 0 0 30rpx rgba(167, 139, 250, 0.1);
}
.search-field {
  flex: 1;
  height: 100%;
  color: #fff;
  font-size: 30rpx;
  background: transparent;
  margin-left: 16rpx;
}
.search-field::placeholder {
  color: rgba(255, 255, 255, 0.35);
}
.search-trigger {
  width: 68rpx;
  height: 68rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #A78BFA, #7C3AED);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 25rpx rgba(124, 58, 237, 0.4);
  transition: transform 0.15s;
  margin-left: 12rpx;
}
.search-trigger:active {
  transform: scale(0.9);
}

/* CSS 图标：搜索 */
.icon-search-css {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid rgba(255,255,255,0.6);
  border-radius: 50%;
  position: relative;
}
.icon-search-css::after {
  content: "";
  position: absolute;
  bottom: -4rpx;
  right: -4rpx;
  width: 4rpx;
  height: 12rpx;
  background: rgba(255,255,255,0.6);
  border-radius: 2rpx;
  transform: rotate(-45deg);
}
.icon-search-css.small {
  width: 26rpx;
  height: 26rpx;
}
.icon-search-css.light {
  border-color: #fff;
}
.icon-search-css.light::after {
  background: #fff;
}

/* 歌曲列表 */
.playlist {
  position: relative;
  z-index: 2;
  flex: 1;
  margin-bottom: 160rpx; /* 给底部播放器留空 */
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 0 10rpx 20rpx 10rpx;
}
.list-title {
  font-size: 34rpx;
  font-weight: 700;
  color: #fff;
  opacity: 0.9;
}
.list-count {
  font-size: 24rpx;
  color: rgba(255,255,255,0.4);
}

.track-row {
  display: flex;
  align-items: center;
  padding: 16rpx 16rpx;
  border-radius: 24rpx;
  margin-bottom: 14rpx;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10rpx);
  transition: 0.2s;
  border: 1rpx solid transparent;
}
.track-row:active {
  background: rgba(255, 255, 255, 0.08);
  transform: scale(0.98);
}
.track-active {
  background: rgba(167, 139, 250, 0.12);
  border-color: rgba(167, 139, 250, 0.25);
  box-shadow: 0 8rpx 24rpx rgba(124, 58, 237, 0.1);
}
.track-cover-wrap {
  position: relative;
  width: 88rpx;
  height: 88rpx;
  border-radius: 20rpx;
  overflow: hidden;
  flex-shrink: 0;
}
.track-cover {
  width: 100%;
  height: 100%;
}
.cover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
}
.track-desc {
  flex: 1;
  margin-left: 20rpx;
  overflow: hidden;
}
.track-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #fff;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.track-singer {
  font-size: 24rpx;
  color: rgba(255,255,255,0.5);
  margin-top: 4rpx;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.track-play-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
}
.track-active .track-play-btn {
  background: rgba(167, 139, 250, 0.3);
}

/* CSS 图标：播放（三角形） */
.icon-play-css {
  width: 0;
  height: 0;
  border-top: 14rpx solid transparent;
  border-bottom: 14rpx solid transparent;
  border-left: 24rpx solid #fff;
  margin-left: 4rpx;
}
.icon-play-css.large {
  border-top-width: 18rpx;
  border-bottom-width: 18rpx;
  border-left-width: 30rpx;
}
.icon-play-css.small {
  border-top-width: 10rpx;
  border-bottom-width: 10rpx;
  border-left-width: 16rpx;
}

/* CSS 图标：暂停（两条竖线） */
.icon-pause-css {
  display: flex;
  gap: 8rpx;
}
.icon-pause-css::before,
.icon-pause-css::after {
  content: "";
  width: 6rpx;
  height: 24rpx;
  background: #fff;
  border-radius: 2rpx;
}
.icon-pause-css.small::before,
.icon-pause-css.small::after {
  width: 5rpx;
  height: 18rpx;
}
.icon-pause-css.large::before,
.icon-pause-css.large::after {
  width: 8rpx;
  height: 30rpx;
}

/* CSS 图标：上一首/下一首（三角形+竖线） */
.icon-prev-css {
  width: 0;
  height: 0;
  border-top: 16rpx solid transparent;
  border-bottom: 16rpx solid transparent;
  border-right: 20rpx solid #fff;
  position: relative;
}
.icon-prev-css::after {
  content: "";
  position: absolute;
  left: -10rpx;
  top: -10rpx;
  width: 4rpx;
  height: 20rpx;
  background: #fff;
  border-radius: 2rpx;
}
.icon-next-css {
  width: 0;
  height: 0;
  border-top: 16rpx solid transparent;
  border-bottom: 16rpx solid transparent;
  border-left: 20rpx solid #fff;
  position: relative;
}
.icon-next-css::after {
  content: "";
  position: absolute;
  right: -10rpx;
  top: -10rpx;
  width: 4rpx;
  height: 20rpx;
  background: #fff;
  border-radius: 2rpx;
}

/* 空列表 */
.empty-playlist {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 500rpx;
}
.empty-icon {
  font-size: 64rpx;
  color: rgba(255,255,255,0.15);
}
.empty-main {
  font-size: 34rpx;
  color: rgba(255,255,255,0.35);
  margin: 20rpx 0 8rpx 0;
  font-weight: 500;
}
.empty-sub {
  font-size: 24rpx;
  color: rgba(255,255,255,0.2);
}

/* 底部播放条（整合信息、进度、控制） */
.mini-player {
  position: fixed;
  left: 20rpx;
  right: 20rpx;
  bottom: 30rpx;
  z-index: 10;
  background: rgba(10, 10, 20, 0.75);
  backdrop-filter: blur(50rpx);
  border-radius: 48rpx;
  padding: 24rpx 28rpx 30rpx 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 25rpx 60rpx rgba(0, 0, 0, 0.6);
}

/* 歌曲信息行 */
.player-info-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
  gap: 20rpx;
}
.player-cover {
  width: 80rpx;
  height: 80rpx;
  border-radius: 24rpx;
  flex-shrink: 0;
  box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.4);
}
.player-text {
  flex: 1;
  overflow: hidden;
}
.player-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #fff;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.player-status {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 6rpx;
}
.status-dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: #6B7280;
}
.status-dot.live {
  background: #4ADE80;
  box-shadow: 0 0 10rpx #4ADE80;
}
.status-label {
  font-size: 22rpx;
  color: rgba(255,255,255,0.6);
}

/* 进度条 */
.progress-area {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 26rpx;
}
.time {
  font-size: 22rpx;
  color: rgba(255,255,255,0.6);
  min-width: 70rpx;
  text-align: center;
  font-variant-numeric: tabular-nums;
}
.slider {
  flex: 1;
}

/* 控制按钮 */
.controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48rpx;
}
.ctrl-btn {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.15s;
}
.ctrl-btn:active {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(0.9);
}
.play-btn-main {
  width: 116rpx;
  height: 116rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #A78BFA, #7C3AED);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 15rpx 35rpx rgba(124, 58, 237, 0.5);
  transition: transform 0.15s, box-shadow 0.2s;
}
.play-btn-main:active {
  transform: scale(0.88);
  box-shadow: 0 10rpx 25rpx rgba(124, 58, 237, 0.7);
}
</style>