import request from '@/utils/request'
import { getMpToken } from '@/utils/mpAuth'

function portalHeaders() {
  const headers = { isToken: false }
  const token = getMpToken()
  if (token) {
    headers['Mp-Authorization'] = 'Bearer ' + token
  }
  return headers
}

export function getPortalActivity(activityId) {
  return request({ url: '/portal/meeting/activity/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalActivities(type = 'current') {
  return request({ url: '/portal/meeting/list', method: 'get', params: { type }, headers: portalHeaders() })
}

export function getPortalGrid(activityId) {
  return request({ url: '/portal/meeting/grid/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalModule(moduleKey, activityId) {
  return request({ url: '/portal/meeting/module/' + moduleKey + '/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalApplyChannels(activityId) {
  return request({ url: '/portal/meeting/apply/channels/' + activityId, method: 'get', headers: portalHeaders() })
}

export function submitPortalApply(data) {
  return request({ url: '/portal/meeting/apply/submit', method: 'post', data, headers: portalHeaders() })
}

export function getMyPortalApply(activityId) {
  return request({ url: '/portal/meeting/apply/my', method: 'get', params: { activityId }, headers: portalHeaders() })
}
