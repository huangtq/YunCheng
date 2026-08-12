import request from '@/utils/request'
import { getMpToken } from '@/utils/mpAuth'
const headers=()=>{const h={isToken:false};const token=getMpToken();if(token)h['Mp-Authorization']='Bearer '+token;return h}
export const getHotels=activityId=>request({url:`/portal/meeting/hotel/${activityId}`,method:'get',headers:headers()})
export const getMyHotelOrders=activityId=>request({url:'/portal/meeting/apply/hotel/orders',method:'get',params:{activityId},headers:headers()})
export const bookHotel=data=>request({url:'/portal/meeting/apply/hotel/order',method:'post',data,headers:headers()})
