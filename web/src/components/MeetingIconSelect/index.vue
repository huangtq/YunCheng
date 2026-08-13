<template>
  <div class="meeting-icon-select">
    <el-button class="icon-select-trigger" plain @click="openDialog">
      <MeetingIcon :icon-key="modelValue" :size="28" color="#409eff" />
      <span>{{ currentLabel }}</span>
      <el-icon><ArrowDown /></el-icon>
    </el-button>

    <el-dialog
      v-model="visible"
      title="&#x9009;&#x62E9;&#x56FE;&#x6807;"
      width="560px"
      append-to-body
    >
      <el-input
        v-model="keyword"
        clearable
        placeholder="&#x641C;&#x7D22;&#x56FE;&#x6807;&#x540D;&#x79F0;"
        :prefix-icon="Search"
        class="icon-search"
      />
      <div class="icon-option-grid">
        <button
          v-for="item in pagedIcons"
          :key="item.key"
          type="button"
          class="icon-option"
          :class="{ active: item.key === modelValue }"
          @click="selectIcon(item.key)"
        >
          <img class="icon-option-image" :src="item.url" :alt="item.label" />
          <span>{{ item.label }}</span>
        </button>
      </div>
      <el-empty
        v-if="filteredIcons.length === 0"
        description="&#x672A;&#x627E;&#x5230;&#x5339;&#x914D;&#x56FE;&#x6807;"
      />
      <el-pagination
        v-if="filteredIcons.length > pageSize"
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="filteredIcons.length"
        class="icon-pagination"
        layout="prev, pager, next"
        small
        background
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ArrowDown, Search } from "@element-plus/icons-vue"
import MeetingIcon from "@/components/MeetingIcon"
import { meetingIconFiles } from "@/utils/meeting-icon-files"

const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  }
})

const emit = defineEmits(["update:modelValue"])
const visible = ref(false)
const keyword = ref("")
const currentPage = ref(1)
const pageSize = 16

const currentLabel = computed(() => {
  return meetingIconFiles.find(item => item.key === props.modelValue)?.label
    || props.modelValue
    || "\u8bf7\u9009\u62e9\u56fe\u6807"
})

const filteredIcons = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) {
    return meetingIconFiles
  }
  return meetingIconFiles.filter(item => item.label.toLowerCase().includes(value))
})

const pagedIcons = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredIcons.value.slice(start, start + pageSize)
})

function openDialog() {
  keyword.value = ""
  currentPage.value = 1
  visible.value = true
}

watch(keyword, () => {
  currentPage.value = 1
})

function selectIcon(key) {
  emit("update:modelValue", key)
  visible.value = false
}
</script>

<style scoped>
.icon-select-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 180px;
  justify-content: space-between;
}

.icon-search {
  margin-bottom: 16px;
}

.icon-option-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.icon-option {
  display: flex;
  min-height: 88px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  color: var(--el-text-color-regular);
  background: var(--el-fill-color-blank);
  cursor: pointer;
}

.icon-option-image {
  width: 34px;
  height: 34px;
  object-fit: contain;
}

.icon-option:hover,
.icon-option.active {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.icon-pagination {
  justify-content: center;
  margin-top: 16px;
}
</style>
