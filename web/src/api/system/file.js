import request from '@/utils/request'

// 查询文件列表
export function listFile(query) {
  return request({
    url: '/system/file/list',
    method: 'get',
    params: query
  })
}

// 查询指定会议的文件列表
export function listActivityFile(activityId, query) {
  return request({
    url: '/meeting/activity/' + activityId + '/file/list',
    method: 'get',
    params: query
  })
}

// 查询文件详细
export function getFile(fileId) {
  return request({
    url: '/system/file/' + fileId,
    method: 'get'
  })
}

// 删除文件
export function delFile(fileId) {
  return request({
    url: '/system/file/' + fileId,
    method: 'delete'
  })
}

// 删除指定会议的文件
export function delActivityFile(activityId, fileId) {
  return request({
    url: '/meeting/activity/' + activityId + '/file/' + fileId,
    method: 'delete'
  })
}