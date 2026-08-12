import request from '@/utils/request'
import { getMpToken } from '@/utils/mpAuth'
export function submitMeetingFeedback(data) { const headers = { isToken: false }; const token = getMpToken(); if (token) headers['Mp-Authorization'] = 'Bearer ' + token; return request({ url: '/portal/meeting/feedback', method: 'post', data, headers }) }
