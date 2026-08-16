<template>
  <view class="pdf-page">
    <MeetingContentHeader :title="title" :activity-id="activityId" />
    <view v-if="!pdfUrl" class="state">暂无 PDF 文件</view>
    <template v-else>
      <view class="pdf-toolbar">
        <text class="pdf-name">{{ title }}</text>
        <button class="download-button" size="mini" @click="downloadPdf">下载 PDF</button>
      </view>
      <!-- #ifdef H5 -->
      <iframe v-if="viewerUrl" class="pdf-viewer" :src="viewerUrl" title="PDF预览" />
      <!-- #endif -->
      <!-- #ifndef H5 -->
      <web-view v-if="viewerUrl" class="pdf-viewer" :src="viewerUrl" />
      <!-- #endif -->
      <view v-else class="loading-state">正在加载 PDF...</view>
    </template>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MeetingContentHeader from '@/components/MeetingContentHeader.vue'
import config from '@/config'
import { mergeLocationQuery } from '@/utils/h5Route'

const activityId = ref('')
const title = ref('PDF文件')
const fileUrl = ref('')
const viewerUrl = ref('')
const pdfUrl = computed(() => {
  if (!fileUrl.value) return ''
  return /^https?:\/\//i.test(fileUrl.value) ? fileUrl.value : config.baseUrl + fileUrl.value
})

function prepareViewer() {
  if (!pdfUrl.value) return
  viewerUrl.value = pdfUrl.value
}

onLoad(options => {
  const query = mergeLocationQuery(options || {})
  activityId.value = query.activityId || ''
  title.value = decodeURIComponent(query.title || 'PDF文件')
  fileUrl.value = decodeURIComponent(query.url || '')
  prepareViewer()
})

function downloadPdf() {
  if (!pdfUrl.value) return
  // #ifdef H5
  window.open(pdfUrl.value, '_blank')
  // #endif
  // #ifndef H5
  uni.downloadFile({
    url: pdfUrl.value,
    success: ({ tempFilePath }) => uni.openDocument({ filePath: tempFilePath, showMenu: true }),
    fail: () => uni.showToast({ title: 'PDF 下载失败', icon: 'none' })
  })
  // #endif
}
</script>

<style lang="scss" scoped>
.pdf-page {
  min-height: 100vh;
  background: #f5f7fa;
}
.pdf-toolbar {
  position: fixed;
  top: 80rpx;
  right: 0;
  left: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 16rpx;
  box-sizing: border-box;
  padding: 12rpx 24rpx;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.pdf-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  color: #303133;
  font-size: 26rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.download-button {
  flex: 0 0 auto;
  margin: 0;
  padding: 0 18rpx;
  color: #fff;
  background: #1f6feb;
  border-radius: 6rpx;
  font-size: 24rpx;
  line-height: 56rpx;
}
.download-button::after {
  border: 0;
}
.pdf-viewer {
  position: fixed;
  top: 152rpx;
  right: 0;
  bottom: 0;
  left: 0;
  display: block;
  width: 100%;
  height: calc(100vh - 152rpx);
  border: 0;
}
.state {
  padding-top: 260rpx;
  color: #909399;
  text-align: center;
}
.loading-state {
  padding-top: 260rpx;
  color: #909399;
  text-align: center;
}
</style>
