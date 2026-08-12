<template>
  <div class="app-container checkin-page">
    <el-alert title="现场核验使用参会者短期凭证，不接受订单号。重复提交同一幂等键只会返回首次结果，不会重复签到。" type="info" :closable="false" class="mb16" />
    <el-card header="现场签到核验">
      <el-form :model="form" label-width="100px" style="max-width: 760px">
        <el-form-item label="电子凭证"><el-input v-model="form.credential" type="textarea" :rows="3" placeholder="扫码或粘贴参会者动态凭证" /></el-form-item>
        <el-form-item label="核验点"><el-input v-model="form.checkpoint" placeholder="如：主会场入口" /></el-form-item>
        <el-form-item label="设备标识"><el-input v-model="form.deviceId" placeholder="如：gate-a-01" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="verifying" @click="verify">核验并签到</el-button></el-form-item>
      </el-form>
      <el-result v-if="result" :icon="result.result === 'success' ? 'success' : 'warning'" :title="title" :sub-title="result.reason || '核验已被记录'" />
    </el-card>
  </div>
</template>
<script setup name="MeetingCheckin">
import { computed, getCurrentInstance, ref } from 'vue'
import { useRoute } from 'vue-router'
import { verifyCheckin } from '@/api/meeting/checkin'
const { proxy } = getCurrentInstance(); const route = useRoute(); const form = ref({ credential: '', checkpoint: '', deviceId: '', idempotencyKey: '' }); const verifying = ref(false); const result = ref(null); const title = computed(() => result.value?.result === 'success' ? '签到成功' : result.value?.result === 'already_checked_in' ? '已完成签到' : '核验未通过')
function verify() { if (!form.value.credential) return proxy.$modal.msgError('请扫描或填写电子凭证'); verifying.value = true; form.value.idempotencyKey = form.value.idempotencyKey || `${Date.now()}-${Math.random()}`; verifyCheckin({ ...form.value, activityId: route.query.id }).then(res => { result.value = res.data || {}; if (result.value.result === 'success') form.value.idempotencyKey = '' }).finally(() => { verifying.value = false }) }
</script>
