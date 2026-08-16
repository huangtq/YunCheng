<template>
  <view class="content-page">
    <view class="sub-header">
      <view class="breadcrumb" @click="goHome">
        <text class="home-icon">⌂</text>
        <text>首页</text>
        <text class="separator">›</text>
        <text class="current-title">{{ title }}</text>
      </view>
      <view class="menu-button" @click="drawerOpen = !drawerOpen">{{ drawerOpen ? '×' : '☰' }}</view>
    </view>

    <view class="sub-content">
      <view class="content-card">
        <rich-text class="content-body" :nodes="content" />
      </view>
      <view v-if="attachments.length" class="attachments-card">
        <view class="attachments-title">附件</view>
        <view
          v-for="item in attachments"
          :key="item.attachmentId || item.fileUrl"
          class="attachment-item"
          @click="openAttachment(item)"
        >
          <view class="attachment-main">
            <text class="attachment-name">{{ item.displayName || item.fileName || '附件' }}</text>
            <text v-if="item.fileSize" class="attachment-size">{{ formatSize(item.fileSize) }}</text>
          </view>
          <text class="attachment-action">下载</text>
        </view>
      </view>
    </view>

    <view v-if="drawerOpen" class="drawer-mask" @click="drawerOpen = false">
      <view class="drawer" @click.stop>
        <view
          v-for="item in gridItems"
          :key="item.gridId"
          class="drawer-item"
          @click="selectMenu(item)"
        >
          {{ item.title }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getPortalGrid } from '@/api/portal/meeting'
import { openMeetingItem } from '@/utils/meetingNavigation'
import { setupMeetingShare } from '@/utils/wxShare'
import { getMpToken } from '@/utils/mpAuth'
import config from '@/config'
import { mergeLocationQuery } from '@/utils/h5Route'

const title = ref('内容')
const content = ref('')
const activityId = ref('')
const gridId = ref('')
const drawerOpen = ref(false)
const gridItems = ref([])
const attachments = ref([])

onLoad(options => {
  const query = mergeLocationQuery(options || {})
  title.value = decodeURIComponent(query.title || '内容')
  content.value = decodeURIComponent(query.content || '')
  activityId.value = query.activityId || ''
  gridId.value = query.gridId || ''
  if (activityId.value) setupMeetingShare(activityId.value)
  if (!activityId.value) return
  getPortalGrid(activityId.value)
    .then(res => {
      gridItems.value = res.data || []
      const currentItem = gridItems.value.find(item => normalizeGridId(item.gridId) === normalizeGridId(gridId.value))
      if (currentItem?.content) {
        content.value = currentItem.content
      }
      attachments.value = normalizeAttachments(currentItem?.attachments || [])
    })
    .catch(() => { gridItems.value = [] })
})

function normalizeGridId(value) {
  return String(value || '').replace(/^(legacy-)?grid-/, '')
}

function normalizeAttachments(items) {
  return (items || []).map(item => ({
    ...item,
    downloadUrl: item.downloadUrl || (item.attachmentId && gridId.value
      ? `/portal/meeting/grid/${activityId.value}/attachment/${normalizeGridId(gridId.value)}/${item.attachmentId}`
      : item.fileUrl)
  })).filter(item => item.downloadUrl)
}

function openAttachment(item) {
  const url = item.downloadUrl
  if (!url) return
  uni.downloadFile({
    url: /^https?:\/\//i.test(url) ? url : config.baseUrl + url,
    header: getMpToken() ? { 'Mp-Authorization': `Bearer ${getMpToken()}` } : {},
    success: ({ tempFilePath }) => uni.openDocument({ filePath: tempFilePath, showMenu: true }),
    fail: () => uni.showToast({ title: '附件下载失败', icon: 'none' })
  })
}

function formatSize(size) {
  const bytes = Number(size || 0)
  if (!bytes) return ''
  if (bytes < 1024 * 1024) return `${Math.ceil(bytes / 1024)} KB`
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

onShow(() => {
  if (activityId.value) setupMeetingShare(activityId.value)
})

function goHome() {
  if (activityId.value) {
    uni.redirectTo({ url: `/pages/meeting/home?activityId=${activityId.value}` })
  } else {
    uni.navigateBack()
  }
}

function selectMenu(item) {
  drawerOpen.value = false
  openMeetingItem(activityId.value, item, { replace: true })
}
</script>

<style lang="scss" scoped>
.content-page {
  min-height: 100vh;
  background: #fff;
}
.sub-header {
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  z-index: 20;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  padding: 0 24px;
  color: #fff;
  background: #5181f1;
}
.breadcrumb {
  display: flex;
  align-items: center;
  min-width: 0;
  font-size: 26px;
  white-space: nowrap;
}
.home-icon { margin-right: 8px; font-size: 24px; }
.separator { margin: 0 10px; opacity: 0.9; }
.current-title { overflow: hidden; text-overflow: ellipsis; }
.menu-button { padding: 8px; font-size: 28px; line-height: 1; }
.sub-content {
  box-sizing: border-box;
  width: 500px;
  max-width: 100%;
  min-height: 100vh;
  margin: 0 auto;
  padding: 100px 20px 60px;
}
.content-card {
  padding: 24px;
  background: #fff;
}
.attachments-card {
  margin-top: 16px;
  padding: 20px 24px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}
.attachments-title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}
.attachment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 0;
  border-top: 1px solid #f0f2f5;
}
.attachment-main {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}
.attachment-name {
  overflow: hidden;
  color: #303133;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.attachment-size {
  color: #909399;
  font-size: 12px;
}
.attachment-action {
  flex: 0 0 auto;
  color: #1f6feb;
  font-size: 13px;
}
.content-body {
  display: block;
  color: #303133;
  font-size: 17px;
  line-height: 1.8;
}
.content-body :deep(img) {
  display: block;
  max-width: 100%;
  height: auto;
}
.content-body :deep(p) {
  margin: 0 0 16px;
}
.drawer-mask {
  position: fixed;
  top: 80px;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 30;
  background: rgba(0, 0, 0, 0.38);
}
.drawer {
  width: min(400px, 82vw);
  height: 100%;
  padding-top: 0;
  background: #fff;
  box-shadow: 8px 0 24px rgba(0, 0, 0, 0.16);
}
.drawer-title {
  padding: 20px 24px;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}
.drawer-item {
  box-sizing: border-box;
  min-height: 72px;
  display: flex;
  align-items: center;
  padding: 16px 24px;
  color: #606266;
  font-size: 17px;
  border-top: 1px solid #ebeef5;
}
@media screen and (max-width: 750px) {
  .sub-header { height: 56px; padding: 0 16px; }
  .breadcrumb { font-size: 18px; }
  .home-icon { font-size: 18px; }
  .menu-button { font-size: 22px; }
  .drawer-mask { top: 56px; }
  .sub-content { padding-top: 72px; padding-right: 12px; padding-left: 12px; }
}
</style>
