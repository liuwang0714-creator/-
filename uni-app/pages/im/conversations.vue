<template>
  <view class="page">
    <!-- 顶部用户信息 -->
    <view class="top-bar">
      <view class="user-info" v-if="currentUser">
        <image v-if="currentUser.avatar" class="avatar-img" :src="avatarUrl(currentUser.avatar)" mode="aspectFill" />
        <view v-else class="avatar">{{ currentUser.nickname?.[0] || '?' }}</view>
        <view class="user-meta">
          <text class="nickname">{{ currentUser.nickname }}</text>
          <text class="role-tag">{{ roleLabel(currentUser.roleCode) }}</text>
        </view>
        <text :class="['status', wsConnected ? 'online' : 'offline']">
          {{ wsConnected ? '在线' : '离线' }}
        </text>
      </view>
    </view>

    <!-- 最近会话 -->
    <view class="section-title">最近会话</view>
    <scroll-view class="conv-list" scroll-y>
      <view
        v-for="conv in conversations"
        :key="conv.partnerUsername"
        class="conv-item"
        @click="openChat(conv.partnerUsername)"
      >
        <image v-if="conv.partnerAvatar" class="conv-avatar-img" :src="avatarUrl(conv.partnerAvatar)" mode="aspectFill" />
        <view v-else class="conv-avatar">{{ conv.partnerNickname?.[0] || '?' }}</view>
        <view class="conv-info">
          <view class="conv-top">
            <text class="conv-name">{{ conv.partnerNickname }}</text>
            <text class="conv-role">{{ roleLabel(conv.partnerRoleCode) }}</text>
          </view>
          <text class="conv-msg">{{ conv.lastMessage }}</text>
        </view>
      </view>
      <view v-if="!conversations.length" class="empty-tip">暂无会话</view>
    </scroll-view>

    <!-- 可联系用户（仅管理员） -->
    <view v-if="currentUser && currentUser.roleCode === 'admin'" class="section-title">可联系用户</view>
    <scroll-view v-if="currentUser && currentUser.roleCode === 'admin'" class="user-list" scroll-y>
      <view
        v-for="user in chatTargets"
        :key="user.username"
        class="conv-item"
        @click="openChat(user.username)"
      >
        <image v-if="user.avatar" class="conv-avatar-img" :src="avatarUrl(user.avatar)" mode="aspectFill" />
        <view v-else class="conv-avatar">{{ user.nickname?.[0] || '?' }}</view>
        <view class="conv-info">
          <view class="conv-top">
            <text class="conv-name">{{ user.nickname }}</text>
            <text class="conv-role">{{ roleLabel(user.roleCode) }}</text>
          </view>
        </view>
      </view>
      <view v-if="!chatTargets.length" class="empty-tip">无可联系用户</view>
    </scroll-view>
  </view>
</template>

<script>
import {
  getChatTargets, getConversations, getMessageHistory, canChat,
  connectWebSocket, disconnectWebSocket, roleLabel, getUserInfo, avatarUrl
} from '../../utils/im.js'

