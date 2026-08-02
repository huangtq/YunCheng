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

    <el-dialog v-model="qrOpen" title="查看二维码" width="480px" append-to-body destroy-on-close>
      <el-form :model="configForm" label-width="100px">
        <el-form-item label="落地链接">
          <el-input v-model="configForm.qrUrl" placeholder="请输入小程序/H5落地链接" />
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
import MeetingActivityForm from "./form"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const activityId = computed(() => route.query.id)
const loading = ref(false)
const activity = ref({})
const configForm = ref({})
const editOpen = ref(false)
const switchOpen = ref(false)
const qrOpen = ref(false)
const switchLoading = ref(false)
const switchSaving = ref(false)

const qrImageUrl = computed(() => {
  if (!configForm.value.qrUrl) return ""
  return "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=" + encodeURIComponent(configForm.value.qrUrl)
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
      { key: "gridBottom", label: "九宫格底部配置", icon: "Grid", color: "#909399", action: "todo" },
      { key: "qr", label: "查看二维码", icon: "Postcard", color: "#F56C6C", action: "qr" },
      { key: "nav", label: "导航管理", icon: "Guide", color: "#909399", action: "todo" }
    ]
  },
  {
    title: "报名与订单",
    items: [
      { key: "apply", label: "报名配置", icon: "Tickets", color: "#409EFF", action: "apply" },
      { key: "order", label: "报名订单", icon: "List", color: "#909399", action: "todo" }
    ]
  },
  {
    title: "会场与日程",
    items: [
      { key: "venue", label: "会场管理", icon: "OfficeBuilding", color: "#909399", action: "todo" },
      { key: "topic", label: "主题管理", icon: "Collection", color: "#909399", action: "todo" },
      { key: "schedule", label: "日程管理", icon: "Calendar", color: "#909399", action: "todo" },
      { key: "live", label: "直播标签", icon: "VideoCamera", color: "#909399", action: "todo" }
    ]
  },
  {
    title: "嘉宾与人员",
    items: [
      { key: "guest", label: "嘉宾管理", icon: "User", color: "#909399", action: "todo" },
      { key: "staff", label: "工作人员", icon: "Avatar", color: "#909399", action: "todo" }
    ]
  },
  {
    title: "酒店与住宿",
    items: [
      { key: "hotel", label: "会议酒店", icon: "House", color: "#909399", action: "todo" },
      { key: "hotelOrder", label: "酒店订单", icon: "Document", color: "#909399", action: "todo" }
    ]
  },
  {
    title: "特色功能",
    items: [
      { key: "ai", label: "AI问答", icon: "ChatDotRound", color: "#909399", action: "todo" },
      { key: "paper", label: "征文", icon: "EditPen", color: "#909399", action: "todo" }
    ]
  },
  {
    title: "数据与财务",
    items: [
      { key: "pv", label: "PV & UV 记录", icon: "DataLine", color: "#909399", action: "todo" }
    ]
  }
]

function loadActivity() {
  if (!activityId.value) {
    proxy.$modal.msgError("缺少会议ID")
    return
  }
  loading.value = true
  getActivity(activityId.value).then(res => {
    activity.value = res.data || {}
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
  if (item.action === "todo") {
    proxy.$modal.msgWarning("功能开发中")
    return
  }
  if (item.action === "edit") {
    editOpen.value = true
    return
  }
  if (item.action === "switch") {
    loadConfig().then(() => { switchOpen.value = true })
    return
  }
  if (item.action === "qr") {
    loadConfig().then(() => { qrOpen.value = true })
    return
  }
  if (item.action === "grid") {
    router.push({ path: "/meeting/activity-config/grid", query: { id: activityId.value } })
    return
  }
  if (item.action === "apply") {
    router.push({ path: "/meeting/activity-config/apply-channel", query: { id: activityId.value } })
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
</style>