<template>
  <view class="mine-page">
    <view v-if="loading" class="state">加载中...</view>
    <view v-else-if="!list.length" class="state">暂无报名记录</view>
    <view v-else class="list">
      <view v-for="item in list" :key="item.orderId" class="card">
        <view class="title">{{ item.channelName || '报名订单' }}</view>
        <view class="sub">订单号：{{ item.orderNo }}</view>
        <view class="sub">姓名：{{ item.contactName }} · {{ item.mobile }}</view>
        <view class="extra">状态：{{ statusLabel(item.orderStatus) }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMyPortalApply } from '@/api/portal/meeting'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'

const loading = ref(true)
const list = ref([])
const activityId = ref('')

onLoad((options) => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  uni.setNavigationBarTitle({ title: '我的报名' })
  loadData()
})

function statusLabel(v) {
  return ({ '0': '已报名', '2': '已取消' })[v] || v || '-'
}

function loadData() {
  loading.value = true
  getMyPortalApply(activityId.value).then(res => {
    list.value = res.data || []
    loading.value = false
  }).catch(() => { loading.value = false })
}
</script>

<style lang="scss" scoped>
.mine-page { min-height: 100vh; padding: 24rpx; background: #f5f7fa; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { padding: 28rpx; border-radius: 16rpx; background: #fff; }
.title { font-size: 30rpx; font-weight: 600; color: #303133; }
.sub, .extra { margin-top: 12rpx; color: #909399; font-size: 24rpx; }
.state { padding: 160rpx 24rpx; text-align: center; color: #909399; }
</style>