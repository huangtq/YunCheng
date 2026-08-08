<template>
  <div class="app-container config-hub" v-loading="loading">
    <div class="hub-header">
      <div class="hub-title">
        <span class="name">{{ activity.activityName || '会议配置' }}</span>
        <span class="code" v-if="activity.activityCode">编号：{{ activity.activityCode }}</span>
      </div>
    </div>

    <div v-for="group in groups" :key="group.title" class="hub-group">
      <div class="group-title">{{ group.title }}</div>
      <el-row :gutter="16">
        <el-col v-for="item in group.items" :key="item.key" :xs="12" :sm="8" :md="6" :lg="4">
          <div class="hub-card" @click="handleCard(item)">
            <div class="card-icon" :style="{ background: item.color }">
              <el-icon :size="22"><component :is="item.icon" /></el-icon>
            </div>
            <div class="card-label">{{ item.label }}</div>
            <div v-if="item.moduleKey" class="card-meta">
              <el-tag size="small" :type="gridModuleKeys.has(item.moduleKey) ? 'success' : 'info'" effect="plain">
                {{ gridModuleKeys.has(item.moduleKey) ? '已挂九宫格' : '可挂九宫格' }}
              </el-tag>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-drawer v-model="editOpen" title="编辑会议信息" size="760px" append-to-body destroy-on-close>
      <MeetingActivityForm
        v-if="editOpen"
        embedded
        :activity-id="activityId"
        @success="handleEditSuccess"
        @cancel="editOpen = false"
      />
    </el-drawer>

    <el-dialog v-model="switchOpen" title="常用开关" width="760px" append-to-body destroy-on-close class="switch-dialog">
      <el-form v-loading="switchLoading" :model="configForm" label-width="180px" class="switch-form">
        <div v-for="group in switchGroups" :key="group.title" class="switch-group">
          <div class="switch-group-title">{{ group.title }}</div>
          <el-row :gutter="12">
            <el-col v-for="item in group.items" :key="item.prop" :span="item.span || 12">
              <el-form-item :label="item.label">
                <el-select
                  v-if="item.type === 'select'"
                  v-model="configForm[item.prop]"
                  style="width: 100%"
                >
                  <el-option
                    v-for="opt in item.options"
                    :key="opt.value"
                    :label="opt.label"
                    :value="opt.value"
                  />
                </el-select>
                <el-switch
                  v-else
                  v-model="configForm[item.prop]"
                  active-value="1"
                  inactive-value="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" :loading="switchSaving" @click="saveSwitches">保 存</el-button>
        <el-button @click="switchOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="mobileOpen" title="移动端详情布局" width="760px" append-to-body destroy-on-close>
      <el-form :model="configForm" label-width="130px">
        <el-form-item label="详情模板">
          <el-select v-model="configForm.mobileTemplate" style="width: 100%">
            <el-option label="标准会议页（九宫格，一期主推）" value="standard" />
            <el-option label="宣传图热点页（二期）" value="image-map" />
          </el-select>
        </el-form-item>
        <el-form-item label="主题色">
          <el-color-picker v-model="configForm.mobileThemeColor" show-alpha />
        </el-form-item>
        <el-form-item label="自定义背景">
          <el-input v-model="configForm.mobileBackgroundUrl" placeholder="宣传图热点页使用的背景图片地址" />
        </el-form-item>
        <el-form-item label="进入提示">
          <el-input
            v-model="configForm.mobileNotice"
            type="textarea"
            :rows="3"
            placeholder="可选，例如会议须知、报名提示"
          />
        </el-form-item>
        <el-form-item label="热点区块 JSON">
          <el-input
            v-model="configForm.mobileBlocksJson"
            type="textarea"
            :rows="10"
            placeholder='例如：[{"title":"参会报名","left":25,"top":35,"width":50,"height":20,"linkType":"module","moduleKey":"apply"}]'
          />
          <div class="form-tip">
            坐标使用百分比；linkType 支持 module、url、content，image-map 模板可用此配置还原宣传图菜单。
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="previewMobile">预览 H5</el-button>
        <el-button type="primary" :loading="switchSaving" @click="saveMobile">保 存</el-button>
        <el-button @click="mobileOpen = false">关 闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="qrOpen" title="查看二维码" width="480px" append-to-body destroy-on-close>
      <el-form :model="configForm" label-width="100px">
        <el-form-item label="落地链接">
          <el-input v-model="configForm.qrUrl" placeholder="例：http://localhost:9090/#/pages/meeting/home?activityId=1" />
          <div class="form-tip">默认建议：H5 会议首页 /#/pages/meeting/home?activityId={会议ID}</div>
        </el-form-item>
        <el-form-item label="二维码">
          <div class="qr-box" v-if="configForm.qrUrl">
            <img :src="qrImageUrl" alt="qrcode" />
          </div>
          <span v-else class="qr-empty">请先填写落地链接</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" :loading="switchSaving" @click="saveQr">保 存</el-button>
        <el-button @click="qrOpen = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MeetingActivityConfig">
