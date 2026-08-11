<template>
  <div class="material-select">
    <div class="material-preview" v-if="displayUrl">
      <div v-if="mediaType === 'image'" class="material-image-wrap is-selected">
        <el-image
          :src="displayUrl"
          fit="contain"
          class="material-image"
          :preview-src-list="[displayUrl]"
          preview-teleported
        />
      </div>
      <audio v-else controls :src="displayUrl" class="material-audio" />
      <div class="material-actions">
        <el-button type="primary" link @click="openDialog">重新选择</el-button>
        <el-button type="danger" link @click="clearValue">清除</el-button>
      </div>
    </div>
    <el-button
      v-else
      type="primary"
      plain
      :icon="mediaType === 'audio' ? 'Headset' : 'Picture'"
      @click="openDialog"
    >从素材库选择</el-button>
    <div class="el-upload__tip" v-if="showTip">
      请从「文件管理」素材库选择{{ mediaType === "audio" ? "音频" : "图片" }}；如需上传请先到文件管理上传
    </div>
    <div v-if="isLegacyPath" class="material-legacy-tip">当前素材为旧静态资源，重新选择后将切换为文件管理资源</div>

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
          <div v-if="mediaType === 'image'" class="material-image-wrap">
            <el-image
              :src="resolveUrl(item)"
              fit="contain"
              class="material-image"
            />
          </div>
          <div v-else class="material-audio-item">
            <el-icon :size="36"><Headset /></el-icon>
            <span>{{ item.fileSuffix?.toUpperCase() || "音频" }}</span>
          </div>
          <div class="material-name" :title="item.originalName">{{ item.originalName || item.fileName }}</div>
        </div>
      </div>
      <el-empty v-if="!loading && fileList.length === 0" description="暂无图片素材，请先到文件管理上传" />

      <pagination
        v-show="total > 0"
        :total="total"
        :page-sizes="[12, 24, 48]"
        :page="queryParams.pageNum"
        :limit="queryParams.pageSize"
        @update:page="queryParams.pageNum = $event"
        @update:limit="queryParams.pageSize = $event"
        @pagination="handlePagination"
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
import { Headset } from "@element-plus/icons-vue"

const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  },
  showTip: {
    type: Boolean,
    default: true
  },
  mediaType: {
    type: String,
    default: "image",
    validator: value => ["image", "audio"].includes(value)
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
    mediaType: "image"
  }
})

const mediaType = computed(() => props.mediaType)
const isLegacyPath = computed(() => String(props.modelValue || "").startsWith("/reference/"))

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
  queryParams.value.pageNum = 1
  queryParams.value.originalName = undefined
  visible.value = true
}

function loadList() {
  loading.value = true
  // 素材选择器展示全部可用文件；会议归属过滤留给文件管理页处理
  queryParams.value.params.mediaType = mediaType.value
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

function handlePagination({ page, limit } = {}) {
  if (page) {
    queryParams.value.pageNum = page
  }
  if (limit) {
    queryParams.value.pageSize = limit
  }
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
.material-image-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #3a3f4b;
  background-image:
    linear-gradient(45deg, #2f3440 25%, transparent 25%),
    linear-gradient(-45deg, #2f3440 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, #2f3440 75%),
    linear-gradient(-45deg, transparent 75%, #2f3440 75%);
  background-size: 12px 12px;
  background-position: 0 0, 0 6px, 6px -6px, -6px 0;
}
.material-image-wrap.is-selected {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
}
.material-image-wrap .material-image {
  width: 100%;
  height: 110px;
}
.material-image-wrap.is-selected .material-image {
  height: 100%;
}
.material-audio {
  width: 300px;
  max-width: 100%;
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
.material-audio-item {
  display: flex;
  height: 110px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
  color: var(--el-color-primary);
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
.material-legacy-tip {
  margin-top: 6px;
  color: var(--el-color-warning);
  font-size: 12px;
}
</style>
