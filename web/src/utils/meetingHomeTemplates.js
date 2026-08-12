/**
 * Reference-site page blueprints. A template owns layout only; a meeting owns
 * its visual assets and entry targets. Keep the key stable once published.
 */
export const MEETING_HOME_TEMPLATES = [
  {
    key: 'sciconf-image-menu',
    label: '参考站：图片菜单',
    description: '对应 38736、38335、30995、24669：主视觉、倒计时、连续图片入口。',
    preview: 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png',
    layout: { template: 'standard', gridTemplate: '68', gridColumns: 2, gridStyle: 'image-card', showCoverMeta: false, showSectionTitle: false, showCountdown: true, countdownStyle: 'digital', visual: { heroHeight: 0, countdownTop: 18, countdownBottom: 18, itemGap: 10, itemPadding: 12 } },
    slots: ['hero', 'countdown', 'menu', 'footer'],
    entrySections: [
      { key: 'menu', label: '图片菜单', columns: 2, ratio: 'wide', min: 1, max: 12 }
    ],
    entryDefaults: { iconType: 'image', imageRatio: 'wide' }
  },
  {
    key: 'sciconf-image-menu-sections',
    label: '参考站：分区图片菜单',
    description: '对应 38760：主视觉、倒计时、小图入口区和重点大图入口区。',
    preview: 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png',
    layout: { template: 'standard', gridTemplate: '68', gridColumns: 2, gridStyle: 'image-card', showCoverMeta: false, showSectionTitle: false, showCountdown: true, countdownStyle: 'digital', visual: { heroHeight: 0, countdownTop: 18, countdownBottom: 18, itemGap: 10, itemPadding: 12 } },
    slots: ['hero', 'countdown', 'quick-menu', 'feature-menu', 'footer'],
    entrySections: [
      { key: 'quick-menu', label: '快捷入口区', columns: 2, ratio: 'wide', min: 1, max: 4 },
      { key: 'feature-menu', label: '重点入口区', columns: 2, ratio: 'tall', min: 0, max: 6 }
    ],
    entryDefaults: { iconType: 'image', imageRatio: 'wide' }
  },
  {
    key: 'sciconf-icon-grid',
    label: '参考站：主视觉图标导航',
    description: '对应 29690：主视觉、翻牌倒计时、三列图标入口。',
    preview: 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20231225/3181354ccb4b44f0984581f5401a18fc.jpg',
    layout: { template: 'standard', gridTemplate: '1', gridColumns: 3, gridStyle: 'icon-text', showCoverMeta: false, showSectionTitle: false, showCountdown: true, countdownStyle: 'digital', visual: { heroHeight: 0, countdownTop: 18, countdownBottom: 16, itemGap: 8, itemPadding: 16 } },
    slots: ['hero', 'countdown', 'menu', 'footer'],
    entrySections: [
      { key: 'menu', label: '图标导航', columns: 3, ratio: 'square', min: 1, max: 12 }
    ],
    entryDefaults: { iconType: 'icon', imageRatio: 'square' }
  },
  {
    key: 'sciconf-poster-map',
    label: '参考站：整图热点海报',
    description: '对应 36428：长海报即页面，透明热点打开对应栏目。',
    preview: 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240528/f372efc58bfe4b93900b0dd93c113f6e.jpg',
    layout: { template: 'image-map', gridColumns: 0, gridStyle: 'none', showCoverMeta: false, showSectionTitle: false, showCountdown: false, visual: { heroHeight: 0, countdownTop: 0, countdownBottom: 0, itemGap: 0, itemPadding: 0 } },
    slots: ['poster', 'hotspots'],
    entrySections: [{ key: 'hotspots', label: '海报热点', columns: 0, ratio: 'none', min: 1, max: 20 }],
    entryDefaults: { iconType: 'none', imageRatio: 'none' }
  },
  {
    key: 'sciconf-tile-service',
    label: '服务台：不规则 Tile',
    description: '适合把报名、日程、导航、酒店等会务服务做成不同大小的功能块。',
    preview: 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20260428/902a51e381f7464abb1441a5f6fc220a.png',
    layout: { template: 'standard', gridTemplate: 'tile', gridColumns: 6, gridStyle: 'tile', showCoverMeta: false, showSectionTitle: false, showCountdown: true, countdownStyle: 'simple', visual: { heroHeight: 0, countdownTop: 14, countdownBottom: 14, itemGap: 8, itemPadding: 12 } },
    slots: ['hero', 'countdown', 'tiles', 'footer'],
    entrySections: [{ key: 'tiles', label: '服务入口', columns: 6, ratio: 'tile', min: 1, max: 20 }],
    entryDefaults: { iconType: 'image', imageRatio: 'tile' }
  }
]

export function getMeetingHomeTemplate(key) {
  return MEETING_HOME_TEMPLATES.find(item => item.key === key) || MEETING_HOME_TEMPLATES[0]
}

export function createTemplatePage(key, entries = []) {
  const template = getMeetingHomeTemplate(key)
  const layout = JSON.parse(JSON.stringify(template.layout))
  layout.entrySections = JSON.parse(JSON.stringify(template.entrySections || []))
  const defaultSection = template.entrySections?.[0]?.key || 'menu'
  const normalizedEntries = entries.map((entry, index) => ({
    ...entry,
    id: entry.id || `entry_${Date.now()}_${index}`,
    title: entry.title || '',
    enabled: entry.enabled !== false,
    iconType: entry.iconType || template.entryDefaults.iconType,
    imageRatio: entry.imageRatio || template.entryDefaults.imageRatio,
    targetType: entry.targetType || 'module',
    target: entry.target || { moduleKey: 'apply' },
    iconUrl: entry.iconUrl || '',
    // A page can be switched from another template. Its old visual slot must
    // never leak into the new template's section structure.
    sectionKey: template.entrySections?.some(section => section.key === entry.sectionKey)
      ? entry.sectionKey
      : defaultSection
  }))
  return {
    mode: 'standard',
    schemaVersion: '2',
    templateKey: template.key,
    theme: { color: '#1f6feb' },
    layout,
    sections: template.slots.map(slot => ({ id: slot, type: slot, enabled: true, entries: normalizedEntries.filter(entry => entry.sectionKey === slot) })),
    entryTree: normalizedEntries
  }
}
