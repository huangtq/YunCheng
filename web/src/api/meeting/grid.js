import request from '@/utils/request'

export function listGrid(query) {
  return request({
    url: '/meeting/grid/list',
    method: 'get',
    params: query
  })
}

export function getGrid(gridId) {
  return request({
    url: '/meeting/grid/' + gridId,
    method: 'get'
  })
}

export function addGrid(data) {
  return request({
    url: '/meeting/grid',
    method: 'post',
    data: data
  })
}

export function updateGrid(data) {
  return request({
    url: '/meeting/grid',
    method: 'put',
    data: data
  })
}

export function delGrid(gridId) {
  return request({
    url: '/meeting/grid/' + gridId,
    method: 'delete'
  })
}