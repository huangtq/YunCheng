<template>
  <div class="app-container guest-page">
    <GuestTabs active="guest" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8"><div class="summary-card"><div class="summary-label">嘉宾总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">确认参会</div><div class="summary-value ok">{{ stats.attendCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">需要酒店</div><div class="summary-value">{{ stats.hotelCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.guestName" clearable placeholder="姓名" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-input v-model="queryParams.phone" clearable placeholder="手机号" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.attendFlag" clearable placeholder="是否参会" style="width:120px">
          <el-option label="是" value="1" /><el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.needHotel" clearable placeholder="需要酒店" style="width:120px">
          <el-option label="是" value="1" /><el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:guest:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:guest:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="姓名" prop="guestName" min-width="100" />
      <el-table-column label="手机号" prop="phone" width="120" />
      <el-table-column label="单位" prop="orgName" min-width="140" show-overflow-tooltip />
      <el-table-column label="职称" prop="title" width="100" show-overflow-tooltip />
      <el-table-column label="类别" prop="guestType" width="100" />
      <el-table-column label="参会" width="70" align="center">
        <template #default="s"><el-tag :type="s.row.attendFlag==='1'?'success':'info'">{{ s.row.attendFlag==='1'?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="酒店" width="70" align="center">
        <template #default="s">{{ s.row.needHotel==='1'?'是':'否' }}</template>
      </el-table-column>
      <el-table-column label="行程" prop="tripCount" width="70" align="center" />
      <el-table-column label="劳务" prop="feeCount" width="70" align="center" />
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:guest:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:guest:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="720px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="姓名" prop="guestName"><el-input v-model="form.guestName" maxlength="100" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" maxlength="20" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="工作单位"><el-input v-model="form.orgName" maxlength="200" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="职称"><el-input v-model="form.title" maxlength="100" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="英文名"><el-input v-model="form.englishName" maxlength="100" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="专家类别"><el-input v-model="form.guestType" maxlength="50" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="身份证号"><el-input v-model="form.idCard" maxlength="30" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="是否参会"><el-switch v-model="form.attendFlag" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="需要酒店"><el-switch v-model="form.needHotel" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12" v-if="form.needHotel==='1'"><el-form-item label="入住日期"><el-date-picker v-model="form.checkInDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12" v-if="form.needHotel==='1'"><el-form-item label="退房日期"><el-date-picker v-model="form.checkOutDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="头像"><material-select v-model="form.avatar" :show-tip="false" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="简介"><el-input v-model="form.intro" type="textarea" :rows="3" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingGuest">
import GuestTabs from './GuestTabs'
import MaterialSelect from '@/components/MaterialSelect'
import { listGuest, getGuestStats, getGuest, addGuest, updateGuest, delGuest } from '@/api/meeting/guest'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, guestName:undefined, phone:undefined, attendFlag:undefined, needHotel:undefined })
const rules = { guestName: [{ required:true, message:'姓名不能为空', trigger:'blur' }] }
function loadStats(){ getGuestStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listGuest(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.guestName=undefined; queryParams.value.phone=undefined; queryParams.value.attendFlag=undefined; queryParams.value.needHotel=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.guestId); multiple.value=!s.length }
function reset(){ form.value={ guestId:undefined, activityId:Number(activityId.value), guestName:'', phone:'', orgName:'', title:'', englishName:'', guestType:'', avatar:'', intro:'', needHotel:'0', checkInDate:undefined, checkOutDate:undefined, idCard:'', attendFlag:'1', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增嘉宾'; open.value=true }
function handleUpdate(row){ reset(); getGuest(row.guestId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改嘉宾' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.guestId?updateGuest(form.value):addGuest(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const guestIds=row?.guestId||ids.value; proxy.$modal.confirm('确认删除选中嘉宾？相关行程与劳务费将一并删除。').then(()=>delGuest(guestIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>
