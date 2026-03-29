<template>
    <el-card>
        <el-form :inline="true" :model="query" class="demo-form-inline">
            <el-form-item label="项目">
                <el-select v-model="query.projectId" placeholder="请选择" style="width:180px">
                    <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
                </el-select>
            </el-form-item>

            <!-- 1. 模块搜索 -->
            <el-form-item label="模块">
                <el-input v-model="query.module" placeholder="输入模块" style="width:160px" />
            </el-form-item>

            <el-form-item label="状态">
                <el-select v-model="query.status" placeholder="请选择" style="width:180px">
                    <el-option label="待指派" value="待指派" />
                    <el-option label="处理中" value="处理中" />
                    <el-option label="待验证" value="待验证" />
                    <el-option label="已验证" value="已验证" />
                    <el-option label="已关闭" value="已关闭" />
                </el-select>
            </el-form-item>

            <!-- 2. 处理人下拉（自动加载用户 + 默认当前用户） -->
            <el-form-item label="处理人">
                <el-select v-model="query.assigneeId" placeholder="选择处理人" style="width:160px">
                    <el-option v-for="u in userList" :key="u.id" :label="u.username" :value="u.id" />
                </el-select>
            </el-form-item>

            <el-form-item label="标题">
                <el-input v-model="query.title" placeholder="搜索标题" style="width:220px" />
            </el-form-item>

            <el-button type="primary" @click="getList">查询</el-button>
            <el-button @click="resetQuery">清空</el-button>
            <el-button type="success" @click="toAdd">新增问题</el-button>
        </el-form>

        <el-table :data="list" border>
            <el-table-column prop="title" label="标题" min-width="200" />

            <el-table-column label="项目" width="120">
                <template #default="scope">
                    {{ getProjectName(scope.row.projectId) }}
                </template>
            </el-table-column>

            <el-table-column prop="module" label="模块" width="120" />

            <el-table-column prop="issueType" label="类型" width="100" />
            <el-table-column prop="severity" label="级别" width="90" />
            <el-table-column prop="status" label="状态" width="100" />

            <el-table-column label="当前处理人" width="120">
                <template #default="scope">
                    {{ getAssigneeName(scope.row.assigneeId) }}
                </template>
            </el-table-column>

            <el-table-column prop="createTime" label="创建时间" width="180" />

            <el-table-column label="操作" width="200">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="toDetail(scope.row.id)">详情</el-button>
                    <el-button size="small" type="warning" @click="openAssign(scope.row)">指派</el-button>

                    <el-button size="small" type="danger" v-if="isAdmin" @click="deleteIssue(scope.row.id)">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination @current-change="handlePageChange" :current-page="current" :page-size="size"
            layout="total,prev,pager,next" :total="total" style="margin-top:10px;text-align:right" />
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import issueApi from '../api/issue'
import projectApi from '../api/project'
import userApi from '../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const projects = ref([])
const userList = ref([])
const list = ref([])

const current = ref(1)
const size = ref(10)
const total = ref(0)

// 当前登录用户
const loginUser = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))
const isAdmin = ref(loginUser.value.username === 'admin')

// 查询条件：默认 处理人 = 当前登录用户
const query = ref({
    projectId: '',
    module: '',
    status: '',
    assigneeId: loginUser.value.id, // 默认选中自己
    title: ''
})

// 获取项目列表
const getProject = async () => {
    const res = await projectApi.list()
    projects.value = res.data
}

// 获取用户列表（处理人下拉用）
const getUsers = async () => {
    const res = await userApi.list()
    userList.value = res.data
}

// 获取问题列表
const getList = async () => {
    const res = await issueApi.page({
        ...query.value,
        current: current.value,
        size: size.value
    })
    list.value = res.data.records
    total.value = res.data.total
}

// 项目名称映射
const getProjectName = (projectId) => {
    if (!projectId) return '-'
    const p = projects.value.find(item => item.id === projectId)
    return p ? p.projectName : '-'
}

// 处理人名称映射
const getAssigneeName = (id) => {
    if (!id) return '未指派'
    const u = userList.value.find(i => i.id === id)
    return u ? u.username : '未知'
}

// 分页
const handlePageChange = (page) => {
    current.value = page
    getList()
}

// 清空查询
const resetQuery = () => {
    query.value = {
        projectId: '',
        module: '',
        status: '',
        assigneeId: '', // 清空后不默认，可查全部
        title: ''
    }
    current.value = 1
    getList()
}

// 跳转
const toAdd = () => router.push('/issue/add')
const toDetail = (id) => router.push(`/issue/detail/${id}`)

// 指派（兼容弹窗选择用户）
const openAssign = (row) => {
    ElMessageBox.prompt('请输入处理人ID', '指派问题', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
    }).then(async ({ value }) => {
        await issueApi.assign({ id: row.id, assigneeId: value })
        ElMessage.success('指派成功')
        getList()
    })
}

// 删除（仅管理员）
const deleteIssue = async (id) => {
    await ElMessageBox.confirm('确定要删除该问题吗？删除后不可恢复！', '提示', { type: 'warning' })
    await issueApi.delete(id)
    ElMessage.success('删除成功')
    getList()
}

// 初始化加载
onMounted(() => {
    getProject()
    getUsers()
    getList()
})
</script>