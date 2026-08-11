const isLocalH5 = typeof window !== 'undefined'
  && ['localhost', '127.0.0.1'].includes(window.location.hostname)

export default {
  // 正式环境 API；本地联调改为 http://localhost:8080
  baseUrl: isLocalH5 ? 'http://localhost:8080' : 'https://yunchengmice.cn/prod-api',
  // 与 manifest.json -> h5.router.base 保持一致
  h5Base: '/h5',
  appInfo: {
    name: 'YunCheng Meeting',
    version: '1.0.0',
    logo: '/static/logo.png',
    site_url: 'https://yunchengmice.cn',
    agreements: []
  }
}
