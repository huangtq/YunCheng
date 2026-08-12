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

export function getPortalSchedule(activityId) {
  return request({ url: '/portal/meeting/schedule/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalGuests(activityId) {
  return request({ url: '/portal/meeting/guest/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalNavigation(activityId) {
  return request({ url: '/portal/meeting/navigation/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalHome(activityId) {
  return request({ url: '/portal/meeting/home/' + activityId, method: 'get', headers: portalHeaders() })
}

export function getPortalContent(activityId, contentId) {
  return request({ url: '/portal/meeting/content/' + activityId + '/' + contentId, method: 'get', headers: portalHeaders() })
}

export function recordMeetingEvent(data) {
  return request({ url: '/portal/meeting/event', method: 'post', data, headers: portalHeaders() })
}
