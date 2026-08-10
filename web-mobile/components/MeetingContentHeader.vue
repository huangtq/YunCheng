<template>
  <view class="meeting-content-header">
    <view class="sub-header">
      <view class="breadcrumb" @click="goHome">
        <text class="home-icon">⌂</text>
        <text>首页</text>
        <text class="separator">›</text>
        <text class="current-title">{{ title }}</text>
      </view>
      <view class="menu-button" @click="drawerOpen = !drawerOpen">{{ drawerOpen ? '×' : '☰' }}</view>
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
        <view v-if="!gridItems.length" class="drawer-empty">暂无菜单</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getPortalGrid } from '@/api/portal/meeting'
import { openMeetingItem } from '@/utils/meetingNavigation'

const props = defineProps({
  title: {
    type: String,
    default: '内容'
  },
  activityId: {
    type: [String, Number],
    default: ''
  }
})

const drawerOpen = ref(false)
const gridItems = ref([])

watch(
  () => props.activityId,
  (id) => {
    loadGrid(id)
  },
  { immediate: true }
)

function loadGrid(id) {
  if (!id) {
    gridItems.value = []
    return
  }
  getPortalGrid(id)
    .then((res) => { gridItems.value = res.data || [] })
    .catch(() => { gridItems.value = [] })
}

function goHome() {
  if (props.activityId) {
    uni.redirectTo({ url: `/pages/meeting/home?activityId=${props.activityId}` })
  } else {
    uni.navigateBack()
  }
}

function selectMenu(item) {
  drawerOpen.value = false
  openMeetingItem(props.activityId, item, { replace: true })
}
</script>

<style lang="scss" scoped>
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
  max-width: 52vw;
  font-weight: 600;
}
.menu-button {
  flex: 0 0 auto;
  padding: 6px;
  font-size: 20px;
  line-height: 1;
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
  background: #fff;
  box-shadow: 8px 0 24px rgba(0, 0, 0, 0.16);
  overflow-y: auto;
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
.drawer-empty {
  padding: 28px 20px;
  color: #909399;
  font-size: 14px;
}
</style>
