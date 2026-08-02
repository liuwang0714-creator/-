// 即时通讯 IM 工具模块
// 与 my_im 后端通信，替换 uni-im

const BASE_URL = 'http://127.0.0.1:8081'
const WS_URL = 'ws://127.0.0.1:8081/ws/chat'

/**
 * 基础请求封装
 */
function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        'Content-Type': method === 'POST' ? 'application/json' : 'application/x-www-form-urlencoded',
        'satoken': uni.getStorageSync('tokenValue') || ''
      },
      success: (res) => resolve(res.data),
      fail: (err) => {
        console.error('IM request error:', err)
        reject(err)
      }
    })
  })
}

// ========== 用户相关 ==========

/** 用户登录 */
export function login(username, password) {
  return request('/api/user/login', 'POST', { username, password })
}

/** 获取当前用户信息 */
export function getUserInfo() {
  return request('/api/user/info', 'GET')
}

/** 根据用户ID获取用户信息 */
export function getUserInfoById(userId) {
  return request('/api/user/infoid?userid=' + userId, 'GET')
}

// ========== 聊天相关 API ==========

/** 获取可聊天目标用户 */
export function getChatTargets(username) {
  return request('/chat/targets?username=' + encodeURIComponent(username))
}

/** 获取最近会话列表 */
export function getConversations(username) {
  return request('/chat/conversations?username=' + encodeURIComponent(username))
}

/** 检查是否可以发起会话 */
export function canChat(fromUsername, toUsername) {
  return request('/chat/can-chat?fromUsername=' + encodeURIComponent(fromUsername) +
    '&toUsername=' + encodeURIComponent(toUsername))
}

/** 获取聊天历史消息 */
export function getMessageHistory(fromUsername, toUsername, limit = 50) {
  return request('/message/history?fromUsername=' + encodeURIComponent(fromUsername) +
    '&toUsername=' + encodeURIComponent(toUsername) + '&limit=' + limit)
}

// ========== WebSocket 管理 ==========

let socketTask = null
let reconnectTimer = null
let currentUsername = ''
let onMessageCallback = null
let onStatusCallback = null
let pendingQueue = []        // 离线时排队待发的消息

/**
 * 连接 WebSocket
 * @param {string} username 当前用户名
 * @param {function} onMessage 消息回调 (msg)
 * @param {function} onStatus 状态回调 (connected: boolean)
 */
export function connectWebSocket(username, onMessage, onStatus) {
  currentUsername = username
  onMessageCallback = onMessage
  onStatusCallback = onStatus

  // 清理旧连接
  if (socketTask) {
    try { socketTask.close() } catch (e) { /* ignore */ }
  }
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }

  socketTask = uni.connectSocket({
    url: WS_URL,
    complete: () => {}
  })

  socketTask.onOpen(() => {
    if (onStatusCallback) onStatusCallback(true)
    // 发送 CONNECT 消息认证身份
    socketTask.send({
      data: JSON.stringify({ type: 'CONNECT', fromUsername: currentUsername })
    })
    // 发送排队消息
    if (pendingQueue.length) {
      const queue = [...pendingQueue]
      pendingQueue = []
      queue.forEach(msg => {
        socketTask.send({ data: JSON.stringify(msg) })
      })
    }
  })

  socketTask.onMessage((res) => {
    try {
      const msg = JSON.parse(res.data)
      if (onMessageCallback) onMessageCallback(msg)
    } catch (e) {
      console.error('WS parse error:', e)
    }
  })

  socketTask.onClose(() => {
    if (onStatusCallback) onStatusCallback(false)
    // 自动重连
    if (currentUsername) {
      reconnectTimer = setTimeout(() => {
        connectWebSocket(currentUsername, onMessageCallback, onStatusCallback)
      }, 3000)
    }
  })

  socketTask.onError(() => {
    if (onStatusCallback) onStatusCallback(false)
  })
}

/**
 * 发送 WebSocket 消息（支持离线排队）
 */
export function sendWsMessage(data) {
  if (socketTask) {
    socketTask.send({ data: JSON.stringify(data) })
    return true
  }
  // 离线排队，连接恢复后自动发送
  pendingQueue.push(data)
  uni.showToast({ title: '消息已排队，连接恢复后发送', icon: 'none' })
  return true
}

/**
 * 断开 WebSocket 连接
 */
export function disconnectWebSocket() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  if (socketTask) {
    try {
      socketTask.close()
    } catch (e) { /* ignore */ }
    socketTask = null
  }
  pendingQueue = []
  currentUsername = ''
  onMessageCallback = null
  onStatusCallback = null
}

// ========== 工具函数 ==========

const ROLE_LABEL = { admin: '管理员', merchant: '商家', user: '用户' }
export function roleLabel(roleCode) {
  return ROLE_LABEL[roleCode] || roleCode
}

export function formatTime(time) {
  if (!time) return ''
  return typeof time === 'string' ? time.replace('T', ' ').substring(0, 19) : time
}

/** 获取头像完整 URL */
export function avatarUrl(avatar) {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return BASE_URL + '/images/' + avatar
}

export { BASE_URL, WS_URL }
