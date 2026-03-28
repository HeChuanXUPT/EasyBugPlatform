<template>
    <div class="login">
        <el-card style="width:400px;margin:100px auto">
            <h2 style="text-align:center">登录</h2>
            <el-form :model="form" label-width="80px">
                <el-form-item label="账号">
                    <el-input v-model="form.username" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="form.password" type="password" />
                </el-form-item>
                <el-button type="primary" block @click="login">登录</el-button>
            </el-form>
        </el-card>
    </div>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import userApi from '../api/user'
import { ElMessage } from 'element-plus'
const router = useRouter()
const form = ref({ username: '', password: '' })

const login = async () => {
  try {
    const res = await userApi.login(form.value)
    sessionStorage.setItem('user', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    // 登录成功 → 直接跳问题管理页面
    router.push('/issue')
  } catch (err) {
    ElMessage.error('账号或密码错误')
  }
}
</script>