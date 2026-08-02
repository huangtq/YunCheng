import request from '@/utils/request'

export function getActivityConfig(activityId) {
  return request({
    url: '/meeting/config/' + activityId,
    method: 'get'
  })
}

export function updateActivityConfig(data) {
  return request({
    url: '/meeting/config',
    method: 'put',
    data: data
  })
}