<template>
  <div class="material-select">
    <div class="material-preview" v-if="displayUrl">
      <el-image :src="displayUrl" fit="cover" style="width: 120px; height: 120px" :preview-src-list="[displayUrl]" preview-teleported />
      <div class="material-actions">
        <el-button type="primary" link @click="openDialog">重新选择</el-button>
        <el-button type="danger" link @click="clearValue">清除</el-button>
      </div>
    </div>
    <el-button v-else type="primary" plain icon="Picture" @click="openDialog">从素材库选择</el-button>
    <div class="el-upload__tip" v-if="showTip">请从「文件管理」素材库选择图片；如需上传请先到文件管理上传</div>

    <el-dialog title="选择素材" v-model="visible" width="820px" append-to-body destroy-on-close @opened="loadList">
      <el-form :inline="true" class="mb8">
        <el-form-item>
          <el-input v-model="queryParams.originalName" placeholder="文件名" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div v-loading="loading" class="material-grid">
        <div
          v-for="item in fileList"
          :key="item.fileId"
          class="material-item"
          :class="{ active: selected && selected.fileId === item.fileId }"
          @click="selected = item"
        >
          <el-image :src="resolveUrl(item)" fit="cover" style="width: 100%; height: 110px" />
          <div class="material-name" :title="item.originalName">{{ item.originalName || item.fileName }}</div>
        </div>
      </div>
      <el-empty v-if="!loading && fileList.length === 0" description="暂无图片素材，请先到文件管理上传" />

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="loadList"
      />

      <template #footer>
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" :disabled="!selected" @click="confirmSelect">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { listFile } from "@/api/system/file"

const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  showTip: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(["update:modelValue"])
const baseUrl = import.meta.env.VITE_APP_BASE_API

const visible = ref(false)
const loading = ref(false)
const fileList = ref([])
const selected = ref(null)
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 12,
  originalName: undefined,
  params: {
    imageOnly: 'true'
  }
})

const displayUrl = computed(() => {
  if (!props.modelValue) return ""
  if (props.modelValue.startsWith("http://") || props.modelValue.startsWith("https://")) {
    return props.modelValue
  }
  return baseUrl + props.modelValue
})

function resolveUrl(row) {
  const path = row?.fileName || row?.url || ""
  if (!path) return ""
  if (path.startsWith("http://") || path.startsWith("https://")) return path
  return baseUrl + path
}

function openDialog() {
  selected.value = null
  visible.value = true
}

function loadList() {
  loading.value = true
  listFile(queryParams.value).then(res => {
    fileList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  loadList()
}

function resetQuery() {
  queryParams.value.originalName = undefined
  handleQuery()
}

function confirmSelect() {
  if (!selected.value) return
  // 存相对路径，与文件管理一致，便于 /profile 访问
  emit("update:modelValue", selected.value.fileName || selected.value.url)
  visible.value = false
}

function clearValue() {
  emit("update:modelValue", "")
}
</script>

<style scoped>
.material-preview {
  display: flex;
  align-items: flex-end;
  gap: 12px;
}
.material-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.material-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  min-height: 160px;
}
.material-item {
  border: 2px solid transparent;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  background: var(--el-fill-color-light);
}
.material-item.active {
  border-color: var(--el-color-primary);
}
.material-name {
  padding: 6px 8px;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.el-upload__tip {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>