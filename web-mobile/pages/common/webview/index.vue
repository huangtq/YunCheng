<template>
  <view v-if="params.url">
    <web-view :webview-styles="webviewStyles" :src="`${params.url}`"></web-view>
  </view>
</template>

<script>
  import { setupMeetingShare } from '@/utils/wxShare'

  export default {
    data() {
      return {
        params: {},
        webviewStyles: {
          progress: {
            color: "#FF3333"
          }
        }
      }
    },
    props: {
      src: {
        type: [String],
        default: null
      }
    },
    onLoad(event) {
      this.params = event || {}
      // 会议场景下网站标题始终用会议名，不随外链菜单名变化
      if (event && event.activityId) {
        setupMeetingShare(event.activityId)
      } else if (event && event.title) {
        uni.setNavigationBarTitle({
          title: event.title
        })
      }
    },
    onShow() {
      if (this.params && this.params.activityId) {
        setupMeetingShare(this.params.activityId)
      }
    }
  }
</script>
