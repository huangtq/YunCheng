import cache from '@/plugins/cache'
import useSettingsStore from '@/store/modules/settings'

const PERSIST_KEY = 'tags-view-visited'
const MEETING_ROUTE_TAG_GROUPS = new Map([
  ['meeting-place', new Set([
    '/meeting/activity-config/venue',
    '/meeting/activity-config/topic',
    '/meeting/activity-config/schedule',
    '/meeting/activity-config/expert'
  ])],
  ['meeting-guest', new Set([
    '/meeting/activity-config/guest',
    '/meeting/activity-config/guest-trip',
    '/meeting/activity-config/guest-rule',
    '/meeting/activity-config/guest-fee'
  ])],
  ['meeting-hotel', new Set([
    '/meeting/activity-config/hotel',
    '/meeting/activity-config/hotel-order',
    '/meeting/activity-config/hotel-assign'
  ])]
])

const ROUTE_TAG_GROUP_BY_PATH = new Map(
  [...MEETING_ROUTE_TAG_GROUPS.entries()].flatMap(([group, paths]) => [...paths].map(path => [path, group]))
)

function getTagsGroup(view) {
  const configuredGroup = view?.meta?.tagsGroup
  const pathGroup = ROUTE_TAG_GROUP_BY_PATH.get(view?.path) || ''
  const group = configuredGroup || pathGroup
  if (!group) return ''
  const activityId = view?.query?.id
  return activityId ? `${group}:${activityId}` : group
}

function isPersistEnabled() {
  return useSettingsStore().tagsViewPersist
}

function saveVisitedViews(views) {
  if (!isPersistEnabled()) return
  const toSave = views.filter(v => !(v.meta && v.meta.affix)).map(v => ({ path: v.path, fullPath: v.fullPath, name: v.name, title: v.title, query: v.query, meta: v.meta }))
  cache.local.setJSON(PERSIST_KEY, toSave)
}

function loadVisitedViews() {
  return cache.local.getJSON(PERSIST_KEY) || []
}

function clearVisitedViews() {
  cache.local.remove(PERSIST_KEY)
}

function getViewKey(view) {
  const isMeetingConfig = view?.path?.startsWith('/meeting/activity-config')
  return view?.meta?.tabKey === 'fullPath' || isMeetingConfig ? view.fullPath : view.path
}

function getViewTitle(view) {
  const title = view.meta?.title || 'no-name'
  const isMeetingConfig = view?.path?.startsWith('/meeting/activity-config')
  const activityId = view.meta?.tabKey === 'fullPath' || isMeetingConfig ? view.query?.id : ''
  return activityId ? `${title}（会议 ${activityId}）` : title
}

function isSameView(first, second) {
  return getViewKey(first) === getViewKey(second)
}

