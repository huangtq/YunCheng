<template>
  <div class="app-container guest-page">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="8"><div class="summary-card"><div class="summary-label">工作人员数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.staffName" clearable placeholder="姓名" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-input v-model="queryParams.phone" clearable placeholder="手机号" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-input v-model="queryParams.roleName" clearable placeholder="岗位" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:staff:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:staff:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="姓名" prop="staffName" min-width="120" />
      <el-table-column label="手机号" prop="phone" width="140" />
      <el-table-column label="岗位" prop="roleName" min-width="140" />
      <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:staff:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:staff:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="520px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="staffName"><el-input v-model="form.staffName" maxlength="100" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" maxlength="20" /></el-form-item>
        <el-form-item label="岗位"><el-input v-model="form.roleName" maxlength="100" placeholder="如：会务、接待、现场" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingStaff">
import { listStaff, getStaffStats, getStaff, addStaff, updateStaff, delStaff } from '@/api/meeting/staff'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, staffName:undefined, phone:undefined, roleName:undefined })
const rules = { staffName: [{ required:true, message:'姓名不能为空', trigger:'blur' }] }
function loadStats(){ getStaffStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listStaff(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.staffName=undefined; queryParams.value.phone=undefined; queryParams.value.roleName=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.staffId); multiple.value=!s.length }
function reset(){ form.value={ staffId:undefined, activityId:Number(activityId.value), staffName:'', phone:'', roleName:'', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增工作人员'; open.value=true }
function handleUpdate(row){ reset(); getStaff(row.staffId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改工作人员' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.staffId?updateStaff(form.value):addStaff(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const staffIds=row?.staffId||ids.value; proxy.$modal.confirm('确认删除选中工作人员？').then(()=>delStaff(staffIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}
</style>
