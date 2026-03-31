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
            <div style="white-space: pre-wrap; word-break: break-all; line-height: 1.6"
              v-html="renderContentWithImages(info.content)"></div>
          </el-descriptions-item>

          <el-descriptions-item label="问题原因" v-if="info.reason">
            <div style="white-space: pre-wrap">{{ info.reason }}</div>
          </el-descriptions-item>

          <el-descriptions-item label="解决方法" v-if="info.solution">
            <div style="white-space: pre-wrap">{{ info.solution }}</div>
          </el-descriptions-item>

          <el-descriptions-item label="关闭原因" v-if="info.closeReason">
            <div style="white-space: pre-wrap">{{ info.closeReason }}</div>
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

        <div v-else style="margin-top:10px; color:#999">
          当前工单已结束，不可操作
        </div>
      </el-card>
    </el-col>

    <el-col :span="12">
      <el-card title="流转记录">
        <el-timeline>
          <el-timeline-item v-for="f in [...flows].reverse()" :key="f.id" style="line-height:1.5; padding:4px 0">
            <span style="font-weight:bold; margin-right:8px">{{ getUserName(f.operatorId) }}</span>
            <span style="margin:0 8px; color:#666">|</span>
            <span style="margin-right:8px">{{ f.oldStatus || '初始' }} → {{ f.newStatus }}</span>
            <span style="margin:0 8px; color:#666">|</span>
            <span style="margin-right:8px">{{ f.remark }}</span>
            <span style="margin:0 8px; color:#666">|</span>
            <span style="color:#999; font-size:12px">{{ f.createTime }}</span>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <el-card title="评论" style="margin-top:10px">
        <el-input v-model="content" type="textarea" placeholder="输入评论" rows="3" />
        <el-button type="primary" @click="send" style="margin-top:10px">发送</el-button>

        <div v-for="c in comments" :key="c.id"
          style="margin-top:12px; padding-bottom:8px; border-bottom:1px solid #f5f5f5">
          <div style="font-weight:bold; color:#333">{{ getUserName(c.userId) }}</div>
          <div style="white-space:pre-wrap; margin:4px 0; color:#555">{{ c.content }}</div>
          <div style="font-size:12px; color:#999">{{ c.createTime }}</div>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <!-- 图片预览弹窗 -->
  <div v-if="previewImgUrl" class="img-modal-overlay" @click.self="closePreview">
    <div class="img-modal-close" @click="closePreview">×</div>
    <img :src="previewImgUrl" class="img-modal-content" />
  </div>

  <!-- 待验证弹窗：原因 + 解决方法 -->
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

  <!-- 关闭问题弹窗 -->
  <el-dialog v-model="closeVisible" title="关闭问题" width="500px">
    <el-input v-model="closeForm.reason" type="textarea" rows="4" placeholder="请输入关闭原因" />
    <template #footer>
      <el-button @click="closeVisible = false">取消</el-button>
      <el-button type="primary" @click="submitClose">确认关闭</el-button>
    </template>
  </el-dialog>

  <!-- 转派弹窗 -->
  <el-dialog v-model="transferVisible" title="转派问题" width="500px">
    <el-form label-width="80px">
      <el-form-item label="处理人">
        <el-select v-model="transferForm.assigneeId" style="width:100%">
          <el-option v-for="u in userList" :key="u.id" :label="u.username" :value="u.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="transferVisible = false">取消</el-button>
      <el-button type="primary" @click="submitTransfer">确认转派</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import issueApi from '../api/issue'
import flowApi from '../api/flow'
import commentApi from '../api/comment'
import userApi from '../api/user'

const route = useRoute()
const id = route.params.id
const info = ref({})
const flows = ref([])
const comments = ref('')
const content = ref('')
const userList = ref([])
const user = JSON.parse(sessionStorage.getItem('user'))

const previewImgUrl = ref('')

const getDetail = () => issueApi.detail(id).then(res => (info.value = res.data))
const getFlow = () => flowApi.list(id).then(res => (flows.value = res.data))
const getComment = () => commentApi.list(id).then(res => (comments.value = res.data))
const getUsers = async () => {
  const res = await userApi.list()
  userList.value = res.data
}

const getUserName = (uid) => {
  const u = userList.value.find(x => x.id === uid)
  return u ? u.username : '未知用户'
}
const getAssigneeName = (id) => {
  const u = userList.value.find(x => x.id === id)
  return u ? u.username : '未指派'
}
const getReporterName = (id) => {
  const u = userList.value.find(x => x.id === id)
  return u ? u.username : '未知'
}

// ✅ 修复后的安全方法
const renderContentWithImages = (content) => {
  if (!content) return ''
  return content.replace(/<img[^>]*src="([^"]+)"[^>]*>/g, (full, src) => {
    return `<img src="${src}" style="max-width:400px;height:auto;border-radius:8px;margin:6px 0;cursor:zoom-in" onclick="window.openPreview('${src}')">`
  })
}

onMounted(() => {
  window.openPreview = (url) => {
    previewImgUrl.value = url
  }
  getDetail()
  getFlow()
  getComment()
  getUsers()
})

const closePreview = () => {
  previewImgUrl.value = ''
}

// 转派
const transferVisible = ref(false)
const transferForm = ref({ assigneeId: null })
const openTransferDialog = () => {
  transferForm.value.assigneeId = info.value.assigneeId
  transferVisible.value = true
}
const submitTransfer = async () => {
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

// 待验证
const solutionVisible = ref(false)
const solutionForm = ref({ reason: '', solution: '' })
const openSolutionDialog = () => {
  solutionForm.value = { reason: '', solution: '' }
  solutionVisible.value = true
}
const submitSolution = async () => {
  await issueApi.updateStatus({
    id,
    status: '待验证',
    reason: solutionForm.value.reason,
    solution: solutionForm.value.solution,
    operatorId: user.id
  })
  solutionVisible.value = false
  ElMessage.success('已改为待验证')
  getDetail()
  getFlow()
}

// 关闭
const closeVisible = ref(false)
const closeForm = ref({ reason: '' })
const openCloseDialog = () => {
  closeForm.value.reason = ''
  closeVisible.value = true
}
const submitClose = async () => {
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

// 状态变更
const setStatus = async (status) => {
  await issueApi.updateStatus({ id, status, operatorId: user.id })
  ElMessage.success('状态已更新')
  getDetail()
  getFlow()
}

// 评论
const send = async () => {
  await commentApi.add({ issueId: id, userId: user.id, content: content.value })
  content.value = ''
  ElMessage.success('评论成功')
  getComment()
}
</script>

<style>
.img-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999999;
}

.img-modal-close {
  position: fixed;
  top: 20px;
  right: 30px;
  font-size: 40px;
  color: white;
  cursor: pointer;
  user-select: none;
}

.img-modal-content {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
}
</style>