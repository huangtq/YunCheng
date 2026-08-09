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
        v-if="url"
        class="content-image"
        :src="resolveUrl(url)"
        mode="widthFix"
        @error="onError"
      />
      <view v-else class="state">暂无图片内容</view>
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
import { onLoad } from '@dcloudio/uni-app'
import config from '@/config'
import { getPortalGrid } from '@/api/portal/meeting'
import { openMeetingItem } from '@/utils/meetingNavigation'

const url = ref('')
const title = ref('图片内容')
const activityId = ref('')
const drawerOpen = ref(false)
const gridItems = ref([])

onLoad(options => {
  url.value = options?.url || ''
  title.value = decodeURIComponent(options?.title || '图片内容')
  activityId.value = options?.activityId || ''
  if (!activityId.value) return
  getPortalGrid(activityId.value)
    .then(res => { gridItems.value = res.data || [] })
    .catch(() => { gridItems.value = [] })
})

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

function onError() {
  uni.showToast({ title: '图片加载失败', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.image-page {
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
.home-icon {
  margin-right: 8px;
  font-size: 24px;
}
.separator {
  margin: 0 10px;
  opacity: 0.9;
}
.current-title {
  overflow: hidden;
  text-overflow: ellipsis;
}
.menu-button {
  flex: 0 0 auto;
  padding: 8px;
  font-size: 28px;
  line-height: 1;
}
.sub-content {
  box-sizing: border-box;
  width: 500px;
  max-width: 100%;
  min-height: 100vh;
  margin: 0 auto;
  padding: 100px 20px 60px;
  background: #fff;
  text-align: center;
}
.content-image {
  width: 100%;
  display: block;
  height: auto;
}
.state {
  padding-top: 240rpx;
  color: #fff;
  font-size: 28rpx;
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
  .sub-header {
    height: 56px;
    padding: 0 16px;
  }
  .breadcrumb {
    font-size: 18px;
  }
  .home-icon {
    font-size: 18px;
  }
  .menu-button {
    font-size: 22px;
  }
  .drawer-mask {
    top: 56px;
  }
  .sub-content {
    padding-top: 72px;
    padding-right: 12px;
    padding-left: 12px;
  }
}
</style>
