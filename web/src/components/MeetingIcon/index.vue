<template>
  <img
    v-if="fileIconUrl && !imageFailed"
    class="meeting-icon-image"
    :src="fileIconUrl"
    :width="size"
    :height="size"
    alt=""
    @error="imageFailed = true"
  />
  <el-icon v-else :size="size" :color="color">
    <Grid />
  </el-icon>
</template>

<script setup>
import { Grid } from "@element-plus/icons-vue"

const props = defineProps({
  iconKey: {
    type: String,
    default: ""
  },
  size: {
    type: [String, Number],
    default: 28
  },
  color: {
    type: String,
    default: "currentColor"
  }
})

const imageFailed = ref(false)
const fileIconUrl = computed(() => {
  return props.iconKey ? `/icons/meeting/${props.iconKey}.svg` : ""
})

watch(() => props.iconKey, () => {
  imageFailed.value = false
})
</script>

<style scoped>
.meeting-icon-image {
  display: block;
  object-fit: contain;
}
</style>
