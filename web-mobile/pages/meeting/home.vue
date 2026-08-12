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

      <view v-else-if="isTile" class="tile-page" :class="{ 'is-light-tile': isLightTile }" :style="tilePageStyle">
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
            :class="{
              'is-color-tile': isColorTile(item),
              'is-tall-color-tile': isTallColorTile(item)
            }"
            :style="tileStyle(item)"
            @click="onGridClick(item)"
          >
            <text v-if="!item.iconUrl || isColorTile(item)" class="tile-title">{{ item.title }}</text>
            <image
              v-if="isColorTile(item) && item.iconUrl"
              class="tile-icon"
              :src="resolveUrl(item.iconUrl)"
              mode="aspectFit"
            />
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

      <view v-else class="standard-page" :class="{ 'is-image-card-grid': isImageCardGrid }" :style="standardPageStyle">
        <view class="cover" :style="coverStyle">
          <image
            v-if="isImageCardGrid && (layout.heroUrl || activity.coverUrl)"
            class="cover-auto-image"
            :src="resolveUrl(layout.heroUrl || activity.coverUrl)"
            mode="widthFix"
          />
          <view v-if="layout.showCoverMeta" class="cover-mask">
            <view class="title">{{ activity.activityName || '会议' }}</view>
            <view v-if="layout.showRegisterCount" class="meta-row">
              已报名 {{ activity.registerCount || 0 }} 人
            </view>
            <view v-if="layout.showCountdown && countdown && !isImageCardGrid" class="countdown-wrap">
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

        <view v-if="layout.showCountdown && countdown && isImageCardGrid" class="image-card-countdown">
          <CountdownBoard :countdown="countdown" :theme-color="layout.themeColor" />
        </view>

        <view v-if="layout.notice" class="notice" @click="showNotice">
          {{ layout.notice }}
        </view>

        <view v-for="section in entrySections" :key="section.key" class="section" :class="`section-${section.key}`">
          <view v-if="layout.showSectionTitle" class="section-title">
            <text class="section-bar"></text>
            <text>{{ section.label || '会议菜单' }}</text>
          </view>
          <view class="grid" :class="gridClassFor(section)">
            <view
              v-for="item in section.entries"
              :key="item.gridId"
              class="grid-item"
              :class="{ 'is-icon-only': isIconOnlyFor(section), 'is-image-card': isImageCard(item, section), 'is-tall-image-card': section.ratio === 'tall' }"
              @click="onGridClick(item)"
            >
              <image
                v-if="isImageCard(item)"
                class="grid-card-image"
                :src="resolveUrl(gridCardUrl(item))"
                mode="widthFix"
              />
              <template v-else>
                <view class="grid-icon-wrap" :style="gridIconWrapStyle">
                  <MeetingIcon
                    :icon-type="item.iconType"
                    :icon-key="item.iconKey"
                    :icon-url="item.iconUrl"
                    :size="isIconOnlyFor(section) ? 64 : 56"
                    color="#fff"
                  />
                </view>
                <text v-if="!isIconOnlyFor(section)" class="grid-text">{{ item.title }}</text>
              </template>
            </view>
            <view v-if="!section.entries.length" class="empty">暂无菜单，请在后台配置</view>
          </view>
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
import { getPortalHome, recordMeetingEvent } from '@/api/portal/meeting'
import { captureMpTokenFromQuery } from '@/utils/mpAuth'
import { resolveEntryPage, resolveModulePage } from '@/utils/meetingModules'
import { buildHomeLayout, formatCountdownParts } from '@/utils/meetingLayout'
import { setupMeetingShare } from '@/utils/wxShare'
import config from '@/config'

const loading = ref(true)
const activityId = ref('')
const activity = ref({})
const meetingConfig = ref({})
const layout = ref(buildHomeLayout())
const gridList = ref([])
const entrySections = ref([])
const now = ref(Date.now())
let timer = null

const pageThemeStyle = computed(() => ({
  '--theme-color': layout.value.themeColor || '#1f6feb',
  backgroundColor: layout.value.gridTemplate === 'tile'
    ? (isLightTile.value ? tileSurfaceColor.value : '#000')
    : '#f5f7fa',
  paddingBottom: layout.value.gridTemplate === 'tile' ? '0' : undefined
}))

