<template>
  <div class="app-container">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">底部项数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">启用中</div><div class="summary-value ok">{{ stats.enabledCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.bottomName" clearable placeholder="名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.bottomType" clearable placeholder="类型" style="width:120px">
          <el-option label="链接" value="link" /><el-option label="模块" value="module" />
          <el-option label="电话" value="phone" /><el-option label="文本" value="text" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.status" clearable placeholder="状态" style="width:100px">
          <el-option label="启用" value="1" /><el-option label="停用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:bottom:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:bottom:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="类型" width="90" align="center">
        <template #default="s">{{ typeLabel(s.row.bottomType) }}</template>
      </el-table-column>
      <el-table-column label="名称" prop="bottomName" min-width="140" />
      <el-table-column label="链接/模块/电话" min-width="180" show-overflow-tooltip>
        <template #default="s">
          <span v-if="s.row.bottomType==='module'">{{ moduleLabel(s.row.moduleKey) }}</span>
          <span v-else-if="s.row.bottomType==='phone'">{{ s.row.phone || '-' }}</span>
          <span v-else>{{ s.row.linkUrl || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="s"><el-tag :type="s.row.status==='1'?'success':'info'">{{ s.row.status==='1'?'启用':'停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:bottom:edit']">修改</el-button>
          <el-button
            v-if="s.row.bottomType==='module' && getMeetingModule(s.row.moduleKey)"
            link
            type="success"
            @click="goModuleConfig(s.row.moduleKey)"
          >去配置</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:bottom:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="bottomName"><el-input v-model="form.bottomName" maxlength="100" /></el-form-item>
        <el-form-item label="类型" prop="bottomType">
          <el-radio-group v-model="form.bottomType">
            <el-radio value="link">链接</el-radio>
            <el-radio value="module">模块</el-radio>
            <el-radio value="phone">电话</el-radio>
            <el-radio value="text">文本</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.bottomType==='link'" label="链接"><el-input v-model="form.linkUrl" placeholder="https://" /></el-form-item>
        <el-form-item v-if="form.bottomType==='module'" label="模块">
          <el-select v-model="form.moduleKey" style="width:100%" @change="onModuleChange">
            <el-option v-for="m in moduleOptions" :key="m.value" :label="m.label" :value="m.value" />
          </el-select>
          <div v-if="selectedModule" class="module-tip">
            <span>{{ selectedModule.desc }}</span>
            <el-button link type="primary" @click="goModuleConfig(form.moduleKey)">去配置该模块数据</el-button>
          </div>
        </el-form-item>
        <el-form-item v-if="form.bottomType==='phone'" label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="图标"><material-select v-model="form.iconUrl" :show-tip="false" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" active-value="1" inactive-value="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingGridBottom">
import MaterialSelect from '@/components/MaterialSelect'
import { listBottom, getBottomStats, getBottom, addBottom, updateBottom, delBottom } from '@/api/meeting/bottom'
import { MEETING_MODULE_OPTIONS, getMeetingModule, meetingModuleLabel } from '@/utils/meetingModules'
const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, bottomName:undefined, bottomType:undefined, status:undefined })
const moduleOptions = MEETING_MODULE_OPTIONS
const selectedModule = computed(() => getMeetingModule(form.value.moduleKey))
const rules = {
  bottomName: [{ required:true, message:'名称不能为空', trigger:'blur' }],
  bottomType: [{ required:true, message:'请选择类型', trigger:'change' }]
}
function typeLabel(v){ return ({link:'链接',module:'模块',phone:'电话',text:'文本'})[v]||v||'-' }
function moduleLabel(v){ return meetingModuleLabel(v) }
function goModuleConfig(key){
  const mod = getMeetingModule(key)
  if (!mod?.adminPath) return
  router.push({ path: mod.adminPath, query: { id: activityId.value } })
}
function onModuleChange(key){
  const mod = getMeetingModule(key)
  if (mod && !form.value.bottomName) form.value.bottomName = mod.label
}
function loadStats(){ getBottomStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listBottom(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.bottomName=undefined; queryParams.value.bottomType=undefined; queryParams.value.status=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.bottomId); multiple.value=!s.length }
function reset(){ form.value={ bottomId:undefined, activityId:Number(activityId.value), bottomName:'', bottomType:'link', linkUrl:'', moduleKey:'schedule', phone:'', iconUrl:'', sortOrder:0, status:'1', remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增底部项'; open.value=true }
function handleUpdate(row){ reset(); getBottom(row.bottomId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改底部项' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.bottomId?updateBottom(form.value):addBottom(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const bottomIds=row?.bottomId||ids.value; proxy.$modal.confirm('确认删除选中底部项？').then(()=>delBottom(bottomIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
.module-tip{display:flex;align-items:center;gap:8px;margin-top:8px;color:#909399;font-size:13px}
</style>
