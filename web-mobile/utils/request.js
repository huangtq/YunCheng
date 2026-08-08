import config from '@/config'
import errorCode from '@/utils/errorCode'
import { toast, tansParams } from '@/utils/common'

let timeout = 10000
const baseUrl = config.baseUrl

const request = conf => {
  const headers = conf.headers || {}
  const isToken = headers.isToken === false
  conf.header = conf.header || {}
  Object.keys(headers).forEach(key => {
    if (key !== 'isToken' && headers[key] != null) {
      conf.header[key] = headers[key]
    }
  })
  if (conf.params) {
    let url = conf.url + '?' + tansParams(conf.params)
    url = url.slice(0, -1)
    conf.url = url
  }
  return new Promise((resolve, reject) => {
    uni.request({
      method: conf.method || 'get',
      timeout: conf.timeout || timeout,
      url: conf.baseUrl || baseUrl + conf.url,
      data: conf.data,
      header: conf.header,
      dataType: 'json'
    }).then(response => {
      const res = response
      const code = res.data.code || 200
      const msg = errorCode[code] || res.data.msg || errorCode['default']
      if (code === 401) {
        toast(msg || '授权已失效，请重新进入会议页面')
        reject('mp unauthorized')
      } else if (code === 500) {
        toast(msg)
        reject('500')
      } else if (code !== 200) {
        toast(msg)
        reject(code)
      } else {
        resolve(res.data)
      }
    }).catch(error => {
      let { message } = error
      if (message === 'Network Error') {
        message = '后端接口连接异常'
      } else if (message && message.includes('timeout')) {
        message = '系统接口请求超时'
      } else if (message && message.includes('Request failed with status code')) {
        message = '系统接口' + message.slice(-3) + '异常'
      }
      toast(message || '请求失败')
      reject(error)
    })
  })
}

export default request