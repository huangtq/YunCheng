<template>
  <image
    v-if="source && !imageFailed"
    class="meeting-icon"
    :style="{ width: `${size}rpx`, height: `${size}rpx` }"
    :src="source"
    mode="aspectFit"
    @error="imageFailed = true"
  />
  <uni-icons v-else type="grid" :size="size / 2" :color="color || '#9ca3af'" />
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import config from '@/config'

const props = defineProps({
  iconType: {
    type: String,
    default: 'image'
  },
  iconKey: {
    type: String,
    default: ''
  },
  iconUrl: {
    type: String,
    default: ''
  },
  size: {
    type: Number,
    default: 64
  },
  color: {
    type: String,
    default: ''
  }
})

const imageFailed = ref(false)

const source = computed(() => {
  if (props.iconType === 'icon' && props.iconKey) {
    return `/static/icons/meeting/${props.iconKey}.svg`
  }
  if (!props.iconUrl) {
    return ''
  }
  if (/^https?:\/\//.test(props.iconUrl)) {
    return props.iconUrl
  }
  return `${config.baseUrl}${props.iconUrl}`
})

watch(
  () => [props.iconType, props.iconKey, props.iconUrl],
  () => {
    imageFailed.value = false
  }
)
</script>

<style scoped>
.meeting-icon {
  display: block;
}
</style>
