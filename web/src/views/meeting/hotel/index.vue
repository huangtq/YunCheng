<template>
  <div class="app-container hotel-page">
    <HotelTabs active="hotel" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">酒店总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">开售中</div><div class="summary-value ok">{{ stats.openCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.hotelName" clearable placeholder="酒店名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.saleStatus" clearable placeholder="销售状态" style="width:120px">
          <el-option label="开售" value="1" /><el-option label="关闭" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:hotel:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:hotel:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="酒店名称" prop="hotelName" min-width="140" />
      <el-table-column label="联系方式" prop="phone" width="130" />
      <el-table-column label="地址" prop="address" min-width="180" show-overflow-tooltip />
      <el-table-column label="房型数" prop="roomCount" width="80" align="center" />
      <el-table-column label="订单数" prop="orderCount" width="80" align="center" />
      <el-table-column label="销售状态" width="100" align="center">
        <template #default="s"><el-tag :type="s.row.saleStatus==='1'?'success':'info'">{{ s.row.saleStatus==='1'?'开售':'关闭' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="openRooms(s.row)" v-hasPermi="['meeting:room:list']">房型</el-button>
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:hotel:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:hotel:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="酒店名称" prop="hotelName"><el-input v-model="form.hotelName" maxlength="200" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="form.phone" maxlength="50" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" maxlength="300" /></el-form-item>
        <el-form-item label="封面图"><material-select v-model="form.coverUrl" :show-tip="false" /></el-form-item>
        <el-form-item label="销售状态"><el-switch v-model="form.saleStatus" active-value="1" inactive-value="0" active-text="开售" inactive-text="关闭" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>

    <el-drawer v-model="roomOpen" :title="'房型管理 - '+(currentHotel.hotelName||'')" size="720px" append-to-body destroy-on-close>
      <div class="mb12">
        <el-button type="primary" plain icon="Plus" @click="handleAddRoom" v-hasPermi="['meeting:room:add']">新增房型</el-button>
      </div>
      <el-table v-loading="roomLoading" :data="roomList" border>
        <el-table-column label="房型" prop="roomName" min-width="120" />
        <el-table-column label="床型" prop="bedType" width="100" />
        <el-table-column label="单价" prop="price" width="90" align="center" />
        <el-table-column label="库存" width="90" align="center">
          <template #default="s">{{ s.row.stock===0?'不限':s.row.stock }}</template>
        </el-table-column>
        <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
        <el-table-column label="操作" width="140" align="center">
          <template #default="s">
            <el-button link type="primary" @click="handleUpdateRoom(s.row)" v-hasPermi="['meeting:room:edit']">修改</el-button>
            <el-button link type="danger" @click="handleDeleteRoom(s.row)" v-hasPermi="['meeting:room:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <el-dialog :title="roomTitle" v-model="roomFormOpen" width="520px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="roomFormRef" :model="roomForm" :rules="roomRules" label-width="90px">
        <el-form-item label="房型名称" prop="roomName"><el-input v-model="roomForm.roomName" maxlength="100" /></el-form-item>
        <el-form-item label="床型"><el-input v-model="roomForm.bedType" placeholder="如：大床/双床" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="roomForm.price" :min="0" :precision="2" controls-position="right" style="width:100%" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="roomForm.stock" :min="0" controls-position="right" /><span class="tip">0 表示不限</span></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="roomForm.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="roomForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submitRoom">确 定</el-button><el-button @click="roomFormOpen=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingHotel">
import HotelTabs from './HotelTabs'
import MaterialSelect from '@/components/MaterialSelect'
import { listHotel, getHotelStats, getHotel, addHotel, updateHotel, delHotel, listHotelRoom, getHotelRoom, addHotelRoom, updateHotelRoom, delHotelRoom } from '@/api/meeting/hotel'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, hotelName:undefined, saleStatus:undefined })
const rules = { hotelName: [{ required:true, message:'酒店名称不能为空', trigger:'blur' }] }
const roomOpen = ref(false), roomLoading = ref(false), roomList = ref([]), currentHotel = ref({})
const roomFormOpen = ref(false), roomTitle = ref(''), roomForm = ref({})
const roomRules = { roomName: [{ required:true, message:'房型名称不能为空', trigger:'blur' }] }
function loadStats(){ getHotelStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listHotel(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.hotelName=undefined; queryParams.value.saleStatus=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.hotelId); multiple.value=!s.length }
function reset(){ form.value={ hotelId:undefined, activityId:Number(activityId.value), hotelName:'', phone:'', address:'', coverUrl:'', saleStatus:'1', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增酒店'; open.value=true }
function handleUpdate(row){ reset(); getHotel(row.hotelId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改酒店' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.hotelId?updateHotel(form.value):addHotel(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const hotelIds=row?.hotelId||ids.value; proxy.$modal.confirm('确认删除选中酒店？').then(()=>delHotel(hotelIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
function openRooms(row){ currentHotel.value=row; roomOpen.value=true; loadRooms() }
function loadRooms(){ roomLoading.value=true; listHotelRoom({ activityId:activityId.value, hotelId:currentHotel.value.hotelId, pageNum:1, pageSize:200 }).then(res=>{ roomList.value=res.rows||[]; roomLoading.value=false }).catch(()=>roomLoading.value=false) }
function resetRoom(){ roomForm.value={ roomId:undefined, activityId:Number(activityId.value), hotelId:currentHotel.value.hotelId, roomName:'', bedType:'', price:0, stock:0, sortOrder:0, remark:'' }; proxy.resetForm('roomFormRef') }
function handleAddRoom(){ resetRoom(); roomTitle.value='新增房型'; roomFormOpen.value=true }
function handleUpdateRoom(row){ resetRoom(); getHotelRoom(row.roomId).then(res=>{ roomForm.value={...res.data}; roomFormOpen.value=true; roomTitle.value='修改房型' }) }
function submitRoom(){ proxy.$refs.roomFormRef.validate(v=>{ if(!v) return; const req=roomForm.value.roomId?updateHotelRoom(roomForm.value):addHotelRoom(roomForm.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); roomFormOpen.value=false; loadRooms(); getList() }) }) }
function handleDeleteRoom(row){ proxy.$modal.confirm('确认删除该房型？').then(()=>delHotelRoom(row.roomId)).then(()=>{ loadRooms(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}.mb12{margin-bottom:12px}.tip{margin-left:8px;color:#909399;font-size:12px}
</style>
