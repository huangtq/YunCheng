<template>
  <div class="app-container">
    <div class="grid-workbench">
      <div class="grid-editor">
        <div class="template-config-card mb8">
          <div class="template-config-form">
        <el-form :model="configForm" inline>
          <el-form-item label="会议模板">
            <el-select
              v-model="configForm.gridTemplate"
              filterable
              style="width: 330px"
              placeholder="请选择会议模板"
            >
              <el-option
                v-for="item in templateOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
                <el-popover
                  placement="right-start"
                  trigger="hover"
                  :width="240"
                  :show-after="120"
                  :hide-after="120"
                  popper-class="meeting-template-popover"
                >
                  <template #reference>
                    <div class="template-option">{{ item.label }}</div>
                  </template>
                  <div class="template-popover-content">
                    <div class="template-popover-title">{{ item.label }}</div>
                    <el-image
                      class="template-popover-image"
                      :src="item.preview"
                      :preview-src-list="[item.preview]"
                      fit="contain"
                      preview-teleported
                    />
                    <div v-if="item.description" class="template-popover-description">
                      {{ item.description }}
                    </div>
                  </div>
                </el-popover>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="saveTemplate"
              v-hasPermi="['meeting:activity:edit']"
            >
              保存模板
            </el-button>
          </el-form-item>
        </el-form>
        <div class="template-tip">
          一期标准九宫格已对接移动端：模板会控制 C 端列数（1/2/3列）与纯图标/图文样式，保存后扫码预览即可验证。
        </div>
          </div>
        </div>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:grid:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain @click="handleSyncModules" v-hasPermi="['meeting:grid:add']">同步常用模块</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:grid:remove']">删除</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="loading" :data="gridList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="图标" align="center" width="90">
            <template #default="scope">
              <MeetingIcon
                v-if="scope.row.iconType === 'icon' && scope.row.iconKey"
                :icon-key="scope.row.iconKey"
                :size="40"
                color="#4f46e5"
              />
              <el-image
                v-else-if="scope.row.iconUrl"
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
              <span v-if="scope.row.linkType === 'module'">{{ moduleLabel(scope.row.moduleKey) }}</span>
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
          <el-table-column label="操作" align="center" width="220" fixed="right">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:grid:edit']">修改</el-button>
              <el-button
                v-if="scope.row.linkType === 'module' && getMeetingModule(scope.row.moduleKey)"
                link
                type="success"
                @click="goModuleConfig(scope.row.moduleKey)"
              >去配置</el-button>
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['meeting:grid:remove']">删除</el-button>
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
      </div>

      <aside class="phone-preview-panel">
        <div class="preview-scan">
          <img class="preview-qrcode" :src="qrImageUrl" alt="会议预览二维码" />
          <div class="preview-scan-title">扫码通过手机预览</div>
        </div>
        <div class="phone-frame">
          <div class="phone-screen">
            <div class="phone-status-bar">会议预览</div>
            <div class="phone-cover" :style="previewCoverStyle">
              <div class="phone-cover-title">{{ activityInfo.activityName || "会议名称" }}</div>
              <div v-if="String(configForm.showRegisterCount) === '1'" class="phone-cover-meta">
                已报名 {{ activityInfo.registerCount || 0 }} 人
              </div>
              <div v-if="String(configForm.showCountdown) === '1'" class="phone-cover-countdown">
                {{ countdownPreviewText }}
              </div>
            </div>
            <div class="phone-grid" :class="[previewGridClass, previewGridStyleClass]">
              <div v-for="item in previewItems" :key="item.gridId" class="phone-grid-item">
                <div class="phone-grid-icon" :style="{ background: `${themeColor}22` }">
                  <MeetingIcon
                    v-if="item.iconType === 'icon' && item.iconKey"
                    :icon-key="item.iconKey"
                    :size="34"
                    :color="themeColor"
                  />
                  <img v-else-if="item.iconUrl" :src="resolveUrl(item.iconUrl)" alt="" />
                  <el-icon v-else :size="28"><Grid /></el-icon>
                </div>
                <span v-if="!isIconOnlyPreview">{{ item.title }}</span>
              </div>
              <div v-if="!previewItems.length" class="phone-empty">暂无启用的菜单项</div>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <el-dialog :title="title" v-model="open" width="920px" append-to-body destroy-on-close>
      <el-scrollbar max-height="70vh">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="grid-form">
          <section class="grid-form-section">
            <h3>基础信息</h3>
            <el-row :gutter="24">
              <el-col :span="10">
                <el-form-item label="标题" prop="title">
                  <el-input v-model="form.title" maxlength="100" placeholder="标题" />
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="父节点">
                  <el-select v-model="form.parentId" placeholder="无" style="width: 100%">
                    <el-option label="无" :value="0" />
                    <el-option
                      v-for="item in parentOptions"
                      :key="item.gridId"
                      :label="item.title"
                      :value="item.gridId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="排序" prop="sortOrder">
                  <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="移动端图标">
              <el-radio-group v-model="form.iconType" @change="handleIconTypeChange">
                <el-radio-button value="image">图片素材</el-radio-button>
                <el-radio-button value="icon">图标库</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.iconType === 'image'" label="素材图片" prop="iconUrl">
              <material-select v-model="form.iconUrl" :show-tip="false" />
            </el-form-item>
            <el-form-item v-else label="图标选择" prop="iconKey">
              <meeting-icon-select v-model="form.iconKey" />
            </el-form-item>
          </section>

          <section class="grid-form-section">
            <h3>点击后的动作</h3>
            <el-form-item label="动作类型" prop="linkType">
              <el-radio-group v-model="form.linkType" class="action-type-group">
                <el-radio-button value="none">无动作</el-radio-button>
                <el-radio-button value="module">模块跳转</el-radio-button>
                <el-radio-button value="url">外部链接</el-radio-button>
                <el-radio-button value="content">内容页</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.linkType === 'module'" label="模块跳转" prop="moduleKey">
              <el-select v-model="form.moduleKey" placeholder="请选择模块" style="width: 360px" @change="onModuleChange">
                <el-option
                  v-for="item in moduleOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <div v-if="selectedModule" class="module-tip">
                <span>{{ selectedModule.desc }}</span>
                <el-button link type="primary" @click="goModuleConfig(form.moduleKey)">去配置该模块数据</el-button>
              </div>
            </el-form-item>
            <el-form-item v-if="form.linkType === 'url'" label="外部链接" prop="externalUrl">
              <el-input v-model="form.externalUrl" maxlength="500" placeholder="https://" style="max-width: 620px" />
            </el-form-item>
            <el-form-item v-if="form.linkType === 'content'" label="内容">
              <editor v-model="form.content" :min-height="220" />
            </el-form-item>
          </section>

          <section class="grid-form-section">
            <h3>展示设置</h3>
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="动画效果">
                  <el-select v-model="form.animation" style="width: 100%">
                    <el-option v-for="item in animationOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="渐变色">
                  <el-color-picker v-model="form.gradientColor" show-alpha />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="透明度">
                  <el-input-number v-model="form.opacity" :min="0" :max="1" :step="0.1" :precision="1" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="隐藏">
              <el-switch v-model="form.hidden" active-text="隐藏此菜单" />
            </el-form-item>
          </section>
        </el-form>
      </el-scrollbar>
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
import MeetingIcon from "@/components/MeetingIcon"
import MeetingIconSelect from "@/components/MeetingIconSelect"
import { MEETING_MODULE_OPTIONS, getMeetingModule, meetingModuleLabel, buildMeetingH5HomeUrl } from "@/utils/meetingModules"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const baseUrl = import.meta.env.VITE_APP_BASE_API

