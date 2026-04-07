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

            <el-form-item label="问题描述" required>
                <div ref="editorRef" contenteditable class="problem-editor" @paste="handlePaste" style="
            width: 100%;
            min-height: 180px;
            padding: 10px;
            border: 1px solid #DCDFE6;
            border-radius: 4px;
            outline: none;
            white-space: pre-wrap;
            word-wrap: break-word;
          "></div>
                <div style="color:#999;font-size:12px;margin-top:4px">
                    提示：支持文字输入，可直接 Ctrl+V 粘贴截图
                </div>
            </el-form-item>

            <el-form-item label=" ">
                <el-button type="primary" @click="submit">提交上报</el-button>
                <el-button @click="goBack">返回</el-button>
            </el-form-item>
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
const editorRef = ref(null)

const form = ref({
    title: '',
    projectId: '',
    module: '',
    assigneeId: '',
    issueType: 'BUG',
    severity: 'P2',
    content: '',
    reporterId: loginUser.id
})

let isComposing = false

onMounted(() => {
    getProject()
    getUsers()

    const el = editorRef.value
    if (!el) return

    el.addEventListener('compositionstart', () => {
        isComposing = true
    })
    el.addEventListener('compositionend', () => {
        isComposing = false
        form.value.content = el.innerHTML
    })
    el.addEventListener('input', () => {
        if (!isComposing) {
            form.value.content = el.innerHTML
        }
    })
})

const getProject = async () => {
    const res = await projectApi.list()
    projects.value = res.data
}

const getUsers = async () => {
    const res = await userApi.list()
    userList.value = res.data
}

// 修复：同时支持粘贴 文字 + 图片
// 纯文本粘贴 + 图片粘贴（完美版，格式永远不乱）
const handlePaste = async (e) => {
    const items = e.clipboardData?.items;
    if (!items) return;

    // 判断是否有图片
    let hasImage = false;
    for (let item of items) {
        if (item.type.indexOf("image") >= 0) {
            hasImage = true;
            break;
        }
    }

    if (hasImage) {
        // ====================
        // 粘贴图片：正常上传
        // ====================
        e.preventDefault();
        for (let item of items) {
            if (item.type.indexOf("image") >= 0) {
                const file = item.getAsFile();
                const formData = new FormData();
                formData.append("file", file);

                try {
                    const res = await issueApi.uploadImage(formData);
                    const imgUrl = res.data;

                    const sel = window.getSelection();
                    const range = sel.getRangeAt(0);
                    const img = document.createElement("img");
                    img.src = imgUrl;
                    img.style.maxWidth = "400px";
                    img.style.height = "auto";
                    img.style.borderRadius = "8px";
                    img.style.margin = "6px 0";
                    img.style.cursor = "zoom-in";
                    img.onclick = () => window.openPreview(imgUrl);

                    range.deleteContents();
                    range.insertNode(img);
                    sel.collapseToEnd();
                    form.value.content = editorRef.value.innerHTML;
                    ElMessage.success("图片上传成功");
                } catch (err) {
                    ElMessage.error("图片上传失败");
                }
            }
        }
    } else {
        // ====================
        // 粘贴文字 → 纯文本（无格式、不乱）
        // ====================
        e.preventDefault();
        const text = e.clipboardData.getData("text/plain");
        if (!text) return;

        const sel = window.getSelection();
        const range = sel.getRangeAt(0);
        range.deleteContents();

        // 插入纯文本，不带任何样式
        const textNode = document.createTextNode(text);
        range.insertNode(textNode);
        sel.collapseToEnd();

        form.value.content = editorRef.value.innerHTML;
    }
};

const submit = async () => {
    await issueApi.report(form.value)
    ElMessage.success("上报成功")
    router.push("/issue")
}

const goBack = () => {
    router.push("/issue")
}
</script>

<!-- 这里加上你需要的样式 ✅ 解决小图标问题 ✅ -->
<style scoped>
.problem-editor img:not([src]) {
    display: none !important;
}

.problem-editor img {
    display: inline-block !important;
    max-width: 400px !important;
    height: auto !important;
}
</style>