<template>
  <div class="chat-panel">
    <!-- 空状态 -->
    <div v-if="!store.activeChatUsername" class="empty-state">
      <el-icon :size="60" color="#d9d9d9"><ChatDotRound /></el-icon>
      <p>选择一个联系人开始聊天</p>
    </div>

    <!-- 聊天区域 -->
    <template v-else>
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-info">
          <el-badge :is-dot="store.isUserOnline(store.activeChatUsername)">
            <el-avatar :size="36" :src="partnerAvatar">{{ store.activeChatTitle?.[0] }}</el-avatar>
          </el-badge>
          <div class="header-text">
            <span class="header-name">{{ store.activeChatTitle }}</span>
            <span class="header-status" :class="{ online: store.isUserOnline(store.activeChatUsername) }">
              {{ store.isUserOnline(store.activeChatUsername) ? '在线' : '离线' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div
          v-for="msg in store.messages"
          :key="msg.id || msg.sendTime + msg.content"
          :class="['message-item', msg.fromUsername === currentUsername ? 'self' : 'other']"
        >
          <el-avatar v-if="msg.fromUsername !== currentUsername" :size="30" :src="avatarUrl(msg.fromAvatar)" class="msg-avatar">
            {{ msg.fromNickname?.[0] }}
          </el-avatar>
          <div class="message-body">
            <div class="message-meta">
              <span class="sender">{{ msg.fromNickname || msg.fromUsername }}</span>
              <span class="time">{{ msg.sendTime }}</span>
            </div>
            <div :class="['message-content', msg.fromUsername === currentUsername ? 'self-bubble' : 'other-bubble']">
              {{ msg.content }}
            </div>
          </div>
          <el-avatar v-if="msg.fromUsername === currentUsername" :size="30" :src="avatarUrl(store.currentUser?.avatar)" class="msg-avatar self-avatar">
            {{ store.currentUser?.nickname?.[0] }}
          </el-avatar>
        </div>
        <el-empty v-if="!store.messages.length" description="暂无消息，发送第一条吧" :image-size="50" class="empty-msg" />
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          placeholder="输入消息，Enter 发送"
          @keyup.enter="sendMessage"
          clearable
          class="msg-input"
        />
        <el-button
          type="primary"
          @click="sendMessage"
          :disabled="!inputMessage.trim()"
          class="send-btn"
        >
          <el-icon><Promotion /></el-icon>
          发送
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue'
import { useImStore } from '@/store/im'
import { avatarUrl } from '@/api/im'

const store = useImStore()
const inputMessage = ref('')
const messageListRef = ref(null)

const currentUsername = computed(() => store.currentUser?.username)

const partnerAvatar = computed(() => {
  const c = store.conversations.find(x => x.partnerUsername === store.activeChatUsername)
  if (c) return avatarUrl(c.partnerAvatar)
  const u = store.chatTargets.find(x => x.username === store.activeChatUsername)
  if (u) return avatarUrl(u.avatar)
  return ''
})

watch(
  () => store.messages.length,
  () => scrollToBottom()
)

watch(
  () => store.activeChatUsername,
  () => scrollToBottom()
)

function sendMessage() {
  const content = inputMessage.value.trim()
  if (!content) return
  const sent = store.sendMessage(content)
  if (sent) inputMessage.value = ''
}

function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.chat-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f7f8fa;
}
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  gap: 12px;
}
.empty-state p {
  font-size: 14px;
  margin: 0;
}
.chat-header {
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-text {
  display: flex;
  flex-direction: column;
}
.header-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}
.header-status {
  font-size: 12px;
  color: #999;
}
.header-status.online {
  color: #67c23a;
}
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}
.message-item {
  display: flex;
  align-items: flex-end;
  margin-bottom: 16px;
  gap: 8px;
}
.message-item.self {
  justify-content: flex-end;
}
.msg-avatar {
  flex-shrink: 0;
}
.self-avatar {
  margin-left: 4px;
}
.message-body {
  max-width: 65%;
}
.message-item.self .message-body {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.message-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
  display: flex;
  gap: 8px;
}
.message-item.self .message-meta {
  flex-direction: row-reverse;
}
.message-content {
  display: inline-block;
  padding: 10px 16px;
  border-radius: 16px;
  line-height: 1.5;
  font-size: 14px;
  word-break: break-word;
}
.self-bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.other-bubble {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.06);
}
.empty-msg {
  margin-top: 40px;
}
.input-area {
  display: flex;
  gap: 10px;
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
  align-items: center;
}
.msg-input {
  flex: 1;
}
.send-btn {
  flex-shrink: 0;
}
.send-btn .el-icon {
  margin-right: 4px;
}
</style>
