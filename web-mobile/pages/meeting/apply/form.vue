<template>
  <view class="form-page">
    <view class="channel">通道：{{ channelName }}</view>
    <view v-for="field in fields" :key="field.fieldId" class="field">
      <view class="label">
        <text v-if="field.requiredFlag === '1'" class="req">*</text>
        {{ field.fieldName }}
      </view>
      <picker
        v-if="field.fieldType === 'select' || field.fieldType === 'radio'"
        :range="parseOptions(field.optionsJson)"
        @change="e => onSelect(field.fieldKey, parseOptions(field.optionsJson)[e.detail.value])"
      >
        <view class="input">{{ formData[field.fieldKey] || field.placeholder || '请选择' }}</view>
      </picker>
      <textarea
        v-else-if="field.fieldType === 'textarea'"
        class="textarea"
        v-model="formData[field.fieldKey]"
        :placeholder="field.placeholder || ('请输入' + field.fieldName)"
      />
      <input
        v-else
        class="input"
        v-model="formData[field.fieldKey]"
        :placeholder="field.placeholder || ('请输入' + field.fieldName)"
      />
    </view>

    <view v-if="!fields.length" class="field">
      <view class="label"><text class="req">*</text>姓名</view>
      <input class="input" v-model="formData.contactName" placeholder="请输入姓名" />
    </view>

    <button class="submit" type="primary" :loading="submitting" @click="submit">提交报名</button>

    <view v-if="showPhone" class="phone-mask">
      <view class="phone-box">
        <view class="phone-title">绑定手机号</view>
        <input class="input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" />
        <button type="primary" @click="doBindPhone">确认绑定</button>
        <button @click="showPhone=false">取消</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPortalApplyChannels, submitPortalApply } from '@/api/portal/meeting'
import { getWxMe, bindWxPhone } from '@/api/portal/wx'
import { captureMpTokenFromQuery, hasMpToken } from '@/utils/mpAuth'
import { getOauthUrl, buildMockOauthJump } from '@/api/portal/wx'

const activityId = ref('')
const channelId = ref('')
const channelName = ref('')
const fields = ref([])
const formData = reactive({})
const submitting = ref(false)
const showPhone = ref(false)
const phone = ref('')
const me = ref({})

onLoad(async (options) => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  channelId.value = options?.channelId || ''
  channelName.value = decodeURIComponent(options?.channelName || '')
  uni.setNavigationBarTitle({ title: '填写报名信息' })
  if (!hasMpToken()) {
    await ensureMpLogin()
    return
  }
  await loadChannel()
  await loadMe()
})

async function ensureMpLogin() {
  try {
    const redirect = (typeof location !== 'undefined')
      ? (location.origin + location.pathname + '#/pages/meeting/apply/form?activityId=' + activityId.value + '&channelId=' + channelId.value + '&channelName=' + encodeURIComponent(channelName.value || ''))
      : ''
    const res = await getOauthUrl(activityId.value, redirect)
    const data = res.data || {}
    if (data.mode === 'mock') {
      if (typeof window !== 'undefined') window.location.href = buildMockOauthJump(activityId.value, redirect)
      return
    }
    if (data.url && typeof window !== 'undefined') window.location.href = data.url
  } catch (e) {}
}

async function loadChannel() {
  const res = await getPortalApplyChannels(activityId.value)
  const list = (res.data && res.data.channels) || []
  const channel = list.find(i => String(i.channelId) === String(channelId.value))
  if (channel) {
    channelName.value = channel.channelName || channelName.value
    fields.value = channel.fields || []
  }
}

async function loadMe() {
  if (!hasMpToken()) return
  try {
    const res = await getWxMe()
    me.value = res.data || {}
  } catch (e) {}
}

function parseOptions(json) {
  if (!json) return []
  try {
    const arr = typeof json === 'string' ? JSON.parse(json) : json
    if (Array.isArray(arr)) {
      return arr.map(i => (typeof i === 'string' ? i : (i.label || i.value || String(i))))
    }
  } catch (e) {}
  return String(json).split(/[,，]/).map(s => s.trim()).filter(Boolean)
}

function onSelect(key, value) {
  formData[key] = value
}

async function ensurePhone() {
  if (me.value.phoneBound || me.value.phone) return true
  showPhone.value = true
  return false
}

async function doBindPhone() {
  if (!/^1\d{10}$/.test(phone.value)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  await bindWxPhone(phone.value)
  showPhone.value = false
  await loadMe()
  uni.showToast({ title: '绑定成功', icon: 'success' })

  setTimeout(() => { submit() }, 300)
}

async function submit() {
  if (!(await ensurePhone())) return
  for (const field of fields.value) {
    if (field.requiredFlag === '1' && !formData[field.fieldKey]) {
      uni.showToast({ title: '请填写' + field.fieldName, icon: 'none' })
      return
    }
  }
  if (!fields.value.length && !formData.contactName) {
    uni.showToast({ title: '请填写姓名', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await submitPortalApply({
      activityId: Number(activityId.value),
      channelId: Number(channelId.value),
      contactName: formData.contactName || formData.name || '',
      company: formData.company || '',
      gender: formData.gender || '',
      formData: { ...formData }
    })
    uni.showToast({ title: '报名成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/meeting/apply/mine?activityId=${activityId.value}` })
    }, 500)
  } catch (e) {
    // toast already shown
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.form-page { min-height: 100vh; padding: 24rpx; background: #f5f7fa; }
.channel { margin-bottom: 20rpx; color: #606266; font-size: 26rpx; }
.field { margin-bottom: 20rpx; padding: 24rpx; border-radius: 12rpx; background: #fff; }
.label { margin-bottom: 12rpx; color: #303133; font-size: 28rpx; }
.req { color: #f56c6c; margin-right: 6rpx; }
.input, .textarea {
  width: 100%; min-height: 72rpx; padding: 16rpx; box-sizing: border-box;
  background: #f5f7fa; border-radius: 8rpx; font-size: 26rpx;
}
.textarea { min-height: 160rpx; }
.submit { margin-top: 40rpx; }
.phone-mask {
  position: fixed; inset: 0; background: rgba(0,0,0,.45);
  display: flex; align-items: center; justify-content: center;
}
.phone-box {
  width: 80%; background: #fff; border-radius: 16rpx; padding: 32rpx;
}
.phone-title { font-size: 30rpx; font-weight: 600; margin-bottom: 20rpx; }
.phone-box button { margin-top: 16rpx; }
</style>