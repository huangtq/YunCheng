<template>
  <div class="app-container place-page">
    <PlaceTabs active="topic" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">总主题数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">日程数</div><div class="summary-value ok">{{ stats.scheduleCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item>
        <el-select v-model="queryParams.venueId" clearable placeholder="全部会场" style="width:180px">
          <el-option v-for="v in venues" :key="v.venueId" :label="v.venueName" :value="v.venueId" />
        </el-select>
      </el-form-item>
      <el-form-item><el-input v-model="queryParams.topicName" clearable placeholder="主题名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:topic:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:topic:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="会场名称" prop="venueName" min-width="120" />
      <el-table-column label="主题名称" prop="topicName" min-width="160" />
      <el-table-column label="主席" prop="chairNames" min-width="120" show-overflow-tooltip />
      <el-table-column label="主持人" prop="hostNames" min-width="120" show-overflow-tooltip />
      <el-table-column label="讨论" prop="discussNames" min-width="120" show-overflow-tooltip />
      <el-table-column label="开始时间" width="170" align="center"><template #default="s">{{ parseTime(s.row.startTime) || '-' }}</template></el-table-column>
      <el-table-column label="结束时间" width="170" align="center"><template #default="s">{{ parseTime(s.row.endTime) || '-' }}</template></el-table-column>
      <el-table-column label="日程数" prop="scheduleCount" width="80" align="center" />
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:topic:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:topic:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="680px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="会议会场" prop="venueId">
          <el-select v-model="form.venueId" placeholder="请选择会场" filterable style="width:100%">
            <el-option v-for="v in venues" :key="v.venueId" :label="v.venueName" :value="v.venueId" />
          </el-select>
        </el-form-item>
        <el-form-item label="主题名称" prop="topicName"><el-input v-model="form.topicName" maxlength="200" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="主席">
          <el-select v-model="form.chairList" multiple filterable allow-create default-first-option collapse-tags collapse-tags-tooltip placeholder="从嘉宾库选择，可手动输入" style="width:100%">
            <el-option v-for="g in guests" :key="'c-'+g.guestId" :label="guestOptionLabel(g)" :value="g.guestName" />
          </el-select>
        </el-form-item>
        <el-form-item label="主持人">
          <el-select v-model="form.hostList" multiple filterable allow-create default-first-option collapse-tags collapse-tags-tooltip placeholder="从嘉宾库选择，可手动输入" style="width:100%">
            <el-option v-for="g in guests" :key="'h-'+g.guestId" :label="guestOptionLabel(g)" :value="g.guestName" />
          </el-select>
        </el-form-item>
        <el-form-item label="讨论">
          <el-select v-model="form.discussList" multiple filterable allow-create default-first-option collapse-tags collapse-tags-tooltip placeholder="从嘉宾库选择，可手动输入" style="width:100%">
            <el-option v-for="g in guests" :key="'d-'+g.guestId" :label="guestOptionLabel(g)" :value="g.guestName" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingTopic">
import PlaceTabs from './PlaceTabs'
import { listTopic, getTopicStats, getTopic, addTopic, updateTopic, delTopic } from '@/api/meeting/topic'
import { listVenue } from '@/api/meeting/venue'
import { listGuest } from '@/api/meeting/guest'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), venues = ref([]), guests = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, venueId:undefined, topicName:undefined })
const rules = {
  venueId: [{ required:true, message:'请选择会场', trigger:'change' }],
  topicName: [{ required:true, message:'主题名称不能为空', trigger:'blur' }]
}
function guestOptionLabel(g){ return g.guestName + (g.orgName ? ' / ' + g.orgName : '') + (g.phone ? ' / ' + g.phone : '') }
function splitNames(v){ return String(v || '').split(/[,，、;/；]/).map(s => s.trim()).filter(Boolean) }
function joinNames(arr){ return (arr || []).map(s => String(s).trim()).filter(Boolean).join(',') }
function loadVenues(){ return listVenue({ activityId: activityId.value, pageNum:1, pageSize:200 }).then(res => { venues.value = res.rows || [] }) }
function loadGuests(){ return listGuest({ activityId: activityId.value, pageNum:1, pageSize:500 }).then(res => { guests.value = res.rows || [] }) }
function loadStats(){ getTopicStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listTopic(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.venueId=undefined; queryParams.value.topicName=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.topicId); multiple.value=!s.length }
function reset(){
  form.value={
    topicId:undefined, activityId:Number(activityId.value), venueId:venues.value[0]?.venueId, topicName:'',
    startTime:undefined, endTime:undefined, chairList:[], hostList:[], discussList:[], sortOrder:0, remark:''
  }
  proxy.resetForm('formRef')
}
function handleAdd(){ if(!venues.value.length){ proxy.$modal.msgWarning('请先新增会场'); return } reset(); title.value='新增主题'; open.value=true }
function handleUpdate(row){
  reset()
  getTopic(row.topicId).then(res => {
    const data = res.data || {}
    form.value = {
      ...data,
      chairList: splitNames(data.chairNames),
      hostList: splitNames(data.hostNames),
      discussList: splitNames(data.discussNames)
    }
    open.value = true
    title.value = '修改主题'
  })
}
function submit(){
  proxy.$refs.formRef.validate(v => {
    if (!v) return
    const payload = {
      ...form.value,
      chairNames: joinNames(form.value.chairList),
      hostNames: joinNames(form.value.hostList),
      discussNames: joinNames(form.value.discussList)
    }
    delete payload.chairList
    delete payload.hostList
    delete payload.discussList
    const req = payload.topicId ? updateTopic(payload) : addTopic(payload)
    req.then(() => { proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() })
  })
}
function handleDelete(row){ const topicIds=row?.topicId||ids.value; proxy.$modal.confirm('确认删除选中主题？').then(()=>delTopic(topicIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{
  if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return }
  Promise.all([loadVenues(), loadGuests()]).then(()=>{ loadStats(); getList() })
})
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>
