<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" v-loading="loading">
      <el-form-item label="会议名称" prop="activityName">
        <el-input v-model="form.activityName" placeholder="会议名称" maxlength="200" style="max-width: 480px" />
      </el-form-item>
      <el-form-item label="会议编号" prop="activityCode">
        <el-input v-model="form.activityCode" placeholder="会议编号" maxlength="64" style="max-width: 320px" />
        <el-button type="primary" style="margin-left: 12px" @click="handleGenCode">随机生成</el-button>
      </el-form-item>
      <el-form-item label="会议日期" required>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="max-width: 480px"
        />
      </el-form-item>
      <el-form-item label="第三方链接" prop="thirdPartyUrl">
        <el-input v-model="form.thirdPartyUrl" placeholder="第三方链接" maxlength="500" style="max-width: 480px" />
      </el-form-item>
      <el-form-item label="区域地址" prop="region">
        <el-cascader
          v-model="regionValue"
          :options="regionOptions"
          clearable
          filterable
          placeholder="请选择省/市"
          style="max-width: 320px"
          @change="handleRegionChange"
        />
      </el-form-item>
      <el-form-item label="详细地址" prop="address">
        <el-input v-model="form.address" placeholder="详细地址" maxlength="255" style="max-width: 480px" />
      </el-form-item>
      <el-form-item label="主视觉" prop="coverUrl">
        <material-select v-model="form.coverUrl" />
      </el-form-item>
      <el-form-item label="是否展示" prop="isShow">
        <el-radio-group v-model="form.isShow">
          <el-radio value="1">是</el-radio>
          <el-radio value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否热门" prop="isHot">
        <el-radio-group v-model="form.isHot">
          <el-radio value="1">是</el-radio>
          <el-radio value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否首页" prop="isHome">
        <el-radio-group v-model="form.isHome">
          <el-radio value="1">是</el-radio>
          <el-radio value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="备注" style="max-width: 480px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </el-form-item>
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