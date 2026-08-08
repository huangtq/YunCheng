<template>
  <div class="app-container hotel-page">
    <HotelTabs active="assign" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8"><div class="summary-card"><div class="summary-label">分房总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">已入住</div><div class="summary-value ok">{{ stats.checkedInCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">已退房</div><div class="summary-value">{{ stats.checkedOutCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item>
        <el-select v-model="queryParams.hotelId" clearable filterable placeholder="全部酒店" style="width:160px">
          <el-option v-for="h in hotels" :key="h.hotelId" :label="h.hotelName" :value="h.hotelId" />
        </el-select>
      </el-form-item>
      <el-form-item><el-input v-model="queryParams.guestName" clearable placeholder="入住人" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-input v-model="queryParams.roomNumber" clearable placeholder="房号" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.assignStatus" clearable placeholder="分房状态" style="width:120px">
          <el-option label="未入住" value="0" /><el-option label="已入住" value="1" /><el-option label="已退房" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:assign:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:assign:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="酒店" prop="hotelName" min-width="120" />
      <el-table-column label="房型" prop="roomName" width="110" />
      <el-table-column label="房号" prop="roomNumber" width="90" />
      <el-table-column label="入住人" prop="guestName" width="100" />
      <el-table-column label="手机号" prop="phone" width="120" />
      <el-table-column label="入住" width="110" align="center"><template #default="s">{{ parseTime(s.row.checkInDate,'{y}-{m}-{d}')||'-' }}</template></el-table-column>
      <el-table-column label="退房" width="110" align="center"><template #default="s">{{ parseTime(s.row.checkOutDate,'{y}-{m}-{d}')||'-' }}</template></el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="s"><el-tag :type="statusType(s.row.assignStatus)">{{ statusLabel(s.row.assignStatus) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:assign:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:assign:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="酒店" prop="hotelId">
          <el-select v-model="form.hotelId" filterable style="width:100%" @change="onFormHotelChange">
            <el-option v-for="h in hotels" :key="h.hotelId" :label="h.hotelName" :value="h.hotelId" />
          </el-select>
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="form.roomId" clearable filterable style="width:100%">
            <el-option v-for="r in formRooms" :key="r.roomId" :label="r.roomName" :value="r.roomId" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="房号" prop="roomNumber"><el-input v-model="form.roomNumber" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入住人" prop="guestName"><el-input v-model="form.guestName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.assignStatus" style="width:100%">
                <el-option label="未入住" value="0" /><el-option label="已入住" value="1" /><el-option label="已退房" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="入住日期"><el-date-picker v-model="form.checkInDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="退房日期"><el-date-picker v-model="form.checkOutDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingHotelAssign">
import HotelTabs from './HotelTabs'
import { listHotel, listHotelRoom } from '@/api/meeting/hotel'
import { listHotelAssign, getHotelAssignStats, getHotelAssign, addHotelAssign, updateHotelAssign, delHotelAssign } from '@/api/meeting/hotelAssign'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), hotels = ref([]), rooms = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, hotelId:undefined, guestName:undefined, roomNumber:undefined, assignStatus:undefined })
const formRooms = computed(() => form.value.hotelId ? rooms.value.filter(r => r.hotelId === form.value.hotelId) : rooms.value)
const rules = {
  hotelId: [{ required:true, message:'请选择酒店', trigger:'change' }],
  guestName: [{ required:true, message:'入住人不能为空', trigger:'blur' }],
  roomNumber: [{ required:true, message:'房号不能为空', trigger:'blur' }]
}
function statusLabel(v){ return ({'0':'未入住','1':'已入住','2':'已退房'})[v]||'-' }
function statusType(v){ return ({'0':'info','1':'success','2':'warning'})[v]||'info' }
function loadOptions(){
  return Promise.all([
    listHotel({ activityId: activityId.value, pageNum:1, pageSize:200 }),
    listHotelRoom({ activityId: activityId.value, pageNum:1, pageSize:500 })
  ]).then(([hr, rr]) => { hotels.value = hr.rows || []; rooms.value = rr.rows || [] })
}
function loadStats(){ getHotelAssignStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listHotelAssign(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.hotelId=undefined; queryParams.value.guestName=undefined; queryParams.value.roomNumber=undefined; queryParams.value.assignStatus=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.assignId); multiple.value=!s.length }
function onFormHotelChange(){ form.value.roomId = undefined }
function reset(){ form.value={ assignId:undefined, activityId:Number(activityId.value), hotelId:hotels.value[0]?.hotelId, orderId:undefined, roomId:undefined, guestName:'', phone:'', roomNumber:'', checkInDate:undefined, checkOutDate:undefined, assignStatus:'0', remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ if(!hotels.value.length){ proxy.$modal.msgWarning('请先新增酒店'); return } reset(); title.value='新增分房'; open.value=true }
function handleUpdate(row){ reset(); getHotelAssign(row.assignId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改分房' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.assignId?updateHotelAssign(form.value):addHotelAssign(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const assignIds=row?.assignId||ids.value; proxy.$modal.confirm('确认删除选中分房记录？').then(()=>delHotelAssign(assignIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadOptions().then(()=>{ loadStats(); getList() }) })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>