const activityId = computed(() => route.query.id)
const activityInfo = ref({})
const loading = ref(true)
const gridList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const title = ref("")
const configForm = ref({ gridTemplate: "1" })

const templateOptions = [
  {
    value: "1",
    label: "传统九宫格",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231225/3181354ccb4b44f0984581f5401a18fc.jpg"
  },
  {
    value: "5",
    label: "纯图标九宫格",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231225/00ed86c2392449e28046363a8327bbb4.jpg"
  },
  {
    value: "62",
    label: "3-3(1-2)-2布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231224/5091f4f1293a4780976a89aebef85967.jpg"
  },
  {
    value: "63",
    label: "3-3(1-2)-2-2-2布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231224/5091f4f1293a4780976a89aebef85967.jpg"
  },
  {
    value: "65",
    label: "3-3(1-2)-2图标+文字布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20240528/74e3f8808e9c4f1a9f2ef6e406a03632.jpg"
  },
  {
    value: "7",
    label: "一列布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20240528/f372efc58bfe4b93900b0dd93c113f6e.jpg"
  },
  {
    value: "64",
    label: "3-3(1-2)-3布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20231224/5091f4f1293a4780976a89aebef85967.jpg"
  },
  {
    value: "71",
    label: "一列布局（纯图标）",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20240528/f372efc58bfe4b93900b0dd93c113f6e.jpg"
  },
  {
    value: "68",
    label: "模板68-2-2-2-2-2布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png",
    description: "2-2-2-2-2布局，每行2个item，统一高度100px，支持自定义渐变色和图标"
  },
  {
    value: "681",
    label: "(描边)模板68-2-2-2-2-2布局",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png",
    description: "九宫格透明背景，使用渐变色控制文字、边框和图标色调"
  },
  {
    value: "651",
    label: "(描边)3-3(1-2)-2图标",
    preview: "http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/cac2a41b385d44419af9505cc64d4894.png",
    description: "按 3-3(1-2)-2 模板布局展示，透明背景，白色文字、边框和图标"
  }
]

