import { resolveModulePage } from '@/utils/meetingModules'

export function openMeetingItem(activityId, item, options = {}) {
  if (!item) return false
  const entry = {
    ...item,
    title: item.title || item.menuName || '会议菜单',
    linkType: item.linkType || item.menuType || 'none'
  }
  const navigate = options.replace ? uni.redirectTo : uni.navigateTo

  if (entry.linkType === 'url' && entry.externalUrl) {
    navigate({
      url: `/pages/common/webview/index?title=${encodeURIComponent(entry.title)}&url=${encodeURIComponent(entry.externalUrl)}`
    })
    return true
  }

  if (entry.linkType === 'content' && entry.contentType === 'image' && (entry.contentUrl || entry.iconUrl)) {
    navigate({
      url: `/pages/common/imageview/index?activityId=${activityId}&title=${encodeURIComponent(entry.title)}&url=${encodeURIComponent(entry.contentUrl || entry.iconUrl)}`
    })
    return true
  }

  if (entry.linkType === 'content' && entry.content) {
    navigate({
      url: `/pages/common/textview/index?activityId=${activityId}&title=${encodeURIComponent(entry.title)}&content=${encodeURIComponent(entry.content)}`
    })
    return true
  }

  if (entry.linkType === 'module' && entry.moduleKey === 'apply') {
    navigate({ url: `/pages/meeting/apply/index?activityId=${activityId}` })
    return true
  }

  if (entry.linkType === 'module' && entry.moduleKey) {
    const url = resolveModulePage(activityId, entry.moduleKey, entry.title)
    if (url) {
      navigate({ url })
      return true
    }
  }

  uni.showToast({ title: '暂无可用操作', icon: 'none' })
  return false
}
