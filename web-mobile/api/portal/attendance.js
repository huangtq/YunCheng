import request from '@/utils/request'
import { getMpToken } from '@/utils/mpAuth'
export function getMyAttendance(activityId) { const headers={ isToken:false }; const token=getMpToken(); if(token) headers['Mp-Authorization']='Bearer '+token; return request({ url:'/portal/meeting/apply/attendance', method:'get', params:{ activityId }, headers }) }
