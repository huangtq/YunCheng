<template>
  <view class="module-page">
    <view v-if="loading" class="state">加载中...</view>
    <view v-else-if="!rows.length" class="state">暂无数据，请先在后台配置</view>
    <view v-else class="list">
      <view v-for="(item, index) in rows" :key="itemKey(item, index)" class="card" @click="onItemClick(item)">
        <view class="title">{{ itemTitle(item) }}</view>
        <view v-if="itemSub(item)" class="sub">{{ itemSub(item) }}</view>
        <view v-if="itemExtra(item)" class="extra">{{ itemExtra(item) }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPortalModule } from '@/api/portal/meeting'
import { getMeetingModule } from '@/utils/meetingModules'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'

const loading = ref(true)
const rows = ref([])
const activityId = ref('')
const moduleKey = ref('')

onLoad(options => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  moduleKey.value = options?.moduleKey || ''
  const title = decodeURIComponent(options?.title || '')
  const mod = getMeetingModule(moduleKey.value)
  uni.setNavigationBarTitle({ title: title || mod?.label || '会议模块' })
  loadData()
})

function loadData() {
  if (!activityId.value || !moduleKey.value) {
    loading.value = false
    return
  }
  loading.value = true
  getPortalModule(moduleKey.value, activityId.value).then(res => {
    rows.value = res.data || []
    loading.value = false
  }).catch(() => { loading.value = false })
}

function itemKey(item, index) {
  return item.scheduleId || item.guestId || item.channelId || item.hotelId
    || item.venueId || item.navId || item.exhibitorId || item.ticketId || index
}

function itemTitle(item) {
  return item.scheduleName || item.guestName || item.channelName || item.hotelName
    || item.venueName || item.title || item.exhibitorName || item.ticketName || '未命名'
}

function itemSub(item) {
  if (moduleKey.value === 'schedule') return [item.venueName, item.topicName].filter(Boolean).join(' · ')
  if (moduleKey.value === 'guest') return [item.orgName, item.title].filter(Boolean).join(' · ')
  if (moduleKey.value === 'hotel') return item.address || item.phone || ''
  if (moduleKey.value === 'venue') return item.isLive === '1' ? '直播会场' : '线下会场'
  if (moduleKey.value === 'nav') return item.address || ''
  if (moduleKey.value === 'exhibitor') return item.boothNo || item.contactName || ''
  if (moduleKey.value === 'meal') return item.mealType || item.remark || ''
  return ''
}

function itemExtra(item) {
  if (moduleKey.value === 'schedule' && item.startTime) {
    return item.startTime + (item.endTime ? (' - ' + item.endTime) : '')
  }
  if (moduleKey.value === 'guest' && item.phone) return item.phone
  if (moduleKey.value === 'nav' && item.phone) return item.phone
  return ''
}

function onItemClick(item) {
  if (moduleKey.value === 'nav' && item.latitude && item.longitude) {
    uni.openLocation({
      latitude: Number(item.latitude),
      longitude: Number(item.longitude),
      name: item.title || '导航地点',
      address: item.address || ''
    })
  }
}
</script>

<style lang="scss" scoped>
.module-page { min-height: 100vh; padding: 24rpx; background: #f5f7fa; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { padding: 28rpx; border-radius: 16rpx; background: #fff; }
.title { color: #303133; font-size: 30rpx; font-weight: 600; }
.sub, .extra { margin-top: 12rpx; color: #909399; font-size: 24rpx; line-height: 1.5; }
.state { padding: 180rpx 24rpx; color: #909399; font-size: 28rpx; text-align: center; }
</style>