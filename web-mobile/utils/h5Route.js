import config from '@/config'

/**
 * H5 history 路由基路径（与 manifest.json h5.router.base 保持一致）
 */
export const H5_BASE = String((config && config.h5Base) || '/h5').replace(/\/$/, '') || '/h5'

/**
 * 拼绝对 H5 页面地址。
 * @param {string} pagePath 如 /pages/meeting/home 或 pages/meeting/home
 * @param {Record<string, string|number|undefined|null>} [query]
 */
export function buildH5PageUrl(pagePath, query = {}) {
  if (typeof location === 'undefined') {
    return joinPath(H5_BASE, normalizePagePath(pagePath)) + toQuery(query)
  }
  const path = joinPath(H5_BASE, normalizePagePath(pagePath)) + toQuery(query)
  return location.origin + path
}

export function buildMeetingHomeUrl(activityId) {
  return buildH5PageUrl('/pages/meeting/home', { activityId })
}

/**
 * 兼容旧 hash 链接：
 * /h5/#/pages/meeting/home?activityId=13
 * -> /h5/pages/meeting/home?activityId=13
 */
export function redirectLegacyHashIfNeeded() {
  if (typeof location === 'undefined') return false
  const hash = location.hash || ''
  if (!hash.startsWith('#/')) return false

  const route = hash.slice(1) // /pages/...?a=1
  let basePath = (location.pathname || H5_BASE).replace(/\/index\.html$/i, '')
  // 已在 history 页面上残留 hash 时，用当前目录前的 /h5 前缀
  const h5Index = basePath.indexOf(H5_BASE)
  if (h5Index >= 0) {
    basePath = basePath.slice(0, h5Index + H5_BASE.length)
  } else if (basePath === '/' || basePath === '') {
    basePath = H5_BASE
  } else {
    basePath = H5_BASE
  }
  basePath = basePath.replace(/\/$/, '') || H5_BASE
  const target = location.origin + basePath + route
  location.replace(target)
  return true
}

function normalizePagePath(pagePath) {
  const value = String(pagePath || '').trim()
  if (!value) return '/pages/meeting/index'
  return value.startsWith('/') ? value : '/' + value
}

function joinPath(base, pagePath) {
  return String(base || '').replace(/\/$/, '') + normalizePagePath(pagePath)
}

function toQuery(query = {}) {
  const parts = []
  Object.keys(query || {}).forEach((key) => {
    const value = query[key]
    if (value == null || value === '') return
    parts.push(encodeURIComponent(key) + '=' + encodeURIComponent(String(value)))
  })
  return parts.length ? '?' + parts.join('&') : ''
}

/**
 * 从 hash 或 search 补齐页面参数（兼容旧链接与 history）
 */
export function mergeLocationQuery(options = {}) {
  const query = { ...(options || {}) }
  if (typeof location === 'undefined') return query

  const sources = []
  if (location.search && location.search.length > 1) {
    sources.push(location.search.slice(1))
  }
  if (location.hash && location.hash.includes('?')) {
    sources.push(location.hash.split('?')[1] || '')
  }
  sources.forEach((qs) => {
    const params = new URLSearchParams(qs)
    params.forEach((value, key) => {
      if (query[key] == null || query[key] === '') {
        query[key] = value
      }
    })
  })
  return query
}