const moduleOptions = MEETING_MODULE_OPTIONS
function moduleLabel(v) {
  return meetingModuleLabel(v)
}

const animationOptions = [
  { label: "无", value: "none" },
  { label: "快速翻转", value: "flip" },
  { label: "快速翻转-X", value: "flipX" },
  { label: "快速翻转-Y", value: "flipY" },
  { label: "旋转进入", value: "rotateIn" },
  { label: "中心放大", value: "zoomIn" },
  { label: "淡入", value: "fadeIn" }
]

const previewItems = computed(() => {
  return gridList.value
    .filter(item => String(item.status) !== "0")
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
})

const themeColor = computed(() => configForm.value.mobileThemeColor || "#1f6feb")

const previewGridClass = computed(() => {
  const template = String(configForm.value.gridTemplate)
  if (["7", "71"].includes(template)) return "grid-one"
  if (["68", "681"].includes(template)) return "grid-two"
  return "grid-three"
})

const isIconOnlyPreview = computed(() => ["5", "71"].includes(String(configForm.value.gridTemplate)))
const previewGridStyleClass = computed(() => (isIconOnlyPreview.value ? "grid-icon-only" : ""))

const countdownPreviewText = computed(() => {
  const style = configForm.value.countdownStyle || "classic"
  if (style === "digital") return "倒计时 · 数字翻牌样式"
  if (style === "simple") return "倒计时 · 简洁样式"
  return "倒计时 · 经典样式"
})

const previewCoverStyle = computed(() => {
  const coverUrl = resolveUrl(activityInfo.value.coverUrl)
  if (coverUrl) {
    return {
      backgroundImage: `linear-gradient(180deg, rgba(0, 0, 0, 0.05), rgba(0, 0, 0, 0.55)), url("${coverUrl}")`
    }
  }
  return {
    backgroundImage: `linear-gradient(135deg, ${themeColor.value}, #0b3d91)`
  }
})

const qrImageUrl = computed(() => {
  const target = configForm.value.qrUrl || buildMeetingH5HomeUrl(activityId.value)
  return `https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(target)}`
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  activityId: undefined
})

const form = ref({})
const selectedModule = computed(() => getMeetingModule(form.value.moduleKey))
const parentOptions = computed(() => {
  return gridList.value.filter(item => item.gridId !== form.value.gridId)
})
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
  if (type === "content") return "内容页"
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
    activityInfo.value = res.data || {}
  })
  getActivityConfig(activityId.value).then(res => {
    configForm.value = {
      ...configForm.value,
      ...(res.data || {}),
      gridTemplate: normalizeTemplate(res.data?.gridTemplate)
    }
  })
}

