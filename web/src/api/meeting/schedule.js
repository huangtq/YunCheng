import request from '@/utils/request'
export function listSchedule(query) { return request({ url: '/meeting/schedule/list', method: 'get', params: query }) }
export function getScheduleStats(activityId) { return request({ url: '/meeting/schedule/stats/' + activityId, method: 'get' }) }
export function listExpertTasks(activityId, query) { return request({ url: '/meeting/schedule/expert/' + activityId, method: 'get', params: query }) }
export function getSchedule(scheduleId) { return request({ url: '/meeting/schedule/' + scheduleId, method: 'get' }) }
export function addSchedule(data) { return request({ url: '/meeting/schedule', method: 'post', data }) }
export function updateSchedule(data) { return request({ url: '/meeting/schedule', method: 'put', data }) }
export function delSchedule(scheduleId) { return request({ url: '/meeting/schedule/' + scheduleId, method: 'delete' }) }