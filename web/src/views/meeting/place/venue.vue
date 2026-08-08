<template>
  <div class="app-container place-page">
    <PlaceTabs active="venue" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8"><div class="summary-card"><div class="summary-label">总会场数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">直播会场</div><div class="summary-value ok">{{ stats.liveCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">非直播会场</div><div class="summary-value">{{ stats.normalCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.venueName" clearable placeholder="会场名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.isLive" clearable placeholder="是否直播" style="width: 120px">
          <el-option label="是" value="1" /><el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:venue:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:venue:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="会场名称" prop="venueName" min-width="140" />
      <el-table-column label="是否直播" width="90" align="center">
        <template #default="s"><el-tag :type="s.row.isLive==='1'?'success':'info'">{{ s.row.isLive==='1'?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="直播状态" width="100" align="center">
        <template #default="s"><span>{{ liveStatusLabel(s.row.liveStatus) }}</span></template>
      </el-table-column>
      <el-table-column label="主题数" prop="topicCount" width="80" align="center" />
      <el-table-column label="日程数" prop="scheduleCount" width="80" align="center" />
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:venue:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:venue:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="会场名称" prop="venueName"><el-input v-model="form.venueName" maxlength="100" /></el-form-item>
        <el-form-item label="是否直播"><el-switch v-model="form.isLive" active-value="1" inactive-value="0" /></el-form-item>
        <el-form-item v-if="form.isLive==='1'" label="直播状态">
          <el-select v-model="form.liveStatus" style="width:100%"><el-option label="预告" value="0" /><el-option label="直播中" value="1" /><el-option label="录播" value="2" /></el-select>
        </el-form-item>
        <el-form-item v-if="form.isLive==='1'" label="直播开始"><el-date-picker v-model="form.liveStart" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item v-if="form.isLive==='1'" label="直播结束"><el-date-picker v-model="form.liveEnd" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="封面地址"><el-input v-model="form.coverUrl" placeholder="图片URL，可选" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingVenue">
import PlaceTabs from './PlaceTabs'
import { listVenue, getVenueStats, getVenue, addVenue, updateVenue, delVenue } from '@/api/meeting/venue'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, venueName:undefined, isLive:undefined })
const rules = { venueName: [{ required:true, message:'会场名称不能为空', trigger:'blur' }] }
function liveStatusLabel(v){ return ({'0':'预告','1':'直播中','2':'录播'})[v] || '-' }
function loadStats(){ getVenueStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listVenue(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.venueName=undefined; queryParams.value.isLive=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.venueId); multiple.value=!s.length }
function reset(){ form.value={ venueId:undefined, activityId:Number(activityId.value), venueName:'', isLive:'0', coverUrl:'', liveStart:undefined, liveEnd:undefined, liveStatus:'0', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增会场'; open.value=true }
function handleUpdate(row){ reset(); getVenue(row.venueId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改会场' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.venueId?updateVenue(form.value):addVenue(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const venueIds=row?.venueId||ids.value; proxy.$modal.confirm('确认删除选中会场？').then(()=>delVenue(venueIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>