import request from '@/utils/request'
export function listHotel(query){ return request({ url:'/meeting/hotel/list', method:'get', params:query }) }
export function getHotelStats(activityId){ return request({ url:'/meeting/hotel/stats/'+activityId, method:'get' }) }
export function getHotel(hotelId){ return request({ url:'/meeting/hotel/'+hotelId, method:'get' }) }
export function addHotel(data){ return request({ url:'/meeting/hotel', method:'post', data }) }
export function updateHotel(data){ return request({ url:'/meeting/hotel', method:'put', data }) }
export function delHotel(hotelId){ return request({ url:'/meeting/hotel/'+hotelId, method:'delete' }) }
export function listHotelRoom(query){ return request({ url:'/meeting/hotel/room/list', method:'get', params:query }) }
export function getHotelRoom(roomId){ return request({ url:'/meeting/hotel/room/'+roomId, method:'get' }) }
export function addHotelRoom(data){ return request({ url:'/meeting/hotel/room', method:'post', data }) }
export function updateHotelRoom(data){ return request({ url:'/meeting/hotel/room', method:'put', data }) }
export function delHotelRoom(roomId){ return request({ url:'/meeting/hotel/room/'+roomId, method:'delete' }) }