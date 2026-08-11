<template>
  <div class="app-container order-page">
    <div class="page-title-bar">
      <div class="page-title">{{ activityName ? activityName + '的报名订单' : '报名订单' }}</div>
      <div class="page-actions">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['meeting:order:export']">导出</el-button>
        <el-button icon="Refresh" @click="refreshAll">刷新数据</el-button>
      </div>
    </div>

    <el-row :gutter="12" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card main">
          <div class="summary-main-num">{{ stats.totalCount || 0 }}</div>
          <div class="summary-main-label">订单总数</div>
          <div class="summary-sub">结果 {{ total }} 条</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-card-title">订单状态</div>
          <div class="stat-line"><span>已报名</span><b class="ok">{{ stats.registeredCount || 0 }}</b></div>
          <div class="stat-line"><span>已取消</span><b>{{ stats.cancelledCount || 0 }}</b></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-card-title">签到状态</div>
          <div class="stat-line"><span>未签到</span><b>{{ stats.uncheckedCount || 0 }}</b></div>
          <div class="stat-line"><span>已签到</span><b class="ok">{{ stats.checkedCount || 0 }}</b></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="summary-card">
          <div class="summary-card-title">报名通道</div>
          <div class="channel-stat" v-for="item in channelOptions" :key="item.channelId">
            <span>{{ item.channelName }}</span>
          </div>
          <div v-if="!channelOptions.length" class="channel-empty">暂无通道</div>
        </div>
      </el-col>
    </el-row>

    <el-form :model="queryParams" inline class="filter-form" @submit.prevent>
      <el-form-item>
        <el-input v-model="queryParams.contactName" clearable placeholder="姓名" style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.mobile" clearable placeholder="手机" style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.orderStatus" clearable placeholder="全部订单状态" style="width: 150px">
          <el-option label="已报名" value="0" />
          <el-option label="已取消" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.checkinStatus" clearable placeholder="全部签到状态" style="width: 150px">
          <el-option label="未签到" value="0" />
          <el-option label="已签到" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.channelId" clearable placeholder="全部报名通道" style="width: 170px">
          <el-option v-for="item in channelOptions" :key="item.channelId" :label="item.channelName" :value="item.channelId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:order:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:order:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="orderList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="参会人姓名" prop="contactName" min-width="110" align="center" />
      <el-table-column label="联系方式" prop="mobile" min-width="120" align="center" />
      <el-table-column label="报名通道" prop="channelName" min-width="120" align="center" show-overflow-tooltip />
      <el-table-column label="单位" prop="company" min-width="140" align="center" show-overflow-tooltip />
      <el-table-column label="订单状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.orderStatus === '0' ? 'success' : 'info'">
            {{ orderStatusLabel(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="订单号" prop="orderNo" min-width="160" align="center" show-overflow-tooltip />
      <el-table-column label="签到状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.checkinStatus === '1' ? 'success' : 'warning'">
            {{ scope.row.checkinStatus === '1' ? '已签到' : '未签到' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="签到时间" width="170" align="center">
        <template #default="scope">
          <span>{{ parseTime(scope.row.checkinTime) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="120" align="center" show-overflow-tooltip />
      <el-table-column label="创建时间" width="170" align="center">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="success" :disabled="scope.row.orderStatus === '2' || scope.row.checkinStatus === '1'" @click="handleCheckin(scope.row)" v-hasPermi="['meeting:order:edit']">签到</el-button>
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['meeting:order:list']">报名信息</el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:order:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['meeting:order:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="680px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报名通道" prop="channelId">
          <el-select v-model="form.channelId" placeholder="请选择报名通道" filterable style="width: 100%">
            <el-option v-for="item in channelOptions" :key="item.channelId" :label="item.channelName" :value="item.channelId" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名" prop="contactName">
          <el-input v-model="form.contactName" maxlength="64" placeholder="参会人姓名" />
        </el-form-item>
        <el-form-item label="手机" prop="mobile">
          <el-input v-model="form.mobile" maxlength="20" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
            <el-radio value="">未填</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.company" maxlength="200" placeholder="单位名称" />
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" style="width: 100%">
            <el-option label="已报名" value="0" />
            <el-option label="已取消" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="报名信息" v-model="viewOpen" width="560px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ viewData.orderNo }}</el-descriptions-item>
        <el-descriptions-item v-for="item in viewFormItems" :key="item.key" :label="item.label">
          {{ item.value || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="报名通道">{{ viewData.channelName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ orderStatusLabel(viewData.orderStatus) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewOpen = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MeetingApplyOrder">
import { listApplyOrder, getApplyOrderStats, getApplyOrder, addApplyOrder, updateApplyOrder, checkinApplyOrder, delApplyOrder } from '@/api/meeting/applyOrder'
import { listApplyChannel } from '@/api/meeting/apply'
import { listApplyField } from '@/api/meeting/applyField'
import { getActivity } from '@/api/meeting/activity'

const { proxy } = getCurrentInstance()
const route = useRoute()

const activityId = computed(() => route.query.id)
const activityName = ref('')
const loading = ref(true)
const orderList = ref([])
const channelOptions = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const viewOpen = ref(false)
const title = ref('')
const viewData = ref({})
const stats = ref({})
const applyFieldsByChannel = reactive({})

const viewFormItems = computed(() => {
  const values = parseFormJson(viewData.value.formJson)
  const fields = applyFieldsByChannel[viewData.value.channelId] || []
  const items = fields
    .slice()
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    .filter(field => {
      // Only show data that was actually submitted. This excludes disabled fields
      // and fields hidden by a conditional display rule at submission time.
      return Object.prototype.hasOwnProperty.call(values, field.fieldKey) && hasFieldValue(values[field.fieldKey])
    })
    .map(field => ({
      key: field.fieldKey,
      label: field.fieldName || field.fieldKey,
      value: formatFieldValue(values[field.fieldKey])
    }))

  // Old orders or manually created orders may not have a form snapshot.
  if (!items.length) {
    return [
      { key: 'name', label: '姓名', value: viewData.value.contactName },
      { key: 'mobile', label: '手机', value: viewData.value.mobile },
      { key: 'gender', label: '性别', value: viewData.value.gender },
      { key: 'company', label: '单位', value: viewData.value.company }
    ]
  }

  const configuredKeys = new Set(items.map(item => item.key))
  Object.keys(values).forEach(key => {
    if (!configuredKeys.has(key) && hasFieldValue(values[key])) {
      items.push({ key, label: key, value: formatFieldValue(values[key]) })
    }
  })
  return items
})

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  activityId: undefined,
  contactName: undefined,
  mobile: undefined,
  orderStatus: undefined,
  checkinStatus: undefined,
  channelId: undefined
})

const form = ref({})
const rules = {
  channelId: [{ required: true, message: '请选择报名通道', trigger: 'change' }],
  contactName: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  mobile: [{ required: true, message: '手机不能为空', trigger: 'blur' }],
  orderStatus: [{ required: true, message: '请选择订单状态', trigger: 'change' }]
}

function orderStatusLabel(status) {
  if (status === '2') return '已取消'
  return '已报名'
}

function loadActivity() {
  if (!activityId.value) return
  getActivity(activityId.value).then(res => {
    activityName.value = res.data?.activityName || ''
  })
}

function loadChannels() {
  return listApplyChannel({ activityId: activityId.value, pageNum: 1, pageSize: 100 }).then(res => {
    channelOptions.value = res.rows || []
  })
}

function loadStats() {
  if (!activityId.value) return
  getApplyOrderStats(activityId.value).then(res => {
    stats.value = res.data || {}
  })
}

function getList() {
  loading.value = true
  queryParams.value.activityId = activityId.value
  listApplyOrder(queryParams.value).then(res => {
    orderList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => { loading.value = false })
}

function refreshAll() {
  loadStats()
  getList()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.value.contactName = undefined
  queryParams.value.mobile = undefined
  queryParams.value.orderStatus = undefined
  queryParams.value.checkinStatus = undefined
  queryParams.value.channelId = undefined
  handleQuery()
}

function handleExport() {
  const params = { ...queryParams.value, pageNum: undefined, pageSize: undefined }
  const fileName = (activityName.value || '报名订单').replace(/[\\/:*?"<>|]/g, '_')
  proxy.download('meeting/apply/order/export', params, `${fileName}_报名订单.xlsx`)
}

function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.orderId)
  multiple.value = !selection.length
}

function reset() {
  form.value = {
    orderId: undefined,
    activityId: Number(activityId.value),
    channelId: channelOptions.value[0]?.channelId,
    contactName: '',
    mobile: '',
    gender: '',
    company: '',
    orderStatus: '0',
    remark: ''
  }
  proxy.resetForm('formRef')
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增报名订单'
}

function handleUpdate(row) {
  reset()
  getApplyOrder(row.orderId).then(res => {
    form.value = { ...res.data }
    open.value = true
    title.value = '修改报名订单'
  })
}

function handleView(row) {
  getApplyOrder(row.orderId).then(async res => {
    viewData.value = res.data || {}
    await loadApplyFields(viewData.value.channelId)
    viewOpen.value = true
  })
}

function parseFormJson(formJson) {
  if (!formJson) return {}
  if (typeof formJson === 'object') return formJson
  try {
    const parsed = JSON.parse(formJson)
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed : {}
  } catch (e) {
    return {}
  }
}

function formatFieldValue(value) {
  if (Array.isArray(value)) return value.join('、')
  return value === undefined || value === null ? '' : String(value)
}

function hasFieldValue(value) {
  if (Array.isArray(value)) return value.length > 0
  return value !== undefined && value !== null && String(value).trim() !== ''
}

function loadApplyFields(channelId) {
  if (!channelId || applyFieldsByChannel[channelId]) return Promise.resolve()
  return listApplyField({ channelId }).then(res => {
    applyFieldsByChannel[channelId] = res.data || []
  }).catch(() => {
    applyFieldsByChannel[channelId] = []
  })
}

function handleCheckin(row) {
  proxy.$modal.confirm('确认将「' + row.contactName + '」标记为已签到吗？').then(() => {
    return checkinApplyOrder(row.orderId)
  }).then(() => {
    proxy.$modal.msgSuccess('签到成功')
    refreshAll()
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (!valid) return
    const payload = { ...form.value, activityId: Number(activityId.value) }
    const req = payload.orderId ? updateApplyOrder(payload) : addApplyOrder(payload)
    req.then(() => {
      proxy.$modal.msgSuccess('操作成功')
      open.value = false
      refreshAll()
    })
  })
}

function handleDelete(row) {
  const orderIds = row?.orderId || ids.value
  proxy.$modal.confirm('是否确认删除选中的报名订单？').then(() => delApplyOrder(orderIds)).then(() => {
    refreshAll()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  if (!activityId.value) {
    proxy.$modal.msgError('缺少会议ID')
    return
  }
  loadActivity()
  loadChannels().then(() => refreshAll())
})
</script>

<style scoped>
.order-page { padding-bottom: 20px; }
.page-title-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}
.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}
.summary-row { margin-bottom: 12px; }
.summary-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 14px 16px;
  min-height: 118px;
  margin-bottom: 12px;
}
.summary-card.main {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.summary-main-num {
  font-size: 34px;
  font-weight: 700;
  color: #409eff;
  line-height: 1.1;
}
.summary-main-label {
  margin-top: 6px;
  font-size: 14px;
  color: #606266;
}
.summary-sub {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
.summary-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}
.stat-line {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}
.stat-line b { font-size: 16px; color: #303133; }
.stat-line b.ok { color: #67c23a; }
.channel-stat {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}
.channel-empty { font-size: 13px; color: #c0c4cc; }
.filter-form { margin-bottom: 8px; }
</style>
