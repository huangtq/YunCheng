/**
 * 可挂到九宫格 / 底部栏的 C 端模块目录。
 * 后台运营模块（任务规则、分房、劳务费等）不放入此处。
 */
export const MEETING_MODULES = [
  {
    key: 'schedule',
    label: '会议议程',
    desc: '展示主题与日程安排',
    adminPath: '/meeting/activity-config/schedule',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_jsdt'
  },
  {
    key: 'guest',
    label: '嘉宾列表',
    desc: '展示会议嘉宾信息',
    adminPath: '/meeting/activity-config/guest',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_wwry'
  },
  {
    key: 'apply',
    label: '参会报名',
    desc: '报名通道与字段配置',
    adminPath: '/meeting/activity-config/apply-channel',
    mobilePath: '/pages/meeting/apply/index',
    iconKey: 'menu_hdbm'
  },
  {
    key: 'hotel',
    label: '酒店预订',
    desc: '会议酒店与房型',
    adminPath: '/meeting/activity-config/hotel',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_rzgl'
  },
  {
    key: 'venue',
    label: '会场导航',
    desc: '会场列表（含直播会场）',
    adminPath: '/meeting/activity-config/venue',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_xqfc'
  },
  {
    key: 'nav',
    label: '会议导航',
    desc: '地点/POI 导航',
    adminPath: '/meeting/activity-config/nav',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_bszn'
  },
  {
    key: 'exhibitor',
    label: '展商名录',
    desc: '展商信息展示',
    adminPath: '/meeting/activity-config/exhibitor',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_swgs'
  },
  {
    key: 'meal',
    label: '餐票服务',
    desc: '餐票说明与领取',
    adminPath: '/meeting/activity-config/meal',
    mobilePath: '/pages/meeting/module',
    iconKey: 'menu_cwxx'
  }
]

export const MEETING_MODULE_OPTIONS = MEETING_MODULES.map(item => ({
  label: item.label,
  value: item.key
}))

export function getMeetingModule(key) {
  return MEETING_MODULES.find(item => item.key === key)
}

export function meetingModuleLabel(key) {
  return getMeetingModule(key)?.label || key || '-'
}

/** 中枢卡片 action → 可挂九宫格的 moduleKey（无则不可挂） */
export const HUB_ACTION_MODULE_KEY = {
  schedule: 'schedule',
  guest: 'guest',
  apply: 'apply',
  hotel: 'hotel',
  venue: 'venue',
  nav: 'nav',
  exhibitor: 'exhibitor',
  meal: 'meal'
}

/** H5 会议首页 URL（history 模式，供预览 / 二维码扫码） */
export function buildMeetingH5HomeUrl(activityId) {
  const id = activityId || ''
  const path = '/pages/meeting/home?activityId=' + id
  const h5Origin = String(import.meta.env.VITE_H5_ORIGIN || '').replace(/\/$/, '')
  if (h5Origin) {
    return h5Origin + path
  }
  const origin = typeof window !== 'undefined' ? window.location.origin : ''
  if (origin) {
    return origin + '/h5' + path
  }
  return '/h5' + path
}

/** 微信分享落地页（同域 /h5/share，供爬虫抓标题/地点/时间） */
export function buildMeetingShareLandingUrl(activityId) {
  const id = activityId || ''
  const path = '/h5/share?activityId=' + id
  const origin = typeof window !== 'undefined' ? window.location.origin : ''
  if (origin) {
    return origin + path
  }
  const h5Origin = String(import.meta.env.VITE_H5_ORIGIN || '').replace(/\/$/, '')
  if (h5Origin) {
    // VITE_H5_ORIGIN 形如 https://yunchengmice.cn/h5
    return h5Origin.replace(/\/h5$/, '') + path
  }
  return path
}

