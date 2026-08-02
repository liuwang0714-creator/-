import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

/** 用户登录 */
export function login(username, password) {
  return api.post('/user/login', { username, password })
}

/** 获取当前登录用户信息 */
export function getUserInfo() {
  return api.get('/user/info')
}

/** 根据用户ID获取用户信息（用于获取商家用户名） */
export function getUserInfoById(userId) {
  return api.get('/user/infoid', { params: { userid: userId } })
}

/** 获取可聊天目标用户列表 */
export function getChatTargets(username) {
  return api.get('/chat/targets', { params: { username } })
}

/** 获取最近会话列表 */
export function getConversations(username) {
  return api.get('/chat/conversations', { params: { username } })
}

/** 检查是否可以与对方发起会话 */
export function canChat(fromUsername, toUsername) {
  return api.get('/chat/can-chat', { params: { fromUsername, toUsername } })
}

/** 获取聊天历史消息 */
export function getMessageHistory(fromUsername, toUsername, limit = 50) {
  return api.get('/message/history', { params: { fromUsername, toUsername, limit } })
}

/** 创建 WebSocket 连接 */
export function createWebSocket() {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = location.hostname
  // 开发环境 Vite 端口 5173，后端在 8081
  const port = location.port === '5173' ? '8081' : location.port
  return new WebSocket(`${protocol}//${host}:${port}/ws/chat`)
}

/** 角色标签映射 */
const ROLE_LABEL = { admin: '管理员', merchant: '商家', user: '用户' }
export function roleLabel(roleCode) {
  return ROLE_LABEL[roleCode] || roleCode
}

/** 角色标签颜色映射 */
const ROLE_COLOR = { admin: '#e74c3c', merchant: '#f39c12', user: '#409eff' }
export function roleColor(roleCode) {
  return ROLE_COLOR[roleCode] || '#909399'
}

/** 获取头像完整 URL */
export function avatarUrl(avatar) {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return '/api/images/' + avatar
}
