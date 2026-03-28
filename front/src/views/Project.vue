<template>
    <el-card>
        <el-button type="success" @click="add">新增项目</el-button>
        <el-table :data="list" border style="margin-top:10px">
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="projectName" label="项目名称" />
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
import projectApi from '../api/project'
import { ElMessage } from 'element-plus'
const list = ref([])
const getList = async () => {
    const res = await projectApi.list()
    list.value = res.data
}
const add = async () => {
    const name = prompt('输入项目名称')
    if (!name) return
    await projectApi.add({ projectName: name })
    ElMessage.success('新增成功')
    getList()
}
const del = async (id) => {
    await projectApi.del(id)
    ElMessage.success('删除成功')
    getList()
}
onMounted(getList)
</script>