import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)
  const token = ref(localStorage.getItem('token') || '')

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function setToken(tokenData) {
    token.value = tokenData
    localStorage.setItem('token', tokenData)
  }

  async function login(loginForm) {
    try {
      const data = await loginApi(loginForm)
      // data should be the User object returned from backend
      setUser(data)
      // If backend returns a token, set it here. For now, we simulate or just use user data.
      return true
    } catch (error) {
      console.error(error)
      return false
    }
  }

  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  return {
    user,
    token,
    login,
    logout,
    setUser
  }
})
