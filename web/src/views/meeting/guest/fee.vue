<template>
  <div class="app-container guest-page">
    <GuestTabs active="fee" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="6"><div class="summary-card"><div class="summary-label">记录数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="summary-card"><div class="summary-label">总金额</div><div class="summary-value ok">{{ stats.totalAmount || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="summary-card"><div class="summary-label">已打款</div><div class="summary-value">{{ stats.paidCount || 0 }}</div></div></el-col>
      <el-col :span="6"><div class="summary-card"><div class="summary-label">未打款</div><div class="summary-value">{{ stats.unpaidCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item>
        <el-select v-model="queryParams.guestId" clearable filterable placeholder="全部嘉宾" style="width:160px">
          <el-option v-for="g in guests" :key="g.guestId" :label="g.guestName" :value="g.guestId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.progressStatus" clearable placeholder="办理进度" style="width:120px">
          <el-option label="待确认" value="0" /><el-option label="已确认" value="1" />
          <el-option label="已签字" value="2" /><el-option label="待打款" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.payStatus" clearable placeholder="打款状态" style="width:120px">
          <el-option label="未打款" value="0" /><el-option label="已打款" value="1" /><el-option label="失败" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:fee:add']">新增</el-button>
        <el-button type="success" plain @click="handleGenerate" v-hasPermi="['meeting:fee:add']">按规则生成</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:fee:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="嘉宾" prop="guestName" min-width="110" />
      <el-table-column label="手机号" prop="guestPhone" width="120" />
      <el-table-column label="劳务费" prop="feeAmount" width="110" align="center" />
      <el-table-column label="办理进度" width="100" align="center">
        <template #default="s">{{ progressLabel(s.row.progressStatus) }}</template>
      </el-table-column>
      <el-table-column label="打款状态" width="100" align="center">
        <template #default="s"><el-tag :type="s.row.payStatus==='1'?'success':(s.row.payStatus==='2'?'danger':'info')">{{ payLabel(s.row.payStatus) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="开户行" prop="bankName" min-width="120" show-overflow-tooltip />
      <el-table-column label="账号" prop="bankAccount" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:fee:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:fee:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="嘉宾" prop="guestId">
          <el-select v-model="form.guestId" filterable placeholder="请选择嘉宾" style="width:100%">
            <el-option v-for="g in guests" :key="g.guestId" :label="g.guestName + (g.phone?' / '+g.phone:'')" :value="g.guestId" />
          </el-select>
        </el-form-item>
        <el-form-item label="劳务费" prop="feeAmount"><el-input-number v-model="form.feeAmount" :min="0" :precision="2" controls-position="right" style="width:100%" /></el-form-item>
        <el-form-item label="办理进度">
          <el-select v-model="form.progressStatus" style="width:100%">
            <el-option label="待确认" value="0" /><el-option label="已确认" value="1" />
            <el-option label="已签字" value="2" /><el-option label="待打款" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="打款状态">
          <el-select v-model="form.payStatus" style="width:100%">
            <el-option label="未打款" value="0" /><el-option label="已打款" value="1" /><el-option label="失败" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="开户行"><el-input v-model="form.bankName" /></el-form-item>
        <el-form-item label="银行账号"><el-input v-model="form.bankAccount" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingGuestFee">
import GuestTabs from './GuestTabs'
import { listFee, getFeeStats, getFee, addFee, updateFee, delFee, generateFeeFromRules } from '@/api/meeting/fee'
import { listGuest } from '@/api/meeting/guest'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), guests = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, guestId:undefined, progressStatus:undefined, payStatus:undefined })
const rules = {
  guestId: [{ required:true, message:'请选择嘉宾', trigger:'change' }],
  feeAmount: [{ required:true, message:'请输入劳务费', trigger:'blur' }]
}
function progressLabel(v){ return ({'0':'待确认','1':'已确认','2':'已签字','3':'待打款'})[v]||'-' }
function payLabel(v){ return ({'0':'未打款','1':'已打款','2':'失败'})[v]||'-' }
function loadGuests(){ return listGuest({ activityId: activityId.value, pageNum:1, pageSize:500 }).then(res => { guests.value = res.rows || [] }) }
function loadStats(){ getFeeStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listFee(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.guestId=undefined; queryParams.value.progressStatus=undefined; queryParams.value.payStatus=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.feeId); multiple.value=!s.length }
function reset(){ form.value={ feeId:undefined, activityId:Number(activityId.value), guestId:guests.value[0]?.guestId, feeAmount:0, progressStatus:'0', payStatus:'0', bankName:'', bankAccount:'', remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ if(!guests.value.length){ proxy.$modal.msgWarning('请先新增嘉宾'); return } reset(); title.value='新增劳务费'; open.value=true }
function handleGenerate(){
  proxy.$modal.confirm('将按「专家排期角色 × 启用中的任务规则」为嘉宾生成劳务费（已有记录的嘉宾会跳过）。是否继续？').then(() => {
    return generateFeeFromRules(activityId.value)
  }).then(res => {
    const data = res.data || {}
    const unmatched = (data.unmatchedNames || []).slice(0, 5).join('、')
    let msg = `已生成 ${data.createdCount || 0} 条，跳过 ${data.skippedCount || 0} 条`
    if (data.unmatchedCount) {
      msg += `；未匹配嘉宾 ${data.unmatchedCount} 人` + (unmatched ? `（如：${unmatched}）` : '')
    }
    proxy.$modal.msgSuccess(msg)
    loadStats()
    getList()
  }).catch(() => {})
}
function handleUpdate(row){ reset(); getFee(row.feeId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改劳务费' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.feeId?updateFee(form.value):addFee(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const feeIds=row?.feeId||ids.value; proxy.$modal.confirm('确认删除选中劳务费记录？').then(()=>delFee(feeIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadGuests().then(()=>{ loadStats(); getList() }) })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>
