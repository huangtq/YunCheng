<template>
  <view class="content-page">
    <MeetingContentHeader :title="content.title || '内容详情'" :activity-id="activityId" />
    <view v-if="loading" class="state">加载中...</view>
    <view v-else-if="locked" class="state">该内容需要登录或报名后查看</view>
    <view v-else class="content-body">
      <image v-if="content.coverUrl" class="cover" :src="resolveUrl(content.coverUrl)" mode="widthFix" />
      <view class="summary" v-if="content.summary">{{ content.summary }}</view>
      <rich-text :nodes="content.contentHtml || ''" />
      <view v-if="content.attachments?.length" class="attachments">
        <view class="attachment" v-for="item in content.attachments" :key="item.attachmentId" @click="openAttachment(item)">
          <text>{{ item.fileName }}</text><text class="size">{{ formatSize(item.fileSize) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MeetingContentHeader from '@/components/MeetingContentHeader.vue'
import { getPortalContent } from '@/api/portal/meeting'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'
import { getMpToken } from '@/utils/mpAuth'
import config from '@/config'

const activityId = ref('')
const contentId = ref('')
const content = ref({})
const loading = ref(true)
const locked = ref(false)

onLoad(options => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  contentId.value = options?.contentId || ''
  if (!activityId.value || !contentId.value) return (loading.value = false)
  getPortalContent(activityId.value, contentId.value).then(res => { content.value = res.data || {} }).catch(() => { locked.value = true }).finally(() => { loading.value = false })
})

function resolveUrl(url) { return url && /^https?:\/\//i.test(url) ? url : url }
function openAttachment(item) {
  if (!item.downloadUrl) return
  uni.downloadFile({
    url: /^https?:\/\//i.test(item.downloadUrl) ? item.downloadUrl : config.baseUrl + item.downloadUrl,
    header: getMpToken() ? { 'Mp-Authorization': `Bearer ${getMpToken()}` } : {},
    success: ({ tempFilePath }) => uni.openDocument({ filePath: tempFilePath, showMenu: true }),
    fail: () => uni.showToast({ title: '文件下载失败', icon: 'none' })
  })
}
function formatSize(size) { return size ? `${Math.ceil(Number(size) / 1024)} KB` : '' }
</script>

<style lang="scss" scoped>
.content-page { min-height: 100vh; background: #fff; }
.content-body { padding: 110rpx 30rpx 50rpx; color: #303133; line-height: 1.8; }
.cover { width: 100%; margin-bottom: 24rpx; }
.summary { margin-bottom: 24rpx; color: #909399; }
.attachments { margin-top: 40rpx; border-top: 1px solid #ebeef5; }
.attachment { display: flex; justify-content: space-between; padding: 24rpx 0; border-bottom: 1px solid #ebeef5; }
.size { color: #909399; font-size: 24rpx; }
.state { padding: 200rpx 30rpx; color: #909399; text-align: center; }
</style>
