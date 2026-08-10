<template>
  <view class="mine-page">
    <MeetingContentHeader title="报名成功" :activity-id="activityId" />
    <view class="mine-body">
      <view v-if="loading" class="state">加载中...</view>
      <view v-else-if="authError" class="state">
        <view>{{ authError }}</view>
        <button class="retry-btn" @click="retryAuth">重新获取微信授权</button>
      </view>
      <view v-else-if="!activeOrders.length" class="state">暂无有效报名记录</view>
      <view v-else class="success-wrap">
        <view class="success-banner">
          <view class="success-icon">✓</view>
          <view class="success-title">报名成功</view>
          <view class="success-desc">您已完成报名，请留意会议通知</view>
        </view>
        <view
          v-for="item in activeOrders"
          :key="item.orderId"
          class="card"
        >
          <view class="title">{{ item.channelName || '报名订单' }}</view>
          <view class="sub">订单号：{{ item.orderNo || '-' }}</view>
          <view class="sub">姓名：{{ item.contactName || '-' }} · {{ item.mobile || '-' }}</view>
          <view class="extra">状态：{{ statusLabel(item.orderStatus) }}</view>
        </view>
        <button class="home-btn" @click="goHome">返回会议首页</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import MeetingContentHeader from '@/components/MeetingContentHeader.vue'
import { getMyPortalApply } from '@/api/portal/meeting'
import { getOauthUrl, buildMockOauthJump } from '@/api/portal/wx'
import { captureMpTokenFromQuery, hasMpToken, removeMpToken } from '@/utils/mpAuth'
import { buildH5PageUrl } from '@/utils/h5Route'
import { setupMeetingShare } from '@/utils/wxShare'

const loading = ref(true)
const list = ref([])
const activityId = ref('')
const authError = ref('')

const activeOrders = computed(() =>
  (list.value || []).filter((item) => String(item.orderStatus) === '0')
)

onLoad((options) => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  if (activityId.value) setupMeetingShare(activityId.value)
  bootstrap()
})

onShow(() => {
  if (activityId.value) setupMeetingShare(activityId.value)
})

function statusLabel(v) {
  return ({ '0': '已报名', '2': '已取消' })[v] || v || '-'
}

function isWechatBrowser() {
  if (typeof navigator === 'undefined') return false
  return /MicroMessenger/i.test(navigator.userAgent || '')
}

function buildMineRedirect() {
  if (typeof location === 'undefined') return ''
  return buildH5PageUrl('/pages/meeting/apply/mine', { activityId: activityId.value })
}

async function bootstrap() {
  loading.value = true
  authError.value = ''
  if (!activityId.value) {
    loading.value = false
    authError.value = '缺少会议ID'
    return
  }
  if (!isWechatBrowser()) {
    loading.value = false
    authError.value = '请在微信中打开查看报名结果'
    return
  }
  if (!hasMpToken()) {
    await ensureMpLogin()
    return
  }
  await loadData()
}

async function retryAuth() {
  removeMpToken()
  await bootstrap()
}

async function ensureMpLogin() {
  loading.value = true
  try {
    const redirect = buildMineRedirect()
    const res = await getOauthUrl(activityId.value, redirect)
    const data = res.data || {}
    if (data.mode === 'mock' && typeof window !== 'undefined') {
      window.location.href = buildMockOauthJump(activityId.value, redirect)
      return
    }
    if (data.url && typeof window !== 'undefined') {
      window.location.replace(data.url)
      return
    }
    authError.value = '未能获取微信授权地址，请稍后重试'
    loading.value = false
  } catch (e) {
    authError.value = '获取微信授权失败，请稍后重试'
    loading.value = false
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMyPortalApply(activityId.value)
    list.value = res.data || []
  } catch (e) {
    list.value = []
    authError.value = '加载报名记录失败，请重试'
  } finally {
    loading.value = false
  }
}

function goHome() {
  if (activityId.value) {
    uni.redirectTo({ url: `/pages/meeting/home?activityId=${activityId.value}` })
  }
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.mine-body {
  box-sizing: border-box;
  min-height: 100vh;
  padding: calc(49px + 24rpx) 24rpx 40rpx;
}
.success-wrap {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.success-banner {
  padding: 48rpx 28rpx 40rpx;
  border-radius: 16rpx;
  background: #fff;
  text-align: center;
}
.success-icon {
  width: 88rpx;
  height: 88rpx;
  margin: 0 auto 20rpx;
  border-radius: 50%;
  background: #19be6b;
  color: #fff;
  font-size: 48rpx;
  line-height: 88rpx;
  font-weight: 700;
}
.success-title {
  color: #303133;
  font-size: 36rpx;
  font-weight: 700;
}
.success-desc {
  margin-top: 12rpx;
  color: #909399;
  font-size: 26rpx;
}
.card {
  padding: 28rpx;
  border-radius: 16rpx;
  background: #fff;
}
.title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
}
.sub,
.extra {
  margin-top: 12rpx;
  color: #909399;
  font-size: 24rpx;
}
.home-btn {
  margin-top: 12rpx;
  height: 88rpx;
  border: none;
  border-radius: 44rpx;
  background: #1f6feb;
  color: #fff;
  font-size: 30rpx;
}
.state {
  padding: 160rpx 24rpx;
  text-align: center;
  color: #909399;
  font-size: 28rpx;
}
.retry-btn {
  margin: 28rpx auto 0;
  width: 320rpx;
  height: 72rpx;
  line-height: 72rpx;
  border: none;
  border-radius: 36rpx;
  background: #1f6feb;
  color: #fff;
  font-size: 28rpx;
}
</style>
