<template>
  <view class="page">
    <!-- 自定义顶部栏 -->
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">← 返回</view>
      <text class="nav-title">{{ partnerUsername }}</text>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 消息列表 -->
    <scroll-view class="msg-list" scroll-y :scroll-top="scrollTop" scroll-with-animation @scrolltoupper="loadMore">
      <view
        v-for="(msg, index) in messages"
        :key="msg.id || index"
        :class="['msg-row', msg.fromUsername === currentUsername ? 'self' : '']"
      >
        <view class="msg-time" v-if="showTimeSeparator(index)">{{ msg.sendTime }}</view>
        <view class="msg-inner">
          <image v-if="msg.fromUsername !== currentUsername && msg.fromAvatar" class="msg-avatar" :src="avatarUrl(msg.fromAvatar)" mode="aspectFill" />
          <view v-else-if="msg.fromUsername !== currentUsername" class="msg-avatar-text">{{ msg.fromNickname?.[0] || '?' }}</view>
          <view class="msg-body">
            <view :class="['msg-bubble', msg.fromUsername === currentUsername ? 'self-bubble' : 'other-bubble']">
              {{ msg.content }}
            </view>
          </view>
          <image v-if="msg.fromUsername === currentUsername && currentUser.avatar" class="msg-avatar msg-avatar-self" :src="avatarUrl(currentUser.avatar)" mode="aspectFill" />
          <view v-else-if="msg.fromUsername === currentUsername" class="msg-avatar-text msg-avatar-self">{{ currentUser.nickname?.[0] || '?' }}</view>
        </view>
      </view>
      <view v-if="!messages.length" class="empty-hint">
        <text>暂无消息，发送第一条吧</text>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-bar">
      <input
        class="msg-input"
        v-model="inputMsg"
        placeholder="输入消息..."
        confirm-type="send"
        @confirm="sendMessage"
        :disabled="!wsConnected"
      />
      <button class="send-btn" @click="sendMessage" :disabled="!inputMsg.trim()">发送</button>
    </view>
  </view>
</template>

<script>
import {
  getMessageHistory,
  connectWebSocket, disconnectWebSocket, sendWsMessage,
  formatTime, roleLabel, avatarUrl
} from '../../utils/im.js'

export default {
  data() {
    return {
      currentUser: null,
      currentUsername: '',
      partnerUsername: '',
      partnerNickname: '',
      messages: [],
      inputMsg: '',
      wsConnected: false,
      scrollTop: 0,
      socketTask: null
    }
  },
  onLoad(options) {
    this.partnerUsername = options.username || ''
    if (!this.partnerUsername) {
      uni.showToast({ title: '参数错误', icon: 'none' })
      uni.navigateBack()
      return
    }

    this.currentUser = uni.getStorageSync('currentUser')
    if (!this.currentUser) {
      uni.redirectTo({ url: '/pages/login/login' })
      return
    }

    this.currentUsername = this.currentUser.username
    this.init()
  },
  onUnload() {
    // 不断开 WS，留给后台的 conversations 页面继续接收消息
  },
  methods: {
    formatTime,
    roleLabel,
    avatarUrl,
    async init() {
      await this.loadHistory()
      this.connectWs()
    },
    async loadHistory() {
      try {
        const res = await getMessageHistory(this.currentUsername, this.partnerUsername)
        if (res.code === 0) {
          this.messages = (res.data || []).map(m => ({
            ...m,
            sendTime: this.formatTime(m.sendTime)
          }))
          this.$nextTick(() => { this.scrollTop = 999999 })
        }
      } catch (e) {
        console.error('加载历史消息失败', e)
      }
    },
    connectWs() {
      connectWebSocket(
        this.currentUsername,
        this.handleWsMessage,
        (connected) => { this.wsConnected = connected }
      )
    },
    handleWsMessage(msg) {
      if (msg.type === 'ERROR') {
        uni.showToast({ title: msg.content, icon: 'none' })
        return
      }
      if (msg.type === 'MESSAGE') {
        const isRelevant =
          (msg.fromUsername === this.currentUsername && msg.toUsername === this.partnerUsername) ||
          (msg.fromUsername === this.partnerUsername && msg.toUsername === this.currentUsername)
        if (isRelevant) {
          this.messages.push({
            ...msg,
            sendTime: this.formatTime(msg.sendTime)
          })
          this.$nextTick(() => { this.scrollTop = 999999 })
        }
      }
    },
    sendMessage() {
      const content = this.inputMsg.trim()
      if (!content) return
      const sent = sendWsMessage({
        type: 'MESSAGE',
        toUsername: this.partnerUsername,
        content
      })
      if (sent) {
        this.inputMsg = ''
        // 离线排队时也乐观显示消息
        if (!this.wsConnected) {
          this.messages.push({
            id: Date.now(),
            fromUsername: this.currentUsername,
            toUsername: this.partnerUsername,
            content,
            msgType: 'TEXT',
            sendTime: this.formatTime(new Date().toISOString())
          })
          this.$nextTick(() => { this.scrollTop = 999999 })
        }
      }
    },
    // 显示时间分隔：第一条或与上一条时间差超过5分钟
    showTimeSeparator(index) {
      if (index === 0) return true
      const prev = this.messages[index - 1]
      const curr = this.messages[index]
      if (!prev || !curr) return false
      const t1 = new Date(prev.sendTime?.replace(' ', 'T')).getTime()
      const t2 = new Date(curr.sendTime?.replace(' ', 'T')).getTime()
      return (t2 - t1) > 5 * 60 * 1000
    },
    goBack() {
      uni.navigateBack()
    },
    loadMore() {
      // 上拉加载更多（未来可实现分页）
    }
  }
}
</script>

