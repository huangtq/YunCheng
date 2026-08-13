<template>
  <div class="app-container feature-page">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8"><div class="summary-card"><div class="summary-label">餐票总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">启用中</div><div class="summary-value ok">{{ stats.enabledCount || 0 }}</div></div></el-col>
      <el-col :span="8"><div class="summary-card"><div class="summary-label">已核销</div><div class="summary-value">{{ stats.usedCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.ticketName" clearable placeholder="餐票名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.mealType" clearable placeholder="餐次" style="width:120px">
          <el-option label="早餐" value="breakfast" /><el-option label="午餐" value="lunch" />
          <el-option label="晚餐" value="dinner" /><el-option label="茶歇" value="tea" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:meal:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:meal:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="餐票名称" prop="ticketName" min-width="140" />
      <el-table-column label="餐次" width="90" align="center">
        <template #default="s">{{ mealLabel(s.row.mealType) }}</template>
      </el-table-column>
      <el-table-column label="用餐日期" width="120" align="center">
        <template #default="s">{{ parseTime(s.row.mealDate, '{y}-{m}-{d}') || '-' }}</template>
      </el-table-column>
      <el-table-column label="名额" width="90" align="center">
        <template #default="s">{{ s.row.totalQuota === 0 ? '不限' : s.row.totalQuota }}</template>
      </el-table-column>
      <el-table-column label="已核销" prop="usedCount" width="90" align="center" />
      <el-table-column label="启用" width="90" align="center">
        <template #default="s"><el-tag :type="s.row.enabled==='1'?'success':'info'">{{ s.row.enabled==='1'?'启用':'停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:meal:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:meal:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="560px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="餐票名称" prop="ticketName"><el-input v-model="form.ticketName" maxlength="100" /></el-form-item>
        <el-form-item label="餐次" prop="mealType">
          <el-select v-model="form.mealType" style="width:100%">
            <el-option label="早餐" value="breakfast" /><el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" /><el-option label="茶歇" value="tea" />
          </el-select>
        </el-form-item>
        <el-form-item label="用餐日期"><el-date-picker v-model="form.mealDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="名额"><el-input-number v-model="form.totalQuota" :min="0" controls-position="right" /><span class="tip">0 表示不限</span></el-form-item>
        <el-form-item label="已核销"><el-input-number v-model="form.usedCount" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" active-value="1" inactive-value="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingMeal">
import { listMeal, getMealStats, getMeal, addMeal, updateMeal, delMeal } from '@/api/meeting/meal'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, ticketName:undefined, mealType:undefined, enabled:undefined })
const rules = {
  ticketName: [{ required:true, message:'餐票名称不能为空', trigger:'blur' }],
  mealType: [{ required:true, message:'请选择餐次', trigger:'change' }]
}
function mealLabel(v){ return ({breakfast:'早餐',lunch:'午餐',dinner:'晚餐',tea:'茶歇'})[v]||v||'-' }
function loadStats(){ getMealStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listMeal(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.ticketName=undefined; queryParams.value.mealType=undefined; queryParams.value.enabled=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.ticketId); multiple.value=!s.length }
function reset(){ form.value={ ticketId:undefined, activityId:Number(activityId.value), ticketName:'', mealType:'lunch', mealDate:undefined, totalQuota:0, usedCount:0, enabled:'1', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增餐票'; open.value=true }
function handleUpdate(row){ reset(); getMeal(row.ticketId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改餐票' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.ticketId?updateMeal(form.value):addMeal(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const ticketIds=row?.ticketId||ids.value; proxy.$modal.confirm('确认删除选中餐票？').then(()=>delMeal(ticketIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}.tip{margin-left:8px;color:#909399;font-size:12px}
</style>
