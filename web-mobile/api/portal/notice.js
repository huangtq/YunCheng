import request from '@/utils/request'
import { getMpToken } from '@/utils/mpAuth'
const headers=()=>{const h={isToken:false};const token=getMpToken();if(token)h['Mp-Authorization']='Bearer '+token;return h}
export const getNotices=activityId=>request({url:`/portal/meeting/notice/${activityId}`,method:'get',headers:headers()})
export const getNotice=(activityId,noticeId)=>request({url:`/portal/meeting/notice/${activityId}/${noticeId}`,method:'get',headers:headers()})
