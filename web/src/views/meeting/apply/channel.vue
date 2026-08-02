<template>
  <div class="app-container">
    <div class="page-header mb8">
      <span class="page-title">报名通道配置</span>
      <span class="page-sub" v-if="activityName">{{ activityName }}</span>
    </div>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:apply:add']">新增通道</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:apply:remove']">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="channelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="通道名称" align="center" prop="channelName" min-width="140" />
      <el-table-column label="主通道" align="center" prop="isMain" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.isMain === '1' ? 'success' : 'info'">{{ scope.row.isMain === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="价格类型" align="center" prop="priceType" width="100">
        <template #default="scope">
          <span>{{ scope.row.priceType === 'paid' ? '收费' : '免费' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="price" width="90" />
      <el-table-column label="名额" align="center" prop="quota" width="90">
        <template #default="scope">
          <span>{{ scope.row.quota === 0 ? '不限' : scope.row.quota }}</span>
        </template>
      </el-table-column>
      <el-table-column label="截止时间" align="center" prop="deadline" width="170">
        <template #default="scope">
          <span>{{ parseTime(scope.row.deadline) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="需邀请码" align="center" width="90">
        <template #default="scope">
          <span>{{ scope.row.needInvite === '1' ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="需审核" align="center" width="80">
        <template #default="scope">
          <span>{{ scope.row.needAudit === '1' ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="显示" align="center" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.visible === '1' ? 'success' : 'info'">{{ scope.row.visible === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="操作" align="center" width="160" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:apply:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['meeting:apply:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="640px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="通道名称" prop="channelName">
          <el-input v-model="form.channelName" maxlength="100" placeholder="请输入通道名称" />
        </el-form-item>
        <el-form-item label="主通道" prop="isMain">
          <el-switch v-model="form.isMain" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="价格类型" prop="priceType">
          <el-radio-group v-model="form.priceType">
            <el-radio value="free">免费</el-radio>
            <el-radio value="paid">收费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.priceType === 'paid'" label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="名额" prop="quota">
          <el-input-number v-model="form.quota" :min="0" />
          <span class="tip">0 表示不限</span>
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="form.deadline"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选填"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="需要邀请码">
          <el-switch v-model="form.needInvite" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="需要审核">
          <el-switch v-model="form.needAudit" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="需要发票">
          <el-switch v-model="form.needInvoice" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-switch v-model="form.visible" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="短信通知">
          <el-switch v-model="form.smsNotify" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MeetingApplyChannel">
import { listApplyChannel, getApplyChannel, addApplyChannel, updateApplyChannel, delApplyChannel } from "@/api/meeting/apply"
import { getActivity } from "@/api/meeting/activity"

const { proxy } = getCurrentInstance()
const route = useRoute()

const activityId = computed(() => route.query.id)
const activityName = ref("")
const loading = ref(true)
const channelList = ref([])
const total = ref(0)
const ids = ref([])
const multiple = ref(true)
const open = ref(false)
const title = ref("")

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  activityId: undefined
})

const form = ref({})
const rules = {
  channelName: [{ required: true, message: "通道名称不能为空", trigger: "blur" }]
}

function getList() {
  loading.value = true
  queryParams.value.activityId = activityId.value
  listApplyChannel(queryParams.value).then(res => {
    channelList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
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
    isMain: "0",
    parentId: 0,
    sortOrder: 0,
    priceType: "free",
    price: 0,
    quota: 0,
    deadline: undefined,
    needInvite: "0",
    needAudit: "0",
    needInvoice: "0",
    visible: "1",
    smsNotify: "0",
    remark: undefined
  }
  proxy.resetForm("formRef")
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增报名通道"
}

function handleUpdate(row) {
  reset()
  getApplyChannel(row.channelId).then(res => {
    form.value = res.data
    open.value = true
    title.value = "修改报名通道"
  })
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (!valid) return
    if (form.value.priceType === "free") {
      form.value.price = 0
    }
    const req = form.value.channelId ? updateApplyChannel(form.value) : addApplyChannel(form.value)
    req.then(() => {
      proxy.$modal.msgSuccess("操作成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const channelIds = row?.channelId || ids.value
  proxy.$modal.confirm("是否确认删除选中的报名通道？").then(() => delApplyChannel(channelIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

onMounted(() => {
  if (!activityId.value) {
    proxy.$modal.msgError("缺少会议ID")
    return
  }
  getActivity(activityId.value).then(res => {
    activityName.value = res.data?.activityName || ""
  })
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
.tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>