import { getActivity } from "@/api/meeting/activity"
import { getActivityConfig, updateActivityConfig } from "@/api/meeting/config"
import { listGrid } from "@/api/meeting/grid"
import MeetingActivityForm from "./form"
import { HUB_ACTION_MODULE_KEY, buildMeetingH5HomeUrl } from "@/utils/meetingModules"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const activityId = computed(() => route.query.id)
const loading = ref(false)
const activity = ref({})
const configForm = ref({})
const editOpen = ref(false)
const switchOpen = ref(false)
const mobileOpen = ref(false)
const qrOpen = ref(false)
const switchLoading = ref(false)
const switchSaving = ref(false)
const gridModuleKeys = ref(new Set())

const qrImageUrl = computed(() => {
  const target = configForm.value.qrUrl || buildMeetingH5HomeUrl(activityId.value)
  if (!target) return ""
  return "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=" + encodeURIComponent(target)
})

const switchGroups = [
  {
    title: "展示设置",
    items: [
      { prop: "mpShow", label: "公众号显示" },
      { prop: "homeBanner", label: "首页轮播" },
      { prop: "hotShow", label: "热门会议" },
      { prop: "showRegisterCount", label: "显示报名人数" }
    ]
  },
  {
    title: "倒计时",
    items: [
      { prop: "showCountdown", label: "显示倒计时" },
      {
        prop: "countdownStyle",
        label: "倒计时样式",
        type: "select",
        options: [
          { label: "经典", value: "classic" },
          { label: "简约", value: "simple" },
          { label: "数字", value: "digital" }
        ]
      }
    ]
  },
  {
    title: "酒店与直播",
    items: [
      { prop: "hotelNeedRegister", label: "酒店需先报名" },
      { prop: "liveNeedRegister", label: "直播需先报名" },
      { prop: "registerShowLive", label: "报名成功显示直播" },
      { prop: "registerShowHotel", label: "报名成功显示酒店" },
      { prop: "hotelOnce", label: "只能订一次酒店" },
      { prop: "cancelRegisterCancelHotel", label: "取消报名同步取消酒店" }
    ]
  },
  {
    title: "登录设置",
    items: [
      { prop: "loginSms", label: "登录需要短信验证码" },
      { prop: "registerForceMobile", label: "报名强制手机号登录" }
    ]
  }
]

const groups = [
  {
    title: "会议配置",
    items: [
      { key: "edit", label: "编辑会议信息", icon: "Edit", color: "#409EFF", action: "edit" },
      { key: "switch", label: "常用开关", icon: "Setting", color: "#67C23A", action: "switch" },
      { key: "grid", label: "九宫格配置", icon: "Menu", color: "#E6A23C", action: "grid" },
      { key: "gridBottom", label: "九宫格底部配置", icon: "Grid", color: "#6C5CE7", action: "gridBottom" },
      { key: "mobile", label: "移动端详情", icon: "Iphone", color: "#00B894", action: "mobile" },
      { key: "qr", label: "查看二维码", icon: "Postcard", color: "#F56C6C", action: "qr" },
      { key: "nav", label: "导航管理", icon: "Guide", color: "#00B894", action: "nav", moduleKey: HUB_ACTION_MODULE_KEY.nav }
    ]
  },
  {
    title: "报名与订单",
    items: [
      { key: "apply", label: "报名配置", icon: "Tickets", color: "#409EFF", action: "apply", moduleKey: HUB_ACTION_MODULE_KEY.apply },
      { key: "order", label: "报名订单", icon: "List", color: "#E17055", action: "order" }
    ]
  },
  {
    title: "会场与日程",
    items: [
      { key: "venue", label: "会场管理", icon: "OfficeBuilding", color: "#0984E3", action: "venue", moduleKey: HUB_ACTION_MODULE_KEY.venue },
      { key: "topic", label: "主题管理", icon: "Collection", color: "#6C5CE7", action: "topic" },
      { key: "schedule", label: "日程管理", icon: "Calendar", color: "#00B894", action: "schedule", moduleKey: HUB_ACTION_MODULE_KEY.schedule },
      { key: "expert", label: "专家任务排期", icon: "User", color: "#E17055", action: "expert" }
    ]
  },
  {
    title: "嘉宾与人员",
    items: [
      { key: "guest", label: "嘉宾管理", icon: "User", color: "#0984E3", action: "guest", moduleKey: HUB_ACTION_MODULE_KEY.guest },
      { key: "trip", label: "嘉宾行程", icon: "Position", color: "#00B894", action: "trip" },
      { key: "rule", label: "任务规则配置", icon: "SetUp", color: "#6C5CE7", action: "rule" },
      { key: "staff", label: "工作人员", icon: "Avatar", color: "#E17055", action: "staff" },
      { key: "fee", label: "劳务费管理", icon: "Money", color: "#FDCB6E", action: "fee" }
    ]
  },
  {
    title: "酒店与住宿",
    items: [
      { key: "hotel", label: "会议酒店", icon: "House", color: "#0984E3", action: "hotel", moduleKey: HUB_ACTION_MODULE_KEY.hotel },
      { key: "hotelOrder", label: "酒店订单", icon: "Document", color: "#E17055", action: "hotelOrder" },
      { key: "hotelAssign", label: "分房管理", icon: "Key", color: "#00B894", action: "hotelAssign" }
    ]
  },
  {
    title: "特色功能",
    items: [
      { key: "exhibitor", label: "展商管理", icon: "Shop", color: "#0984E3", action: "exhibitor", moduleKey: HUB_ACTION_MODULE_KEY.exhibitor },
      { key: "meal", label: "餐票管理", icon: "Food", color: "#00B894", action: "meal", moduleKey: HUB_ACTION_MODULE_KEY.meal }
    ]
  }
]

