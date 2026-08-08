import request from '@/utils/request'
export function listMeal(query) { return request({ url: '/meeting/meal/list', method: 'get', params: query }) }
export function getMealStats(activityId) { return request({ url: '/meeting/meal/stats/' + activityId, method: 'get' }) }
export function getMeal(ticketId) { return request({ url: '/meeting/meal/' + ticketId, method: 'get' }) }
export function addMeal(data) { return request({ url: '/meeting/meal', method: 'post', data }) }
export function updateMeal(data) { return request({ url: '/meeting/meal', method: 'put', data }) }
export function delMeal(ticketId) { return request({ url: '/meeting/meal/' + ticketId, method: 'delete' }) }