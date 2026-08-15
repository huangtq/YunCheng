<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="名称/会议编号"
          clearable
          style="width: 220px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:activity:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:activity:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="会议编码" align="center" prop="activityCode" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="会议名称" align="center" prop="activityName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="主视觉" align="center" width="100">
        <template #default="scope">
          <el-image
            v-if="scope.row.coverUrl"
            style="width: 60px; height: 60px"
            :src="resolveUrl(scope.row.coverUrl)"
            :preview-src-list="[resolveUrl(scope.row.coverUrl)]"
            fit="cover"
            preview-teleported
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="详细地址" align="center" prop="address" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="是否展示" align="center" prop="isShow" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.isShow === '1' ? 'success' : 'info'">{{ scope.row.isShow === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否热门" align="center" prop="isHot" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.isHot === '1' ? 'danger' : 'info'" effect="plain">{{ scope.row.isHot === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否首页" align="center" prop="isHome" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.isHome === '1' ? 'warning' : 'info'" effect="plain">{{ scope.row.isHome === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名人数" align="center" prop="registerCount" width="90" />
      <el-table-column label="点击/访问" align="center" prop="visitCount" width="100" />
      <el-table-column label="流量/观看" align="center" prop="viewCount" width="100" />
      <el-table-column label="操作" align="center" width="190" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Setting" @click="handleConfig(scope.row)">会议配置</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['meeting:activity:edit']">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer
      v-model="formOpen"
      :title="formId ? '编辑会议' : '新增会议'"
      size="760px"
      append-to-body
      destroy-on-close
    >
      <MeetingActivityForm
        v-if="formOpen"
        embedded
        :activity-id="formId"
        @success="handleFormSuccess"
        @cancel="handleFormCancel"
      />
    </el-drawer>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="MeetingActivity">
import { listActivity, delActivity } from "@/api/meeting/activity"
import MeetingActivityForm from "./form"

const { proxy } = getCurrentInstance()
const baseUrl = import.meta.env.VITE_APP_BASE_API

const activityList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const formOpen = ref(false)
const formId = ref()

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  keyword: undefined
})

function resolveUrl(url) {
  if (!url) return ""
  if (url.startsWith("http://") || url.startsWith("https://")) return url
  return baseUrl + url
}

function getList() {
  loading.value = true
  listActivity(queryParams.value).then(response => {
    activityList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.activityId)
  multiple.value = !selection.length
}

function handleAdd() {
  formId.value = undefined
  formOpen.value = true
}

function handleUpdate(row) {
  formId.value = row.activityId
  formOpen.value = true
}

function handleFormSuccess() {
  formOpen.value = false
  getList()
}

function handleFormCancel() {
  formOpen.value = false
}

function handleConfig(row) {
  proxy.$router.push({ path: "/meeting/activity-config", query: { id: row.activityId } })
}

function handleDelete(row) {
  const activityIds = row?.activityId || ids.value
  proxy.$modal.confirm('是否确认删除选中的会议？').then(function() {
    return delActivity(activityIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>
