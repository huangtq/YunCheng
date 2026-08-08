<template>
  <div class="app-container guest-page">
    <GuestTabs active="trip" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="6"><div class="summary-card"><div class="summary-label">行程总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="summary-card"><div class="summary-label">来程</div><div class="summary-value ok">{{ stats.comeCount || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="summary-card"><div class="summary-label">返程</div><div class="summary-value">{{ stats.returnCount || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="summary-card"><div class="summary-label">已出票</div><div class="summary-value">{{ stats.issuedCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item>
        <el-select v-model="queryParams.guestId" clearable filterable placeholder="全部嘉宾" style="width:160px">
          <el-option v-for="g in guests" :key="g.guestId" :label="g.guestName" :value="g.guestId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.tripType" clearable placeholder="来/返程" style="width:120px">
          <el-option label="来程" value="come" /><el-option label="返程" value="return" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.ticketStatus" clearable placeholder="出票状态" style="width:120px">
          <el-option label="未出票" value="0" /><el-option label="已出票" value="1" /><el-option label="已取消" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:trip:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:trip:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="嘉宾" prop="guestName" min-width="100" />
      <el-table-column label="类型" width="80" align="center">
        <template #default="s">{{ s.row.tripType==='return'?'返程':'来程' }}</template>
      </el-table-column>
      <el-table-column label="交通" width="80" align="center">
        <template #default="s">{{ transportLabel(s.row.transportType) }}</template>
      </el-table-column>
      <el-table-column label="班次" prop="transportNo" width="100" />
      <el-table-column label="日期" width="110" align="center"><template #default="s">{{ parseTime(s.row.tripDate, '{y}-{m}-{d}') || '-' }}</template></el-table-column>
      <el-table-column label="出发-到达" min-width="140">
        <template #default="s">{{ (s.row.fromPlace||'-') + ' → ' + (s.row.toPlace||'-') }}</template>
      </el-table-column>
      <el-table-column label="出票" width="90" align="center">
        <template #default="s"><el-tag :type="s.row.ticketStatus==='1'?'success':(s.row.ticketStatus==='2'?'info':'warning')">{{ ticketLabel(s.row.ticketStatus) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="接送" width="80" align="center">
        <template #default="s">{{ s.row.pickupStatus==='1'?'已确认':'未确认' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:trip:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:trip:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="720px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="嘉宾" prop="guestId">
          <el-select v-model="form.guestId" filterable placeholder="请选择嘉宾" style="width:100%">
            <el-option v-for="g in guests" :key="g.guestId" :label="g.guestName + (g.phone?' / '+g.phone:'')" :value="g.guestId" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="行程类型">
              <el-radio-group v-model="form.tripType"><el-radio value="come">来程</el-radio><el-radio value="return">返程</el-radio></el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交通工具">
              <el-select v-model="form.transportType" style="width:100%">
                <el-option label="飞机" value="plane" /><el-option label="火车" value="train" />
                <el-option label="汽车" value="car" /><el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="班次"><el-input v-model="form.transportNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="日期"><el-date-picker v-model="form.tripDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出发地"><el-input v-model="form.fromPlace" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="到达地"><el-input v-model="form.toPlace" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出发时间"><el-date-picker v-model="form.departTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="到达时间"><el-date-picker v-model="form.arriveTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="出票状态">
              <el-select v-model="form.ticketStatus" style="width:100%">
                <el-option label="未出票" value="0" /><el-option label="已出票" value="1" /><el-option label="已取消" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="接送确认"><el-switch v-model="form.pickupStatus" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingGuestTrip">
import GuestTabs from './GuestTabs'
import { listTrip, getTripStats, getTrip, addTrip, updateTrip, delTrip } from '@/api/meeting/trip'
import { listGuest } from '@/api/meeting/guest'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), guests = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, guestId:undefined, tripType:undefined, ticketStatus:undefined })
const rules = { guestId: [{ required:true, message:'请选择嘉宾', trigger:'change' }] }
function transportLabel(v){ return ({plane:'飞机',train:'火车',car:'汽车',other:'其他'})[v]||v||'-' }
function ticketLabel(v){ return ({'0':'未出票','1':'已出票','2':'已取消'})[v]||'-' }
function loadGuests(){ return listGuest({ activityId: activityId.value, pageNum:1, pageSize:500 }).then(res => { guests.value = res.rows || [] }) }
function loadStats(){ getTripStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listTrip(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.guestId=undefined; queryParams.value.tripType=undefined; queryParams.value.ticketStatus=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.tripId); multiple.value=!s.length }
function reset(){ form.value={ tripId:undefined, activityId:Number(activityId.value), guestId:guests.value[0]?.guestId, tripType:'come', transportType:'plane', transportNo:'', tripDate:undefined, fromPlace:'', toPlace:'', departTime:undefined, arriveTime:undefined, price:0, ticketStatus:'0', pickupStatus:'0', remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ if(!guests.value.length){ proxy.$modal.msgWarning('请先新增嘉宾'); return } reset(); title.value='新增行程'; open.value=true }
function handleUpdate(row){ reset(); getTrip(row.tripId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改行程' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.tripId?updateTrip(form.value):addTrip(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const tripIds=row?.tripId||ids.value; proxy.$modal.confirm('确认删除选中行程？').then(()=>delTrip(tripIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadGuests().then(()=>{ loadStats(); getList() }) })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>