const coverStyle = computed(() => {
  const url = layout.value.heroUrl || activity.value.coverUrl
  const tileBg = tileSurfaceColor.value
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
    backgroundColor: isTile.value ? tileBg : 'transparent',
    height: isTile.value ? (isPcViewport ? '421.875px' : '56.25vw') : undefined
  }
})

const isImageMap = computed(() => layout.value.template === 'image-map')
const isTile = computed(() => layout.value.template === 'standard' && layout.value.gridTemplate === 'tile')
const isLightTile = computed(() => isTile.value && isLightColor(layout.value.themeColor))
const tileSurfaceColor = computed(() => (isLightTile.value ? (layout.value.themeColor || '#f6f6f6') : '#061a74'))
const isImageCardGrid = computed(() => entrySections.value.some(section => Number(section.columns) === 2 && section.entries.some(item => item?.iconType === 'image' && !!gridCardUrl(item))))
const audioUrl = computed(() => layout.value.audioUrl ? resolveUrl(layout.value.audioUrl) : '')
const audioPlaying = ref(false)
let audioContext = null
const gridIconSurfaceColor = computed(() => {
  const color = layout.value.themeColor || '#1f6feb'
  return isLightColor(color) ? '#4f46e5' : color
})
const gridIconWrapStyle = computed(() => ({
  background: gridIconSurfaceColor.value
}))

