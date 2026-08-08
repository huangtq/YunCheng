<template>
  <div class="app-container guest-page">
    <GuestTabs active="rule" />
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">规则总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">启用中</div><div class="summary-value ok">{{ stats.enabledCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.ruleName" clearable placeholder="规则名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.roleType" clearable placeholder="角色类型" style="width:140px">
          <el-option label="主席" value="chair" /><el-option label="主持" value="host" />
          <el-option label="讲者" value="speaker" /><el-option label="讨论" value="discuss" /><el-option label="自定义" value="custom" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.enabled" clearable placeholder="状态" style="width:100px">
          <el-option label="启用" value="1" /><el-option label="停用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:rule:add']">新增</el-button>
        <el-button type="success" plain @click="handleGenerateFee" v-hasPermi="['meeting:fee:add']">生成劳务费</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:rule:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="规则名称" prop="ruleName" min-width="140" />
      <el-table-column label="角色" width="100" align="center">
        <template #default="s">{{ roleLabel(s.row.roleType) }}</template>
      </el-table-column>
      <el-table-column label="默认劳务费" prop="feeAmount" width="120" align="center" />
      <el-table-column label="启用" width="90" align="center">
        <template #default="s"><el-tag :type="s.row.enabled==='1'?'success':'info'">{{ s.row.enabled==='1'?'启用':'停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:rule:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:rule:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="规则名称" prop="ruleName"><el-input v-model="form.ruleName" maxlength="100" /></el-form-item>
        <el-form-item label="角色类型" prop="roleType">
          <el-select v-model="form.roleType" style="width:100%">
            <el-option label="主席" value="chair" /><el-option label="主持" value="host" />
            <el-option label="讲者" value="speaker" /><el-option label="讨论" value="discuss" /><el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认劳务费"><el-input-number v-model="form.feeAmount" :min="0" :precision="2" controls-position="right" style="width:100%" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" active-value="1" inactive-value="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingGuestRule">
import GuestTabs from './GuestTabs'
import { listRule, getRuleStats, getRule, addRule, updateRule, delRule } from '@/api/meeting/rule'
import { generateFeeFromRules } from '@/api/meeting/fee'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, ruleName:undefined, roleType:undefined, enabled:undefined })
const rules = {
  ruleName: [{ required:true, message:'规则名称不能为空', trigger:'blur' }],
  roleType: [{ required:true, message:'请选择角色类型', trigger:'change' }]
}
function roleLabel(v){ return ({chair:'主席',host:'主持',speaker:'讲者',discuss:'讨论',custom:'自定义'})[v]||v||'-' }
function loadStats(){ getRuleStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listRule(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.ruleName=undefined; queryParams.value.roleType=undefined; queryParams.value.enabled=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.ruleId); multiple.value=!s.length }
function reset(){ form.value={ ruleId:undefined, activityId:Number(activityId.value), ruleName:'', roleType:'speaker', feeAmount:0, enabled:'1', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增任务规则'; open.value=true }
function handleGenerateFee(){
  proxy.$modal.confirm('将按「专家排期角色 × 启用中的任务规则」为嘉宾生成劳务费（已有记录的嘉宾会跳过）。是否继续？').then(() => {
    return generateFeeFromRules(activityId.value)
  }).then(res => {
    const data = res.data || {}
    proxy.$modal.msgSuccess(`已生成 ${data.createdCount || 0} 条，跳过 ${data.skippedCount || 0} 条`)
  }).catch(() => {})
}
function handleUpdate(row){ reset(); getRule(row.ruleId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改任务规则' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.ruleId?updateRule(form.value):addRule(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const ruleIds=row?.ruleId||ids.value; proxy.$modal.confirm('确认删除选中规则？').then(()=>delRule(ruleIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>