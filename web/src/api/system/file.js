import request from '@/utils/request'

// 查询文件列表
export function listFile(query) {
  return request({
    url: '/system/file/list',
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