import request from '@/utils/request'
export function listTrip(query) { return request({ url: '/meeting/guest/trip/list', method: 'get', params: query }) }
export function getTripStats(activityId) { return request({ url: '/meeting/guest/trip/stats/' + activityId, method: 'get' }) }
export function getTrip(tripId) { return request({ url: '/meeting/guest/trip/' + tripId, method: 'get' }) }
export function addTrip(data) { return request({ url: '/meeting/guest/trip', method: 'post', data }) }
export function updateTrip(data) { return request({ url: '/meeting/guest/trip', method: 'put', data }) }
export function delTrip(tripId) { return request({ url: '/meeting/guest/trip/' + tripId, method: 'delete' }) }