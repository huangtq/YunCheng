import request from '@/utils/request'
export function listNav(query){ return request({ url:'/meeting/nav/list', method:'get', params:query }) }
export function getNavStats(activityId){ return request({ url:'/meeting/nav/stats/'+activityId, method:'get' }) }
export function getNav(navId){ return request({ url:'/meeting/nav/'+navId, method:'get' }) }
export function addNav(data){ return request({ url:'/meeting/nav', method:'post', data }) }
export function updateNav(data){ return request({ url:'/meeting/nav', method:'put', data }) }
export function delNav(navId){ return request({ url:'/meeting/nav/'+navId, method:'delete' }) }