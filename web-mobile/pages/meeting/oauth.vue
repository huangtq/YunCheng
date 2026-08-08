<template>
  <view class="oauth-page">
    <view class="state">正在完成微信授权...</view>
  </view>
</template>

<script setup>
import { onLoad } from '@dcloudio/uni-app'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'

onLoad((options) => {
  captureMpTokenFromQuery(options || {})
  const activityId = options?.activityId || ''
  const url = activityId
    ? `/pages/meeting/home?activityId=${activityId}`
    : '/pages/meeting/home'
  uni.reLaunch({ url })
})
</script>

<style scoped>
.oauth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; }
.state { color: #909399; font-size: 28rpx; }
</style>