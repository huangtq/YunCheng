<template>
  <view class="image-page">
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
      <image
        v-for="(item, index) in imageUrls"
        :key="`${item}-${index}`"
        class="content-image"
        :src="resolveUrl(item)"
        mode="widthFix"
      />
      <view v-if="!imageUrls.length" class="state">暂无图片内容</view>
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
import config from '@/config'
import { getPortalGrid } from '@/api/portal/meeting'
import { openMeetingItem } from '@/utils/meetingNavigation'
import { mergeLocationQuery } from '@/utils/h5Route'
import { setupMeetingShare } from '@/utils/wxShare'

const imageUrls = ref([])
const title = ref('图片内容')
const activityId = ref('')
const drawerOpen = ref(false)
const gridItems = ref([])
let lastQueryKey = ''

onLoad((options) => {
  loadPage(options || {})
})

onShow(() => {
  const pages = typeof getCurrentPages === 'function' ? getCurrentPages() : []
  const current = pages.length ? pages[pages.length - 1] : null
  const options = (current && (current.options || (current.$page && current.$page.options))) || {}
  const key = `${options.activityId || ''}|${options.gridId || ''}|${options.url || ''}|${options.title || ''}`
  if (key && key !== lastQueryKey) {
    loadPage(options)
  } else if (activityId.value) {
    setupMeetingShare(activityId.value)
  }
})

async function loadPage(options) {
  const query = readQuery(options || {})
  const nextTitle = decodeURIComponent(query.title || '图片内容')
  const nextActivityId = query.activityId || ''
  const gridId = query.gridId || ''
  const fallbackUrl = query.url ? decodeURIComponent(query.url) : ''
  const queryKey = `${nextActivityId}|${gridId}|${query.url || ''}|${query.title || ''}`
  lastQueryKey = queryKey

  title.value = nextTitle
  activityId.value = nextActivityId
  drawerOpen.value = false
  if (activityId.value) setupMeetingShare(activityId.value)

  if (activityId.value) {
    try {
      const res = await getPortalGrid(activityId.value)
      gridItems.value = res.data || []
    } catch (e) {
      gridItems.value = []
    }
  } else {
    gridItems.value = []
  }

  let urls = []
  if (gridId && gridItems.value.length) {
    const item = gridItems.value.find((g) => String(g.gridId) === String(gridId))
    if (item) {
      if (item.title) title.value = item.title
      urls = parseContentImages(item.content, item.contentUrl)
    }
  }
  if (!urls.length && fallbackUrl) {
    urls = [fallbackUrl]
  }
  imageUrls.value = urls
}

function readQuery(options = {}) {
  return mergeLocationQuery(options)
}

function parseContentImages(content, contentUrl) {
  const urls = []
  const raw = (content || '').trim()
  if (raw) {
    if (raw.startsWith('[')) {
      try {
        const parsed = JSON.parse(raw)
        if (Array.isArray(parsed)) {
          parsed.forEach((item) => {
            if (typeof item === 'string' && item) urls.push(item)
            else if (item && item.url) urls.push(item.url)
          })
        }
      } catch (e) {
        // ignore json parse error and continue
      }
    }
    if (!urls.length) {
      const matches = [...raw.matchAll(/src=["']([^"']+)["']/gi)]
      matches.forEach((m) => urls.push(m[1]))
    }
    if (!urls.length && /https?:\/\//.test(raw)) {
      raw.split(/[\n,]+/).map((s) => s.trim()).filter((s) => /^https?:\/\//.test(s)).forEach((s) => urls.push(s))
    }
  }
  if (!urls.length && contentUrl) urls.push(contentUrl)
  return [...new Set(urls)]
}

function resolveUrl(value) {
  return value && value.startsWith('http') ? value : config.baseUrl + (value || '')
}

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
.image-page {
  min-height: 100vh;
  background: #f6f6f6;
}
.sub-header {
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  z-index: 20;
  height: 49px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  padding: 0 14px;
  color: #001a4d;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.breadcrumb {
  display: flex;
  align-items: center;
  min-width: 0;
  font-size: 15px;
  white-space: nowrap;
}
.home-icon {
  margin-right: 6px;
  font-size: 16px;
}
.separator {
  margin: 0 8px;
  opacity: 0.7;
}
.current-title {
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 600;
}
.menu-button {
  flex: 0 0 auto;
  padding: 6px;
  font-size: 20px;
  line-height: 1;
}
.sub-content {
  box-sizing: border-box;
  width: 100%;
  max-width: 750px;
  min-height: 100vh;
  margin: 0 auto;
  padding: 49px 0 24px;
  background: #fff;
}
.content-image {
  width: 100%;
  display: block;
  height: auto;
  vertical-align: top;
}
.state {
  padding-top: 240rpx;
  color: #909399;
  font-size: 28rpx;
  text-align: center;
}
.drawer-mask {
  position: fixed;
  top: 49px;
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
.drawer-item {
  box-sizing: border-box;
  min-height: 56px;
  display: flex;
  align-items: center;
  padding: 14px 20px;
  color: #606266;
  font-size: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
