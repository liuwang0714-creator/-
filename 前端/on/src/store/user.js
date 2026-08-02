import { defineStore } from 'pinia'
import { ref,computed  } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('satoken') || '')
  const role = ref(localStorage.getItem('role') || '')

  const isLogin = computed(() => !!token.value)

  const login = (satoken, userRole) => {
    token.value = satoken
    role.value = userRole
    localStorage.setItem('satoken', satoken)
    localStorage.setItem('role', userRole)
  }
  const logout = () => {
    token.value = ''
    role.value = ''
    localStorage.removeItem('satoken')
    localStorage.removeItem('role')
  }

  return { token, role, isLogin,login,logout }
})