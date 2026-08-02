<template>
  <div class="chat-page">
    <!-- 左侧：会话列表 -->
    <div class="chat-sidebar">
      <div class="sidebar-header">
        <div class="user-info" v-if="store.currentUser">
          <el-avatar :size="36" :src="avatarUrl(store.currentUser?.avatar)">{{ store.currentUser.nickname?.[0] }}</el-avatar>
          <div class="user-meta">
            <span class="user-name">{{ store.currentUser.nickname }}</span>
            <el-tag size="small" :color="roleColor(store.currentUser.roleCode)" effect="dark">
              {{ roleLabel(store.currentUser.roleCode) }}
            </el-tag>
          </div>
          <div class="status-indicator">
            <span :class="store.wsConnected ? 'online' : 'offline'"></span>
          </div>
        </div>
      </div>

      <div class="conv-scroll">
        <div class="section-label">最近会话</div>
        <div
          v-for="conv in store.conversations"
          :key="conv.partnerUsername"
          :class="['conv-item', store.activeChatUsername === conv.partnerUsername ? 'active' : '']"
          @click="store.switchChat(conv.partnerUsername)"
        >
          <el-badge :is-dot="store.isUserOnline(conv.partnerUsername)">
            <el-avatar :size="40" :src="avatarUrl(conv.partnerAvatar)">{{ conv.partnerNickname?.[0] }}</el-avatar>
          </el-badge>
          <div class="conv-info">
            <div class="conv-top">
              <span class="conv-name">{{ conv.partnerNickname }}</span>
              <el-tag :color="roleColor(conv.partnerRoleCode)" size="small" effect="dark">
                {{ roleLabel(conv.partnerRoleCode) }}
              </el-tag>
            </div>
            <p class="conv-preview">{{ conv.lastMessage }}</p>
          </div>
        </div>

        <template v-if="store.currentUser?.roleCode === 'admin'">
          <div class="section-label" style="margin-top:12px;">可联系用户</div>
          <div
            v-for="user in store.chatTargets"
            :key="user.username"
            :class="['conv-item', store.activeChatUsername === user.username ? 'active' : '']"
            @click="store.switchChat(user.username)"
          >
            <el-badge :is-dot="store.isUserOnline(user.username)">
              <el-avatar :size="40" :src="avatarUrl(user.avatar)">{{ user.nickname?.[0] }}</el-avatar>
            </el-badge>
            <div class="conv-info">
              <div class="conv-top">
                <span class="conv-name">{{ user.nickname }}</span>
                <el-tag :color="roleColor(user.roleCode)" size="small" effect="dark">
                  {{ roleLabel(user.roleCode) }}
                </el-tag>
              </div>
            </div>
          </div>
        </template>
        <el-empty v-if="!store.chatTargets.length && !store.conversations.length" description="暂无联系人" :image-size="60" />
      </div>
    </div>

    <!-- 右侧：聊天面板 -->
    <div class="chat-main-area">
      <ChatPanel />
    </div>
  </div>
</template>

<script setup>
import { useImStore } from '@/store/im'
import { roleLabel, roleColor, avatarUrl } from '@/api/im'
import ChatPanel from '@/components/ChatPanel.vue'

const store = useImStore()
</script>

<style scoped>
.chat-page {
  display: flex;
  height: calc(100vh - 120px);
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.chat-sidebar {
  width: 300px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.user-name {
  font-size: 15px;
  font-weight: 600;
}
.status-indicator {
  display: flex;
  align-items: center;
}
.online {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
}
.offline {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #c0c4cc;
  border-radius: 50%;
}
.conv-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}
.section-label {
  font-size: 12px;
  color: #999;
  padding: 8px 16px 4px;
  font-weight: 500;
}
.conv-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.conv-item:hover {
  background: #f0f2f5;
}
.conv-item.active {
  background: #e6f0ff;
}
.conv-info {
  flex: 1;
  min-width: 0;
}
.conv-top {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 2px;
}
.conv-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.conv-preview {
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 0;
}
.chat-main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
</style>
