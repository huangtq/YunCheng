<template>
  <div class="app-container">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">导航点数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">启用中</div><div class="summary-value ok">{{ stats.enabledCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.title" clearable placeholder="标题" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.status" clearable placeholder="状态" style="width:100px">
          <el-option label="启用" value="1" /><el-option label="停用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:nav:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:nav:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="标题" prop="title" min-width="140" />
      <el-table-column label="地址" prop="address" min-width="180" show-overflow-tooltip />
      <el-table-column label="经度" prop="longitude" width="120" />
      <el-table-column label="纬度" prop="latitude" width="120" />
      <el-table-column label="电话" prop="phone" width="120" />
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="s"><el-tag :type="s.row.status==='1'?'success':'info'">{{ s.row.status==='1'?'启用':'停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:nav:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:nav:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="640px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" maxlength="100" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" maxlength="300" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="经度" prop="longitude"><el-input v-model="form.longitude" placeholder="如 118.123456" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="纬度" prop="latitude"><el-input v-model="form.latitude" placeholder="如 24.123456" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="封面"><material-select v-model="form.coverUrl" :show-tip="false" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-switch v-model="form.status" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingNav">
import MaterialSelect from '@/components/MaterialSelect'
import { listNav, getNavStats, getNav, addNav, updateNav, delNav } from '@/api/meeting/nav'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, title:undefined, status:undefined })
const rules = {
  title: [{ required:true, message:'标题不能为空', trigger:'blur' }],
  longitude: [{ required:true, message:'经度不能为空', trigger:'blur' }],
  latitude: [{ required:true, message:'纬度不能为空', trigger:'blur' }]
}
function loadStats(){ getNavStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listNav(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.title=undefined; queryParams.value.status=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.navId); multiple.value=!s.length }
function reset(){ form.value={ navId:undefined, activityId:Number(activityId.value), title:'', address:'', longitude:'', latitude:'', phone:'', coverUrl:'', sortOrder:0, status:'1', remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增导航点'; open.value=true }
function handleUpdate(row){ reset(); getNav(row.navId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改导航点' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.navId?updateNav(form.value):addNav(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const navIds=row?.navId||ids.value; proxy.$modal.confirm('确认删除选中导航？').then(()=>delNav(navIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#67c23a}
</style>