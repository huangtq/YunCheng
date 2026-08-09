<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Upload"
          @click="handleUpload"
          v-hasPermi="['system:file:upload']"
        >上传文件</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:file:remove']"
        >批量删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="fileList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="fileId" width="100" />
      <el-table-column label="URL地址" align="center" min-width="280">
        <template #default="scope">
          <el-image
            v-if="isImage(scope.row.fileSuffix)"
            style="width: 80px; height: 80px"
            :src="resolveUrl(scope.row)"
            :preview-src-list="[resolveUrl(scope.row)]"
            fit="cover"
            preview-teleported
          />
          <el-link
            v-else
            type="primary"
            :href="resolveUrl(scope.row)"
            target="_blank"
            :underline="false"
          >{{ resolveUrl(scope.row) }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:file:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page="queryParams.pageNum"
      :limit="queryParams.pageSize"
      @update:page="queryParams.pageNum = $event"
      @update:limit="queryParams.pageSize = $event"
      @pagination="getList"
    />

    <!-- 上传对话框 -->
    <el-dialog title="上传文件" v-model="open" width="480px" append-to-body destroy-on-close>
      <el-upload
        ref="uploadRef"
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :limit="5"
        :file-list="uploadList"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :on-exceed="handleExceed"
        :before-upload="beforeUpload"
        multiple
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">支持常见图片与文档，单文件不超过 10MB</div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="File">
import { delActivityFile, delFile, listActivityFile, listFile } from "@/api/system/file"
import { getToken } from "@/utils/auth"
import { UploadFilled } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const route = useRoute()

const fileList = ref([])
const open = ref(false)
const loading = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const uploadList = ref([])
const baseUrl = import.meta.env.VITE_APP_BASE_API
const activityId = computed(() => route.query.id || "")
const uploadUrl = computed(() => activityId.value
  ? `${baseUrl}/meeting/activity/${activityId.value}/file/upload`
  : `${baseUrl}/system/file/upload`)
const uploadHeaders = ref({ Authorization: "Bearer " + getToken() })
const imageSuffixes = ["jpg", "jpeg", "png", "gif", "bmp", "webp"]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10
})

/** 是否图片 */
function isImage(suffix) {
  if (!suffix) {
    return false
  }
  return imageSuffixes.includes(String(suffix).toLowerCase())
}

/** 解析访问地址 */
function resolveUrl(row) {
  if (!row) {
    return ""
  }
  const path = row.fileName || row.url || ""
  if (!path) {
    return ""
  }
  if (path.startsWith("http://") || path.startsWith("https://")) {
    return path
  }
  return baseUrl + path
}

/** 查询文件列表 */
function getList() {
  loading.value = true
  const request = activityId.value
    ? listActivityFile(activityId.value, queryParams.value)
    : listFile(queryParams.value)
  request.then(response => {
    fileList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.fileId)
  multiple.value = !selection.length
}

/** 打开上传 */
function handleUpload() {
  uploadList.value = []
  uploadHeaders.value = { Authorization: "Bearer " + getToken() }
  open.value = true
}

/** 上传前校验 */
function beforeUpload(file) {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    proxy.$modal.msgError("上传文件大小不能超过 10MB")
  }
  return isLt10M
}

/** 上传成功 */
function handleUploadSuccess(response) {
  if (response.code === 200) {
    proxy.$modal.msgSuccess("上传成功")
    getList()
    resetUploadForm()
  } else {
    proxy.$modal.msgError(response.msg || "上传失败")
  }
}

/** 重置上传表单 */
function resetUploadForm() {
  uploadList.value = []
  nextTick(() => {
    uploadRef.value?.clearFiles()
  })
}

/** 上传失败 */
function handleUploadError() {
  proxy.$modal.msgError("上传失败")
}

/** 超出数量限制 */
function handleExceed() {
  proxy.$modal.msgError("一次最多上传 5 个文件")
}

/** 删除按钮操作 */
function handleDelete(row) {
  const fileIds = row?.fileId || ids.value
  proxy.$modal.confirm('是否确认删除选中的文件？').then(function() {
    return activityId.value
      ? delActivityFile(activityId.value, fileIds)
      : delFile(fileIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>