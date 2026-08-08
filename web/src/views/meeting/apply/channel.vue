<template>
  <div class="app-container channel-page">
    <div class="summary-row">
      <div class="summary-card">
        <div class="summary-label">报名通道总数</div>
        <div class="summary-value">{{ total }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">当前页</div>
        <div class="summary-value">{{ channelList.length }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">已选报名通道</div>
        <div class="summary-value">{{ ids.length }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">显示中通道</div>
        <div class="summary-value">{{ visibleCount }}</div>
      </div>
    </div>

    <el-alert
      class="toolbar-hint"
      type="info"
      :closable="false"
      show-icon
      title="先配置报名通道和字段表单，再细调名额与截止时间。"
    />

    <el-form :model="queryParams" inline class="filter-form" @submit.prevent>
      <el-form-item>
        <el-input
          v-model="queryParams.channelName"
          clearable
          placeholder="搜索报名通道名称/说明"
          style="width: 260px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['meeting:apply:add']">新增报名通道</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:apply:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="channelList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />

      <el-table-column label="报名通道信息" min-width="280">
        <template #default="scope">
          <div class="channel-cell">
            <div class="channel-title">
              <span class="channel-name">{{ scope.row.channelName || '-' }}</span>
              <el-tag size="small" :type="isMainChannel(scope.row) ? 'success' : 'warning'">
                {{ isMainChannel(scope.row) ? '主通道' : '子通道' }}
              </el-tag>
            </div>
            <div class="channel-subtitle">
              <span>上级通道：{{ scope.row.parentName || '无' }}</span>
              <span>排序：{{ scope.row.sortOrder || 0 }}</span>
            </div>
            <div v-if="scope.row.remark" class="channel-description">{{ scope.row.remark }}</div>
            <div class="channel-tags">
              <el-tag size="small" :type="scope.row.visible === '1' ? 'success' : 'info'">
                {{ scope.row.visible === '1' ? '显示' : '隐藏' }}
              </el-tag>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="规则摘要" min-width="240">
        <template #default="scope">
          <div class="metric-list">
            <div class="metric-item">
              <span class="metric-label">名额</span>
              <span class="metric-value">{{ scope.row.quota ? scope.row.quota : '不限' }}</span>
            </div>
            <div class="metric-item">
              <span class="metric-label">截止</span>
              <span class="metric-value">{{ parseTime(scope.row.deadline) || '未设置' }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="是否显示" width="120" align="center" header-align="center">
        <template #default="scope">
          <el-switch
            v-model="scope.row.visible"
            active-value="1"
            inactive-value="0"
            active-text="开"
            inactive-text="关"
            @change="(val) => handleVisibleChange(scope.row, val)"
          />
        </template>
      </el-table-column>

      <el-table-column label="配置情况" width="160" align="center" header-align="center">
        <template #default="scope">
          <div class="progress-card">
            <div class="progress-title">字段配置</div>
            <div class="progress-value">{{ scope.row.fieldCount || 0 }} 项</div>
            <el-button link type="primary" @click="goFieldConfig(scope.row)" v-hasPermi="['meeting:apply:list']">去配置字段</el-button>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="goFieldConfig(scope.row)" v-hasPermi="['meeting:apply:list']">字段配置</el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:apply:edit']">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['meeting:apply:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="820px" append-to-body destroy-on-close>
      <el-scrollbar max-height="70vh">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="apply-form">
          <section class="apply-form-section">
            <h3>基础信息</h3>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="上级报名通道">
                  <el-select v-model="form.parentId" placeholder="无" style="width: 100%">
                    <el-option label="无" :value="0" />
                    <el-option
                      v-for="item in parentOptions"
                      :key="item.channelId"
                      :label="item.channelName"
                      :value="item.channelId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="报名通道名称" prop="channelName">
                  <el-input v-model="form.channelName" maxlength="100" placeholder="例如：普通报名、嘉宾报名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="排序" prop="sortOrder">
                  <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否显示">
                  <el-switch v-model="form.visible" active-value="1" inactive-value="0" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="报名通道说明">
              <el-input
                v-model="form.remark"
                type="textarea"
                :rows="3"
                maxlength="500"
                show-word-limit
                placeholder="用于后台识别该报名通道面向的人群或使用场景"
              />
            </el-form-item>
          </section>

          <section class="apply-form-section">
            <h3>报名规则</h3>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="人数上限">
                  <el-input-number v-model="form.quota" :min="0" controls-position="right" />
                  <span class="tip">0 表示不限</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="报名截止时间">
                  <el-date-picker
                    v-model="form.deadline"
                    type="datetime"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    placeholder="选填"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
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

<script setup name="MeetingApplyChannel">
import { listApplyChannel, getApplyChannel, addApplyChannel, updateApplyChannel, delApplyChannel } from '@/api/meeting/apply'

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const activityId = computed(() => route.query.id)
const loading = ref(true)
const channelList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const title = ref('')

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  activityId: undefined,
  channelName: undefined
})

const form = ref({})
const parentOptions = computed(() => {
  return channelList.value.filter(item => item.channelId !== form.value.channelId)
})
const visibleCount = computed(() => channelList.value.filter(item => item.visible === '1').length)
const rules = {
  channelName: [{ required: true, message: '通道名称不能为空', trigger: 'blur' }]
}

function isMainChannel(row) {
  return row.isMain === '1' || !row.parentId
}

function getList() {
  loading.value = true
  queryParams.value.activityId = activityId.value
  listApplyChannel(queryParams.value).then(res => {
    channelList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => { loading.value = false })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.value.channelName = undefined
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(i => i.channelId)
  multiple.value = !selection.length
}

function reset() {
  form.value = {
    channelId: undefined,
    activityId: Number(activityId.value),
    channelName: undefined,
    isMain: '1',
    parentId: 0,
    sortOrder: 0,
    quota: 0,
    deadline: undefined,
    visible: '1',
    remark: undefined
  }
  proxy.resetForm('formRef')
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增报名通道'
}

function goFieldConfig(row) {
  router.push({
    path: '/meeting/activity-config/apply-field',
    query: {
      id: activityId.value,
      channelId: row.channelId
    }
  })
}

function handleUpdate(row) {
  reset()
  getApplyChannel(row.channelId).then(res => {
    form.value = {
      ...res.data,
      parentId: res.data.parentId || 0
    }
    open.value = true
    title.value = '修改报名通道'
  })
}

function handleVisibleChange(row, val) {
  const payload = {
    ...row,
    visible: val,
    isMain: isMainChannel(row) ? '1' : '0'
  }
  updateApplyChannel(payload).then(() => {
    proxy.$modal.msgSuccess(val === '1' ? '已开启显示' : '已关闭显示')
    getList()
  }).catch(() => {
    row.visible = val === '1' ? '0' : '1'
  })
}

function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (!valid) return
    form.value.isMain = form.value.parentId ? '0' : '1'
    const payload = { ...form.value }
    delete payload.priceType
    delete payload.price
    delete payload.needInvite
    delete payload.needAudit
    delete payload.needInvoice
    delete payload.smsNotify
    delete payload.parentName
    delete payload.fieldCount
    const req = form.value.channelId ? updateApplyChannel(payload) : addApplyChannel(payload)
    req.then(() => {
      proxy.$modal.msgSuccess('操作成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const channelIds = row?.channelId || ids.value
  proxy.$modal.confirm('是否确认删除选中的报名通道？').then(() => delApplyChannel(channelIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

onMounted(() => {
  if (!activityId.value) {
    proxy.$modal.msgError('缺少会议ID')
    return
  }
  getList()
})
</script>

<style scoped>
.channel-page {
  padding-bottom: 20px;
}
.summary-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}
.summary-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 16px 18px;
}
.summary-label {
  font-size: 13px;
  color: #909399;
}
.summary-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
  color: #409eff;
  line-height: 1.1;
}
.toolbar-hint {
  margin-bottom: 12px;
}
.filter-form {
  margin-bottom: 8px;
}
.channel-cell {
  padding: 4px 0;
}
.channel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.channel-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.channel-subtitle {
  display: flex;
  gap: 16px;
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
.channel-description {
  margin-top: 6px;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}
.channel-tags {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  flex-wrap: wrap;
}
.metric-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px 16px;
  padding: 2px 0;
}
.metric-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.metric-label {
  font-size: 12px;
  color: #909399;
}
.metric-value {
  font-size: 13px;
  color: #303133;
  word-break: break-all;
}
.progress-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 0;
}
.progress-title {
  font-size: 12px;
  color: #909399;
}
.progress-value {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}
.apply-form {
  padding: 0 4px;
}
.apply-form-section {
  margin-bottom: 16px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fff;
}
.apply-form-section:last-child {
  margin-bottom: 0;
}
.apply-form-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 18px;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}
.apply-form-section h3::before {
  width: 4px;
  height: 18px;
  border-radius: 2px;
  background: #409eff;
  content: '';
}
.apply-form-section :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}
.apply-form-section :deep(.el-form-item__label) {
  color: #606266;
}
.apply-form-section :deep(.el-form-item) {
  margin-bottom: 18px;
}
.apply-form :deep(.el-input),
.apply-form :deep(.el-select),
.apply-form :deep(.el-date-editor) {
  max-width: 100%;
}
:deep(.el-dialog__body) {
  padding: 16px 20px 4px;
  background: #f5f7fa;
}
:deep(.el-dialog__footer) {
  padding-top: 12px;
}
.tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
@media (max-width: 1100px) {
  .summary-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>