import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    id: null,
    username: '',
    name: '',
    role: '', // 'VOL' 或 'EMP'
    token: ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isVolunteer: (state) => state.role === 'VOL',
    isEmployee: (state) => state.role === 'EMP'
  },
  actions: {
    setUser(userData) {
      this.id = userData.id
      this.username = userData.username
      this.name = userData.name
      this.role = userData.role
      this.token = userData.token
    },
    clearUser() {
      this.id = null
      this.username = ''
      this.name = ''
      this.role = ''
      this.token = ''
    }
  },
  persist: true // 启用持久化存储
}) 