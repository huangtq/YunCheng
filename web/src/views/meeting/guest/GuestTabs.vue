<template>
  <div class="guest-tabs">
    <el-radio-group :model-value="active" size="large" @change="onChange">
      <el-radio-button value="guest">嘉宾管理</el-radio-button>
      <el-radio-button value="trip">嘉宾行程</el-radio-button>
      <el-radio-button value="rule">任务规则</el-radio-button>
      <el-radio-button value="fee">劳务费管理</el-radio-button>
    </el-radio-group>
  </div>
</template>
<script setup>
const props = defineProps({ active: { type: String, required: true } })
const route = useRoute()
const router = useRouter()
function onChange(val) {
  const map = {
    guest: '/meeting/activity-config/guest',
    trip: '/meeting/activity-config/guest-trip',
    rule: '/meeting/activity-config/guest-rule',
    fee: '/meeting/activity-config/guest-fee'
  }
  router.push({ path: map[val], query: { id: route.query.id } })
}
</script>
<style scoped>
.guest-tabs { margin-bottom: 14px; }
</style>