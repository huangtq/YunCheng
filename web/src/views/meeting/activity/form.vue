<template>
  <div class="activity-form-page" v-loading="loading">
    <div class="form-intro">
      <div class="form-intro-title">{{ form.activityId ? "编辑会议" : "新增会议" }}</div>
      <div class="form-intro-desc">完善会议基础信息后，可继续配置九宫格、报名通道等会议功能。</div>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="96px" class="activity-form">
      <section class="form-section">
        <div class="section-heading">
          <span class="section-index">01</span>
          <div>
            <div class="section-title">基本信息</div>
            <div class="section-desc">设置会议名称{{ props.embedded ? "和时间" : "、编号和时间" }}</div>
          </div>
        </div>
        <el-row :gutter="24">
          <el-col :xs="24" :md="14">
            <el-form-item label="会议名称" prop="activityName">
              <el-input v-model="form.activityName" placeholder="请输入会议名称" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col v-if="!props.embedded" :xs="24" :md="10">
            <el-form-item label="会议编号" prop="activityCode">
              <div class="code-field">
                <el-input v-model="form.activityCode" placeholder="请输入会议编号" maxlength="64" />
                <el-button type="primary" plain @click="handleGenCode">随机生成</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="14">
            <el-form-item label="会议日期" required>
              <el-date-picker
                v-model="dateRange"
                type="datetimerange"
                value-format="YYYY-MM-DD HH:mm:ss"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                class="form-control"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="!props.embedded" :xs="24" :md="10">
            <el-form-item label="第三方链接" prop="thirdPartyUrl">
              <el-input v-model="form.thirdPartyUrl" placeholder="可选，填写外部访问地址" maxlength="500" />
            </el-form-item>
          </el-col>
        </el-row>
      </section>

      <section class="form-section">
        <div class="section-heading">
          <span class="section-index">02</span>
          <div>
            <div class="section-title">会议地点与视觉</div>
            <div class="section-desc">设置会议举办地点和移动端主视觉</div>
          </div>
        </div>
        <el-row :gutter="24">
          <el-col :xs="24" :md="10">
            <el-form-item label="区域地址" prop="region">
              <el-cascader
                v-model="regionValue"
                :options="regionOptions"
                clearable
                filterable
                placeholder="请选择省/市"
                class="form-control"
                @change="handleRegionChange"
              />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="14">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入会议详细地址" maxlength="255" />
            </el-form-item>
          </el-col>
          <el-col :xs="24">
            <el-form-item label="主视觉" prop="coverUrl">
              <material-select v-model="form.coverUrl" :show-tip="false" />
            </el-form-item>
          </el-col>
        </el-row>
      </section>

      <section class="form-section">
        <div class="section-heading">
          <span class="section-index">03</span>
          <div>
            <div class="section-title">展示设置</div>
            <div class="section-desc">控制会议在前台的展示方式</div>
          </div>
        </div>
        <el-row :gutter="24">
          <el-col :xs="24" :md="8">
            <el-form-item label="是否展示" prop="isShow">
              <el-switch v-model="form.isShow" active-value="1" inactive-value="0" active-text="展示" inactive-text="隐藏" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-form-item label="是否热门" prop="isHot">
              <el-switch v-model="form.isHot" active-value="1" inactive-value="0" active-text="热门" inactive-text="普通" />
            </el-form-item>
          </el-col>
          <el-col v-if="!props.embedded" :xs="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="请输入会议备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </section>

      <div class="form-actions">
        <el-button type="primary" @click="submitForm">保存会议</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup name="MeetingActivityForm">
import { getActivity, addActivity, updateActivity, genActivityCode } from "@/api/meeting/activity"
import { regionOptions } from "@/utils/region-data"
import MaterialSelect from "@/components/MaterialSelect"

const { proxy } = getCurrentInstance()
const route = useRoute()
const props = defineProps({
  embedded: {
    type: Boolean,
    default: false
  },
  activityId: {
    type: [String, Number],
    default: undefined
  }
})
const emit = defineEmits(["success", "cancel"])

const loading = ref(false)
const dateRange = ref([])
const regionValue = ref([])

