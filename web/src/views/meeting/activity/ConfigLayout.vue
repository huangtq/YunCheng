<template>
  <div class="config-layout">
    <div class="config-breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <a @click.prevent="goList">会议列表</a>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="isChildPage">
          <a @click.prevent="goHub">会议配置</a>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-else>
          <span class="current">会议配置</span>
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="isChildPage">
          <span class="current">{{ currentTitle }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <router-view />
  </div>
</template>

<script setup name="MeetingConfigLayout">
const route = useRoute()
const router = useRouter()

const activityId = computed(() => route.query.id)

const isChildPage = computed(() => {
  return route.name === 'MeetingGrid' || route.name === 'MeetingApplyChannel'
})

const currentTitle = computed(() => {
  return (route.meta && route.meta.title) || ''
})

function goList() {
  router.push({ path: '/meeting/activity' })
}

function goHub() {
  if (!activityId.value) {
    router.push({ path: '/meeting/activity-config' })
    return
  }
  router.push({ path: '/meeting/activity-config', query: { id: activityId.value } })
}
</script>

<style scoped>
.config-layout {
  min-height: 100%;
}
.config-breadcrumb {
  padding: 12px 20px 0;
  background: transparent;
}
.config-breadcrumb a {
  color: #409eff;
  cursor: pointer;
}
.config-breadcrumb .current {
  color: #97a8be;
}
</style>