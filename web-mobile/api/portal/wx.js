import request from '@/utils/request'
import { getMpToken } from '@/utils/mpAuth'
import config from '@/config'

function portalHeaders() {
  const headers = { isToken: false }
  const token = getMpToken()
  if (token) {
    headers['Mp-Authorization'] = 'Bearer ' + token
  }
  return headers
}

export function getOauthUrl(activityId, redirect) {
  return request({
    url: '/portal/wx/oauth/url',
    method: 'get',
    params: { activityId, redirect },
    headers: { isToken: false }
  })
}

export function getShareInfo(activityId) {
  return request({
    url: '/portal/wx/share-info',
    method: 'get',
    params: { activityId },
    headers: { isToken: false }
  })
}

export function getJsConfig(url) {
  return request({
    url: '/portal/wx/js-config',
    method: 'get',
    params: { url },
    headers: { isToken: false }
  })
}

export function getWxMe() {
  return request({ url: '/portal/wx/me', method: 'get', headers: portalHeaders() })
}

export function bindWxPhone(phone) {
  return request({
    url: '/portal/wx/bindPhone',
    method: 'put',
    data: { phone },
    headers: portalHeaders()
  })
}

export function buildMockOauthJump(activityId, redirect) {
  const base = config.baseUrl.replace(/\/$/, '')
  let url = base + '/portal/wx/oauth/mock?activityId=' + (activityId || '')
  if (redirect) {
    url += '&redirect=' + encodeURIComponent(redirect)
  }
  return url
}