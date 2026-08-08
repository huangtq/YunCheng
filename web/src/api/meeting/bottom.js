import request from '@/utils/request'
export function listBottom(query){ return request({ url:'/meeting/bottom/list', method:'get', params:query }) }
export function getBottomStats(activityId){ return request({ url:'/meeting/bottom/stats/'+activityId, method:'get' }) }
export function getBottom(bottomId){ return request({ url:'/meeting/bottom/'+bottomId, method:'get' }) }
export function addBottom(data){ return request({ url:'/meeting/bottom', method:'post', data }) }
export function updateBottom(data){ return request({ url:'/meeting/bottom', method:'put', data }) }
export function delBottom(bottomId){ return request({ url:'/meeting/bottom/'+bottomId, method:'delete' }) }