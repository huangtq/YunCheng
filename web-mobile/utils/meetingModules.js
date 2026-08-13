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
  { key: 'meal', label: '餐票服务', title: '餐票服务' },
  { key: 'feedback', label: '意见反馈', title: '意见反馈' }
]

export function getMeetingModule(key) {
  return MEETING_MODULES.find(item => item.key === key)
}

export function resolveModulePage(activityId, moduleKey, title) {
  // Legacy entries remain routable even though new configurations use rich-text content cards.
  if (moduleKey === 'notice') return `/pages/meeting/notice?activityId=${activityId}`
  const mod = getMeetingModule(moduleKey)
  if (!mod) return ''
  if (moduleKey === 'apply') {
    return `/pages/meeting/apply/index?activityId=${activityId}`
  }
  if (moduleKey === 'schedule' || moduleKey === 'guest' || moduleKey === 'nav') {
    return `/pages/meeting/${moduleKey}?activityId=${activityId}`
  }
  if (moduleKey === 'hotel') return `/pages/meeting/hotel?activityId=${activityId}`
  if (moduleKey === 'feedback') return `/pages/meeting/feedback?activityId=${activityId}`
  if (moduleKey === 'meal' || moduleKey === 'my-attendance') return `/pages/meeting/apply/mine?activityId=${activityId}`
  const name = encodeURIComponent(title || mod.title || mod.label)
  return `/pages/meeting/module?activityId=${activityId}&moduleKey=${moduleKey}&title=${name}`
}

export function resolveEntryPage(activityId, entry) {
  if (!entry || entry.enabled === false || entry.available === false) return ''
  const targetType = entry.targetType || entry.type
  const target = entry.target || {}
  if (targetType === 'group') {
    return entry.id ? `/pages/meeting/entry?activityId=${activityId}&entryId=${encodeURIComponent(entry.id)}` : ''
  }
  if (targetType === 'content') {
    const contentId = typeof target === 'object' ? target.contentId : target
    return /^\d+$/.test(String(contentId || ''))
      ? `/pages/meeting/content?activityId=${activityId}&contentId=${contentId}` : ''
  }
  if (targetType === 'module') {
    const moduleKey = typeof target === 'object' ? target.moduleKey : target
    return moduleKey ? resolveModulePage(activityId, moduleKey, entry.title) : ''
  }
  if (targetType === 'external') {
    const url = typeof target === 'object' ? target.url : target
    return /^https?:\/\//i.test(String(url || ''))
      ? `/pages/common/webview/index?title=${encodeURIComponent(entry.title || '')}&url=${encodeURIComponent(url)}` : ''
  }
  if (targetType === 'phone') {
    const phone = typeof target === 'object' ? target.phone : target
    return phone ? `tel:${encodeURIComponent(phone)}` : ''
  }
  if (targetType === 'file') {
    const fileUrl = typeof target === 'object' ? target.url : target
    return fileUrl ? `/pages/common/webview/index?title=${encodeURIComponent(entry.title || '资料')}&url=${encodeURIComponent(fileUrl)}` : ''
  }
  return ''
}
