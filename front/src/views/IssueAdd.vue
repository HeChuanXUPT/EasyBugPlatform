<template>
    <el-card title="问题上报">
        <el-form :model="form" label-width="120px">
            <el-form-item label="标题" required>
                <el-input v-model="form.title" />
            </el-form-item>

            <el-form-item label="所属项目" required>
                <el-select v-model="form.projectId" style="width: 240px">
                    <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
                </el-select>
            </el-form-item>

            <el-form-item label="模块">
                <el-input v-model="form.module" placeholder="例如：登录、支付、订单" />
            </el-form-item>

            <!-- 新增：直接指派处理人 -->
            <el-form-item label="指派处理人">
                <el-select v-model="form.assigneeId" placeholder="请选择处理人" style="width: 240px">
                    <el-option v-for="u in userList" :key="u.id" :label="u.username" :value="u.id" />
                </el-select>
            </el-form-item>

            <el-form-item label="类型">
                <el-select v-model="form.issueType">
                    <el-option label="BUG" value="BUG" />
                    <el-option label="需求反馈" value="需求反馈" />
                    <el-option label="优化建议" value="优化建议" />
                </el-select>
            </el-form-item>

            <el-form-item label="严重程度">
                <el-select v-model="form.severity">
                    <el-option label="P0致命" value="P0" />
                    <el-option label="P1严重" value="P1" />
                    <el-option label="P2一般" value="P2" />
                    <el-option label="P3轻微" value="P3" />
                </el-select>
            </el-form-item>

            <el-form-item label="问题描述">
                <el-input v-model="form.content" type="textarea" rows="4" />
            </el-form-item>

            <el-button type="primary" @click="submit">提交上报</el-button>
        </el-form>
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import projectApi from '../api/project'
import userApi from '../api/user'
import issueApi from '../api/issue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const projects = ref([])
const userList = ref([])
const loginUser = JSON.parse(sessionStorage.getItem('user') || '{}')

const form = ref({
    title: '',
    projectId: '',
    module: '',
    assigneeId: '',  // 指派处理人ID
    issueType: 'BUG',
    severity: 'P2',
    content: '',
    reporterId: loginUser.id
})

// 获取项目
const getProject = async () => {
    const res = await projectApi.list()
    projects.value = res.data
}

// 获取用户列表（指派下拉）
const getUsers = async () => {
    const res = await userApi.list()
    userList.value = res.data
}

const submit = async () => {
    await issueApi.report(form.value)
    ElMessage.success('上报成功')
    router.push('/issue')
}

onMounted(() => {
    getProject()
    getUsers()
})
</script>