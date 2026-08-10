<template>
  <view class="redirect-state">正在打开报名页...</view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'

onLoad((options) => {
  captureMpTokenFromQuery(options || {})
  const activityId = options?.activityId || ''
  uni.redirectTo({
    url: `/pages/meeting/apply/index?activityId=${activityId}`
  })
})
</script>

<style scoped>
.redirect-state {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: #f5f6f8;
}
</style>