<style scoped>
/* 页面容器填满可视区域 */
.page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}
/* 自定义导航栏 */
.nav-bar {
  margin-top: 8vh;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 12px;
  padding-top: env(safe-area-inset-top);
  background: #fff;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}
.nav-back {
  font-size: 15px;
  color: #409eff;
  padding: 4px 8px;
}
.nav-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.nav-placeholder {
  width: 60px;
}
.msg-list {
  flex: 1;
  padding: 20rpx;
  overflow-y: auto;
}
.msg-row {
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: column;
}
.msg-row.self {
  align-items: flex-end;
}
.msg-inner {
  display: flex;
  align-items: flex-end;
  gap: 10rpx;
}
.msg-row.self .msg-inner {
  justify-content: flex-end;
}
.msg-avatar {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  flex-shrink: 0;
}
.msg-avatar-self {
  margin-left: 4rpx;
}
.msg-avatar-text {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #fff;
  flex-shrink: 0;
}
.msg-time {
  text-align: center;
  font-size: 22rpx;
  color: #bbb;
  margin-bottom: 16rpx;
  align-self: center;
}
.msg-body {
  max-width: 70%;
}
.msg-bubble {
  display: inline-block;
  padding: 16rpx 24rpx;
  border-radius: 16rpx;
  font-size: 28rpx;
  line-height: 1.6;
  word-break: break-word;
}
.self-bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4rpx;
}
.other-bubble {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4rpx;
  box-shadow: 0 1rpx 4rpx rgba(0,0,0,0.06);
}
.empty-hint {
  text-align: center;
  padding: 60rpx 0;
  color: #ccc;
  font-size: 26rpx;
}

.input-bar {
  display: flex;
  gap: 12rpx;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #eee;
  align-items: center;
  flex-shrink: 0;
}
.msg-input {
  flex: 1;
  height: 68rpx;
  background: #f5f5f5;
  border-radius: 34rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}
.send-btn {
  height: 68rpx;
  line-height: 68rpx;
  padding: 0 28rpx;
  background: #409eff;
  color: #fff;
  border-radius: 34rpx;
  font-size: 28rpx;
  border: none;
  flex-shrink: 0;
}
.send-btn[disabled] { opacity: 0.5; }
</style>
