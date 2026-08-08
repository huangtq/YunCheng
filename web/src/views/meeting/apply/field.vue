<template>
  <div class="app-container field-page">
    <el-alert
      v-if="!channelId"
      title="请从报名通道配置页点击「字段配置」进入"
      type="warning"
      :closable="false"
      show-icon
      class="mb12"
    />

    <el-row :gutter="16">
      <el-col :xs="24" :lg="15">
        <el-row :gutter="12" class="stat-row">
          <el-col :span="6"><div class="stat-card"><div class="stat-num">{{ stats.enabled }}</div><div class="stat-label">已启用字段</div></div></el-col>
          <el-col :span="6"><div class="stat-card"><div class="stat-num">{{ stats.required }}</div><div class="stat-label">必填字段</div></div></el-col>
          <el-col :span="6"><div class="stat-card"><div class="stat-num">{{ stats.extend }}</div><div class="stat-label">扩展字段</div></div></el-col>
          <el-col :span="6"><div class="stat-card"><div class="stat-num">{{ stats.options }}</div><div class="stat-label">选项字段</div></div></el-col>
        </el-row>

        <el-card shadow="never" class="section-card">
          <div class="section-head">
            <div>
              <div class="section-title">标准字段</div>
              <div class="section-desc">控制性别、省市区、单位等基础字段是否启用，以及展示名称与校验方式。</div>
            </div>
            <div class="section-actions">
              <el-button type="primary" plain :disabled="!selectedStandard.length" @click="batchEnable(true)">批量启用</el-button>
              <el-button type="danger" plain :disabled="!selectedStandard.length" @click="batchEnable(false)">批量停用</el-button>
            </div>
          </div>
          <el-table v-loading="loading" :data="standardFields" @selection-change="handleStandardSelect">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="状态" width="90" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.enabledFlag === '1' ? 'success' : 'info'">
                  {{ scope.row.enabledFlag === '1' ? '已启用' : '未启用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="标准字段" min-width="110">
              <template #default="scope">
                <span>{{ standardLabel(scope.row.fieldKey) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="展示名称" prop="fieldName" min-width="100" />
            <el-table-column label="显示条件" min-width="140" show-overflow-tooltip>
              <template #default="scope">
                <span>{{ conditionSummary(scope.row) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="字段类型" width="90" align="center">
              <template #default="scope">
                <span>{{ typeLabel(scope.row.fieldType) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="是否必填" width="90" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.requiredFlag === '1' ? 'warning' : 'info'" effect="plain">
                  {{ scope.row.requiredFlag === '1' ? '必填' : '选填' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
            <el-table-column label="操作" width="160" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button link :type="scope.row.enabledFlag === '1' ? 'danger' : 'success'" @click="toggleEnable(scope.row)">
                  {{ scope.row.enabledFlag === '1' ? '停用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="never" class="section-card">
          <div class="section-head">
            <div>
              <div class="section-title">扩展字段</div>
              <div class="section-desc">用于补充自定义问题，例如职务、用餐偏好、住宿需求等。</div>
            </div>
            <el-button type="primary" icon="Plus" :disabled="!channelId" @click="handleAdd">新增扩展字段</el-button>
          </div>
          <el-table v-loading="loading" :data="extendFields">
            <el-table-column label="状态" width="90" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.enabledFlag === '1' ? 'success' : 'info'">
                  {{ scope.row.enabledFlag === '1' ? '已启用' : '未启用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="字段标识" prop="fieldKey" min-width="110" show-overflow-tooltip />
            <el-table-column label="展示名称" prop="fieldName" min-width="100" />
            <el-table-column label="显示条件" min-width="140" show-overflow-tooltip>
              <template #default="scope">
                <span>{{ conditionSummary(scope.row) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="字段类型" width="90" align="center">
              <template #default="scope">
                <span>{{ typeLabel(scope.row.fieldType) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="是否必填" width="90" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.requiredFlag === '1' ? 'warning' : 'info'" effect="plain">
                  {{ scope.row.requiredFlag === '1' ? '必填' : '选填' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
            <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button link :type="scope.row.enabledFlag === '1' ? 'danger' : 'success'" @click="toggleEnable(scope.row)">
                  {{ scope.row.enabledFlag === '1' ? '停用' : '启用' }}
                </el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="9">
        <div class="preview-panel">
          <div class="preview-title">手机端报名预览</div>
          <div class="phone-frame">
            <div class="phone-notch"></div>
            <div class="phone-screen">
              <div class="preview-header">报名信息</div>
              <div class="preview-body">
                <div v-for="item in previewFields" :key="item.fieldId" class="preview-field">
                  <div class="preview-label">
                    <span v-if="item.requiredFlag === '1'" class="req">*</span>
                    {{ item.fieldName }}
                  </div>
                  <div v-if="conditionSummary(item) !== '始终显示'" class="preview-cond">{{ conditionSummary(item) }}</div>
                  <div class="preview-control">
                    <template v-if="isOptionType(item.fieldType)">
                      <div class="preview-options" :class="{ multi: item.fieldType === 'checkbox' }">
                        <span v-for="(opt, idx) in parseOptions(item.optionsJson)" :key="idx" class="opt">{{ opt }}</span>
                        <span v-if="!parseOptions(item.optionsJson).length" class="opt muted">选项待配置</span>
                      </div>
                    </template>
                    <template v-else-if="item.fieldType === 'textarea'">
                      <div class="fake-textarea">{{ item.placeholder || '请输入' }}</div>
                    </template>
                    <template v-else-if="item.fieldType === 'system'">
                      <div class="fake-input">{{ item.placeholder || ('请选择' + item.fieldName) }}</div>
                    </template>
                    <template v-else>
                      <div class="fake-input">{{ item.placeholder || '请输入' }}</div>
                    </template>
                  </div>
                </div>
                <div v-if="!previewFields.length" class="preview-empty">暂无启用字段</div>
              </div>
              <div class="preview-footer">
                <div class="submit-btn">提交报名</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="dialogTitle" v-model="open" width="680px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <div class="dialog-section">
          <div class="dialog-section-title">基础设置</div>
          <el-form-item label="字段类型" prop="fieldType">
            <el-select v-model="form.fieldType" placeholder="请选择类型" style="width: 100%" :disabled="form.fieldScope === 'standard'">
              <el-option v-for="item in fieldTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="是否必填" prop="requiredFlag">
            <el-radio-group v-model="form.requiredFlag">
              <el-radio value="1">必填</el-radio>
              <el-radio value="0">选填</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="form.fieldScope === 'extend'" label="字段标识" prop="fieldKey">
            <el-input v-model="form.fieldKey" placeholder="如 custom_job" maxlength="64" :disabled="!!form.fieldId" />
          </el-form-item>
          <el-form-item label="展示名称" prop="fieldName">
            <el-input v-model="form.fieldName" placeholder="请输入展示名称" maxlength="64" />
          </el-form-item>
          <el-form-item label="排序" prop="sortOrder">
            <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" />
          </el-form-item>
          <el-form-item label="是否启用" prop="enabledFlag">
            <el-radio-group v-model="form.enabledFlag">
              <el-radio value="1">启用</el-radio>
              <el-radio value="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="提示文案" prop="placeholder">
            <el-input v-model="form.placeholder" placeholder="例如：请输入单位名称" maxlength="200" />
          </el-form-item>
          <el-form-item v-if="needOptions" label="选项配置" prop="optionsText">
            <el-input v-model="form.optionsText" type="textarea" :rows="4" placeholder="每行一个选项，例如：&#10;男&#10;女" />
          </el-form-item>
        </div>

        <div class="dialog-section">
          <div class="dialog-section-title">显示条件</div>
          <el-form-item label="启用条件">
            <el-switch
              v-model="conditionEnabled"
              active-text="按选项显示"
              inactive-text="始终显示"
            />
          </el-form-item>
          <template v-if="conditionEnabled">
            <el-form-item label="关联字段" required>
              <el-select
                v-model="conditionForm.fieldKey"
                clearable
                filterable
                placeholder="选择单选/多选/下拉字段"
                style="width: 100%"
                @change="handleConditionFieldChange"
              >
                <el-option
                  v-for="item in conditionFieldOptions"
                  :key="item.fieldKey"
                  :label="item.fieldName"
                  :value="item.fieldKey"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="触发选项" required>
              <el-select
                v-model="conditionForm.value"
                clearable
                filterable
                allow-create
                default-first-option
                placeholder="选择或输入选项值"
                style="width: 100%"
              >
                <el-option v-for="opt in conditionValueOptions" :key="opt" :label="opt" :value="opt" />
              </el-select>
            </el-form-item>
            <div class="condition-tip">当前字段仅在关联字段选择了触发选项时显示。</div>
          </template>
        </div>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ApplyField">
import { listApplyField, getApplyField, addApplyField, updateApplyField, delApplyField, changeApplyFieldEnabled } from '@/api/meeting/applyField'

const { proxy } = getCurrentInstance()
const route = useRoute()

const channelId = computed(() => Number(route.query.channelId || 0))
const activityId = computed(() => Number(route.query.id || route.query.activityId || 0))
const fields = ref([])
const loading = ref(false)
const selectedStandard = ref([])
const open = ref(false)
const dialogTitle = ref('')
const conditionEnabled = ref(false)
const conditionForm = reactive({ fieldKey: '', value: '' })

const fieldTypeOptions = [
  { label: '单行文本', value: 'input' },
  { label: '多行文本', value: 'textarea' },
  { label: '单选', value: 'radio' },
  { label: '多选', value: 'checkbox' },
  { label: '下拉选择', value: 'select' },
  { label: '日期', value: 'date' },
  { label: '上传', value: 'upload' },
  { label: '系统组件', value: 'system' }
]

const STANDARD_LABEL = {
  gender: '性别',
  region: '省市区',
  company: '单位',
  position: '职务',
  hotel: '是否预定酒店',
  idCard: '身份证',
  age: '年龄',
  department: '科室',
  grassroots: '是否来自基层',
  westProvince: '是否隶属西部十二省',
  attendType: '参会形式',
  email: '邮箱',
  title: '职称',
  name: '姓名',
  mobile: '手机号'
}

const data = reactive({
  form: {},
  rules: {
    fieldType: [{ required: true, message: '字段类型不能为空', trigger: 'change' }],
    fieldName: [{ required: true, message: '展示名称不能为空', trigger: 'blur' }],
    fieldKey: [{ required: true, message: '字段标识不能为空', trigger: 'blur' }],
    requiredFlag: [{ required: true, message: '请选择是否必填', trigger: 'change' }],
    enabledFlag: [{ required: true, message: '请选择是否启用', trigger: 'change' }]
  }
})
const { form, rules } = toRefs(data)

const standardFields = computed(() => fields.value.filter(item => item.fieldScope === 'standard'))
const extendFields = computed(() => fields.value.filter(item => item.fieldScope === 'extend'))
const previewFields = computed(() =>
  fields.value
    .filter(item => item.enabledFlag === '1')
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
)
const stats = computed(() => {
  const enabled = fields.value.filter(item => item.enabledFlag === '1')
  return {
    enabled: enabled.length,
    required: enabled.filter(item => item.requiredFlag === '1').length,
    extend: extendFields.value.length,
    options: enabled.filter(item => isOptionType(item.fieldType)).length
  }
})
const needOptions = computed(() => isOptionType(form.value.fieldType))

const conditionFieldOptions = computed(() => {
  return fields.value.filter(item => {
    if (!item.fieldKey || !isOptionType(item.fieldType)) return false
    if (form.value.fieldId && item.fieldId === form.value.fieldId) return false
    if (form.value.fieldKey && item.fieldKey === form.value.fieldKey) return false
    return true
  })
})

const conditionValueOptions = computed(() => {
  const target = fields.value.find(item => item.fieldKey === conditionForm.fieldKey)
  return target ? parseOptions(target.optionsJson) : []
})

function typeLabel(type) {
  const hit = fieldTypeOptions.find(item => item.value === type)
  return hit ? hit.label : type
}

function standardLabel(key) {
  return STANDARD_LABEL[key] || key
}

function isOptionType(type) {
  return ['radio', 'checkbox', 'select'].includes(type)
}

function parseOptions(raw) {
  if (!raw) return []
  const text = String(raw).trim()
  if (!text) return []
  try {
    const arr = JSON.parse(text)
    if (Array.isArray(arr)) {
      return arr.map(item => String(item).trim()).filter(Boolean)
    }
  } catch (e) {
    // plain text options
  }
  return text.split(/[\n,，/／|]/).map(s => s.trim()).filter(Boolean)
}

function optionsToText(raw) {
  return parseOptions(raw).join('\n')
}

function textToOptionsJson(text) {
  const arr = String(text || '').split(/\n/).map(s => s.trim()).filter(Boolean)
  return arr.length ? JSON.stringify(arr) : ''
}

function parseCondition(raw) {
  if (!raw) return null
  try {
    const obj = typeof raw === 'string' ? JSON.parse(raw) : raw
    if (obj && obj.fieldKey && obj.value !== undefined && obj.value !== null && String(obj.value).trim() !== '') {
      return { fieldKey: String(obj.fieldKey), value: String(obj.value) }
    }
  } catch (e) {
    return null
  }
  return null
}

function conditionSummary(row) {
  const cond = parseCondition(row.showCondition)
  if (!cond) return '始终显示'
  const dep = fields.value.find(item => item.fieldKey === cond.fieldKey)
  const name = dep ? dep.fieldName : (STANDARD_LABEL[cond.fieldKey] || cond.fieldKey)
  return '当「' + name + '」=「' + cond.value + '」'
}

function handleConditionFieldChange() {
  conditionForm.value = ''
}

function getList() {
  if (!channelId.value) {
    fields.value = []
    return
  }
  loading.value = true
  listApplyField({ channelId: channelId.value }).then(res => {
    fields.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

function handleStandardSelect(selection) {
  selectedStandard.value = selection
}

function resetCondition() {
  conditionEnabled.value = false
  conditionForm.fieldKey = ''
  conditionForm.value = ''
}

function resetForm() {
  form.value = {
    fieldId: undefined,
    channelId: channelId.value,
    activityId: activityId.value,
    fieldScope: 'extend',
    fieldKey: '',
    fieldName: '',
    fieldType: 'input',
    requiredFlag: '0',
    enabledFlag: '1',
    sortOrder: (extendFields.value.length + 20) * 10,
    placeholder: '',
    optionsText: '',
    optionsJson: '',
    showCondition: '',
    remark: ''
  }
  resetCondition()
  proxy.resetForm('formRef')
}

function handleAdd() {
  resetForm()
  dialogTitle.value = '新增扩展字段'
  open.value = true
}

function handleEdit(row) {
  resetForm()
  getApplyField(row.fieldId).then(res => {
    form.value = {
      ...res.data,
      optionsText: optionsToText(res.data.optionsJson)
    }
    const cond = parseCondition(res.data.showCondition)
    if (cond) {
      conditionEnabled.value = true
      conditionForm.fieldKey = cond.fieldKey
      conditionForm.value = cond.value
    } else {
      resetCondition()
    }
    dialogTitle.value = form.value.fieldScope === 'standard' ? '编辑标准字段' : '编辑扩展字段'
    open.value = true
  })
}

function buildShowCondition() {
  if (!conditionEnabled.value) return ''
  if (!conditionForm.fieldKey || !String(conditionForm.value || '').trim()) {
    return null
  }
  return JSON.stringify({
    fieldKey: conditionForm.fieldKey,
    value: String(conditionForm.value).trim()
  })
}

function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (!valid) return
    if (needOptions.value && !String(form.value.optionsText || '').trim()) {
      proxy.$modal.msgError('请配置选项内容')
      return
    }
    const showCondition = buildShowCondition()
    if (showCondition === null) {
      proxy.$modal.msgError('请完整配置显示条件')
      return
    }
    const payload = {
      ...form.value,
      optionsJson: needOptions.value ? textToOptionsJson(form.value.optionsText) : (form.value.optionsJson || ''),
      showCondition
    }
    delete payload.optionsText
    const req = payload.fieldId ? updateApplyField(payload) : addApplyField(payload)
    req.then(() => {
      proxy.$modal.msgSuccess('保存成功')
      open.value = false
      getList()
    })
  })
}

function toggleEnable(row) {
  const next = row.enabledFlag === '1' ? '0' : '1'
  const tip = next === '1' ? '启用' : '停用'
  proxy.$modal.confirm('确认要' + tip + '字段「' + row.fieldName + '」吗？').then(() => {
    return changeApplyFieldEnabled({ fieldIds: [row.fieldId], enabledFlag: next })
  }).then(() => {
    proxy.$modal.msgSuccess(tip + '成功')
    getList()
  }).catch(() => {})
}

function batchEnable(enable) {
  const flag = enable ? '1' : '0'
  const tip = enable ? '启用' : '停用'
  const ids = selectedStandard.value.map(item => item.fieldId)
  proxy.$modal.confirm('确认批量' + tip + '选中的 ' + ids.length + ' 个标准字段吗？').then(() => {
    return changeApplyFieldEnabled({ fieldIds: ids, enabledFlag: flag })
  }).then(() => {
    proxy.$modal.msgSuccess('批量' + tip + '成功')
    getList()
  }).catch(() => {})
}

function handleDelete(row) {
  if (row.fieldScope === 'standard') {
    proxy.$modal.msgError('标准字段不可删除，可停用')
    return
  }
  proxy.$modal.confirm('确认删除扩展字段「' + row.fieldName + '」吗？').then(() => {
    return delApplyField(row.fieldId)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

watch(() => route.query.channelId, () => getList(), { immediate: true })
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
.field-page { padding-bottom: 24px; }
.stat-row { margin-bottom: 12px; }
.stat-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 16px 12px;
  text-align: center;
}
.stat-num { font-size: 24px; font-weight: 700; color: #303133; line-height: 1.2; }
.stat-label { margin-top: 6px; font-size: 13px; color: #909399; }
.section-card { margin-bottom: 16px; border-radius: 12px; }
.section-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; margin-bottom: 14px; }
.section-title { font-size: 16px; font-weight: 600; color: #303133; }
.section-desc { margin-top: 4px; font-size: 13px; color: #909399; line-height: 1.5; }
.section-actions { display: flex; gap: 8px; flex-shrink: 0; }
.dialog-section {
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 14px 14px 4px;
  margin-bottom: 14px;
  background: #fafbfc;
}
.dialog-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}
.condition-tip {
  margin: -4px 0 12px 100px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.preview-panel { position: sticky; top: 12px; }
.preview-title { font-size: 15px; font-weight: 600; margin-bottom: 12px; color: #303133; }
.phone-frame {
  width: 320px;
  margin: 0 auto;
  background: #111;
  border-radius: 28px;
  padding: 12px;
  box-shadow: 0 12px 30px rgba(0,0,0,.18);
}
.phone-notch {
  width: 110px;
  height: 8px;
  border-radius: 8px;
  background: #333;
  margin: 4px auto 10px;
}
.phone-screen {
  background: #f7f8fa;
  border-radius: 18px;
  overflow: hidden;
  min-height: 560px;
  display: flex;
  flex-direction: column;
}
.preview-header {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
  text-align: center;
  padding: 16px 12px;
  font-size: 16px;
  font-weight: 600;
}
.preview-body { flex: 1; padding: 14px 12px; }
.preview-field { margin-bottom: 14px; }
.preview-label { font-size: 13px; color: #303133; margin-bottom: 6px; font-weight: 500; }
.preview-cond { font-size: 11px; color: #909399; margin: -2px 0 6px; }
.req { color: #f56c6c; margin-right: 2px; }
.fake-input, .fake-textarea {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  color: #c0c4cc;
  font-size: 13px;
  padding: 10px 12px;
}
.fake-textarea { min-height: 64px; }
.preview-options { display: flex; flex-wrap: wrap; gap: 8px; }
.preview-options .opt {
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 16px;
  padding: 4px 12px;
  font-size: 12px;
  color: #606266;
}
.preview-options .opt.muted { color: #c0c4cc; }
.preview-options.multi .opt { border-radius: 6px; }
.preview-empty { text-align: center; color: #909399; padding: 40px 0; font-size: 13px; }
.preview-footer { padding: 12px; }
.submit-btn {
  background: #2563eb;
  color: #fff;
  text-align: center;
  border-radius: 22px;
  padding: 10px 0;
  font-size: 14px;
  font-weight: 600;
}
@media (max-width: 1200px) {
  .preview-panel { position: static; margin-top: 16px; }
}
</style>