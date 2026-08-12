<template>
  <view class="entry-page">
    <MeetingContentHeader :title="entry.title || '会议栏目'" :activity-id="activityId" />
    <view v-if="loading" class="state">加载中...</view>
    <view v-else-if="!entry.children?.length" class="state">暂无可用栏目</view>
    <view v-else class="entry-list"><view v-for="item in entry.children" :key="item.id" class="entry-card" @click="go(item)"><image v-if="item.iconUrl" :src="item.iconUrl" mode="aspectFit" /><view><view>{{ item.title }}</view><view v-if="item.unavailableMessage" class="hint">{{ item.unavailableMessage }}</view></view></view></view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MeetingContentHeader from '@/components/MeetingContentHeader.vue'
import { getPortalHome } from '@/api/portal/meeting'
import { resolveEntryPage } from '@/utils/meetingModules'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'

const activityId = ref('')
const entry = ref({})
const loading = ref(true)
onLoad(options => { captureMpTokenFromQuery(options || {}); activityId.value = options?.activityId || ''; getPortalHome(activityId.value).then(res => { entry.value = findEntry(res.data?.page?.entryTree || [], options?.entryId) || {} }).finally(() => { loading.value = false }) })
function findEntry(entries, id) { for (const item of entries) { if (item.id === id) return item; const found = findEntry(item.children || [], id); if (found) return found } return null }
function go(item) { if (item.available === false) return uni.showToast({ title: item.unavailableMessage || '暂未开放', icon: 'none' }); const url = resolveEntryPage(activityId.value, item); if (url) uni.navigateTo({ url }) }
</script>

<style scoped>.entry-page { min-height: 100vh; background: #f5f7fa; }.entry-list { padding: 110rpx 24rpx 24rpx; }.entry-card { display: flex; gap: 18rpx; align-items: center; margin-bottom: 16rpx; padding: 24rpx; border-radius: 16rpx; background: #fff; }.entry-card image { width: 64rpx; height: 64rpx; }.hint, .state { color: #909399; font-size: 24rpx; }.state { padding: 200rpx 24rpx; text-align: center; }</style>
