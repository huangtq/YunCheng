import request from '@/utils/request'
export function listGuest(query) { return request({ url: '/meeting/guest/list', method: 'get', params: query }) }
export function getGuestStats(activityId) { return request({ url: '/meeting/guest/stats/' + activityId, method: 'get' }) }
export function getGuest(guestId) { return request({ url: '/meeting/guest/' + guestId, method: 'get' }) }
export function addGuest(data) { return request({ url: '/meeting/guest', method: 'post', data }) }
export function updateGuest(data) { return request({ url: '/meeting/guest', method: 'put', data }) }
export function delGuest(guestId) { return request({ url: '/meeting/guest/' + guestId, method: 'delete' }) }