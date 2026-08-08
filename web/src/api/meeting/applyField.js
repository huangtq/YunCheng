import request from '@/utils/request'

export function listApplyField(query) {
  return request({
    url: '/meeting/apply/field/list',
    method: 'get',
    params: query
  })
}

export function getApplyField(fieldId) {
  return request({
    url: '/meeting/apply/field/' + fieldId,
    method: 'get'
  })
}

export function addApplyField(data) {
  return request({
    url: '/meeting/apply/field',
    method: 'post',
    data: data
  })
}

export function updateApplyField(data) {
  return request({
    url: '/meeting/apply/field',
    method: 'put',
    data: data
  })
}

export function changeApplyFieldEnabled(data) {
  return request({
    url: '/meeting/apply/field/enabled',
    method: 'put',
    data: data
  })
}

export function delApplyField(fieldId) {
  return request({
    url: '/meeting/apply/field/' + fieldId,
    method: 'delete'
  })
}