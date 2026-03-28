import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: '',
    username: '',
    token: ''
  }),
  actions: {
    // 登录成功后保存用户信息
    setUserInfo(info) {
      this.userId = info.id
      this.username = info.username
      this.token = info.id // 简化版，实际用JWT
    },
    // 退出登录
    logout() {
      this.userId = ''
      this.username = ''
      this.token = ''
    }
  }
})
