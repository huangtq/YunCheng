import request from '@/utils/request'
export function listRule(query) { return request({ url: '/meeting/guest/rule/list', method: 'get', params: query }) }
export function getRuleStats(activityId) { return request({ url: '/meeting/guest/rule/stats/' + activityId, method: 'get' }) }
export function getRule(ruleId) { return request({ url: '/meeting/guest/rule/' + ruleId, method: 'get' }) }
export function addRule(data) { return request({ url: '/meeting/guest/rule', method: 'post', data }) }
export function updateRule(data) { return request({ url: '/meeting/guest/rule', method: 'put', data }) }
export function delRule(ruleId) { return request({ url: '/meeting/guest/rule/' + ruleId, method: 'delete' }) }