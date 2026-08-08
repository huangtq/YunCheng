import request from '@/utils/request'
export function listVenue(query) { return request({ url: '/meeting/venue/list', method: 'get', params: query }) }
export function getVenueStats(activityId) { return request({ url: '/meeting/venue/stats/' + activityId, method: 'get' }) }
export function getVenue(venueId) { return request({ url: '/meeting/venue/' + venueId, method: 'get' }) }
export function addVenue(data) { return request({ url: '/meeting/venue', method: 'post', data }) }
export function updateVenue(data) { return request({ url: '/meeting/venue', method: 'put', data }) }
export function delVenue(venueId) { return request({ url: '/meeting/venue/' + venueId, method: 'delete' }) }