function loadActivity() {
  if (!activityId.value) {
    proxy.$modal.msgError("缺少会议ID")
    return
  }
  loading.value = true
  Promise.all([
    getActivity(activityId.value),
    listGrid({ activityId: activityId.value, pageNum: 1, pageSize: 200 })
  ]).then(([actRes, gridRes]) => {
    activity.value = actRes.data || {}
    const keys = new Set()
    ;(gridRes.rows || []).forEach(item => {
      if (item.linkType === "module" && item.moduleKey && String(item.status) !== "0") {
        keys.add(item.moduleKey)
      }
    })
    gridModuleKeys.value = keys
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function loadConfig() {
  switchLoading.value = true
  return getActivityConfig(activityId.value).then(res => {
    configForm.value = { ...(res.data || {}) }
    switchLoading.value = false
  }).catch(() => {
    switchLoading.value = false
  })
}

function handleCard(item) {
  if (item.action === "edit") {
    editOpen.value = true
    return
  }
  if (item.action === "switch") {
    loadConfig().then(() => { switchOpen.value = true })
    return
  }
  if (item.action === "qr") {
    loadConfig().then(() => {
      if (!configForm.value.qrUrl) {
        configForm.value.qrUrl = buildMeetingH5HomeUrl(activityId.value)
      }
      qrOpen.value = true
    })
    return
  }
  if (item.action === "mobile") {
    loadConfig().then(() => {
      configForm.value.mobileTemplate = configForm.value.mobileTemplate || "standard"
      configForm.value.mobileThemeColor = configForm.value.mobileThemeColor || "#1f6feb"
      configForm.value.mobileBlocksJson = configForm.value.mobileBlocksJson || "[]"
      mobileOpen.value = true
    })
    return
  }
  if (item.action === "grid") {
    router.push({ path: "/meeting/activity-config/grid", query: { id: activityId.value } })
    return
  }
  if (item.action === "gridBottom") {
    router.push({ path: "/meeting/activity-config/grid-bottom", query: { id: activityId.value } })
    return
  }
  if (item.action === "nav") {
    router.push({ path: "/meeting/activity-config/nav", query: { id: activityId.value } })
    return
  }
  if (item.action === "apply") {
    router.push({ path: "/meeting/activity-config/apply-channel", query: { id: activityId.value } })
    return
  }
  if (item.action === "order") {
    router.push({ path: "/meeting/activity-config/apply-order", query: { id: activityId.value } })
    return
  }
  if (item.action === "venue") {
    router.push({ path: "/meeting/activity-config/venue", query: { id: activityId.value } })
    return
  }
  if (item.action === "topic") {
    router.push({ path: "/meeting/activity-config/topic", query: { id: activityId.value } })
    return
  }
  if (item.action === "schedule") {
    router.push({ path: "/meeting/activity-config/schedule", query: { id: activityId.value } })
    return
  }
  if (item.action === "expert") {
    router.push({ path: "/meeting/activity-config/expert", query: { id: activityId.value } })
    return
  }
  if (item.action === "guest") {
    router.push({ path: "/meeting/activity-config/guest", query: { id: activityId.value } })
    return
  }
  if (item.action === "trip") {
    router.push({ path: "/meeting/activity-config/guest-trip", query: { id: activityId.value } })
    return
  }
  if (item.action === "rule") {
    router.push({ path: "/meeting/activity-config/guest-rule", query: { id: activityId.value } })
    return
  }
  if (item.action === "staff") {
    router.push({ path: "/meeting/activity-config/staff", query: { id: activityId.value } })
    return
  }
  if (item.action === "fee") {
    router.push({ path: "/meeting/activity-config/guest-fee", query: { id: activityId.value } })
    return
  }
  if (item.action === "exhibitor") {
    router.push({ path: "/meeting/activity-config/exhibitor", query: { id: activityId.value } })
    return
  }
  if (item.action === "meal") {
    router.push({ path: "/meeting/activity-config/meal", query: { id: activityId.value } })
    return
  }
  if (item.action === "hotel") {
    router.push({ path: "/meeting/activity-config/hotel", query: { id: activityId.value } })
    return
  }
  if (item.action === "hotelOrder") {
    router.push({ path: "/meeting/activity-config/hotel-order", query: { id: activityId.value } })
    return
  }
  if (item.action === "hotelAssign") {
    router.push({ path: "/meeting/activity-config/hotel-assign", query: { id: activityId.value } })
  }
}

function handleEditSuccess() {
  editOpen.value = false
  loadActivity()
}

function saveSwitches() {
  switchSaving.value = true
  updateActivityConfig({ ...configForm.value, activityId: Number(activityId.value) }).then(() => {
    proxy.$modal.msgSuccess("保存成功")
    switchOpen.value = false
    switchSaving.value = false
  }).catch(() => {
    switchSaving.value = false
  })
}

function saveMobile() {
  try {
    JSON.parse(configForm.value.mobileBlocksJson || "[]")
  } catch (e) {
    proxy.$modal.msgError("热点区块 JSON 格式不正确")
    return
  }
  switchSaving.value = true
  updateActivityConfig({
    activityId: Number(activityId.value),
    mobileTemplate: configForm.value.mobileTemplate || "standard",
    mobileThemeColor: configForm.value.mobileThemeColor || "#1f6feb",
    mobileBackgroundUrl: configForm.value.mobileBackgroundUrl || "",
    mobileBlocksJson: configForm.value.mobileBlocksJson || "[]",
    mobileNotice: configForm.value.mobileNotice || ""
  }).then(() => {
    proxy.$modal.msgSuccess("移动端布局已保存")
    mobileOpen.value = false
    switchSaving.value = false
  }).catch(() => {
    switchSaving.value = false
  })
}

function previewMobile() {
  window.open(buildMeetingH5HomeUrl(activityId.value), "_blank")
}

function saveQr() {
  switchSaving.value = true
  updateActivityConfig({
    activityId: Number(activityId.value),
    qrUrl: configForm.value.qrUrl || ""
  }).then(() => {
    proxy.$modal.msgSuccess("保存成功")
    switchSaving.value = false
  }).catch(() => {
    switchSaving.value = false
  })
}

onMounted(() => {
  loadActivity()
})
</script>

<style scoped>
.config-hub {
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}
.hub-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
}
.hub-title .name {
  font-size: 18px;
  font-weight: 600;
  margin-right: 12px;
}
.hub-title .code {
  color: #909399;
  font-size: 13px;
}
.hub-group {
  margin-bottom: 20px;
  padding: 16px 20px 8px;
  background: #fff;
  border-radius: 8px;
}
.group-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 14px;
  color: #303133;
}
.hub-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 18px 8px;
  margin-bottom: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}
.hub-card:hover {
  background: #f2f6fc;
}
.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.card-label {
  font-size: 13px;
  color: #606266;
  text-align: center;
}
.card-meta {
  min-height: 22px;
}
.qr-box {
  width: 220px;
  height: 220px;
  border: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: center;
}
.qr-box img {
  width: 220px;
  height: 220px;
}
.qr-empty {
  color: #909399;
}
.switch-form {
  max-height: 62vh;
  overflow-y: auto;
  padding-right: 8px;
}
.switch-group {
  margin-bottom: 18px;
  padding: 12px 14px 2px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #eef2f7;
}
.switch-group:last-child {
  margin-bottom: 0;
}
.switch-group-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
  line-height: 1.2;
}
.switch-group :deep(.el-form-item) {
  margin-bottom: 14px;
}
.switch-group :deep(.el-form-item__label) {
  color: #606266;
}
.form-tip { margin-top: 6px; color: #909399; font-size: 12px; line-height: 1.4; }
</style>