const tilePageStyle = computed(() => {
  const background = layout.value.backgroundUrl
  return {
    backgroundColor: isLightTile.value ? tileSurfaceColor.value : '#1100ab',
    backgroundImage: background ? `url(${resolveUrl(background)})` : 'none',
    backgroundSize: '100% 100%',
    backgroundPosition: '50% 100%',
    backgroundRepeat: 'no-repeat',
    backgroundAttachment: isLightTile.value ? 'scroll' : 'fixed'
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

const standardPageStyle = computed(() => {
  const background = layout.value.backgroundUrl
  const visual = layout.value.visual || {}
  const imageCardGap = Math.max(Number(visual.itemGap) || 10, 18)
  const imageCardPadding = Math.max(Number(visual.itemPadding) || 10, 12)
  return {
    backgroundColor: isImageCardGrid.value ? '#eaf8ff' : '#fff',
    backgroundImage: background ? `url(${resolveUrl(background)})` : 'none',
    backgroundSize: '100% auto',
    backgroundPosition: 'top center',
    backgroundRepeat: 'repeat-y',
    '--grid-hero-height': visual.heroHeight > 0 ? `${visual.heroHeight}rpx` : 'auto',
    '--grid-countdown-top': `${visual.countdownTop || 16}rpx`,
    '--grid-countdown-bottom': `${visual.countdownBottom || 20}rpx`,
    '--grid-item-gap': `${imageCardGap}rpx`,
    '--grid-item-padding': `${imageCardPadding}rpx`
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
  // 首页允许匿名浏览；微信授权改到「注册报名」等需要身份的页面再唤起
  await loadHome()
  timer = setInterval(() => { now.value = Date.now() }, 1000)
})

onShow(() => {
  // keep page live；返回首页时恢复会议网站标题
  if (activityId.value) {
    setupMeetingShare(activityId.value, activity.value.activityName || '')
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

async function loadHome() {
  loading.value = true
  try {
    const res = await getPortalHome(activityId.value)
    const home = res.data || {}
    activity.value = home.activity || {}
    const page = home.page || {}
    meetingConfig.value = {}
    layout.value = buildHomeLayout(page.layout || {}, { pageThemeColor: page.theme?.color })
    gridList.value = flattenEntries(page.entryTree || page.sections?.flatMap(section => section.entries || []) || [])
    entrySections.value = buildEntrySections(gridList.value)
    if (page.layout?.template === 'image-map') {
      layout.value.blocks = gridList.value.map(entry => ({ ...entry, ...(entry.bounds || {}) }))
    }
    const meetingTitle = activity.value.activityName || '会议首页'
    uni.setNavigationBarTitle({ title: meetingTitle })
    setupMeetingShare(activityId.value, meetingTitle)
    recordEvent('home', 'view', { source: 'home' })
    if (layout.value.notice) {
      setTimeout(showNotice, 200)
    }
    initAudio()
  } finally {
    loading.value = false
  }
}

function flattenEntries(entries = []) {
  return entries.filter(entry => entry && entry.enabled !== false).map(entry => ({
    ...entry,
    gridId: entry.id,
    linkType: entry.targetType,
    moduleKey: entry.targetType === 'module' ? (entry.target?.moduleKey || entry.target) : '',
    externalUrl: entry.targetType === 'external' ? (entry.target?.url || entry.target) : '',
    contentUrl: entry.targetType === 'content' ? (entry.target?.url || '') : ''
  }))
}

function buildEntrySections(entries) {
  const definitions = layout.value.entrySections || []
  if (!definitions.length) return [{ key: 'menu', label: '会议菜单', columns: layout.value.gridColumns, ratio: 'wide', entries }]
  return definitions.map(definition => ({ ...definition, entries: entries.filter(entry => (entry.sectionKey || definitions[0].key) === definition.key) }))
}

function gridClassFor(section) {
  const columns = Number(section.columns || layout.value.gridColumns)
  return `cols-${columns || 3}`
}

function isIconOnlyFor(section) {
  return section.ratio === 'icon' || (Number(section.columns || layout.value.gridColumns) === 3 && layout.value.gridStyle === 'icon')
}

function isImageCard(item, section) {
  return item && item.iconType === 'image' && !!gridCardUrl(item) && Number(section.columns || layout.value.gridColumns) === 2
}

function gridCardUrl(item) {
  return item?.contentUrl || item?.iconUrl || ''
}

function isLightColor(color) {
  if (!color || typeof color !== 'string') return false
  const value = color.trim().toLowerCase()
  let r = 0
  let g = 0
  let b = 0
  if (value.startsWith('#')) {
    const hex = value.slice(1)
    const full = hex.length === 3
      ? hex.split('').map((ch) => ch + ch).join('')
      : hex
    if (full.length < 6) return false
    r = parseInt(full.slice(0, 2), 16)
    g = parseInt(full.slice(2, 4), 16)
    b = parseInt(full.slice(4, 6), 16)
  } else {
    const match = value.match(/rgba?\((\d+)[,\s]+(\d+)[,\s]+(\d+)/)
    if (!match) return false
    r = Number(match[1])
    g = Number(match[2])
    b = Number(match[3])
  }
  if ([r, g, b].some((n) => Number.isNaN(n))) return false
  const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
  return luminance >= 0.72
}

function parseTileMeta(item) {
  const raw = (item && item.remark) || ''
  if (!raw) return {}
  try {
    const parsed = JSON.parse(raw)
    if (!parsed || typeof parsed !== 'object') return {}
    const bg = resolveTileBgMeta(parsed)
    return {
      ...parsed,
      bg: bg || undefined
    }
  } catch (e) {
    if (raw.startsWith('tile-bg:')) {
      return { bg: raw.slice(8) }
    }
    return {}
  }
}

function resolveTileBgMeta(options = {}, depth = 0) {
  if (!options || typeof options !== 'object' || depth > 3) return ''
  const direct = options.bg || options.background || options.tileBg || options.gradientColor || ''
  if (direct) return direct
  if (typeof options.remark !== 'string' || !options.remark) return ''
  try {
    const nested = JSON.parse(options.remark)
    if (nested && typeof nested === 'object') {
      return resolveTileBgMeta(nested, depth + 1)
    }
  } catch (e) {
    if (options.remark.startsWith('tile-bg:')) {
      return options.remark.slice(8)
    }
  }
  return ''
}

function isColorTile(item) {
  const meta = parseTileMeta(item)
  return !!(meta.bg || meta.background || meta.tileBg || meta.gradientColor)
}

function isTallColorTile(item) {
  return isColorTile(item) && Number(item.tileRowSpan || 1) >= 2
}

function tileStyle(item) {
  const meta = parseTileMeta(item)
  const style = {
    gridColumn: `${item.tileCol || 'auto'} / span ${item.tileColSpan || 1}`,
    gridRow: `${item.tileRow || 'auto'} / span ${item.tileRowSpan || 1}`
  }
  const gradient = meta.bg || meta.background || meta.tileBg || meta.gradientColor
  if (gradient) {
    style.backgroundImage = gradient
    style.backgroundColor = 'transparent'
  } else if (item.iconUrl) {
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

function onGridClick(item) {
  recordEvent(String(item.id || item.gridId || item.entryId || item.moduleKey || item.title || 'unknown'), 'click', { targetType: item.targetType || item.linkType || 'legacy' })
  if (item.targetType) {
    if (item.available === false) {
      uni.showToast({ title: item.unavailableMessage || '暂未开放', icon: 'none' })
      return
    }
    if (item.targetType === 'content' && item.legacyContent) {
      const legacyUrl = item.contentUrl || item.target || item.iconUrl || ''
      if (item.contentType === 'image' && legacyUrl) {
        uni.navigateTo({ url: `/pages/common/imageview/index?activityId=${activityId.value}&title=${encodeURIComponent(item.title || '图片内容')}&url=${encodeURIComponent(legacyUrl)}` })
      } else {
        uni.navigateTo({ url: `/pages/common/textview/index?activityId=${activityId.value}&title=${encodeURIComponent(item.title || '内容')}&content=${encodeURIComponent(item.legacyContent)}` })
      }
      return
    }
    const target = resolveEntryPage(activityId.value, item)
    if (target) {
      uni.navigateTo({ url: target })
      return
    }
    uni.showToast({ title: '暂无可用操作', icon: 'none' })
    return
  }
  if (item.linkType === 'url' && item.externalUrl) {
    uni.navigateTo({
      url: `/pages/common/webview/index?activityId=${encodeURIComponent(activityId.value || '')}&title=${encodeURIComponent(item.title)}&url=${encodeURIComponent(item.externalUrl)}`
    })
    return
  }
  if (item.linkType === 'content' && item.contentType === 'image' && (item.gridId || item.contentUrl || item.content || item.iconUrl)) {
    const query = [
      `activityId=${encodeURIComponent(activityId.value || '')}`,
      `title=${encodeURIComponent(item.title || '图片内容')}`
    ]
    if (item.gridId) query.push(`gridId=${encodeURIComponent(item.gridId)}`)
    if (item.contentUrl || item.iconUrl) {
      query.push(`url=${encodeURIComponent(item.contentUrl || item.iconUrl)}`)
    }
    uni.navigateTo({
      url: `/pages/common/imageview/index?${query.join('&')}`
    })
    return
  }
  if (item.linkType === 'content' && item.content) {
    const query = [
      `activityId=${encodeURIComponent(activityId.value || '')}`,
      `title=${encodeURIComponent(item.title || '内容')}`
    ]
    if (item.gridId) {
      query.push(`gridId=${encodeURIComponent(item.gridId)}`)
    } else {
      // 兼容没有 gridId 的旧入口。
      query.push(`content=${encodeURIComponent(item.content)}`)
    }
    uni.navigateTo({
      url: `/pages/common/textview/index?${query.join('&')}`
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

function recordEvent(entryId, eventType, context) {
  if (!activityId.value) return
  recordMeetingEvent({ activityId: activityId.value, entryId, eventType, context }).catch(() => {})
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

function onFooterClick() {
  if (layout.value.footer.linkUrl) {
    uni.navigateTo({
      url: `/pages/common/webview/index?activityId=${encodeURIComponent(activityId.value || '')}&title=${encodeURIComponent(layout.value.footer.company || layout.value.footer.text || '会务支持')}&url=${encodeURIComponent(layout.value.footer.linkUrl)}`
    })
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 24rpx;
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
.standard-page.is-image-card-grid {
  padding: 0 20rpx 32rpx;
}
.standard-page.is-image-card-grid .cover {
  height: var(--grid-hero-height, auto);
  min-height: 0;
  margin: 0 -20rpx;
  background: none;
}
.standard-page.is-image-card-grid .cover-auto-image {
  display: block;
  width: 100%;
  height: auto;
}
.standard-page.is-image-card-grid .cover-mask {
  background: transparent;
}
.standard-page.is-image-card-grid .title,
.standard-page.is-image-card-grid .meta-row {
  display: none;
}
.image-card-countdown {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 -20rpx;
  padding: var(--grid-countdown-top, 16rpx) 20rpx var(--grid-countdown-bottom, 20rpx);
  background: #eaf8ff;
}
.image-card-countdown :deep(.countdown-board) {
  color: #202a38;
}
.image-card-countdown :deep(.heading-line) {
  background: rgba(32, 42, 56, 0.68);
}
.image-card-countdown :deep(.group-label) {
  color: #202a38;
}
.standard-page.is-image-card-grid .section {
  margin: 12rpx 0 0;
  padding: 10rpx;
  background: transparent;
  box-shadow: none;
}
.standard-page.is-image-card-grid .section-title {
  display: none;
}
.standard-page.is-image-card-grid .grid-item.is-image-card {
  padding: var(--grid-item-padding, 10rpx);
}
.standard-page.is-image-card-grid .grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--grid-item-gap, 10rpx);
}
.standard-page.is-image-card-grid .grid-item {
  width: auto;
}
.standard-page.is-image-card-grid .grid-card-image {
  border-radius: 10rpx;
  box-shadow: none;
}
.tile-page {
  min-height: 100vh;
  background: #061a74;
}
.tile-page.is-light-tile {
  background: #f6f6f6;
}
.tile-cover {
  height: 56.25vw;
  min-height: 0;
  background-color: #061a74;
  background-size: cover;
  background-position: top center;
  background-repeat: no-repeat;
}
.tile-page.is-light-tile .tile-cover {
  background-color: #f6f6f6;
}
.tile-countdown {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24rpx 18rpx;
  background: #061a74;
}
.tile-page.is-light-tile .tile-countdown {
  background: #f6f6f6;
}
.tile-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  grid-template-rows: 18vw repeat(3, 20.7vw);
  gap: 0;
  padding: 18rpx;
  background: #061a74;
}
.tile-page.is-light-tile .tile-grid {
  background: transparent;
  padding: 10px 0 24rpx;
  grid-template-rows: 100px 110px 110px 110px;
}
.tile-page.is-light-tile .tile-item {
  margin: 5px;
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
.tile-item.is-color-tile {
  position: relative;
  display: block;
  overflow: hidden;
  border-radius: 10px;
  box-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
}
.tile-item.is-color-tile .tile-title {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
  display: block;
  min-height: auto;
  max-width: none;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.25;
  text-align: left;
  white-space: nowrap;
  word-break: keep-all;
}
.tile-item.is-color-tile .tile-icon {
  position: absolute;
  right: 5px;
  bottom: 5px;
  z-index: 1;
  width: 50px;
  height: 50px;
  margin: 0;
}
.tile-item.is-tall-color-tile .tile-title {
  top: 15px;
  left: 15px;
  max-width: none;
  font-size: 24px;
  white-space: nowrap;
}
.tile-item.is-tall-color-tile .tile-icon {
  right: 8px;
  bottom: 8px;
  width: 80px;
  height: 80px;
}
.tile-icon {
  width: 56rpx;
  height: 56rpx;
  margin-bottom: 10rpx;
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
  .tile-page.is-light-tile .tile-grid {
    padding: 10px 0 20px;
    grid-template-rows: 100px 110px 110px 110px;
  }
  .tile-item {
    margin: 6px;
  }
  .tile-item.is-color-tile .tile-title {
    top: 10px;
    left: 10px;
    font-size: 18px;
  }
  .tile-item.is-color-tile .tile-icon {
    right: 5px;
    bottom: 5px;
    width: 50px;
    height: 50px;
  }
  .tile-item.is-tall-color-tile .tile-title {
    top: 15px;
    left: 15px;
    font-size: 24px;
  }
  .tile-item.is-tall-color-tile .tile-icon {
    right: 8px;
    bottom: 8px;
    width: 80px;
    height: 80px;
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
.tile-page.is-light-tile .tile-footer {
  color: #666;
  background: transparent;
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
.grid-item.is-tall-image-card .grid-card-image { aspect-ratio: 1 / 1.4; object-fit: cover; }
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
.image-map-page {
  position: relative;
  min-height: 56.25vw;
  padding-bottom: 24rpx;
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
