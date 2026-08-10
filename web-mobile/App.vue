<script setup>
import { onLaunch } from '@dcloudio/uni-app'
import { redirectLegacyHashIfNeeded } from '@/utils/h5Route'

onLaunch(() => {
  redirectLegacyHashIfNeeded()
})
</script>

<style lang="scss">
@import '@/static/scss/index.scss';
</style>
