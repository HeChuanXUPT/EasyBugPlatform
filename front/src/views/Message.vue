<template>
    <el-card title="我的消息">
        <!-- 一键已读按钮 -->
        <div style="text-align: right; margin-bottom: 10px;">
            <el-button type="primary" size="small" @click="readAll">
                一键已读
            </el-button>
        </div>

        <div v-for="m in list" :key="m.id" style="
        padding:10px;
        border-bottom:1px solid #eee;
        cursor:pointer;
        background: #fcfcfc;
      " @click="goDetail(m)">
            <span :style="{ fontWeight: m.isRead == 0 ? 'bold' : 'normal', color: m.isRead == 0 ? '#333' : '#999' }">
                {{ m.title }}
            </span>
            <span style="color:#999; font-size:12px; margin-left:10px;">
                {{ m.createTime }}
            </span>
        </div>

        <div v-if="list.length === 0" style="text-align:center; padding:20px;">
            暂无消息
        </div>
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMsgList, readMsg, readAllMsg } from '../api/msg.js'

const router = useRouter()
const list = ref([])
const loginUser = JSON.parse(sessionStorage.getItem('user'))

// 加载消息列表
const loadList = async () => {
    const res = await getMsgList(loginUser.id)
    list.value = res.data
}

// 点击消息 → 标记已读 + 跳转详情
const goDetail = async (m) => {
    await readMsg(m.id)
    loadList() // 重新加载列表，消息就会变灰/消失未读状态
    router.push(`/issue/detail/${m.issueId}`)
}

// 一键已读
const readAll = async () => {
    await readAllMsg(loginUser.id)
    ElMessage.success('全部标记已读成功')
    loadList()
}

onMounted(() => loadList())
</script>