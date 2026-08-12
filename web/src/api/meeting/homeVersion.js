import request from '@/utils/request'

export function listHomeVersions(query) {
  return request({ url: '/meeting/home-version/list', method: 'get', params: query })
}

export function getHomeVersion(versionId) {
  return request({ url: `/meeting/home-version/${versionId}`, method: 'get' })
}

export function saveHomeDraft(data) {
  return request({ url: '/meeting/home-version/draft', method: 'post', data })
}

export function publishHomeVersion(versionId, publishRemark) {
  return request({ url: `/meeting/home-version/${versionId}/publish`, method: 'post', data: { publishRemark } })
}
