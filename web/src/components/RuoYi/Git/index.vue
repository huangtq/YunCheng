<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script setup>
const url = ref('https://yunchengmice.cn')

function goto() {
  window.open(url.value)
}
</script>
