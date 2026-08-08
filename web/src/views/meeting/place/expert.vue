<template>
  <div class="app-container place-page">
    <PlaceTabs active="expert" />
    <el-alert type="info" :closable="false" show-icon class="mb12" title="根据主题/日程中的主席、主持、讲者、讨论名单自动汇总，并检测时间冲突；姓名会与嘉宾库匹配。" />
    <el-form :model="query" inline @submit.prevent>
      <el-form-item><el-input v-model="query.expertName" clearable placeholder="专家姓名" @keyup.enter="getList" /></el-form-item>
      <el-form-item>
        <el-select v-model="query.role" clearable placeholder="全部角色" style="width:140px">
          <el-option label="主席" value="chair" /><el-option label="主持" value="host" />
          <el-option label="讲者" value="speaker" /><el-option label="讨论" value="discuss" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="query.guestMatched" clearable placeholder="嘉宾库匹配" style="width:140px">
          <el-option label="已匹配" value="1" /><el-option label="未匹配" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getList">查询</el-button>
        <el-button type="warning" @click="onlyConflict">只看冲突</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="filteredList" border>
      <el-table-column label="专家" prop="expertName" min-width="110" />
      <el-table-column label="嘉宾库" width="100" align="center">
        <template #default="s">
          <el-tag :type="s.row.guestMatched==='1'?'success':'info'">{{ s.row.guestMatched==='1'?'已匹配':'未匹配' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="90" align="center">
        <template #default="s"><el-tag>{{ roleLabel(s.row.role) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="来源" width="90" align="center">
        <template #default="s">{{ s.row.source === 'topic' ? '主题' : '日程' }}</template>
      </el-table-column>
      <el-table-column label="任务标题" prop="title" min-width="160" show-overflow-tooltip />
      <el-table-column label="会场" prop="venueName" min-width="120" />
      <el-table-column label="主题" prop="topicName" min-width="120" show-overflow-tooltip />
      <el-table-column label="单位" prop="guestOrg" min-width="120" show-overflow-tooltip />
      <el-table-column label="开始" width="170" align="center"><template #default="s">{{ parseTime(s.row.startTime) || '-' }}</template></el-table-column>
      <el-table-column label="结束" width="170" align="center"><template #default="s">{{ parseTime(s.row.endTime) || '-' }}</template></el-table-column>
      <el-table-column label="任务数" prop="taskCount" width="80" align="center" />
      <el-table-column label="冲突" width="90" align="center">
        <template #default="s">
          <el-tag :type="s.row.conflict==='1'?'danger':'success'">{{ s.row.conflict==='1'?'冲突':'正常' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup name="MeetingExpert">
import PlaceTabs from './PlaceTabs'
import { listExpertTasks } from '@/api/meeting/schedule'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(false), list = ref([])
const query = ref({ expertName: undefined, role: undefined, onlyConflict: undefined, guestMatched: undefined })
const filteredList = computed(() => {
  if (query.value.guestMatched === undefined || query.value.guestMatched === null || query.value.guestMatched === '') {
    return list.value
  }
  return list.value.filter(i => String(i.guestMatched || '0') === String(query.value.guestMatched))
})
function roleLabel(r){ return ({ chair:'主席', host:'主持', speaker:'讲者', discuss:'讨论' })[r] || r }
function getList(){
  if (!activityId.value) return
  loading.value = true
  const params = {
    expertName: query.value.expertName,
    role: query.value.role,
    onlyConflict: query.value.onlyConflict
  }
  listExpertTasks(activityId.value, params).then(res => {
    list.value = res.data || []
    loading.value = false
  }).catch(() => { loading.value = false })
}
function onlyConflict(){ query.value.onlyConflict = '1'; getList() }
function resetQuery(){ query.value = { expertName: undefined, role: undefined, onlyConflict: undefined, guestMatched: undefined }; getList() }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } getList() })
</script>
<style scoped>
.mb12{margin-bottom:12px}
</style>
