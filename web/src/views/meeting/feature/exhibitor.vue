<template>
  <div class="app-container feature-page">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="12"><div class="summary-card"><div class="summary-label">展商总数</div><div class="summary-value">{{ stats.totalCount || 0 }}</div></div></el-col>
      <el-col :span="12"><div class="summary-card"><div class="summary-label">大牌展商</div><div class="summary-value ok">{{ stats.featuredCount || 0 }}</div></div></el-col>
    </el-row>
    <el-form :model="queryParams" inline @submit.prevent>
      <el-form-item><el-input v-model="queryParams.exhibitorName" clearable placeholder="商家名称" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item><el-input v-model="queryParams.contactName" clearable placeholder="联系人" @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.isFeatured" clearable placeholder="是否大牌" style="width:120px">
          <el-option label="是" value="1" /><el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['meeting:exhibitor:add']">新增</el-button>
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['meeting:exhibitor:remove']">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list" border @selection-change="onSelect">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="商家名称" prop="exhibitorName" min-width="140" />
      <el-table-column label="展位号" prop="boothNo" width="100" />
      <el-table-column label="联系人" prop="contactName" width="100" />
      <el-table-column label="联系方式" prop="phone" width="120" />
      <el-table-column label="大牌" width="70" align="center">
        <template #default="s"><el-tag :type="s.row.isFeatured==='1'?'warning':'info'">{{ s.row.isFeatured==='1'?'是':'否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="简介" prop="intro" min-width="160" show-overflow-tooltip />
      <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template #default="s">
          <el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['meeting:exhibitor:edit']">修改</el-button>
          <el-button link type="danger" @click="handleDelete(s.row)" v-hasPermi="['meeting:exhibitor:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="680px" class="meeting-form-dialog" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商家名称" prop="exhibitorName"><el-input v-model="form.exhibitorName" maxlength="200" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="展位号"><el-input v-model="form.boothNo" maxlength="50" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="是否大牌"><el-switch v-model="form.isFeatured" active-value="1" inactive-value="0" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系人"><el-input v-model="form.contactName" maxlength="100" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系方式"><el-input v-model="form.phone" maxlength="20" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="商家图片"><material-select v-model="form.logoUrl" :show-tip="false" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="第三方链接"><el-input v-model="form.linkUrl" placeholder="可选，外链地址" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="简介"><el-input v-model="form.intro" type="textarea" :rows="3" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" controls-position="right" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button type="primary" @click="submit">确 定</el-button><el-button @click="open=false">取 消</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="MeetingExhibitor">
import MaterialSelect from '@/components/MaterialSelect'
import { listExhibitor, getExhibitorStats, getExhibitor, addExhibitor, updateExhibitor, delExhibitor } from '@/api/meeting/exhibitor'
const { proxy } = getCurrentInstance()
const route = useRoute()
const activityId = computed(() => route.query.id)
const loading = ref(true), list = ref([]), total = ref(0), ids = ref([]), multiple = ref(true)
const open = ref(false), title = ref(''), stats = ref({}), form = ref({})
const queryParams = ref({ pageNum:1, pageSize:10, activityId:undefined, exhibitorName:undefined, contactName:undefined, isFeatured:undefined })
const rules = { exhibitorName: [{ required:true, message:'商家名称不能为空', trigger:'blur' }] }
function loadStats(){ getExhibitorStats(activityId.value).then(res => stats.value = res.data || {}) }
function getList(){ loading.value=true; queryParams.value.activityId=activityId.value; listExhibitor(queryParams.value).then(res=>{ list.value=res.rows||[]; total.value=res.total||0; loading.value=false }).catch(()=>loading.value=false) }
function handleQuery(){ queryParams.value.pageNum=1; getList() }
function resetQuery(){ queryParams.value.exhibitorName=undefined; queryParams.value.contactName=undefined; queryParams.value.isFeatured=undefined; handleQuery() }
function onSelect(s){ ids.value=s.map(i=>i.exhibitorId); multiple.value=!s.length }
function reset(){ form.value={ exhibitorId:undefined, activityId:Number(activityId.value), exhibitorName:'', intro:'', contactName:'', phone:'', logoUrl:'', linkUrl:'', boothNo:'', isFeatured:'0', sortOrder:0, remark:'' }; proxy.resetForm('formRef') }
function handleAdd(){ reset(); title.value='新增展商'; open.value=true }
function handleUpdate(row){ reset(); getExhibitor(row.exhibitorId).then(res=>{ form.value={...res.data}; open.value=true; title.value='修改展商' }) }
function submit(){ proxy.$refs.formRef.validate(v=>{ if(!v) return; const req=form.value.exhibitorId?updateExhibitor(form.value):addExhibitor(form.value); req.then(()=>{ proxy.$modal.msgSuccess('操作成功'); open.value=false; loadStats(); getList() }) }) }
function handleDelete(row){ const exhibitorIds=row?.exhibitorId||ids.value; proxy.$modal.confirm('确认删除选中展商？').then(()=>delExhibitor(exhibitorIds)).then(()=>{ loadStats(); getList(); proxy.$modal.msgSuccess('删除成功') }).catch(()=>{}) }
onMounted(()=>{ if(!activityId.value){ proxy.$modal.msgError('缺少会议ID'); return } loadStats(); getList() })
</script>
<style scoped>
.summary-row{margin-bottom:12px}.summary-card{background:#fff;border:1px solid #ebeef5;border-radius:10px;padding:14px 16px}.summary-label{font-size:13px;color:#909399}.summary-value{margin-top:6px;font-size:26px;font-weight:700;color:#409eff}.summary-value.ok{color:#e6a23c}
</style>
