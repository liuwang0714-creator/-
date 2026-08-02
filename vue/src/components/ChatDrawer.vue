<template>
  <el-drawer
    v-model="store.drawerVisible"
    title="即时通讯"
    size="780px"
    direction="rtl"
    :before-close="handleClose"
    class="chat-drawer"
  >
    <div class="drawer-body">
      <!-- 左侧：会话列表 -->
      <div class="drawer-sidebar">
        <div class="sidebar-header">
          <div class="user-info" v-if="store.currentUser">
            <el-avatar :size="32" :src="avatarUrl(store.currentUser?.avatar)">{{ store.currentUser.nickname?.[0] }}</el-avatar>
            <div class="user-meta">
              <span class="user-name">{{ store.currentUser.nickname }}</span>
              <el-tag size="small" :color="roleColor(store.currentUser.roleCode)" effect="dark">
                {{ roleLabel(store.currentUser.roleCode) }}
              </el-tag>
            </div>
            <el-tag v-if="store.wsConnected" type="success" size="small" effect="plain">在线</el-tag>
          </div>
        </div>
        <ConversationList />
      </div>

      <!-- 右侧：聊天面板 -->
      <div class="drawer-main">
        <ChatPanel />
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { useImStore } from '@/store/im'
import { roleLabel, roleColor, avatarUrl } from '@/api/im'
import ConversationList from './ConversationList.vue'
import ChatPanel from './ChatPanel.vue'

const store = useImStore()

function handleClose() {
  store.closeDrawer()
}
</script>

<style scoped>
.drawer-body {
  display: flex;
  height: 100%;
  overflow: hidden;
}
/* 消除 el-drawer 默认内边距，让内容撑满 */
:deep(.el-drawer__body) {
  padding: 0;
}
.drawer-sidebar {
  width: 260px;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  background: #fafafa;
  overflow: hidden;
}
.sidebar-header {
  padding: 12px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.user-name {
  font-size: 14px;
  font-weight: 600;
}
.new-chat {
  display: flex;
  gap: 6px;
  padding: 8px 12px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}
.drawer-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
</style>
