/**
 * 九宫格 / 底部栏可跳转模块（与后台目录保持一致）
 */
export const MEETING_MODULES = [
  { key: 'schedule', label: '会议议程', title: '会议议程' },
  { key: 'guest', label: '嘉宾列表', title: '嘉宾列表' },
  { key: 'apply', label: '参会报名', title: '参会报名' },
  { key: 'hotel', label: '酒店预订', title: '酒店预订' },
  { key: 'venue', label: '会场导航', title: '会场导航' },
  { key: 'nav', label: '会议导航', title: '会议导航' },
  { key: 'exhibitor', label: '展商名录', title: '展商名录' },
  { key: 'meal', label: '餐票服务', title: '餐票服务' }
]

export function getMeetingModule(key) {
  return MEETING_MODULES.find(item => item.key === key)
}

export function resolveModulePage(activityId, moduleKey, title) {
  const mod = getMeetingModule(moduleKey)
  if (!mod) return ''
  if (moduleKey === 'apply') {
    return `/pages/meeting/apply/index?activityId=${activityId}`
  }
  const name = encodeURIComponent(title || mod.title || mod.label)
  return `/pages/meeting/module?activityId=${activityId}&moduleKey=${moduleKey}&title=${name}`
}