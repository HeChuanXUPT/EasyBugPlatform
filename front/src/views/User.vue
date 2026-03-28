<template>
    <el-card>
        <el-button type="success" @click="add">新增用户</el-button>
        <el-table :data="list" border style="margin-top:10px">
            <el-table-column prop="username" label="账号" />
            <el-table-column prop="role" label="角色" />
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="del(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import userApi from '../api/user'
import { ElMessage } from 'element-plus'
const list = ref([])
const getList = async () => {
    const res = await userApi.list()
    list.value = res.data
}
const add = async () => {
    const username = prompt('输入账号')
    const password = prompt('输入密码')
    await userApi.add({ username, password })
    ElMessage.success('新增成功')
    getList()
}
const del = async (id) => {
    await userApi.del(id)
    ElMessage.success('删除成功')
    getList()
}
onMounted(getList)
</script>