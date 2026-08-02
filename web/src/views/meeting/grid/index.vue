<template>
  <div class="app-container">
    <div class="page-header mb8">
      <span class="page-title">九宫格配置</span>
      <span class="page-sub" v-if="activityName">{{ activityName }}</span>
    </div>

    <el-form :model="configForm" inline class="mb8">
      <el-form-item label="模板">
        <el-select v-model="configForm.gridTemplate" style="width: 180px">
          <el-option label="3x3 九宫格" value="grid3x3" />
          <el-option label="2x2 四宫格" value="grid2x2" />
          <el-option label="列表样式" value="list" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveTemplate" v-hasPermi="['meeting:activity:edit']">保存模板</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:grid:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:grid:remove']">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="gridList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="图标" align="center" width="90">
        <template #default="scope">
          <el-image
            v-if="scope.row.iconUrl"
            style="width: 40px; height: 40px"
            :src="resolveUrl(scope.row.iconUrl)"
            fit="cover"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" min-width="120" />
      <el-table-column label="链接类型" align="center" prop="linkType" width="100">
        <template #default="scope">
          <span>{{ linkTypeLabel(scope.row.linkType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="模块/外链" align="center" min-width="160" :show-overflow-tooltip="true">
        <template #default="scope">
          <span v-if="scope.row.linkType === 'module'">{{ scope.row.moduleKey }}</span>
          <span v-else-if="scope.row.linkType === 'url'">{{ scope.row.externalUrl }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'info'">{{ scope.row.status === '1' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:grid:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['meeting:grid:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="100" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图标" prop="iconUrl">
          <material-select v-model="form.iconUrl" />
        </el-form-item>
        <el-form-item label="链接类型" prop="linkType">
          <el-radio-group v-model="form.linkType">
            <el-radio value="none">无</el-radio>
            <el-radio value="module">内置模块</el-radio>
            <el-radio value="url">外部链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.linkType === 'module'" label="模块" prop="moduleKey">
          <el-select v-model="form.moduleKey" placeholder="请选择模块" style="width: 100%">
            <el-option label="报名" value="apply" />
            <el-option label="日程" value="schedule" />
            <el-option label="嘉宾" value="guest" />
            <el-option label="酒店" value="hotel" />
            <el-option label="直播" value="live" />
            <el-option label="会场" value="venue" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.linkType === 'url'" label="外链" prop="externalUrl">
          <el-input v-model="form.externalUrl" maxlength="500" placeholder="https://" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MeetingGrid">
import { listGrid, getGrid, addGrid, updateGrid, delGrid } from "@/api/meeting/grid"
import { getActivity } from "@/api/meeting/activity"
import { getActivityConfig, updateActivityConfig } from "@/api/meeting/config"
import MaterialSelect from "@/components/MaterialSelect"

const { proxy } = getCurrentInstance()
const route = useRoute()
const baseUrl = import.meta.env.VITE_APP_BASE_API

const activityId = computed(() => route.query.id)
const activityName = ref("")
const loading = ref(true)
const gridList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const title = ref("")
const configForm = ref({ gridTemplate: "grid3x3" })

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  activityId: undefined
})

const form = ref({})
const rules = {
  title: [{ required: true, message: "标题不能为空", trigger: "blur" }]
}

function resolveUrl(url) {
  if (!url) return ""
  if (url.startsWith("http://") || url.startsWith("https://")) return url
  return baseUrl + url
}

function linkTypeLabel(type) {
  if (type === "module") return "内置模块"
  if (type === "url") return "外部链接"
  return "无"
}

function getList() {
  loading.value = true
  queryParams.value.activityId = activityId.value
  listGrid(queryParams.value).then(res => {
    gridList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}

function loadMeta() {
  getActivity(activityId.value).then(res => {
    activityName.value = res.data?.activityName || ""
  })
  getActivityConfig(activityId.value).then(res => {
    configForm.value.gridTemplate = res.data?.gridTemplate || "grid3x3"
  })
}

function saveTemplate() {
  updateActivityConfig({
    activityId: Number(activityId.value),
    gridTemplate: configForm.value.gridTemplate
  }).then(() => proxy.$modal.msgSuccess("模板已保存"))
}

function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.gridId)
  multiple.value = !selection.length
}

function reset() {
  form.value = {
    gridId: undefined,
    activityId: Number(activityId.value),
    title: undefined,
    iconUrl: undefined,
    linkType: "none",
    moduleKey: "none",
    externalUrl: undefined,
    sortOrder: 0,
    status: "1"
  }
  proxy.resetForm("formRef")
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增九宫格项"
}

function handleUpdate(row) {
  reset()
  getGrid(row.gridId).then(res => {
    form.value = res.data
    open.value = true
    title.value = "修改九宫格项"
  })
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (!valid) return
    const req = form.value.gridId ? updateGrid(form.value) : addGrid(form.value)
    req.then(() => {
      proxy.$modal.msgSuccess("操作成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const gridIds = row?.gridId || ids.value
  proxy.$modal.confirm("是否确认删除选中的九宫格项？").then(() => delGrid(gridIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

onMounted(() => {
  if (!activityId.value) {
    proxy.$modal.msgError("缺少会议ID")
    return
  }
  loadMeta()
  getList()
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.page-sub {
  color: #909399;
  font-size: 13px;
}
</style>