import config from '@/config'
import { getShareInfo, getJsConfig } from '@/api/portal/wx'

const WX_SDK_SRC = 'https://res.wx.qq.com/open/js/jweixin-1.6.0.js'
const TITLE_CACHE_KEY = 'yc_meeting_page_title'

function isWechatBrowser() {
  if (typeof navigator === 'undefined') return false
  return /MicroMessenger/i.test(navigator.userAgent || '')
}

function isH5() {
  return typeof window !== 'undefined' && typeof document !== 'undefined'
}

function titleCacheStore() {
  if (typeof sessionStorage === 'undefined') return null
  return sessionStorage
}

function readCachedTitle(activityId) {
  const store = titleCacheStore()
  if (!store || !activityId) return ''
  try {
    const raw = store.getItem(TITLE_CACHE_KEY)
    if (!raw) return ''
    const map = JSON.parse(raw)
    return (map && map[String(activityId)]) || ''
  } catch (e) {
    return ''
  }
}

function writeCachedTitle(activityId, title) {
  const store = titleCacheStore()
  if (!store || !activityId || !title) return
  try {
    const raw = store.getItem(TITLE_CACHE_KEY)
    const map = raw ? JSON.parse(raw) : {}
    map[String(activityId)] = title
    store.setItem(TITLE_CACHE_KEY, JSON.stringify(map))
  } catch (e) {
    // ignore storage failures
  }
}

function applyPageTitle(title) {
  if (!title) return
  if (typeof document !== 'undefined') {
    document.title = title
  }
  if (typeof uni !== 'undefined' && typeof uni.setNavigationBarTitle === 'function') {
    uni.setNavigationBarTitle({ title })
  }
}

function ensureMetaTag(attr, key, content) {
  if (typeof document === 'undefined' || !content) return
  let el = document.querySelector(`meta[${attr}="${key}"]`)
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute(attr, key)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

function applyDocumentShareMeta({ title, desc, imgUrl, link }) {
  if (typeof document === 'undefined') return
  if (title) {
    applyPageTitle(title)
  }
  ensureMetaTag('name', 'description', desc)
  ensureMetaTag('property', 'og:title', title)
  ensureMetaTag('property', 'og:description', desc)
  ensureMetaTag('property', 'og:url', link)
  if (imgUrl) {
    ensureMetaTag('property', 'og:image', imgUrl)
    ensureMetaTag('itemprop', 'image', imgUrl)
  }
  ensureMetaTag('itemprop', 'name', title)
  ensureMetaTag('itemprop', 'description', desc)
}

function loadWxSdk() {
  if (typeof window === 'undefined') {
    return Promise.reject(new Error('no window'))
  }
  if (window.wx && window.wx.config) {
    return Promise.resolve(window.wx)
  }
  return new Promise((resolve, reject) => {
    const existing = document.querySelector(`script[src="${WX_SDK_SRC}"]`)
    if (existing) {
      existing.addEventListener('load', () => resolve(window.wx))
      existing.addEventListener('error', reject)
      if (window.wx && window.wx.config) resolve(window.wx)
      return
    }
    const script = document.createElement('script')
    script.src = WX_SDK_SRC
    script.async = true
    script.onload = () => resolve(window.wx)
    script.onerror = () => reject(new Error('load jweixin failed'))
    document.head.appendChild(script)
  })
}

function currentPageUrlForSign() {
  if (typeof location === 'undefined') return ''
  return location.href.split('#')[0]
}

function bindShareHandlers(wx, share) {
  const payload = {
    title: share.title || '会议邀请',
    desc: share.desc || '点击查看会议详情，欢迎报名参加',
    link: share.link || share.h5Url || location.href,
    imgUrl: share.imgUrl || ''
  }
  const legacy = {
    title: payload.title,
    desc: payload.desc,
    link: payload.link,
    imgUrl: payload.imgUrl
  }
  if (typeof wx.updateAppMessageShareData === 'function') {
    wx.updateAppMessageShareData(payload)
  }
  if (typeof wx.updateTimelineShareData === 'function') {
    wx.updateTimelineShareData({
      title: payload.title,
      link: payload.link,
      imgUrl: payload.imgUrl
    })
  }
  // 兼容旧版微信客户端
  if (typeof wx.onMenuShareAppMessage === 'function') {
    wx.onMenuShareAppMessage(legacy)
  }
  if (typeof wx.onMenuShareTimeline === 'function') {
    wx.onMenuShareTimeline({
      title: payload.title,
      link: payload.link,
      imgUrl: payload.imgUrl
    })
  }
}

/**
 * 同步恢复会议页浏览器标题（优先用缓存，避免子页闪成 pages.json 默认标题）。
 */
export function applyMeetingPageTitle(activityId, fallbackTitle = '') {
  if (!activityId) return ''
  const title = readCachedTitle(activityId) || fallbackTitle || ''
  if (title) applyPageTitle(title)
  return title
}

/**
 * 记住会议标题，供后续九宫格子页复用。
 */
export function rememberMeetingPageTitle(activityId, title) {
  if (!activityId || !title) return
  writeCachedTitle(activityId, title)
  applyPageTitle(title)
}

/**
 * 配置会议页微信分享标题/描述/封面，并统一网站标题为会议名称。
 * - 非微信或非 H5：仅更新 document title / meta
 * - 微信内：再走 JS-SDK
 */
export async function setupMeetingShare(activityId, fallbackTitle = '') {
  if (!activityId || !isH5()) return null

  // 先同步套用缓存/兜底，避免路由切换瞬间变成「注册报名」「浏览文本」等
  applyMeetingPageTitle(activityId, fallbackTitle)

  try {
    const res = await getShareInfo(activityId)
    const share = (res && res.data) || null
    if (!share) {
      if (fallbackTitle) rememberMeetingPageTitle(activityId, fallbackTitle)
      return null
    }

    if (share.imgUrl && !/^https?:\/\//i.test(share.imgUrl)) {
      share.imgUrl = config.baseUrl.replace(/\/$/, '') + (share.imgUrl.startsWith('/') ? share.imgUrl : '/' + share.imgUrl)
    }

    if (share.title) {
      writeCachedTitle(activityId, share.title)
    } else if (fallbackTitle) {
      share.title = fallbackTitle
      writeCachedTitle(activityId, fallbackTitle)
    }

    applyDocumentShareMeta(share)

    if (!isWechatBrowser()) {
      return share
    }

    const wx = await loadWxSdk()
    if (!wx || typeof wx.config !== 'function') {
      return share
    }

    const signUrl = currentPageUrlForSign()
    const cfgRes = await getJsConfig(signUrl)
    const cfg = (cfgRes && cfgRes.data) || null
    if (!cfg || cfg.enabled === false || !cfg.appId) {
      return share
    }

    await new Promise((resolve) => {
      wx.config({
        debug: false,
        appId: cfg.appId,
        timestamp: cfg.timestamp,
        nonceStr: cfg.nonceStr,
        signature: cfg.signature,
        jsApiList: [
          'updateAppMessageShareData',
          'updateTimelineShareData',
          'onMenuShareAppMessage',
          'onMenuShareTimeline'
        ]
      })
      wx.ready(() => {
        bindShareHandlers(wx, share)
        resolve()
      })
      wx.error(() => resolve())
    })
    return share
  } catch (e) {
    // 分享失败不影响页面浏览
    console.warn('[wxShare] setup failed', e)
    if (fallbackTitle) rememberMeetingPageTitle(activityId, fallbackTitle)
    return null
  }
}
