import request from '@/utils/request'
export function listHotelAssign(query){ return request({ url:'/meeting/hotel/assign/list', method:'get', params:query }) }
export function getHotelAssignStats(activityId){ return request({ url:'/meeting/hotel/assign/stats/'+activityId, method:'get' }) }
export function getHotelAssign(assignId){ return request({ url:'/meeting/hotel/assign/'+assignId, method:'get' }) }
export function addHotelAssign(data){ return request({ url:'/meeting/hotel/assign', method:'post', data }) }
export function createAssignFromOrder(orderId){ return request({ url:'/meeting/hotel/assign/fromOrder/'+orderId, method:'post' }) }
export function updateHotelAssign(data){ return request({ url:'/meeting/hotel/assign', method:'put', data }) }
export function delHotelAssign(assignId){ return request({ url:'/meeting/hotel/assign/'+assignId, method:'delete' }) }
