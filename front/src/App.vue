<template>
  <el-container style="height:100vh">
    <el-header
      style="background:#409eff;color:white;padding:0 20px;display:flex;justify-content:space-between;align-items:center">
      <h3>BUG协作平台</h3>
      <div v-if="loginUser.username" style="display:flex;align-items:center;gap:10px">
        <span>欢迎，{{ loginUser.username }}</span>
        <el-button type="info" size="small" @click="logout">登出</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" style="background:#f5f5f5">
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
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginUser = ref({})
const isAdmin = ref(false)

const getLoginUser = () => {
  const user = sessionStorage.getItem('user')
  if (user) {
    loginUser.value = JSON.parse(user)
    // 管理员判断：用户名为 admin 就是管理员
    isAdmin.value = loginUser.value.username === 'admin'
  } else {
    loginUser.value = {}
    isAdmin.value = false
  }
}

const logout = () => {
  sessionStorage.removeItem('user')
  ElMessage.success('已安全登出')
  getLoginUser()
  router.push('/login')
}

onMounted(() => {
  getLoginUser()
})

watch(
  () => router.currentRoute.value,
  () => {
    getLoginUser()
  },
  { immediate: true }
)
</script>