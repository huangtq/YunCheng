import request from '@/utils/request'

export function listMeetingContent(query) { return request({ url: '/meeting/content/list', method: 'get', params: query }) }
export function getMeetingContent(contentId) { return request({ url: `/meeting/content/${contentId}`, method: 'get' }) }
export function addMeetingContent(data) { return request({ url: '/meeting/content', method: 'post', data }) }
export function updateMeetingContent(data) { return request({ url: '/meeting/content', method: 'put', data }) }
export function deleteMeetingContent(contentIds) { return request({ url: `/meeting/content/${contentIds}`, method: 'delete' }) }
export function listContentAttachments(contentId) { return request({ url: `/meeting/content/${contentId}/attachments`, method: 'get' }) }
export function saveContentAttachment(data) { return request({ url: '/meeting/content/attachment', method: 'post', data }) }
export function deleteContentAttachments(attachmentIds) { return request({ url: `/meeting/content/attachment/${attachmentIds}`, method: 'delete' }) }
