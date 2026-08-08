<template>
  <view class="portal-page">
    <view class="hero">
      <view class="hero-title">会议服务</view>
      <view class="hero-subtitle">发现会议，便捷参会</view>
      <input
        v-model="keyword"
        class="search-input"
        confirm-type="search"
        placeholder="搜索会议名称"
      />
    </view>

    <view class="content">
      <view class="section-title">
        <text class="bar"></text>
        <text>{{ activeTab === 'current' ? '当前会议' : '历史会议' }}</text>
      </view>

      <view v-if="loading" class="state">加载中...</view>
      <view v-else-if="!visibleActivities.length" class="state">
        {{ activeTab === 'current' ? '暂无当前会议' : '暂无历史会议' }}
      </view>
      <view v-else>
        <view v-for="section in activitySections" :key="section.title" class="activity-section">
          <view class="section-title section-title-small">
            <text class="bar"></text>
            <text>{{ section.title }}</text>
          </view>
          <view class="activity-list">
            <view
              v-for="item in section.items"
              :key="item.activityId"
              class="activity-card"
              @click="openActivity(item)"
            >
              <image
                v-if="item.coverUrl"
                class="activity-cover"
                :src="resolveUrl(item.coverUrl)"
                mode="aspectFill"
              />
              <view v-else class="activity-cover activity-cover-empty">会议</view>
              <view class="activity-info">
                <view class="activity-name">{{ item.activityName }}</view>
                <view class="activity-time">{{ formatRange(item.startTime, item.endTime) }}</view>
                <view class="activity-address">{{ item.city || '' }}{{ item.address ? ` · ${item.address}` : '' }}</view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="tabbar">
      <view
        class="tabbar-item"
        :class="{ active: activeTab === 'current' }"
        @click="switchTab('current')"
      >
        <text class="tab-icon">▣</text>
        <text>当前会议</text>
      </view>
      <view
        class="tabbar-item"
        :class="{ active: activeTab === 'history' }"
        @click="switchTab('history')"
      >
        <text class="tab-icon">◷</text>
        <text>历史会议</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPortalActivities } from '@/api/portal/meeting'
import config from '@/config'

const activeTab = ref('current')
const loading = ref(false)
const activities = ref([])
const keyword = ref('')
const visibleActivities = computed(() => activities.value.filter(item => {
  const value = `${item.activityName || ''}${item.city || ''}${item.address || ''}`
  return !keyword.value.trim() || value.includes(keyword.value.trim())
}))
const activitySections = computed(() => {
  const matched = visibleActivities.value
  if (activeTab.value === 'history') {
    return [{ title: '历史会议', items: matched }]
  }
  const hot = matched.filter(item => String(item.isHot) === '1')
  const recent = matched.filter(item => String(item.isHot) !== '1')
  const sections = []
  if (hot.length) sections.push({ title: '热门会议', items: hot })
  if (recent.length) sections.push({ title: '近期会议', items: recent })
  return sections
})

onLoad(() => {
  uni.setNavigationBarTitle({ title: '会议首页' })
  loadActivities()
})

function loadActivities() {
  loading.value = true
  getPortalActivities(activeTab.value).then(res => {
    activities.value = res.data || []
  }).catch(() => {
    activities.value = []
  }).finally(() => {
    loading.value = false
  })
}

function switchTab(tab) {
  if (activeTab.value === tab) return
  activeTab.value = tab
  loadActivities()
}

function openActivity(item) {
  if (item.thirdPartyUrl) {
    uni.navigateTo({
      url: `/pages/common/webview/index?title=${encodeURIComponent(item.activityName || '会议详情')}&url=${encodeURIComponent(item.thirdPartyUrl)}`
    })
    return
  }
  uni.navigateTo({
    url: `/pages/meeting/home?activityId=${item.activityId}`
  })
}

function resolveUrl(url) {
  return url && url.startsWith('http') ? url : config.baseUrl + (url || '')
}

function formatRange(start, end) {
  const date = value => value ? String(value).slice(0, 10) : ''
  const left = date(start)
  const right = date(end)
  if (!left) return '时间待定'
  return right && right !== left ? `${left} 至 ${right}` : left
}
</script>

<style lang="scss" scoped>
.portal-page {
  min-height: 100vh;
  padding-bottom: 112rpx;
  background: #f5f7fa;
}

.hero {
  padding: 72rpx 40rpx 56rpx;
  color: #fff;
  background: linear-gradient(135deg, #1f6feb, #65b7ff);
}

.hero-title { font-size: 44rpx; font-weight: 700; }
.hero-subtitle { margin-top: 14rpx; font-size: 26rpx; opacity: .9; }
.search-input { height: 72rpx; margin-top: 28rpx; padding: 0 24rpx; box-sizing: border-box; border-radius: 36rpx; color: #303133; background: #fff; font-size: 26rpx; }
.content { padding: 26rpx 24rpx; }
.section-title { display: flex; align-items: center; gap: 12rpx; color: #303133; font-size: 30rpx; font-weight: 600; }
.section-title-small { margin-top: 26rpx; }
.bar { width: 8rpx; height: 32rpx; border-radius: 4rpx; background: #f56c6c; }
.activity-list { display: flex; flex-direction: column; gap: 20rpx; margin-top: 20rpx; }
.activity-card { display: flex; overflow: hidden; border-radius: 16rpx; background: #fff; box-shadow: 0 4rpx 16rpx rgba(31, 111, 235, .06); }
.activity-cover { flex: 0 0 220rpx; width: 220rpx; height: 170rpx; }
.activity-cover-empty { display: flex; align-items: center; justify-content: center; color: #fff; background: #9fc7f4; }
.activity-info { min-width: 0; padding: 22rpx 20rpx; }
.activity-name { display: -webkit-box; overflow: hidden; color: #303133; font-size: 28rpx; font-weight: 600; line-height: 1.45; -webkit-box-orient: vertical; -webkit-line-clamp: 2; }
.activity-time, .activity-address { margin-top: 12rpx; overflow: hidden; color: #909399; font-size: 23rpx; text-overflow: ellipsis; white-space: nowrap; }
.state { padding: 180rpx 24rpx; color: #909399; font-size: 28rpx; text-align: center; }
.tabbar { position: fixed; right: 0; bottom: 0; left: 0; z-index: 10; display: flex; padding-bottom: env(safe-area-inset-bottom); border-top: 1px solid #ebeef5; background: #fff; }
.tabbar-item { display: flex; flex: 1; flex-direction: column; align-items: center; padding: 18rpx 8rpx 14rpx; color: #909399; font-size: 23rpx; }
.tabbar-item.active { color: #1f6feb; }
.tab-icon { margin-bottom: 4rpx; font-size: 34rpx; line-height: 1; }
</style>
