<template>
  <div class="app-container place-page">
    <PlaceTabs active="schedule" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">总日程数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">总时长(分钟)</div><div class="summary-value ok">{{ stats.totalDuration || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item>
        <el-select v-model="queryParams.venueId" clearable placeholder="全部会场" style="width:160px" @change="onVenueFilter">
          <el-option v-for="v in venues" :key="v.venueId" :label="v.venueName" :value="v.venueId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.topicId" clearable placeholder="全部主题" style="width:180px">
          <el-option v-for="t in filteredTopics" :key="t.topicId" :label="t.topicName" :value="t.topicId" />
        </el-select>
      </el-form-item>
      <el-form-item><el-input v-model="queryParams.scheduleName" clearable placeholder="日程名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:schedule:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:schedule:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="会场" prop="venueName" min-width="110" />
      <el-table-column label="主题" prop="topicName" min-width="130" />
      <el-table-column label="日程名称" prop="scheduleName" min-width="150" />
      <el-table-column label="日期" width="110" align="center"><template #default="s">{{ parseTime(s.row.scheduleDate, '{y}-{m}-{d}') || '-' }}</template></el-table-column>
      <el-table-column label="开始" width="160" align="center"><template #default="s">{{ parseTime(s.row.startTime) || '-' }}</template></el-table-column>
      <el-table-column label="结束" width="160" align="center"><template #default="s">{{ parseTime(s.row.endTime) || '-' }}</template></el-table-column>
      <el-table-column label="时长" prop="durationMin" width="70" align="center" />
      <el-table-column label="讲者" prop="speakerNames" min-width="110" show-overflow-tooltip />
      <el-table-column label="主持" prop="hostNames" min-width="100" show-overflow-tooltip />
      <el-table-column label="讨论" prop="discussNames" min-width="100" show-overflow-tooltip />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:schedule:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:schedule:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="720px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="会议主题" prop="topicId">
          <el-select v-model="form.topicId" placeholder="请选择主题" filterable style="width:100%">
            <el-option v-for="t in topics" :key="t.topicId" :label="(t.venueName ? t.venueName + ' / ' : '') + t.topicName" :value="t.topicId" />
          </el-select>
        </el-form-item>
        <el-form-item label="日程名称" prop="scheduleName"><el-input v-model="form.scheduleName" maxlength="200" /></el-form-item>
        <el-form-item label="会议日期"><el-date-picker v-model="form.scheduleDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" @change="calcDuration" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" @change="calcDuration" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="form.durationMin" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="讲者">
          <el-select v-model="form.speakerList" multiple filterable allow-create default-first-option collapse-tags collapse-tags-tooltip placeholder="从嘉宾库选择，可手动输入" style="width:100%">
            <el-option v-for="g in guests" :key="'s-'+g.guestId" :label="guestOptionLabel(g)" :value="g.guestName" />
          </el-select>
        </el-form-item>
        <el-form-item label="主持">
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
<script setup name="MeetingSchedule">
import PlaceTabs from './PlaceTabs'
import { listSchedule, getScheduleStats, getSchedule, addSchedule, updateSchedule, delSchedule } from '@/api/meeting/schedule'
import { listTopic } from '@/api/meeting/topic'
import { listVenue } from '@/api/meeting/venue'
import { listGuest } from '@/api/meeting/guest'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), venues = ref([]), topics = ref([]), guests = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, venueId:undefined, topicId:undefined, scheduleName:undefined })
const filteredTopics = computed(() => queryParams.value.venueId ? topics.value.filter(t => t.venueId === queryParams.value.venueId) : topics.value)
const rules = {
  topicId: [{ required:true, message:'请选择主题', trigger:'change' }],
  scheduleName: [{ required:true, message:'日程名称不能为空', trigger:'blur' }]
}
function guestOptionLabel(g){ return g.guestName + (g.orgName ? ' / ' + g.orgName : '') + (g.phone ? ' / ' + g.phone : '') }
function splitNames(v){ return String(v || '').split(/[,，、;/；]/).map(s => s.trim()).filter(Boolean) }
function joinNames(arr){ return (arr || []).map(s => String(s).trim()).filter(Boolean).join(',') }
function onVenueFilter(){ queryParams.value.topicId = undefined }
function calcDuration(){
  if (!form.value.startTime || !form.value.endTime) return
  const a = new Date(form.value.startTime.replace(/-/g,'/')).getTime()
  const b = new Date(form.value.endTime.replace(/-/g,'/')).getTime()
  if (b > a) form.value.durationMin = Math.round((b - a) / 60000)
}
function loadOptions(){
  return Promise.all([
    listVenue({ activityId: activityId.value, pageNum:1, pageSize:200 }),
    listTopic({ activityId: activityId.value, pageNum:1, pageSize:500 }),
    listGuest({ activityId: activityId.value, pageNum:1, pageSize:500 })
  ]).then(([vr, tr, gr]) => {
    venues.value = vr.rows || []
    topics.value = tr.rows || []
    guests.value = gr.rows || []
  })
}
function loadStats(){ getScheduleStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listSchedule(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.venueId=undefined; queryParams.value.topicId=undefined; queryParams.value.scheduleName=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.scheduleId); multiple.value=!s.length }
function reset(){
  form.value={
    scheduleId:undefined, activityId:Number(activityId.value), topicId:topics.value[0]?.topicId, scheduleName:'',
    scheduleDate:undefined, startTime:undefined, endTime:undefined, durationMin:0,
    speakerList:[], hostList:[], discussList:[], sortOrder:0, remark:''
  }
  proxy.resetForm('formRef')
}
function handleAdd(){ if(!topics.value.length){ proxy.$modal.msgWarning('请先新增主题'); return } reset(); title.value='新增日程'; open.value=true }
function handleUpdate(row){
  reset()
  getSchedule(row.scheduleId).then(res => {
    const data = res.data || {}
    form.value = {
      ...data,
      speakerList: splitNames(data.speakerNames),
      hostList: splitNames(data.hostNames),
      discussList: splitNames(data.discussNames)
    }
    open.value = true
    title.value = '修改日程'
  })
}
function submit(){
  proxy.$refs.formRef.validate(v => {
    if (!v) return
    const payload = {
      ...form.value,
      speakerNames: joinNames(form.value.speakerList),
      hostNames: joinNames(form.value.hostList),
      discussNames: joinNames(form.value.discussList)
    }
    delete payload.speakerList
    delete payload.hostList
    delete payload.discussList
    const req = payload.scheduleId ? updateSchedule(payload) : addSchedule(payload)
    req.then(() => { proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() })
  })
}
function handleDelete(row){ const scheduleIds=row?.scheduleId||ids.value; proxy.$modal.confirm('确认删除选中日程？').then(()=>delSchedule(scheduleIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadOptions().then(()=>{ loadStats(); getList() }) })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>
