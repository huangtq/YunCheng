<template>
  <div class="place-tabs">
    <el-radio-group :model-value="active" size="large" @change="onChange">
      <el-radio-button value="venue">会场管理</el-radio-button>
      <el-radio-button value="topic">主题管理</el-radio-button>
      <el-radio-button value="schedule">日程管理</el-radio-button>
      <el-radio-button value="expert">专家任务排期</el-radio-button>
    </el-radio-group>
  </div>
</template>
<script setup>
const props = defineProps({ active: { type: String, required: true } })
const route = useRoute()
const router = useRouter()
function onChange(val) {
  const map = {
    venue: '/meeting/activity-config/venue',
    topic: '/meeting/activity-config/topic',
    schedule: '/meeting/activity-config/schedule',
    expert: '/meeting/activity-config/expert'
  }
  router.push({ path: map[val], query: { id: route.query.id } })
}
</script>
<style scoped>
.place-tabs { margin-bottom: 14px; }
</style>