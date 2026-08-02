import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getChatTargets, getConversations, getMessageHistory, canChat,
  createWebSocket
} from '@/api/im'
import { ElMessage } from 'element-plus'

export const useImStore = defineStore('im', () => {
  // ========== 状态 ==========
  const currentUser = ref(null)        // 当前登录用户 { id, username, nickname, roleCode }
  const chatTargets = ref([])          // 可联系用户列表
  const conversations = ref([])        // 最近会话列表
  const onlineUsers = ref([])          // 在线用户列表
  const messages = ref([])             // 当前会话的消息列表
  const activeChatUsername = ref('')   // 当前正在聊天的对方用户名
  const wsConnected = ref(false)       // WebSocket 连接状态
  const drawerVisible = ref(false)     // 聊天抽屉显隐
  const unreadCount = ref(0)           // 未读消息总数

  let ws = null                        // WebSocket 实例
  let reconnectTimer = null            // 重连定时器
  let lastMessageTimestamps = {}       // 按会话记录已读的最后时间戳

  // ========== 计算属性 ==========
  const isLoggedIn = computed(() => !!currentUser.value)

  const activeChatTitle = computed(() => {
    if (!activeChatUsername.value) return ''
    const fromTargets = chatTargets.value.find(u => u.username === activeChatUsername.value)
    if (fromTargets) return `${fromTargets.nickname}（${fromTargets.roleCode}）`
    const fromConvs = conversations.value.find(c => c.partnerUsername === activeChatUsername.value)
    if (fromConvs) return `${fromConvs.partnerNickname}（${fromConvs.partnerRoleCode}）`
    return activeChatUsername.value
  })

  // ========== 初始化 ==========
  async function init(user) {
    // 先清掉旧用户的所有状态（切换账号时关键）
    reset()
    currentUser.value = user
    await refreshLists()
    connectWebSocket()
  }

  /** 刷新会话列表和可联系用户 */
  async function refreshLists() {
    if (!currentUser.value) return
    const [targetsRes, convRes] = await Promise.all([
      getChatTargets(currentUser.value.username),
      getConversations(currentUser.value.username)
    ])
    chatTargets.value = targetsRes.data?.data || []
    conversations.value = convRes.data?.data || []
  }

  // ========== WebSocket 管理 ==========
  function connectWebSocket() {
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) return
    try {
      ws = createWebSocket()
      ws.onopen = () => {
        wsConnected.value = true
        ws.send(JSON.stringify({ type: 'CONNECT', fromUsername: currentUser.value?.username }))
        // 发送连接前排队的消息
        flushPendingMessages()
      }
      ws.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data)
          handleWsMessage(msg)
        } catch { /* ignore parse errors */ }
      }
      ws.onclose = () => {
        wsConnected.value = false
        scheduleReconnect()
      }
      ws.onerror = () => {
        wsConnected.value = false
      }
    } catch {
      scheduleReconnect()
    }
  }

  function scheduleReconnect() {
    if (reconnectTimer) clearTimeout(reconnectTimer)
    if (!currentUser.value) return
    reconnectTimer = setTimeout(() => connectWebSocket(), 3000)
  }

  function disconnectWebSocket() {
    if (reconnectTimer) clearTimeout(reconnectTimer)
    if (ws) {
      ws.onclose = null  // 防止触发重连
      ws.close()
      ws = null
    }
    wsConnected.value = false
  }

  // ========== 消息处理 ==========
  function handleWsMessage(msg) {
    if (msg.type === 'ERROR') {
      ElMessage.error(msg.content)
      return
    }
    if (msg.type === 'ONLINE') {
      onlineUsers.value = msg.data || []
      return
    }
    if (msg.type === 'MESSAGE') {
      // 如果消息属于当前活跃会话，直接添加到消息列表
      const isCurrentChat =
        (msg.fromUsername === currentUser.value?.username && msg.toUsername === activeChatUsername.value) ||
        (msg.fromUsername === activeChatUsername.value && msg.toUsername === currentUser.value?.username)
      if (isCurrentChat) {
        messages.value.push(msg)
      } else {
        // 非当前会话的消息计入未读
        unreadCount.value++
      }
      // 刷新会话列表（更新最后消息预览）
      refreshLists()
    }
  }

  // ========== 会话操作 ==========
  async function switchChat(username) {
    if (!username) return
    activeChatUsername.value = username
    await loadHistory()
  }

  async function startChatByUsername(toUsername) {
    if (!toUsername || !currentUser.value) return false
    try {
      const res = await canChat(currentUser.value.username, toUsername)
      if (res.data?.code === 0) {
        await switchChat(toUsername)
        return true
      } else {
        ElMessage.error(res.data?.message || '无权与该用户发起会话')
        return false
      }
    } catch {
      ElMessage.error('校验失败')
      return false
    }
  }

  async function loadHistory() {
    if (!activeChatUsername.value || !currentUser.value) return
    try {
      const res = await getMessageHistory(currentUser.value.username, activeChatUsername.value)
      if (res.data?.code === 0) {
        messages.value = (res.data?.data || []).map(m => ({
          ...m,
          sendTime: formatTime(m.sendTime)
        }))
      } else {
        messages.value = []
      }
    } catch {
      messages.value = []
    }
  }

  // ========== 发送消息 ==========
  const pendingMessages = ref([])       // 待发送消息队列（断网时排队）

  function sendMessage(content) {
    if (!content || !activeChatUsername.value) return false

    const payload = {
      type: 'MESSAGE',
      toUsername: activeChatUsername.value,
      content
    }

    // WebSocket 已连接 → 直接发送
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(payload))
      return true
    }

    // WebSocket 未连接 → 加入队列，连接恢复后自动发送
    pendingMessages.value.push(payload)
    ElMessage.info('消息已排队，连接恢复后自动发送')
    return true
  }

  /** 发送所有排队消息 */
  function flushPendingMessages() {
    if (!ws || ws.readyState !== WebSocket.OPEN || !pendingMessages.value.length) return
    const queue = [...pendingMessages.value]
    pendingMessages.value = []
    queue.forEach(p => ws.send(JSON.stringify(p)))
  }

  // ========== 抽屉控制 ==========
  function openDrawer(username) {
    drawerVisible.value = true
    if (username) {
      switchChat(username)
    }
  }

  function closeDrawer() {
    drawerVisible.value = false
    activeChatUsername.value = ''
    messages.value = []
  }

  // ========== 工具函数 ==========
  function formatTime(time) {
    if (!time) return ''
    return typeof time === 'string' ? time.replace('T', ' ').substring(0, 19) : time
  }

  function isUserOnline(username) {
    return onlineUsers.value.some(u => u.username === username)
  }

  // ========== 清理 ==========
  function reset() {
    disconnectWebSocket()
    pendingMessages.value = []
    currentUser.value = null
    chatTargets.value = []
    conversations.value = []
    onlineUsers.value = []
    messages.value = []
    activeChatUsername.value = ''
    unreadCount.value = 0
    drawerVisible.value = false
  }

  return {
    // 状态
    currentUser, chatTargets, conversations, onlineUsers,
    messages, activeChatUsername, wsConnected, drawerVisible, unreadCount, pendingMessages,
    // 计算属性
    isLoggedIn, activeChatTitle,
    // 方法
    init, refreshLists, connectWebSocket, disconnectWebSocket,
    switchChat, startChatByUsername, loadHistory, sendMessage,
    openDrawer, closeDrawer, isUserOnline, reset
  }
})