const form = ref({
  activityId: undefined,
  activityCode: undefined,
  activityName: undefined,
  coverUrl: undefined,
  startTime: undefined,
  endTime: undefined,
  province: undefined,
  city: undefined,
  address: undefined,
  thirdPartyUrl: undefined,
  isShow: "1",
  isHot: "0",
  isHome: "0",
  remark: undefined
})

const rules = {
  activityName: [{ required: true, message: "会议名称不能为空", trigger: "blur" }],
  activityCode: [{ required: true, message: "会议编号不能为空", trigger: "blur" }]
}

function handleRegionChange(val) {
  if (val && val.length >= 2) {
    form.value.province = val[0]
    form.value.city = val[1]
  } else if (val && val.length === 1) {
    form.value.province = val[0]
    form.value.city = val[0]
  } else {
    form.value.province = undefined
    form.value.city = undefined
  }
}

function handleGenCode() {
  genActivityCode().then(res => {
    form.value.activityCode = res.data
  })
}

function loadDetail(id) {
  loading.value = true
  getActivity(id).then(res => {
    form.value = res.data
    if (form.value.startTime && form.value.endTime) {
      dateRange.value = [form.value.startTime, form.value.endTime]
    }
    if (form.value.province) {
      regionValue.value = form.value.city
        ? [form.value.province, form.value.city]
        : [form.value.province]
    }
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (!valid) return
    if (!dateRange.value || dateRange.value.length !== 2) {
      proxy.$modal.msgError("请选择会议日期")
      return
    }
    form.value.startTime = dateRange.value[0]
    form.value.endTime = dateRange.value[1]
    const req = form.value.activityId ? updateActivity(form.value) : addActivity(form.value)
    req.then(() => {
      proxy.$modal.msgSuccess(form.value.activityId ? "修改成功" : "新增成功")
      if (props.embedded) {
        emit("success")
      } else {
        proxy.$tab.closeOpenPage({ path: "/meeting/activity" })
      }
    })
  })
}

function cancel() {
  if (props.embedded) {
    emit("cancel")
  } else {
    proxy.$tab.closeOpenPage({ path: "/meeting/activity" })
  }
}

function setDefaultRegion() {
  regionValue.value = ["福建省", "厦门市"]
  form.value.province = "福建省"
  form.value.city = "厦门市"
}

onMounted(() => {
  const id = props.embedded ? props.activityId : route.query.id
  if (id) {
    loadDetail(id)
  } else {
    setDefaultRegion()
    handleGenCode()
  }
})
</script>

<style scoped>
.activity-form-page {
  min-height: 100%;
  padding: 4px;
  background: #f5f7fa;
}
.form-intro {
  margin-bottom: 16px;
  padding: 20px 24px;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  background: linear-gradient(135deg, #ffffff 0%, #f7fbff 100%);
}
.form-intro-title {
  color: #1f2937;
  font-size: 20px;
  font-weight: 600;
}
.form-intro-desc {
  margin-top: 6px;
  color: #909399;
  font-size: 13px;
}
.activity-form {
  padding: 0 2px 16px;
}
.form-section {
  margin-bottom: 16px;
  padding: 22px 24px 8px;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(31, 41, 55, 0.03);
}
.section-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 22px;
}
.section-index {
  display: inline-flex;
  width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  border-radius: 9px;
  color: #409eff;
  background: #ecf5ff;
  font-size: 12px;
  font-weight: 600;
}
.section-title {
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}
.section-desc {
  margin-top: 3px;
  color: #a8abb2;
  font-size: 12px;
}
.code-field {
  display: flex;
  gap: 8px;
}
.code-field .el-input {
  min-width: 0;
  flex: 1;
}
.form-control {
  width: 100%;
}
.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 8px 0 16px;
}
.form-actions .el-button {
  min-width: 112px;
}
.form-section :deep(.el-form-item__label) {
  color: #606266;
}
.form-section :deep(.el-form-item) {
  margin-bottom: 18px;
}
@media (max-width: 768px) {
  .form-section {
    padding: 18px 14px 4px;
  }
  .form-intro {
    padding: 16px;
  }
}
</style>
