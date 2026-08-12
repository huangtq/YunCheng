import request from '@/utils/request'
export function verifyCheckin(data) { return request({ url: '/meeting/checkin/verify', method: 'post', data }) }
