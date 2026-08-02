<template>
  <div class="conv-list-wrapper">
    <div class="section-header">
      <span class="section-title">最近会话</span>
    </div>
    <div class="conv-list">
      <div
        v-for="conv in store.conversations"
        :key="conv.partnerUsername"
        :class="['conv-item', store.activeChatUsername === conv.partnerUsername ? 'active' : '']"
        @click="selectChat(conv.partnerUsername)"
      >
        <el-badge :is-dot="store.isUserOnline(conv.partnerUsername)" class="online-dot">
          <el-avatar :size="36" :src="avatarUrl(conv.partnerAvatar)">{{ conv.partnerNickname?.[0] }}</el-avatar>
        </el-badge>
        <div class="conv-info">
          <div class="conv-top">
            <span class="conv-name">{{ conv.partnerNickname }}</span>
            <el-tag :color="roleColor(conv.partnerRoleCode)" size="small" effect="dark" class="role-tag">
              {{ roleLabel(conv.partnerRoleCode) }}
            </el-tag>
          </div>
          <p class="conv-preview">{{ conv.lastMessage }}</p>
        </div>
      </div>
      <el-empty v-if="!store.conversations.length" description="暂无会话" :image-size="50" class="empty-hint" />
    </div>

    <template v-if="store.currentUser?.roleCode === 'admin'">
      <div class="section-header" style="margin-top: 12px;">
        <span class="section-title">可联系用户</span>
      </div>
      <div class="user-list">
      <div
        v-for="user in store.chatTargets"
        :key="user.username"
        :class="['conv-item', store.activeChatUsername === user.username ? 'active' : '']"
        @click="selectChat(user.username)"
      >
        <el-badge :is-dot="store.isUserOnline(user.username)" class="online-dot">
          <el-avatar :size="36" :src="avatarUrl(user.avatar)">{{ user.nickname?.[0] }}</el-avatar>
        </el-badge>
        <div class="conv-info">
          <div class="conv-top">
            <span class="conv-name">{{ user.nickname }}</span>
            <el-tag :color="roleColor(user.roleCode)" size="small" effect="dark" class="role-tag">
              {{ roleLabel(user.roleCode) }}
            </el-tag>
          </div>
        </div>
      </div>
      <el-empty v-if="!store.chatTargets.length" description="无可联系用户" :image-size="50" class="empty-hint" />
    </div>
    </template>
  </div>
</template>

<script setup>
import { useImStore } from '@/store/im'
import { roleLabel, roleColor, avatarUrl } from '@/api/im'

const store = useImStore()

function selectChat(username) {
  store.switchChat(username)
}
</script>

<style scoped>
.conv-list-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.section-header {
  padding: 8px 12px 4px;
}
.section-title {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}
.conv-list, .user-list {
  flex: 1;
  overflow-y: auto;
}
.conv-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 8px;
  margin: 2px 6px;
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
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.conv-top {
  display: flex;
  align-items: center;
  gap: 6px;
}
.conv-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.role-tag {
  flex-shrink: 0;
}
.conv-preview {
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 0;
}
.online-dot {
  flex-shrink: 0;
}
.empty-hint {
  padding: 20px 0;
}
</style>
