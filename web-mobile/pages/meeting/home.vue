<template>
  <view class="home-page" :style="pageThemeStyle">
    <view v-if="audioUrl" class="audio-toggle" @click="toggleAudio">
      {{ audioPlaying ? '暂停音乐' : '播放音乐' }}
    </view>
    <view v-if="loading" class="state">加载中...</view>
    <template v-else>
      <view v-if="isImageMap" class="image-map-page" :style="imageMapStyle">
        <view
          v-for="(block, index) in layout.blocks"
          :key="block.id || index"
          class="image-map-block"
          :style="blockStyle(block)"
          @click="onBlockClick(block)"
        >
          <image v-if="block.iconUrl" class="block-icon" :src="resolveUrl(block.iconUrl)" mode="aspectFit" />
          <text v-if="block.showTitle !== false" class="block-title">{{ block.title }}</text>
        </view>
      </view>

      <view v-else-if="isTile" class="tile-page" :style="tilePageStyle">
        <view class="cover tile-cover" :style="coverStyle"></view>
        <view v-if="layout.showCountdown && countdown" class="tile-countdown">
          <CountdownBoard :countdown="countdown" :theme-color="layout.themeColor" />
        </view>
        <view v-if="layout.notice" class="notice" @click="showNotice">{{ layout.notice }}</view>
        <view class="tile-grid">
          <view
            v-for="item in gridList"
            :key="item.gridId"
            class="tile-item"
            :style="tileStyle(item)"
            @click="onGridClick(item)"
          >
            <text v-if="!item.iconUrl" class="tile-title">{{ item.title }}</text>
          </view>
        </view>
        <view
          v-if="layout.footer.enabled"
          class="tile-footer"
          @click="onFooterClick"
        >
          <image
            v-if="layout.footer.logoUrl"
            class="tile-footer-logo"
            :src="resolveUrl(layout.footer.logoUrl)"
            mode="aspectFit"
          />
          <text v-if="layout.footer.text">{{ layout.footer.text }}</text>
          <text v-if="layout.footer.company" class="tile-footer-name">{{ layout.footer.company }}</text>
        </view>
      </view>

      <view v-else class="standard-page">
        <view class="cover" :style="coverStyle">
          <view class="cover-mask">
            <view class="title">{{ activity.activityName || '会议' }}</view>
            <view v-if="layout.showRegisterCount" class="meta-row">
              已报名 {{ activity.registerCount || 0 }} 人
            </view>
            <view v-if="layout.showCountdown && countdown" class="countdown-wrap">
              <view v-if="countdown.ended" class="countdown-ended">会议进行中</view>
              <view v-else-if="layout.countdownStyle === 'digital'">
                <CountdownBoard
                  :countdown="countdown"
                  :theme-color="layout.themeColor"
                  :show-title="false"
                />
              </view>
              <view v-else-if="layout.countdownStyle === 'simple'" class="countdown-simple">
                距开始 {{ countdown.days }}天 {{ countdown.hours }}时 {{ countdown.minutes }}分
              </view>
              <view v-else class="countdown-classic">
                <text class="countdown-classic-label">距会议开始还有</text>
                <text class="countdown-classic-value">
                  {{ countdown.days }} 天 {{ countdown.hours }} 时 {{ countdown.minutes }} 分
                </text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="layout.notice" class="notice" @click="showNotice">
          {{ layout.notice }}
        </view>

        <view class="section">
          <view class="section-title">
            <text class="section-bar"></text>
            <text>会议菜单</text>
          </view>
          <view class="grid" :class="gridClass">
            <view
              v-for="item in gridList"
              :key="item.gridId"
              class="grid-item"
              :class="{ 'is-icon-only': isIconOnly, 'is-image-card': isImageCard(item) }"
              @click="onGridClick(item)"
            >
              <image
                v-if="isImageCard(item)"
                class="grid-card-image"
                :src="resolveUrl(item.iconUrl)"
                mode="widthFix"
              />
              <template v-else>
                <view class="grid-icon-wrap" :style="gridIconWrapStyle">
                  <MeetingIcon
                    :icon-type="item.iconType"
                    :icon-key="item.iconKey"
                    :icon-url="item.iconUrl"
                    :size="isIconOnly ? 64 : 56"
                    :color="layout.themeColor"
                  />
                </view>
                <text v-if="!isIconOnly" class="grid-text">{{ item.title }}</text>
              </template>
            </view>
            <view v-if="!gridList.length" class="empty">暂无菜单，请在后台配置九宫格</view>
          </view>
        </view>
      </view>

      <view class="bottom-safe"></view>
      <view v-if="bottomList.length" class="bottom-bar">
        <view
          v-for="item in bottomList"
          :key="item.bottomId"
          class="bottom-item"
          @click="onBottomClick(item)"
        >
          <image
            v-if="item.iconUrl"
            class="bottom-icon"
            :src="resolveUrl(item.iconUrl)"
            mode="aspectFit"
          />
          <MeetingIcon
            v-else-if="item.moduleKey"
            icon-type="icon"
            :icon-key="guessBottomIcon(item.moduleKey)"
            :size="36"
            :color="layout.themeColor"
          />
          <text class="bottom-text">{{ item.bottomName }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import MeetingIcon from '@/components/MeetingIcon/MeetingIcon.vue'
import CountdownBoard from '@/components/CountdownBoard/CountdownBoard.vue'
import { getPortalActivity, getPortalGrid, getPortalBottom } from '@/api/portal/meeting'
import { getOauthUrl, buildMockOauthJump } from '@/api/portal/wx'
import { captureMpTokenFromQuery, hasMpToken } from '@/utils/mpAuth'
import { resolveModulePage } from '@/utils/meetingModules'
import { buildHomeLayout, formatCountdownParts } from '@/utils/meetingLayout'
import config from '@/config'

const loading = ref(true)
const activityId = ref('')
const activity = ref({})
const meetingConfig = ref({})
const layout = ref(buildHomeLayout())
const gridList = ref([])
const bottomList = ref([])
const now = ref(Date.now())
let timer = null

const pageThemeStyle = computed(() => ({
  '--theme-color': layout.value.themeColor || '#1f6feb',
  backgroundColor: layout.value.gridTemplate === 'tile' ? '#000' : '#f5f7fa',
  paddingBottom: layout.value.gridTemplate === 'tile' ? '0' : undefined
}))

const coverStyle = computed(() => {
  const url = activity.value.coverUrl
  if (!url) {
    return {
      background: `linear-gradient(135deg, ${layout.value.themeColor || '#1f6feb'}, #0b3d91)`
    }
  }
  const full = url.startsWith('http') ? url : (config.baseUrl + url)
  const isPcViewport = typeof window !== 'undefined' && window.innerWidth >= 750
  return {
    backgroundImage: `url(${full})`,
    backgroundSize: isTile.value ? 'contain' : 'cover',
    backgroundPosition: isTile.value ? 'top center' : 'center',
    backgroundRepeat: 'no-repeat',
    backgroundColor: isTile.value ? '#061a74' : 'transparent',
    height: isTile.value ? (isPcViewport ? '421.875px' : '56.25vw') : undefined
  }
})

const isImageMap = computed(() => layout.value.template === 'image-map')
const isTile = computed(() => layout.value.template === 'standard' && layout.value.gridTemplate === 'tile')
const isIconOnly = computed(() => layout.value.gridStyle === 'icon')
const gridClass = computed(() => `cols-${layout.value.gridColumns || 3}`)
const audioUrl = computed(() => layout.value.audioUrl ? resolveUrl(layout.value.audioUrl) : '')
const audioPlaying = ref(false)
let audioContext = null
const gridIconWrapStyle = computed(() => ({
  background: `${layout.value.themeColor || '#1f6feb'}14`
}))

const tilePageStyle = computed(() => {
  const background = layout.value.backgroundUrl
  return {
    backgroundColor: '#1100ab',
    backgroundImage: background ? `url(${resolveUrl(background)})` : 'none',
    backgroundSize: '100% 100%',
    backgroundPosition: '50% 100%',
    backgroundRepeat: 'no-repeat',
    backgroundAttachment: 'fixed'
  }
})

const imageMapStyle = computed(() => {
  const background = layout.value.backgroundUrl
  return {
    backgroundColor: layout.value.themeColor || '#f5f7fa',
    backgroundImage: background ? `url(${resolveUrl(background)})` : 'none',
    backgroundSize: '100% auto',
    backgroundRepeat: 'no-repeat'
  }
})

const countdown = computed(() => {
  if (!layout.value.showCountdown) return null
  return formatCountdownParts(activity.value.startTime, now.value)
})

onLoad(async (options) => {
  activityId.value = options?.activityId || ''
  captureMpTokenFromQuery(options || {})
  if (!activityId.value) {
    loading.value = false
    uni.showToast({ title: '缺少会议ID', icon: 'none' })
    return
  }
  if (!hasMpToken()) {
    const redirected = await ensureLogin(options)
    if (redirected) return
  }
  await loadHome()
  timer = setInterval(() => { now.value = Date.now() }, 1000)
})

onShow(() => {
  if (activityId.value && hasMpToken()) {
    // keep silent
  }
})

onUnload(() => {
  if (timer) clearInterval(timer)
  if (audioContext) {
    audioContext.stop()
    audioContext.destroy()
    audioContext = null
  }
})

async function ensureLogin() {
  try {
    const redirect = `${location.origin}${location.pathname}#/pages/meeting/home?activityId=${activityId.value}`
    const res = await getOauthUrl(activityId.value, redirect)
    const data = res.data || {}
    if (data.mode === 'mock') {
      window.location.href = buildMockOauthJump(activityId.value, redirect)
      return true
    }
    if (data.url) {
      window.location.href = data.url
      return true
    }
  } catch (e) {
    console.log(e)
  }
  return false
}

async function loadHome() {
  loading.value = true
  try {
    const [actRes, gridRes, bottomRes] = await Promise.all([
      getPortalActivity(activityId.value),
      getPortalGrid(activityId.value),
      getPortalBottom(activityId.value)
    ])
    activity.value = (actRes.data && actRes.data.activity) || {}
    meetingConfig.value = (actRes.data && actRes.data.config) || {}
    layout.value = buildHomeLayout(actRes.data && actRes.data.layout, meetingConfig.value)
    gridList.value = gridRes.data || []
    bottomList.value = bottomRes.data || []
    uni.setNavigationBarTitle({ title: activity.value.activityName || '会议首页' })
    if (layout.value.notice) {
      setTimeout(showNotice, 200)
    }
    initAudio()
  } finally {
    loading.value = false
  }
}

function isImageCard(item) {
  return item && item.iconType === 'image' && !!item.iconUrl && Number(layout.value.gridColumns) === 2
}

function tileStyle(item) {
  const style = {
    gridColumn: `${item.tileCol || 'auto'} / span ${item.tileColSpan || 1}`,
    gridRow: `${item.tileRow || 'auto'} / span ${item.tileRowSpan || 1}`
  }
  if (item.iconUrl) {
    style.backgroundImage = `url(${resolveUrl(item.iconUrl)})`
  }
  return style
}

function initAudio() {
  if (!audioUrl.value || typeof uni.createInnerAudioContext !== 'function') return
  audioContext = uni.createInnerAudioContext()
  audioContext.src = audioUrl.value
  audioContext.loop = layout.value.audioLoop
  audioContext.onPlay(() => { audioPlaying.value = true })
  audioContext.onPause(() => { audioPlaying.value = false })
  audioContext.onStop(() => { audioPlaying.value = false })
  if (layout.value.audioAutoplay) {
    audioContext.play()
  }
}

function toggleAudio() {
  if (!audioContext) {
    initAudio()
  }
  if (!audioContext) return
  if (audioPlaying.value) {
    audioContext.pause()
  } else {
    audioContext.play()
  }
}

function guessBottomIcon(moduleKey) {
  const map = {
    schedule: 'menu_hdgl',
    guest: 'menu_lrtx',
    apply: 'menu_hdbm',
    hotel: 'menu_fcgl',
    venue: 'menu_xqgl',
    nav: 'menu_jkzx',
    exhibitor: 'menu_cxzp',
    meal: 'menu_ywgl'
  }
  return map[moduleKey] || 'menu_home'
}

function onGridClick(item) {
  if (item.linkType === 'url' && item.externalUrl) {
    uni.navigateTo({
      url: `/pages/common/webview/index?title=${encodeURIComponent(item.title)}&url=${encodeURIComponent(item.externalUrl)}`
    })
    return
  }
  if (item.linkType === 'content' && item.contentType === 'image' && (item.contentUrl || item.iconUrl)) {
    uni.navigateTo({
      url: `/pages/common/imageview/index?activityId=${activityId.value}&title=${encodeURIComponent(item.title)}&url=${encodeURIComponent(item.contentUrl || item.iconUrl)}`
    })
    return
  }
  if (item.linkType === 'content' && item.content) {
    uni.navigateTo({
      url: `/pages/common/textview/index?activityId=${activityId.value}&title=${encodeURIComponent(item.title)}&content=${encodeURIComponent(item.content)}`
    })
    return
  }
  if (item.linkType === 'module' && item.moduleKey === 'apply') {
    uni.navigateTo({ url: `/pages/meeting/apply/index?activityId=${activityId.value}` })
    return
  }
  if (item.linkType === 'module' && item.moduleKey) {
    const url = resolveModulePage(activityId.value, item.moduleKey, item.title)
    if (url) {
      uni.navigateTo({ url })
      return
    }
  }
  uni.showToast({ title: '暂无可用操作', icon: 'none' })
}

function onBlockClick(block) {
  onGridClick({
    ...block,
    title: block.title || '会议入口',
    externalUrl: block.externalUrl || block.url
  })
}

function blockStyle(block) {
  return {
    left: `${block.left ?? block.x ?? 0}%`,
    top: `${block.top ?? block.y ?? 0}%`,
    width: `${block.width ?? 25}%`,
    height: `${block.height ?? 10}%`,
    color: block.color || '#303133'
  }
}

function showNotice() {
  if (layout.value.notice) {
    uni.showModal({
      title: '温馨提示',
      content: layout.value.notice,
      showCancel: false
    })
  }
}

function resolveUrl(url) {
  return url && url.startsWith('http') ? url : config.baseUrl + (url || '')
}

function onBottomClick(item) {
  if (item.bottomType === 'phone' && item.phone) {
    uni.makePhoneCall({ phoneNumber: item.phone })
    return
  }
  if (item.bottomType === 'link' && item.linkUrl) {
    uni.navigateTo({
      url: `/pages/common/webview/index?title=${encodeURIComponent(item.bottomName)}&url=${encodeURIComponent(item.linkUrl)}`
    })
    return
  }
  if (item.bottomType === 'module') {
    onGridClick({
      linkType: 'module',
      moduleKey: item.moduleKey,
      title: item.bottomName
    })
    return
  }
  if (item.bottomType === 'text') {
    uni.showModal({ title: item.bottomName, content: item.remark || item.linkUrl || '暂无内容', showCancel: false })
  }
}

function onFooterClick() {
  if (layout.value.footer.linkUrl) {
    uni.navigateTo({
      url: `/pages/common/webview/index?title=${encodeURIComponent(layout.value.footer.company || layout.value.footer.text || '会务支持')}&url=${encodeURIComponent(layout.value.footer.linkUrl)}`
    })
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 120rpx;
}
:global(html),
:global(body),
:global(#app),
:global(uni-app),
:global(uni-page),
:global(uni-page-wrapper),
:global(uni-page-body) {
  min-height: 100%;
  overflow: visible !important;
}
.audio-toggle {
  position: fixed;
  top: 24rpx;
  right: 24rpx;
  z-index: 30;
  width: 58rpx;
  height: 58rpx;
  padding: 0;
  border: 2rpx solid rgba(255, 255, 255, 0.88);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: rgba(0, 0, 0, 0.42);
  font-size: 0;
}
.audio-toggle::before {
  content: '♫';
  font-size: 30rpx;
  line-height: 1;
}
.standard-page { min-height: 100vh; }
.tile-page {
  min-height: 100vh;
  background: #061a74;
}
.tile-cover {
  height: 56.25vw;
  min-height: 0;
  background-color: #061a74;
  background-size: cover;
  background-position: top center;
  background-repeat: no-repeat;
}
.tile-countdown {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24rpx 18rpx;
  background: #061a74;
}
.tile-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  grid-template-rows: 18vw repeat(3, 20.7vw);
  gap: 0;
  padding: 18rpx;
  background: #061a74;
}
.tile-item {
  min-width: 0;
  margin: 6rpx;
  border-radius: 19.2px;
  background-color: #0b2c9c;
  background-position: center;
  background-repeat: no-repeat;
  background-size: 100% 100%;
}
.tile-title {
  display: flex;
  min-height: 100%;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  text-align: center;
}
@media screen and (min-width: 750px) {
  .tile-page {
    width: 768px;
    margin: 0 auto;
  }
  .tile-cover {
    height: 432px;
  }
  .tile-grid {
    gap: 0;
    padding: 16px 20px 0;
    grid-template-rows: 147px 167px 167px 167px;
  }
  .tile-item {
    margin: 6px;
  }
}
.tile-footer {
  min-height: 92px;
  box-sizing: border-box;
  margin-top: 15px;
  padding: 24rpx 0;
  color: rgba(255, 255, 255, 0.92);
  background: rgba(0, 0, 0, 0.2);
  font-size: 24rpx;
  text-align: center;
}
.tile-footer-name {
  margin-left: 12rpx;
  font-weight: 600;
}
.tile-footer-logo {
  width: 42rpx;
  height: 34rpx;
  margin-right: 10rpx;
  vertical-align: middle;
}
.cover { height: 380rpx; position: relative; }
.cover-mask {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 32rpx;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.58));
}
.title { color: #fff; font-size: 40rpx; font-weight: 700; line-height: 1.35; }
.meta-row { margin-top: 10rpx; color: rgba(255, 255, 255, 0.92); font-size: 24rpx; }
.countdown-wrap { margin-top: 16rpx; }
.countdown-ended,
.countdown-classic-label,
.countdown-classic-value,
.countdown-simple { color: #fff; }
.countdown-classic {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}
.countdown-classic-label { font-size: 22rpx; opacity: 0.9; }
.countdown-classic-value { font-size: 28rpx; font-weight: 600; }
.countdown-simple {
  display: inline-flex;
  padding: 10rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.2);
  font-size: 24rpx;
}
.notice {
  margin: 20rpx 24rpx 0;
  padding: 20rpx 24rpx;
  border-radius: 12rpx;
  color: #8a5a00;
  background: #fff7e6;
  border-left: 6rpx solid var(--theme-color, #1f6feb);
  font-size: 25rpx;
  line-height: 1.5;
}
.section {
  margin: 24rpx;
  padding: 24rpx;
  border-radius: 16rpx;
  background: #fff;
  box-shadow: 0 8rpx 24rpx rgba(15, 35, 80, 0.04);
}
.section-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #303133;
}
.section-bar {
  width: 8rpx;
  height: 28rpx;
  border-radius: 8rpx;
  background: var(--theme-color, #1f6feb);
}
.grid { display: flex; flex-wrap: wrap; }
.grid-item {
  box-sizing: border-box;
  padding: 20rpx 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.cols-1 .grid-item { width: 100%; flex-direction: row; justify-content: flex-start; padding: 22rpx 12rpx; gap: 20rpx; }
.cols-2 .grid-item { width: 50%; }
.cols-3 .grid-item { width: 33.33%; }
.grid-item.is-image-card {
  padding: 10rpx;
}
.grid-card-image {
  width: 100%;
  border-radius: 20rpx;
  display: block;
  box-shadow: 0 8rpx 20rpx rgba(30, 75, 181, 0.12);
}
.grid-icon-wrap {
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cols-1 .grid-icon-wrap { width: 88rpx; height: 88rpx; }
.is-icon-only .grid-icon-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: 32rpx;
}
.grid-text { font-size: 24rpx; color: #303133; text-align: center; }
.cols-1 .grid-text { font-size: 28rpx; font-weight: 500; }
.empty, .state { padding: 80rpx 24rpx; text-align: center; color: #909399; font-size: 28rpx; }
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  display: flex;
  background: #fff;
  border-top: 1px solid #ebeef5;
  padding-bottom: env(safe-area-inset-bottom);
}
.bottom-item {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 14rpx 8rpx 12rpx;
  color: #606266;
}
.bottom-icon { width: 36rpx; height: 36rpx; }
.bottom-text {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 22rpx;
}
.bottom-safe { height: 20rpx; }
.image-map-page {
  position: relative;
  min-height: 56.25vw;
  padding-bottom: 120rpx;
  background-position: top center;
}
.image-map-block {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  padding: 8rpx;
  text-align: center;
}
.block-title { font-size: 26rpx; font-weight: 600; line-height: 1.25; }
.block-icon { width: 48rpx; height: 48rpx; margin-right: 8rpx; }
</style>
