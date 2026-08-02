import request from '@/utils/request'

export function listApplyChannel(query) {
  return request({
    url: '/meeting/apply/channel/list',
    method: 'get',
    params: query
  })
}

export function getApplyChannel(channelId) {
  return request({
    url: '/meeting/apply/channel/' + channelId,
    method: 'get'
  })
}

export function addApplyChannel(data) {
  return request({
    url: '/meeting/apply/channel',
    method: 'post',
    data: data
  })
}

export function updateApplyChannel(data) {
  return request({
    url: '/meeting/apply/channel',
    method: 'put',
    data: data
  })
}

export function delApplyChannel(channelId) {
  return request({
    url: '/meeting/apply/channel/' + channelId,
    method: 'delete'
  })
}