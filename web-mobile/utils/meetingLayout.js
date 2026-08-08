/**
 * Resolve portal layout for standard grid home page.
 * Keep column/style mapping aligned with web admin gridTemplate options.
 */

export function normalizeGridTemplate(value) {
  const legacyMap = {
    grid3x3: '1',
    grid2x2: '5',
    list: '7'
  }
  return legacyMap[value] || String(value || '1')
}

export function resolveGridColumns(gridTemplate) {
  const value = normalizeGridTemplate(gridTemplate)
  if (['7', '71'].includes(value)) return 1
  if (['68', '681'].includes(value)) return 2
  return 3
}

export function resolveGridStyle(gridTemplate) {
  const value = normalizeGridTemplate(gridTemplate)
  if (['5', '71'].includes(value)) return 'icon'
  return 'icon-text'
}

export function buildHomeLayout(rawLayout = {}, config = {}) {
  const gridTemplate = normalizeGridTemplate(rawLayout.gridTemplate || config.gridTemplate || '1')
  return {
    template: rawLayout.template || config.mobileTemplate || 'standard',
    themeColor: rawLayout.themeColor || config.mobileThemeColor || '#1f6feb',
    backgroundUrl: rawLayout.backgroundUrl || config.mobileBackgroundUrl || '',
    notice: rawLayout.notice || config.mobileNotice || '',
    blocks: Array.isArray(rawLayout.blocks) ? rawLayout.blocks : [],
    gridTemplate,
    gridColumns: Number(rawLayout.gridColumns) || resolveGridColumns(gridTemplate),
    gridStyle: rawLayout.gridStyle || resolveGridStyle(gridTemplate),
    showCountdown: rawLayout.showCountdown === true || String(config.showCountdown) === '1',
    countdownStyle: rawLayout.countdownStyle || config.countdownStyle || 'classic',
    showRegisterCount: rawLayout.showRegisterCount === true || String(config.showRegisterCount) === '1'
  }
}

export function formatCountdownParts(startTime, nowMs) {
  if (!startTime) return null
  const start = new Date(String(startTime).replace(/-/g, '/')).getTime()
  const diff = start - nowMs
  if (Number.isNaN(start)) return null
  if (diff <= 0) {
    return { ended: true, days: 0, hours: 0, minutes: 0, seconds: 0 }
  }
  const days = Math.floor(diff / 86400000)
  const hours = Math.floor((diff % 86400000) / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return { ended: false, days, hours, minutes, seconds }
}
