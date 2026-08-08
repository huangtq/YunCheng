<template>
  <div class="hotel-tabs">
    <el-radio-group :model-value="active" size="large" @change="onChange">
      <el-radio-button value="hotel">会议酒店</el-radio-button>
      <el-radio-button value="order">酒店订单</el-radio-button>
      <el-radio-button value="assign">分房管理</el-radio-button>
    </el-radio-group>
  </div>
</template>
<script setup>
defineProps({ active: { type: String, required: true } })
const route = useRoute()
const router = useRouter()
function onChange(val) {
  const map = {
    hotel: '/meeting/activity-config/hotel',
    order: '/meeting/activity-config/hotel-order',
    assign: '/meeting/activity-config/hotel-assign'
  }
  router.push({ path: map[val], query: { id: route.query.id } })
}
</script>
<style scoped>
.hotel-tabs { margin-bottom: 14px; }
</style>