function normalizeTemplate(value) {
  const legacyMap = {
    grid3x3: "1",
    grid2x2: "5",
    list: "7"
  }
  const normalized = legacyMap[value] || String(value || "")
  return templateOptions.some(item => item.value === normalized) ? normalized : "1"
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

function parseGridOptions(remark) {
  if (!remark) return {}
  try {
    const options = JSON.parse(remark)
    return options && options.__gridForm ? options : {}
  } catch {
    return {}
  }
}

function restoreForm(data) {
  const options = parseGridOptions(data.remark)
  return {
    ...data,
    iconType: data.iconType || (data.iconKey ? "icon" : "image"),
    iconKey: data.iconKey || "",
    parentId: options.parentId || 0,
    content: data.content || options.content || "",
    animation: options.animation || "none",
    gradientColor: options.gradientColor || "",
    opacity: options.opacity ?? 1,
    hidden: options.hidden !== undefined
      ? options.hidden === true || options.hidden === 1 || options.hidden === "1"
      : String(data.status) === "0"
  }
}

function buildPayload() {
  const payload = { ...form.value }
  const previousRemark = payload.remark
  delete payload.parentId
  delete payload.animation
  delete payload.gradientColor
  delete payload.opacity
  delete payload.hidden
  payload.status = form.value.hidden ? "0" : "1"
  payload.iconType = form.value.iconType || "image"
  payload.iconUrl = payload.iconType === "image" ? (form.value.iconUrl || "") : ""
  payload.iconKey = payload.iconType === "icon" ? (form.value.iconKey || "") : ""
  payload.remark = JSON.stringify({
    __gridForm: true,
    parentId: form.value.parentId || 0,
    animation: form.value.animation || "none",
    gradientColor: form.value.gradientColor || "",
    opacity: form.value.opacity ?? 1,
    remark: previousRemark || ""
  })
  return payload
}

function reset() {
  form.value = {
    gridId: undefined,
    activityId: Number(activityId.value),
    title: undefined,
    iconType: "image",
    iconKey: "",
    iconUrl: undefined,
    parentId: 0,
    linkType: "content",
    moduleKey: "none",
    externalUrl: undefined,
    content: "",
    animation: "none",
    gradientColor: "",
    opacity: 1,
    hidden: false,
    sortOrder: 0,
    status: "1"
  }
  proxy.resetForm("formRef")
}

function handleIconTypeChange(type) {
  if (type === "icon") {
    form.value.iconUrl = ""
    form.value.iconKey = form.value.iconKey || ""
  } else {
    form.value.iconKey = ""
  }
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增九宫格项"
}

function onModuleChange(key) {
  const mod = getMeetingModule(key)
  if (!mod) return
  if (!form.value.title) form.value.title = mod.label
  if (form.value.iconType === "icon" && !form.value.iconKey) {
    form.value.iconKey = mod.iconKey
  }
}

function goModuleConfig(key) {
  const mod = getMeetingModule(key)
  if (!mod?.adminPath) {
    proxy.$modal.msgWarning("该模块暂无后台配置页")
    return
  }
  router.push({ path: mod.adminPath, query: { id: activityId.value } })
}

async function handleSyncModules() {
  const used = new Set(
    gridList.value
      .filter(i => i.linkType === "module" && i.moduleKey)
      .map(i => i.moduleKey)
  )
  const missing = MEETING_MODULE_OPTIONS
    .map(o => getMeetingModule(o.value))
    .filter(m => m && !used.has(m.key))
  if (!missing.length) {
    proxy.$modal.msgSuccess("常用模块均已在九宫格中")
    return
  }
  try {
    await proxy.$modal.confirm(`将补充 ${missing.length} 个常用模块入口（已有的不会重复）：${missing.map(m => m.label).join("、")}`)
  } catch {
    return
  }
  let sortBase = Math.max(0, ...gridList.value.map(i => i.sortOrder || 0))
  for (const mod of missing) {
    sortBase += 1
    await addGrid({
      activityId: Number(activityId.value),
      title: mod.label,
      iconType: "icon",
      iconKey: mod.iconKey,
      iconUrl: "",
      linkType: "module",
      moduleKey: mod.key,
      externalUrl: "",
      content: "",
      sortOrder: sortBase,
      status: "1",
      remark: JSON.stringify({
        __gridForm: true,
        parentId: 0,
        animation: "none",
        gradientColor: "",
        opacity: 1,
        remark: "同步常用模块"
      })
    })
  }
  proxy.$modal.msgSuccess(`已同步 ${missing.length} 个模块`)
  getList()
}

function handleUpdate(row) {
  reset()
  getGrid(row.gridId).then(res => {
    form.value = restoreForm(res.data)
    open.value = true
    title.value = "修改九宫格项"
  })
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (!valid) return
    const payload = buildPayload()
    const req = form.value.gridId ? updateGrid(payload) : addGrid(payload)
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
.grid-workbench {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.grid-editor {
  flex: 1;
  min-width: 0;
}
.template-config-card {
  padding: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.template-config-form {
  width: 100%;
}
.template-tip {
  margin-top: 4px;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}
.template-option {
  width: 100%;
}
.template-popover-content {
  text-align: center;
}
.template-popover-title {
  margin-bottom: 8px;
  color: #303133;
  font-size: 13px;
  font-weight: 600;
}
.template-popover-image {
  width: 210px;
  height: 300px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #f5f7fa;
}
.template-popover-description {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
  text-align: left;
}
.grid-form {
  padding: 0 4px;
}
.grid-form-section {
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fff;
}
.grid-form-section:last-child {
  margin-bottom: 0;
}
.grid-form-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 18px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}
.grid-form-section h3::before {
  width: 4px;
  height: 18px;
  border-radius: 2px;
  background: #409eff;
  content: "";
}
.grid-form-section :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}
.grid-form-section :deep(.el-form-item__label) {
  color: #606266;
}
.grid-form-section :deep(.el-form-item) {
  margin-bottom: 18px;
}
:deep(.el-dialog__body) {
  padding: 16px 20px 4px;
  background: #f5f7fa;
}
:deep(.el-dialog__footer) {
  padding-top: 12px;
}
.action-type-group {
  display: flex;
}
.module-tip {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}
.grid-form-section :deep(.editor) {
  width: min(100%, 700px);
}
.phone-preview-panel {
  flex: 0 0 300px;
  padding: 14px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.preview-scan {
  display: flex;
  align-items: center;
  min-height: 120px;
  gap: 18px;
}
.preview-qrcode {
  width: 120px;
  height: 120px;
  flex: 0 0 120px;
}
.preview-scan-title {
  color: #303133;
  font-size: 22px;
  font-weight: 600;
  line-height: 1.25;
}
.phone-frame {
  width: 284px;
  margin-top: 8px;
  overflow: hidden;
  border: 2px solid #606266;
  border-radius: 3px;
  background: #f5f7fa;
}
.phone-screen {
  height: 720px;
  overflow-y: auto;
  background: #f5f7fa;
  scrollbar-width: thin;
}
.phone-status-bar {
  height: 30px;
  padding: 7px 12px;
  color: #fff;
  background: #1f3c88;
  font-size: 12px;
  text-align: center;
}
.template-config-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.template-tip {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  padding: 0 2px 4px;
}
.phone-cover {
  display: flex;
  flex-direction: column;
  min-height: 150px;
  justify-content: flex-end;
  gap: 6px;
  padding: 18px;
  background: linear-gradient(135deg, #1d4ed8, #0ea5e9);
  background-position: center;
  background-size: cover;
}
.phone-cover-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.35;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.35);
}
.phone-cover-meta,
.phone-cover-countdown {
  color: rgba(255, 255, 255, 0.92);
  font-size: 12px;
  line-height: 1.4;
}
.phone-grid {
  display: grid;
  gap: 1px;
  background: #e5e7eb;
}
.phone-grid.grid-one {
  grid-template-columns: 1fr;
}
.phone-grid.grid-two {
  grid-template-columns: repeat(2, 1fr);
}
.phone-grid.grid-three {
  grid-template-columns: repeat(3, 1fr);
}
.phone-grid-item {
  display: flex;
  min-height: 100px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
  padding: 12px 6px;
  color: #303133;
  background: #fff;
  font-size: 13px;
  text-align: center;
}
.phone-grid.grid-one .phone-grid-item {
  flex-direction: row;
  justify-content: flex-start;
  gap: 12px;
  padding: 14px 16px;
  min-height: 72px;
}
.phone-grid-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.phone-grid.grid-icon-only .phone-grid-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
}
.phone-grid-item img {
  width: 28px;
  height: 28px;
  object-fit: contain;
}
.phone-grid-item .el-icon {
  color: inherit;
}
.phone-empty {
  grid-column: 1 / -1;
  padding: 40px 12px;
  color: #909399;
  font-size: 13px;
  text-align: center;
}
@media (max-width: 1200px) {
  .phone-preview-panel {
    flex-basis: 270px;
  }
  .phone-frame {
    width: 254px;
  }
  .preview-scan {
    gap: 10px;
  }
  .preview-qrcode {
    width: 100px;
    height: 100px;
    flex-basis: 100px;
  }
  .preview-scan-title {
    font-size: 17px;
  }
}
</style>