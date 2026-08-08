<template>
  <view class="apply-page">
    <view v-if="loading" class="state">加载中...</view>
    <view v-else-if="!channels.length" class="state">暂无报名通道</view>
    <view v-else class="list">
      <view v-for="item in channels" :key="item.channelId" class="card" @click="goForm(item)">
        <view class="title">{{ item.channelName }}</view>
        <view class="sub">
          <text v-if="item.priceType === '1' || item.price">¥{{ item.price || 0 }}</text>
          <text v-else>免费</text>
          <text v-if="item.quota"> · 名额 {{ item.usedCount || 0 }}/{{ item.quota }}</text>
        </view>
        <view v-if="item.deadline" class="extra">截止：{{ item.deadline }}</view>
      </view>
    </view>
    <view class="mine-entry" @click="goMine">我的报名</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPortalApplyChannels } from '@/api/portal/meeting'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'

const loading = ref(true)
const channels = ref([])
const activityId = ref('')

onLoad((options) => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  uni.setNavigationBarTitle({ title: '参会报名' })
  loadData()
})

function loadData() {
  if (!activityId.value) {
    loading.value = false
    return
  }
  loading.value = true
  getPortalApplyChannels(activityId.value).then(res => {
    channels.value = (res.data && res.data.channels) || []
    loading.value = false
  }).catch(() => { loading.value = false })
}

function goForm(item) {
  uni.navigateTo({
    url: `/pages/meeting/apply/form?activityId=${activityId.value}&channelId=${item.channelId}&channelName=${encodeURIComponent(item.channelName || '')}`
  })
}

function goMine() {
  uni.navigateTo({ url: `/pages/meeting/apply/mine?activityId=${activityId.value}` })
}
</script>

<style lang="scss" scoped>
.apply-page { min-height: 100vh; padding: 24rpx; background: #f5f7fa; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { padding: 28rpx; border-radius: 16rpx; background: #fff; }
.title { font-size: 30rpx; font-weight: 600; color: #303133; }
.sub, .extra { margin-top: 12rpx; color: #909399; font-size: 24rpx; }
.state { padding: 160rpx 24rpx; text-align: center; color: #909399; }
.mine-entry {
  margin-top: 32rpx; text-align: center; color: #409eff; font-size: 28rpx; padding: 20rpx;
}
</style>