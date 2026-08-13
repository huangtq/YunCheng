import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    redirect: '/meeting/activity'
  },
  {
    path: '/lock',
    component: () => import('@/views/lock'),
    hidden: true,
    meta: { title: '锁定屏幕' }
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile/:activeTab?',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/meeting/activity-form',
    component: Layout,
    hidden: true,
    permissions: ['meeting:activity:add', 'meeting:activity:edit'],
    children: [
      {
        path: '',
        component: () => import('@/views/meeting/activity/form'),
        name: 'MeetingActivityForm',
        meta: { title: '会议编辑', activeMenu: '/meeting/activity', noCache: true }
      }
    ]
  },
  {
    path: '/meeting/activity-config',
    component: Layout,
    hidden: true,
    permissions: ['meeting:activity:query', 'meeting:activity:edit'],
    children: [
      {
        path: '',
        component: () => import('@/views/meeting/activity/ConfigLayout'),
        meta: { title: '会议配置', activeMenu: '/meeting/activity', breadcrumb: false },
        children: [
          {
            path: '',
            name: 'MeetingActivityConfig',
            component: () => import('@/views/meeting/activity/config'),
            meta: {
              title: '会议配置',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'grid',
            name: 'MeetingGrid',
            component: () => import('@/views/meeting/grid/index'),
            meta: {
              title: '九宫格配置',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'content',
            name: 'MeetingContent',
            component: () => import('@/views/meeting/activity/content'),
            meta: {
              title: '内容与资料',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'checkin',
            name: 'MeetingCheckin',
            component: () => import('@/views/meeting/activity/checkin'),
            meta: { title: '现场核验', activeMenu: '/meeting/activity', noCache: true, tagsGroup: 'meeting-config' }
          },
          { path: 'notice', name: 'MeetingNotice', component: () => import('@/views/meeting/activity/notice'), meta: { title: '会议通知', activeMenu: '/meeting/activity', noCache: true, tagsGroup: 'meeting-config' } },
          {
            path: 'file',
            name: 'MeetingFile',
            component: () => import('@/views/file/index'),
            meta: {
              title: '文件管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'apply-channel',
            name: 'MeetingApplyChannel',
            component: () => import('@/views/meeting/apply/channel'),
            meta: {
              title: '报名通道配置',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'apply-field',
            name: 'MeetingApplyField',
            component: () => import('@/views/meeting/apply/field'),
            meta: {
              title: '报名字段配置',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'apply-order',
            name: 'MeetingApplyOrder',
            component: () => import('@/views/meeting/apply/order'),
            meta: {
              title: '报名订单',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'venue',
            name: 'MeetingVenue',
            component: () => import('@/views/meeting/place/venue'),
            meta: {
              title: '会场管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'topic',
            name: 'MeetingTopic',
            component: () => import('@/views/meeting/place/topic'),
            meta: {
              title: '主题管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'schedule',
            name: 'MeetingSchedule',
            component: () => import('@/views/meeting/place/schedule'),
            meta: {
              title: '日程管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'expert',
            name: 'MeetingExpert',
            component: () => import('@/views/meeting/place/expert'),
            meta: {
              title: '专家任务排期',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'guest',
            name: 'MeetingGuest',
            component: () => import('@/views/meeting/guest/index'),
            meta: {
              title: '嘉宾管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'guest-trip',
            name: 'MeetingGuestTrip',
            component: () => import('@/views/meeting/guest/trip'),
            meta: {
              title: '嘉宾行程',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'guest-rule',
            name: 'MeetingGuestRule',
            component: () => import('@/views/meeting/guest/rule'),
            meta: {
              title: '任务规则配置',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'guest-fee',
            name: 'MeetingGuestFee',
            component: () => import('@/views/meeting/guest/fee'),
            meta: {
              title: '劳务费管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'staff',
            name: 'MeetingStaff',
            component: () => import('@/views/meeting/guest/staff'),
            meta: {
              title: '工作人员',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'exhibitor',
            name: 'MeetingExhibitor',
            component: () => import('@/views/meeting/feature/exhibitor'),
            meta: {
              title: '展商管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'meal',
            name: 'MeetingMeal',
            component: () => import('@/views/meeting/feature/meal'),
            meta: {
              title: '餐票管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'hotel',
            name: 'MeetingHotel',
            component: () => import('@/views/meeting/hotel/index'),
            meta: {
              title: '会议酒店',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'hotel-order',
            name: 'MeetingHotelOrder',
            component: () => import('@/views/meeting/hotel/order'),
            meta: {
              title: '酒店订单',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'hotel-assign',
            name: 'MeetingHotelAssign',
            component: () => import('@/views/meeting/hotel/assign'),
            meta: {
              title: '分房管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
          {
            path: 'nav',
            name: 'MeetingNav',
            component: () => import('@/views/meeting/nav/index'),
            meta: {
              title: '导航管理',
              activeMenu: '/meeting/activity',
              noCache: true,
              tagsGroup: 'meeting-config'
            }
          },
        ]
      }
    ]
  },
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

export default router
