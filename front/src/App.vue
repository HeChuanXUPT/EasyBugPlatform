<template>
  <el-container style="height: 100vh">
    <!-- 顶部栏 -->
    <el-header
      style="background: #409eff; color: white; padding: 0 20px; display: flex; justify-content: space-between; align-items: center">
      <h3>BUG协作平台</h3>

      <div v-if="loginUser.username" style="display: flex; align-items: center; gap: 16px">

        <!-- 消息铃铛 + 未读红点 -->
        <div style="position: relative; cursor: pointer" @click="toMessage">
          <i class="el-icon-bell" style="font-size: 18px"></i>
          <div v-if="unread > 0" style="
              position: absolute; top: -6px; right: -8px;
              min-width: 16px; height: 16px;
              background: red; color: white;
              border-radius: 8px; font-size: 10px;
              text-align: center; line-height: 16px;
            ">
            {{ unread }}
          </div>
        </div>

        <span>欢迎，{{ loginUser.username }}</span>
        <el-button type="info" size="small" @click="logout">登出</el-button>
      </div>
    </el-header>

    <el-container>
      <!-- 左侧菜单 -->
      <el-aside width="200px" style="background: #f5f5f5">
        <el-menu router>
          <!-- 仅管理员可见 -->
          <el-menu-item index="/project" v-if="isAdmin">
            项目管理
          </el-menu-item>
          <el-menu-item index="/user" v-if="isAdmin">
            用户管理
          </el-menu-item>

          <!-- 所有人可见 -->
          <el-menu-item index="/issue">
            问题管理
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUnreadCount } from './api/msg.js'

const router = useRouter()

const loginUser = ref({})
const isAdmin = ref(false)
const unread = ref(0)

// 获取当前登录用户
const getLoginUser = () => {
  const userStr = sessionStorage.getItem('user')
  if (userStr) {
    loginUser.value = JSON.parse(userStr)
    isAdmin.value = loginUser.value.username === 'admin'
  } else {
    loginUser.value = {}
    isAdmin.value = false
  }
}

// 加载未读消息数量
const loadUnread = async () => {
  if (!loginUser.value.id) return
  try {
    const res = await getUnreadCount(loginUser.value.id)
    unread.value = res.data
  } catch (e) {
    // 接口未就绪时不报错
  }
}

// 跳转到消息页面
const toMessage = () => {
  router.push('/message')
}

// 登出
const logout = () => {
  sessionStorage.removeItem('user')
  ElMessage.success('已安全登出')
  loginUser.value = {}
  isAdmin.value = false
  unread.value = 0
  router.push('/login')
}

// 初始化
onMounted(() => {
  getLoginUser()
  loadUnread()

  // 每5秒刷新一次消息
  setInterval(() => {
    getLoginUser()
    loadUnread()
  }, 5000)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.el-container {
  width: 100%;
}

.el-header {
  height: 60px !important;
  line-height: 60px !important;
}

.el-aside {
  height: calc(100vh - 60px);
}

.el-main {
  height: calc(100vh - 60px);
  overflow-y: auto;
}
</style>