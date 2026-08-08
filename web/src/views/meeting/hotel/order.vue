<template>
  <div class="app-container hotel-page">
    <HotelTabs active="order" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8"><div class="summary-card"><div class="summary-label">订单总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">已确认</div><div class="summary-value ok">{{ stats.confirmedCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">已取消</div><div class="summary-value">{{ stats.cancelledCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item>
        <el-select v-model="queryParams.hotelId" clearable filterable placeholder="全部酒店" style="width:160px" @change="onHotelFilter">
          <el-option v-for="h in hotels" :key="h.hotelId" :label="h.hotelName" :value="h.hotelId" />
        </el-select>
      </el-form-item>
      <el-form-item><el-input v-model="queryParams.guestName" clearable placeholder="入住人" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-input v-model="queryParams.phone" clearable placeholder="手机号" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.orderStatus" clearable placeholder="订单状态" style="width:120px">
          <el-option label="待确认" value="0" /><el-option label="已确认" value="1" /><el-option label="已取消" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:horder:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:horder:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="酒店" prop="hotelName" min-width="120" />
      <el-table-column label="房型" prop="roomName" width="110" />
      <el-table-column label="入住人" prop="guestName" width="100" />
      <el-table-column label="手机号" prop="phone" width="120" />
      <el-table-column label="入住" width="110" align="center"><template #default="s">{{ parseTime(s.row.checkInDate,'{y}-{m}-{d}')||'-' }}</template></el-table-column>
      <el-table-column label="退房" width="110" align="center"><template #default="s">{{ parseTime(s.row.checkOutDate,'{y}-{m}-{d}')||'-' }}</template></el-table-column>
      <el-table-column label="间数" prop="roomCount" width="70" align="center" />
      <el-table-column label="金额" prop="amount" width="90" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="s"><el-tag :type="statusType(s.row.orderStatus)">{{ statusLabel(s.row.orderStatus) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:horder:edit']">修改</el-button>
          <el-button
            v-if="s.row.orderStatus !== '2'"
            link
            type="success"
            @click="handleCreateAssign(s.row)"
            v-hasPermi="['meeting:assign:add']"
          >生成分房</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:horder:remove']">删除</el-button>
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
          <el-select v-model="form.roomId" clearable filterable style="width:100%" @change="onRoomChange">
            <el-option v-for="r in formRooms" :key="r.roomId" :label="r.roomName + (r.price!=null?' / ¥'+r.price:'')" :value="r.roomId" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="入住人" prop="guestName"><el-input v-model="form.guestName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="入住日期"><el-date-picker v-model="form.checkInDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="退房日期"><el-date-picker v-model="form.checkOutDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="间数"><el-input-number v-model="form.roomCount" :min="1" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="金额"><el-input-number v-model="form.amount" :min="0" :precision="2" controls-position="right" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.orderStatus" style="width:100%">
                <el-option label="待确认" value="0" /><el-option label="已确认" value="1" /><el-option label="已取消" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingHotelOrder">
import HotelTabs from './HotelTabs'
import { listHotel, listHotelRoom } from '@/api/meeting/hotel'
import { listHotelOrder, getHotelOrderStats, getHotelOrder, addHotelOrder, updateHotelOrder, delHotelOrder } from '@/api/meeting/hotelOrder'
import { createAssignFromOrder } from '@/api/meeting/hotelAssign'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), hotels = ref([]), rooms = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, hotelId:undefined, guestName:undefined, phone:undefined, orderStatus:undefined })
const formRooms = computed(() => form.value.hotelId ? rooms.value.filter(r => r.hotelId === form.value.hotelId) : rooms.value)
const rules = {
  hotelId: [{ required:true, message:'请选择酒店', trigger:'change' }],
  guestName: [{ required:true, message:'入住人不能为空', trigger:'blur' }]
}
function statusLabel(v){ return ({'0':'待确认','1':'已确认','2':'已取消'})[v]||'-' }
function statusType(v){ return ({'0':'warning','1':'success','2':'info'})[v]||'info' }
function onHotelFilter(){}
function loadOptions(){
  return Promise.all([
    listHotel({ activityId: activityId.value, pageNum:1, pageSize:200 }),
    listHotelRoom({ activityId: activityId.value, pageNum:1, pageSize:500 })
  ]).then(([hr, rr]) => { hotels.value = hr.rows || []; rooms.value = rr.rows || [] })
}
function loadStats(){ getHotelOrderStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listHotelOrder(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.hotelId=undefined; queryParams.value.guestName=undefined; queryParams.value.phone=undefined; queryParams.value.orderStatus=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.orderId); multiple.value=!s.length }
function onFormHotelChange(){ form.value.roomId = undefined }
function onRoomChange(roomId){
  const r = rooms.value.find(i => i.roomId === roomId)
  if (r && (form.value.amount == null || form.value.amount === 0)) form.value.amount = r.price || 0
}
function reset(){ form.value={ orderId:undefined, activityId:Number(activityId.value), hotelId:hotels.value[0]?.hotelId, roomId:undefined, guestName:'', phone:'', checkInDate:undefined, checkOutDate:undefined, roomCount:1, amount:0, orderStatus:'0', remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ if(!hotels.value.length){ proxy.$modal.msgWarning('请先新增酒店'); return } reset(); title.value='新增酒店订单'; open.value=true }
function handleUpdate(row){ reset(); getHotelOrder(row.orderId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改酒店订单' }) }
function handleCreateAssign(row){
  proxy.$modal.confirm('确认为该订单生成分房记录？生成后可在「分房」页补充房号。').then(() => {
    return createAssignFromOrder(row.orderId)
  }).then(() => {
    proxy.$modal.msgSuccess('已生成分房')
  }).catch(() => {})
}
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.orderId?updateHotelOrder(form.value):addHotelOrder(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const orderIds=row?.orderId||ids.value; proxy.$modal.confirm('确认删除选中订单？').then(()=>delHotelOrder(orderIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadOptions().then(()=>{ loadStats(); getList() }) })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>