import request from '@/utils/request'
export function listTopic(query) { return request({ url: '/meeting/topic/list', method: 'get', params: query }) }
export function getTopicStats(activityId) { return request({ url: '/meeting/topic/stats/' + activityId, method: 'get' }) }
export function getTopic(topicId) { return request({ url: '/meeting/topic/' + topicId, method: 'get' }) }
export function addTopic(data) { return request({ url: '/meeting/topic', method: 'post', data }) }
export function updateTopic(data) { return request({ url: '/meeting/topic', method: 'put', data }) }
export function delTopic(topicId) { return request({ url: '/meeting/topic/' + topicId, method: 'delete' }) }