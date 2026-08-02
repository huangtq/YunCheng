import request from '@/utils/request'

export function listActivity(query) {
  return request({
    url: '/meeting/activity/list',
    method: 'get',
    params: query
  })
}

export function getActivity(activityId) {
  return request({
    url: '/meeting/activity/' + activityId,
    method: 'get'
  })
}

export function addActivity(data) {
  return request({
    url: '/meeting/activity',
    method: 'post',
    data: data
  })
}

export function updateActivity(data) {
  return request({
    url: '/meeting/activity',
    method: 'put',
    data: data
  })
}

export function delActivity(activityId) {
  return request({
    url: '/meeting/activity/' + activityId,
    method: 'delete'
  })
}

export function genActivityCode() {
  return request({
    url: '/meeting/activity/genCode',
    method: 'get'
  })
}