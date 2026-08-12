import request from '@/utils/request'
export function listMeetingNotices(query) { return request({ url: '/meeting/notice/list', method: 'get', params: query }) }
export function saveMeetingNotice(data) { return request({ url: '/meeting/notice', method: 'post', data }) }
