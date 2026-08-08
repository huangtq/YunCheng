import request from '@/utils/request'

export function listApplyOrder(query) {
  return request({
    url: '/meeting/apply/order/list',
    method: 'get',
    params: query
  })
}

export function getApplyOrderStats(activityId) {
  return request({
    url: '/meeting/apply/order/stats/' + activityId,
    method: 'get'
  })
}

export function getApplyOrder(orderId) {
  return request({
    url: '/meeting/apply/order/' + orderId,
    method: 'get'
  })
}

export function addApplyOrder(data) {
  return request({
    url: '/meeting/apply/order',
    method: 'post',
    data: data
  })
}

export function updateApplyOrder(data) {
  return request({
    url: '/meeting/apply/order',
    method: 'put',
    data: data
  })
}

export function checkinApplyOrder(orderId) {
  return request({
    url: '/meeting/apply/order/checkin/' + orderId,
    method: 'put'
  })
}

export function delApplyOrder(orderId) {
  return request({
    url: '/meeting/apply/order/' + orderId,
    method: 'delete'
  })
}