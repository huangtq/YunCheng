import request from '@/utils/request'
export function listStaff(query) { return request({ url: '/meeting/staff/list', method: 'get', params: query }) }
export function getStaffStats(activityId) { return request({ url: '/meeting/staff/stats/' + activityId, method: 'get' }) }
export function getStaff(staffId) { return request({ url: '/meeting/staff/' + staffId, method: 'get' }) }
export function addStaff(data) { return request({ url: '/meeting/staff', method: 'post', data }) }
export function updateStaff(data) { return request({ url: '/meeting/staff', method: 'put', data }) }
export function delStaff(staffId) { return request({ url: '/meeting/staff/' + staffId, method: 'delete' }) }