const useTagsViewStore = defineStore(
  'tags-view',
  {
    state: () => ({
      visitedViews: [],
      cachedViews: [],
      iframeViews: []
    }),
    actions: {
      addView(view) {
        this.addVisitedView(view)
        this.addCachedView(view)
      },
      addIframeView(view) {
        if (this.iframeViews.some(v => v.path === view.path)) return
        this.iframeViews.push(
          Object.assign({}, view, {
            title: view.meta.title || 'no-name'
          })
        )
      },
      addVisitedView(view) {
        const tagsGroup = getTagsGroup(view)
        if (tagsGroup) {
          const idx = this.visitedViews.findIndex(v => getTagsGroup(v) === tagsGroup)
          const next = Object.assign({}, view, {
            title: getViewTitle(view)
          })
          if (idx > -1) {
            this.visitedViews.splice(idx, 1, next)
            saveVisitedViews(this.visitedViews)
            return
          }
          this.visitedViews.push(next)
          saveVisitedViews(this.visitedViews)
          return
        }
        const existing = this.visitedViews.find(v => getViewKey(v) === getViewKey(view))
        if (existing) {
          Object.assign(existing, view, { title: getViewTitle(view) })
          saveVisitedViews(this.visitedViews)
          return
        }
        this.visitedViews.push(
          Object.assign({}, view, {
            title: getViewTitle(view)
          })
        )
        saveVisitedViews(this.visitedViews)
      },
      addAffixView(view) {
        if (this.visitedViews.some(v => getViewKey(v) === getViewKey(view))) return
        this.visitedViews.unshift(
          Object.assign({}, view, {
            title: getViewTitle(view)
          })
        )
      },
      addCachedView(view) {
        if (this.cachedViews.includes(view.name)) return
        if (!view.meta.noCache) {
          this.cachedViews.push(view.name)
        }
      },
      delView(view) {
        return new Promise(resolve => {
          this.delVisitedView(view)
          this.delCachedView(view)
          resolve({
            visitedViews: [...this.visitedViews],
            cachedViews: [...this.cachedViews]
          })
        })
      },
      delVisitedView(view) {
        return new Promise(resolve => {
          for (const [i, v] of this.visitedViews.entries()) {
            if (getViewKey(v) === getViewKey(view)) {
              this.visitedViews.splice(i, 1)
              break
            }
          }
          this.iframeViews = this.iframeViews.filter(item => item.path !== view.path)
          saveVisitedViews(this.visitedViews)
          resolve([...this.visitedViews])
        })
      },
      delIframeView(view) {
        return new Promise(resolve => {
          this.iframeViews = this.iframeViews.filter(item => item.path !== view.path)
          resolve([...this.iframeViews])
        })
      },
      delCachedView(view) {
        return new Promise(resolve => {
          const index = this.cachedViews.indexOf(view.name)
          index > -1 && this.cachedViews.splice(index, 1)
          resolve([...this.cachedViews])
        })
      },
      delOthersViews(view) {
        return new Promise(resolve => {
          this.delOthersVisitedViews(view)
          this.delOthersCachedViews(view)
          resolve({
            visitedViews: [...this.visitedViews],
            cachedViews: [...this.cachedViews]
          })
        })
      },
      delOthersVisitedViews(view) {
        return new Promise(resolve => {
          this.visitedViews = this.visitedViews.filter(v => {
            return v.meta.affix || getViewKey(v) === getViewKey(view)
          })
          this.iframeViews = this.iframeViews.filter(item => isSameView(item, view))
          saveVisitedViews(this.visitedViews)
          resolve([...this.visitedViews])
        })
      },
      delOthersCachedViews(view) {
        return new Promise(resolve => {
          const index = this.cachedViews.indexOf(view.name)
          if (index > -1) {
            this.cachedViews = this.cachedViews.slice(index, index + 1)
          } else {
            this.cachedViews = []
          }
          resolve([...this.cachedViews])
        })
      },
      delAllViews(view) {
        return new Promise(resolve => {
          this.delAllVisitedViews(view)
          this.delAllCachedViews(view)
          resolve({
            visitedViews: [...this.visitedViews],
            cachedViews: [...this.cachedViews]
          })
        })
      },
      delAllVisitedViews(view) {
        return new Promise(resolve => {
          const affixTags = this.visitedViews.filter(tag => tag.meta.affix)
          this.visitedViews = affixTags
          this.iframeViews = []
          clearVisitedViews()
          resolve([...this.visitedViews])
        })
      },
      delAllCachedViews(view) {
        return new Promise(resolve => {
          this.cachedViews = []
          resolve([...this.cachedViews])
        })
      },
      updateVisitedView(view) {
        const tagsGroup = getTagsGroup(view)
        for (let v of this.visitedViews) {
          const samePath = getViewKey(v) === getViewKey(view)
          const sameGroup = tagsGroup && getTagsGroup(v) === tagsGroup
          if (samePath || sameGroup) {
            Object.assign(v, view, {
              title: getViewTitle(view)
            })
            break
          }
        }
        saveVisitedViews(this.visitedViews)
      },
      delRightTags(view) {
        return new Promise(resolve => {
          const index = this.visitedViews.findIndex(v => getViewKey(v) === getViewKey(view))
          if (index === -1) {
            return
          }
          this.visitedViews = this.visitedViews.filter((item, idx) => {
            if (idx <= index || (item.meta && item.meta.affix)) {
              return true
            }
            const i = this.cachedViews.indexOf(item.name)
            if (i > -1) {
              this.cachedViews.splice(i, 1)
            }
            if(item.meta.link) {
              const fi = this.iframeViews.findIndex(v => v.path === item.path)
              this.iframeViews.splice(fi, 1)
            }
            return false
          })
          saveVisitedViews(this.visitedViews)
          resolve([...this.visitedViews])
        })
      },
      delLeftTags(view) {
        return new Promise(resolve => {
          const index = this.visitedViews.findIndex(v => getViewKey(v) === getViewKey(view))
          if (index === -1) {
            return
          }
          this.visitedViews = this.visitedViews.filter((item, idx) => {
            if (idx >= index || (item.meta && item.meta.affix)) {
              return true
            }
            const i = this.cachedViews.indexOf(item.name)
            if (i > -1) {
              this.cachedViews.splice(i, 1)
            }
            if(item.meta.link) {
              const fi = this.iframeViews.findIndex(v => v.path === item.path)
              this.iframeViews.splice(fi, 1)
            }
            return false
          })
          saveVisitedViews(this.visitedViews)
          resolve([...this.visitedViews])
        })
      },
      // 恢复持久化的 tags
      loadPersistedViews() {
        const views = loadVisitedViews()
        views.forEach(view => {
          this.addVisitedView(view)
        })
      }
    }
  })

export default useTagsViewStore
