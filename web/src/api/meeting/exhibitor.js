import request from '@/utils/request'
export function listExhibitor(query) { return request({ url: '/meeting/exhibitor/list', method: 'get', params: query }) }
export function getExhibitorStats(activityId) { return request({ url: '/meeting/exhibitor/stats/' + activityId, method: 'get' }) }
export function getExhibitor(exhibitorId) { return request({ url: '/meeting/exhibitor/' + exhibitorId, method: 'get' }) }
export function addExhibitor(data) { return request({ url: '/meeting/exhibitor', method: 'post', data }) }
export function updateExhibitor(data) { return request({ url: '/meeting/exhibitor', method: 'put', data }) }
export function delExhibitor(exhibitorId) { return request({ url: '/meeting/exhibitor/' + exhibitorId, method: 'delete' }) }