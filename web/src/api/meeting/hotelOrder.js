import request from '@/utils/request'
export function listHotelOrder(query){ return request({ url:'/meeting/hotel/order/list', method:'get', params:query }) }
export function getHotelOrderStats(activityId){ return request({ url:'/meeting/hotel/order/stats/'+activityId, method:'get' }) }
export function getHotelOrder(orderId){ return request({ url:'/meeting/hotel/order/'+orderId, method:'get' }) }
export function addHotelOrder(data){ return request({ url:'/meeting/hotel/order', method:'post', data }) }
export function updateHotelOrder(data){ return request({ url:'/meeting/hotel/order', method:'put', data }) }
export function delHotelOrder(orderId){ return request({ url:'/meeting/hotel/order/'+orderId, method:'delete' }) }