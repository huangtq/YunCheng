import request from '@/utils/request'
export function listFee(query) { return request({ url: '/meeting/guest/fee/list', method: 'get', params: query }) }
export function getFeeStats(activityId) { return request({ url: '/meeting/guest/fee/stats/' + activityId, method: 'get' }) }
export function getFee(feeId) { return request({ url: '/meeting/guest/fee/' + feeId, method: 'get' }) }
export function addFee(data) { return request({ url: '/meeting/guest/fee', method: 'post', data }) }
export function generateFeeFromRules(activityId) { return request({ url: '/meeting/guest/fee/generate/' + activityId, method: 'post' }) }
export function updateFee(data) { return request({ url: '/meeting/guest/fee', method: 'put', data }) }
export function delFee(feeId) { return request({ url: '/meeting/guest/fee/' + feeId, method: 'delete' }) }