export default {
  data() {
    return {
      currentUser: null,
      chatTargets: [],
      conversations: [],
      newUsername: '',
      wsConnected: false
    }
  },
  onLoad() {
    this.currentUser = uni.getStorageSync('currentUser')
    if (!this.currentUser) {
      this.loadUserInfo()
    } else {
      this.initChat()
    }
  },
  onUnload() {
    disconnectWebSocket()
  },
  // 从 tab 切换回来时检查账号是否变更
  onShow() {
    const savedUser = uni.getStorageSync('currentUser')
    // 没有用户 → 去登录
    if (!savedUser) {
      uni.redirectTo({ url: '/pages/login/login' })
      return
    }
    // 账号变了 → 整页重置
    if (!this.currentUser || this.currentUser.username !== savedUser.username) {
      disconnectWebSocket()
      this.currentUser = savedUser
      this.chatTargets = []
      this.conversations = []
      this.newUsername = ''
      this.initChat()
      return
    }
    // 同账号 → 刷新列表 + 确保 WS 连接
    this.refreshLists()
    if (!this.wsConnected) {
      this.connectWs()
    }
  },
  methods: {
    roleLabel,
    avatarUrl,
    // 从后端加载用户信息
    async loadUserInfo() {
      try {
        const res = await getUserInfo()
        if (res.code === 0) {
          const user = res.data
          this.currentUser = {
            id: user.id,
            username: user.username,
            nickname: user.nickname,
            roleCode: uni.getStorageSync('role') || 'user'
          }
          uni.setStorageSync('currentUser', this.currentUser)
          this.initChat()
        } else {
          uni.redirectTo({ url: '/pages/login/login' })
        }
      } catch (e) {
        uni.redirectTo({ url: '/pages/login/login' })
      }
    },
    async initChat() {
      await this.refreshLists()
      this.connectWs()
    },
    async refreshLists() {
      try {
        const [targetsRes, convRes] = await Promise.all([
          getChatTargets(this.currentUser.username),
          getConversations(this.currentUser.username)
        ])
        this.chatTargets = targetsRes.data || []
        this.conversations = convRes.data || []
      } catch (e) {
        console.error('刷新列表失败', e)
      }
    },
    connectWs() {
      connectWebSocket(
        this.currentUser.username,
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
        // 有新的消息，刷新会话列表更新预览
        this.refreshLists()
        uni.showToast({ title: '新消息', icon: 'none' })
      }
    },
    async startChat() {
      const toUsername = this.newUsername.trim()
      if (!toUsername) return
      try {
        const res = await canChat(this.currentUser.username, toUsername)
        if (res.code === 0) {
          this.newUsername = ''
          this.openChat(toUsername)
        } else {
          uni.showToast({ title: res.message || '无权发起会话', icon: 'none' })
        }
      } catch {
        uni.showToast({ title: '校验失败', icon: 'none' })
      }
    },
    openChat(username) {
      uni.navigateTo({
        url: '/pages/im/chat?username=' + encodeURIComponent(username)
      })
    }
  }
}
</script>

<style scoped>
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
.top-bar {
	margin-top: 5vh;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  padding: 30rpx 24rpx;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #fff;
  font-weight: 600;
}
.user-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}
.nickname {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}
.role-tag {
  font-size: 22rpx;
  color: rgba(255,255,255,0.8);
  background: rgba(255,255,255,0.15);
  padding: 2rpx 14rpx;
  border-radius: 20rpx;
  align-self: flex-start;
}
.status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}
.online { color: #fff; background: rgba(103,194,58,0.5); }
.offline { color: rgba(255,255,255,0.7); background: rgba(0,0,0,0.15); }

.start-chat {
  display: flex;
  gap: 12rpx;
  padding: 16rpx 20rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
}
.chat-input {
  flex: 1;
  height: 60rpx;
  background: #f5f5f5;
  border-radius: 30rpx;
  padding: 0 24rpx;
  font-size: 26rpx;
}
.btn-start {
  height: 60rpx;
  line-height: 60rpx;
  padding: 0 24rpx;
  background: #409eff;
  color: #fff;
  border-radius: 30rpx;
  font-size: 26rpx;
  border: none;
}
.btn-start[disabled] { opacity: 0.5; }

.section-title {
  font-size: 24rpx;
  color: #999;
  padding: 16rpx 24rpx 8rpx;
  font-weight: 500;
}
.conv-list, .user-list {
  max-height: 280rpx;
  background: #fff;
}
.conv-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.conv-item:active { background: #e6f0ff; }
.conv-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #fff;
  flex-shrink: 0;
}
.conv-avatar-img {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  flex-shrink: 0;
}
.avatar-img {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
}
.conv-info { flex: 1; min-width: 0; }
.conv-top { display: flex; align-items: center; gap: 8rpx; }
.conv-name { font-size: 28rpx; font-weight: 500; }
.conv-role { font-size: 22rpx; color: #999; }
.conv-msg { display: block; font-size: 24rpx; color: #bbb; margin-top: 6rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.empty-tip { text-align: center; padding: 30rpx; color: #ccc; font-size: 26rpx; }
</style>
