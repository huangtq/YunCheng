<template>
  <view class="countdown-board" :style="{ '--countdown-color': props.themeColor }">
    <view v-if="props.showTitle" class="countdown-heading">
      <text class="heading-line"></text>
      <text class="heading-text">距会议开始还有</text>
      <text class="heading-line"></text>
    </view>
    <view class="countdown-groups">
      <view v-for="item in groups" :key="item.key" class="countdown-group">
        <view class="flip-pair">
          <view class="flip-sprite" :style="digitStyle(item.value[0])"></view>
          <view class="flip-sprite" :style="digitStyle(item.value[1])"></view>
        </view>
        <text class="group-label">{{ item.label }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  countdown: {
    type: Object,
    default: () => ({ days: 0, hours: 0, minutes: 0, seconds: 0 })
  },
  themeColor: {
    type: String,
    default: '#1f6feb'
  },
  showTitle: {
    type: Boolean,
    default: true
  }
})

function pair(value) {
  return String(Math.max(0, Number(value) || 0)).padStart(2, '0').slice(-2)
}

function digitStyle(digit) {
  const value = Math.min(9, Math.max(0, Number(digit) || 0))
  return {
    backgroundImage: 'url(/static/countdown/jcountdown_flip_white.png)',
    backgroundPosition: `${-(9 - value) * 50}px -896px`
  }
}

const groups = computed(() => [
  { key: 'days', value: pair(props.countdown.days), label: 'Days' },
  { key: 'hours', value: pair(props.countdown.hours), label: 'Hours' },
  { key: 'minutes', value: pair(props.countdown.minutes), label: 'Minutes' },
  { key: 'seconds', value: pair(props.countdown.seconds), label: 'Seconds' }
])
</script>

<style lang="scss" scoped>
.countdown-board {
  width: 100%;
  padding: 24rpx 0 18rpx;
  color: #fff;
}
.countdown-heading,
.countdown-groups {
  position: static;
}
.countdown-heading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 28rpx;
  margin-bottom: 20rpx;
}
.heading-line {
  width: 72rpx;
  height: 3rpx;
  background: rgba(255, 255, 255, 0.92);
}
.heading-text {
  font-size: 34rpx;
  font-weight: 500;
  letter-spacing: 3rpx;
}
.countdown-groups {
  display: flex;
  justify-content: center;
  gap: 24rpx;
}
.countdown-group {
  min-width: 132rpx;
  text-align: center;
}
.flip-pair {
  display: flex;
  gap: 2px;
  justify-content: center;
}
.flip-sprite {
  width: 50px;
  height: 64px;
  flex: 0 0 50px;
  background-repeat: no-repeat;
}
.group-label {
  display: block;
  margin-top: 12rpx;
  color: rgba(255, 255, 255, 0.98);
  font-size: 21rpx;
  letter-spacing: 0.5rpx;
  line-height: 1.2;
}
@media screen and (min-width: 750px) {
  .countdown-board { padding-top: 22px; }
  .countdown-heading { margin-bottom: 20px; gap: 24px; }
  .heading-line { width: 52px; }
  .heading-text { font-size: 26px; }
  .countdown-groups { gap: 15px; }
  .countdown-group { min-width: 136px; }
  .group-label { margin-top: 8px; font-size: 14px; }
}
@media screen and (max-width: 480px) {
  .countdown-groups {
    transform: scale(0.72);
    transform-origin: center top;
    margin-bottom: -18px;
  }
}
</style>
