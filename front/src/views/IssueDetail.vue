<template>
    <el-row :gutter="20">
        <el-col :span="12">
            <el-card title="问题详情">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="标题">{{ info.title }}</el-descriptions-item>
                    <el-descriptions-item label="上报人">{{ getReporterName(info.reporterId) }}</el-descriptions-item>
                    <el-descriptions-item label="当前处理人">{{ getAssigneeName(info.assigneeId) }}</el-descriptions-item>
                    <el-descriptions-item label="模块">{{ info.module || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="状态">{{ info.status }}</el-descriptions-item>

                    <el-descriptions-item label="问题描述">
                        <div style="white-space: pre-wrap; word-break: break-all; margin: 0;">
                            {{ info.content }}
                        </div>
                    </el-descriptions-item>

                    <el-descriptions-item label="问题原因" v-if="info.reason">
                        <div style="white-space: pre-wrap; word-break: break-all; margin: 0;">
                            {{ info.reason }}
                        </div>
                    </el-descriptions-item>

                    <el-descriptions-item label="解决方法" v-if="info.solution">
                        <div style="white-space: pre-wrap; word-break: break-all; margin: 0;">
                            {{ info.solution }}
                        </div>
                    </el-descriptions-item>

                    <el-descriptions-item label="关闭原因" v-if="info.closeReason">
                        <div style="white-space: pre-wrap; word-break: break-all; margin: 0;">
                            {{ info.closeReason }}
                        </div>
                    </el-descriptions-item>
                </el-descriptions>

                <el-button-group style="margin-top:10px" v-if="info.status !== '已验证' && info.status !== '已关闭'">
                    <el-button @click="openTransferDialog">转派</el-button>

                    <el-button v-if="info.status === '处理中'" @click="openSolutionDialog">待验证</el-button>
                    <el-button v-if="info.status === '处理中'" @click="openCloseDialog">已关闭</el-button>

                    <el-button v-if="info.status === '待验证'" @click="setStatus('处理中')">重新处理</el-button>
                    <el-button v-if="info.status === '待验证'" @click="setStatus('已验证')">已验证</el-button>
                    <el-button v-if="info.status === '待验证'" @click="openCloseDialog">已关闭</el-button>
                </el-button-group>

                <div v-else style="margin-top:10px;color: #999;">
                    当前工单已结束，不可操作
                </div>
            </el-card>
        </el-col>

        <el-col :span="12">
            <el-card title="流转记录">
                <el-timeline>
                    <el-timeline-item v-for="f in [...flows].reverse()" :key="f.id"
                        style="line-height: 1.5; padding: 4px 0;">
                        <span style="font-weight: bold;margin-right:8px;">{{ getUserName(f.operatorId) }}</span>
                        <span style="margin:0 8px;color:#666;">|</span>
                        <span style="margin-right:8px;">{{ f.oldStatus || '初始' }} → {{ f.newStatus }}</span>
                        <span style="margin:0 8px;color:#666;">|</span>
                        <span style="margin-right:8px;">{{ f.remark }}</span>
                        <span style="margin:0 8px;color:#666;">|</span>
                        <span style="color:#999;font-size:12px;">{{ f.createTime }}</span>
                    </el-timeline-item>
                </el-timeline>
            </el-card>

            <el-card title="评论" style="margin-top:10px">
                <el-input v-model="content" type="textarea" placeholder="输入评论" />
                <el-button type="primary" @click="send" style="margin-top:10px">发送</el-button>
                <div v-for="c in comments" :key="c.id" style="white-space: pre-wrap; margin-top:10px;">
                    {{ c.content }}<br><small>{{ c.createTime }}</small>
                </div>
            </el-card>
        </el-col>
    </el-row>

    <!-- 转派弹窗 -->
    <el-dialog v-model="transferVisible" title="转派问题" width="500px">
        <el-form label-width="80px">
            <el-form-item label="选择处理人">
                <el-select v-model="transferForm.assigneeId" placeholder="请选择处理人" style="width:100%">
                    <el-option v-for="u in userList" :key="u.id" :label="u.username" :value="u.id" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="transferVisible = false">取消</el-button>
            <el-button type="primary" @click="submitTransfer">确认转派</el-button>
        </template>
    </el-dialog>

    <!-- 待验证：解决方法 + 问题原因 -->
    <el-dialog v-model="solutionVisible" title="提交待验证" width="550px">
        <el-form label-width="100px">
            <el-form-item label="问题原因" required>
                <el-input v-model="solutionForm.reason" type="textarea" rows="3" placeholder="请填写问题产生的原因" />
            </el-form-item>
            <el-form-item label="解决方法" required>
                <el-input v-model="solutionForm.solution" type="textarea" rows="3" placeholder="请填写具体的解决方法" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="solutionVisible = false">取消</el-button>
            <el-button type="primary" @click="submitSolution">确认提交</el-button>
        </template>
    </el-dialog>

    <!-- 关闭：关闭原因 -->
    <el-dialog v-model="closeVisible" title="填写关闭原因" width="500px">
        <el-input v-model="closeForm.reason" type="textarea" rows="4" placeholder="请输入关闭原因" />
        <template #footer>
            <el-button @click="closeVisible = false">取消</el-button>
            <el-button type="primary" @click="submitClose">确认关闭</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import issueApi from '../api/issue'
import flowApi from '../api/flow'
import commentApi from '../api/comment'
import userApi from '../api/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const id = route.params.id
const info = ref({})
const flows = ref([])
const comments = ref([])
const content = ref('')
const userList = ref([])
const user = JSON.parse(sessionStorage.getItem('user'))

// 转派
const transferVisible = ref(false)
const transferForm = ref({ assigneeId: null })

// 待验证（新增原因）
const solutionVisible = ref(false)
const solutionForm = ref({
    reason: '',
    solution: ''
})

// 关闭
const closeVisible = ref(false)
const closeForm = ref({ reason: '' })

// 获取详情
const getDetail = async () => {
    const res = await issueApi.detail(id)
    info.value = res.data
}

// 获取用户
const getUsers = async () => {
    const res = await userApi.list()
    userList.value = res.data
}

// 名称映射
const getAssigneeName = (id) => {
    if (!id) return '未指派'
    const u = userList.value.find(i => i.id === id)
    return u ? u.username : '未知'
}
const getReporterName = (id) => {
    if (!id) return '未知'
    const u = userList.value.find(i => i.id === id)
    return u ? u.username : '未知'
}
const getUserName = (id) => {
    if (!id) return '系统'
    const u = userList.value.find(i => i.id === id)
    return u ? u.username : '未知'
}

// 流转记录
const getFlow = async () => {
    const res = await flowApi.list(id)
    flows.value = res.data
}

// 评论
const getComment = async () => {
    const res = await commentApi.list(id)
    comments.value = res.data
}

// 转派
const openTransferDialog = () => {
    transferForm.value.assigneeId = info.value.assigneeId
    transferVisible.value = true
}
const submitTransfer = async () => {
    if (!transferForm.value.assigneeId) {
        ElMessage.warning('请选择处理人')
        return
    }
    await issueApi.transfer({
        id: info.value.id,
        assigneeId: transferForm.value.assigneeId,
        operatorId: user.id,
        remark: '转派'
    })
    transferVisible.value = false
    ElMessage.success('转派成功')
    getDetail()
    getFlow()
}

// 状态变更
const setStatus = async (status) => {
    await issueApi.updateStatus({
        id,
        status,
        operatorId: user.id
    })
    ElMessage.success('状态已更新')
    getDetail()
    getFlow()
}

// 待验证（带原因 + 解决方法）
const openSolutionDialog = () => {
    solutionForm.value.reason = ''
    solutionForm.value.solution = ''
    solutionVisible.value = true
}
const submitSolution = async () => {
    if (!solutionForm.value.reason) {
        ElMessage.warning('请填写问题原因')
        return
    }
    if (!solutionForm.value.solution) {
        ElMessage.warning('请填写解决方法')
        return
    }

    await issueApi.updateStatus({
        id,
        status: '待验证',
        reason: solutionForm.value.reason,
        solution: solutionForm.value.solution,
        operatorId: user.id
    })

    solutionVisible.value = false
    ElMessage.success('已提交，状态改为待验证')
    getDetail()
    getFlow()
}

// 关闭
const openCloseDialog = () => {
    closeForm.value.reason = ''
    closeVisible.value = true
}
const submitClose = async () => {
    if (!closeForm.value.reason) {
        ElMessage.warning('请输入关闭原因')
        return
    }
    await issueApi.updateStatus({
        id,
        status: '已关闭',
        closeReason: closeForm.value.reason,
        operatorId: user.id
    })
    closeVisible.value = false
    ElMessage.success('已关闭')
    getDetail()
    getFlow()
}

// 发送评论
const send = async () => {
    await commentApi.add({
        issueId: id, userId: user.id, content: content.value
    })
    content.value = ''
    getComment()
}

onMounted(() => {
    getDetail()
    getFlow()
    getComment()
    getUsers()
})
</script>