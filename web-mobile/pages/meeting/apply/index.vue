<template>
  <view class="apply-page">
    <MeetingContentHeader title="注册报名" :activity-id="activityId" />
    <view class="apply-body">
    <view v-if="loading" class="state">{{ loadingText }}</view>
    <view v-else-if="authError" class="state">
      <view>{{ authError }}</view>
      <button class="retry-btn" @click="retryAuth">重新获取微信授权</button>
    </view>
    <view v-else-if="!authReady" class="state">正在获取微信登录信息...</view>
    <view v-else-if="!channels.length" class="state">暂无报名通道</view>
    <template v-else>
      <view v-if="me.nickname || me.avatar" class="wx-user-card">
        <image v-if="me.avatar" class="wx-avatar" :src="me.avatar" mode="aspectFill" />
        <view class="wx-user-meta">
          <text class="wx-user-title">已获取微信资料</text>
          <text class="wx-user-name">{{ me.nickname || '微信用户' }}</text>
        </view>
        <button class="wx-reauth-btn" @click="retryAuth">重新授权</button>
      </view>
      <view class="section">
        <view class="section-title">
          <text class="section-bar"></text>
          <text>报名通道选择</text>
        </view>
        <view class="card channel-card">
          <view
            v-for="item in channels"
            :key="item.channelId"
            class="channel-row"
            :class="{ active: String(selectedChannelId) === String(item.channelId), closed: item.closed }"
            @click="selectChannel(item)"
          >
            <text class="channel-name">{{ channelDisplayName(item) }}</text>
            <view class="channel-check" :class="{ on: String(selectedChannelId) === String(item.channelId) }">
              <text v-if="String(selectedChannelId) === String(item.channelId)">✓</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="phoneFields.length" class="section">
        <view class="section-title">
          <text class="section-bar"></text>
          <text>手机验证</text>
        </view>
        <view class="card field-card">
          <view
            v-for="(field, index) in phoneFields"
            :key="field.fieldId || field.fieldKey"
            class="field-row"
            :class="{ border: index < phoneFields.length - 1 }"
          >
            <view class="field-label">
              <text v-if="field.requiredFlag === '1'" class="req">*</text>
              <text>{{ field.fieldName }}</text>
            </view>
            <input
              class="field-input"
              v-model="formData[field.fieldKey]"
              :type="isMobileKey(field) ? 'number' : 'text'"
              :maxlength="isMobileKey(field) ? 11 : 140"
              :placeholder="field.placeholder || field.fieldName"
            />
          </view>
        </view>
      </view>

      <view v-if="infoFields.length" class="section">
        <view class="section-title">
          <text class="section-bar"></text>
          <text>报名信息填写</text>
        </view>
        <view class="card field-card">
          <view
            v-for="(field, index) in infoFields"
            :key="field.fieldId || field.fieldKey"
            class="field-row"
            :class="{ border: index < infoFields.length - 1, 'field-row--top': field.fieldType === 'textarea' || field.fieldType === 'checkbox' }"
          >
            <view class="field-label">
              <text v-if="field.requiredFlag === '1'" class="req">*</text>
              <text>{{ field.fieldName }}</text>
            </view>

            <picker
              v-if="isOptionPicker(field)"
              class="field-picker"
              :range="parseOptions(field.optionsJson)"
              @change="e => onSelect(field.fieldKey, parseOptions(field.optionsJson)[e.detail.value])"
            >
              <view class="field-value" :class="{ placeholder: !formData[field.fieldKey] }">
                <text>{{ displayValue(field) }}</text>
                <text class="arrow">›</text>
              </view>
            </picker>

            <picker
              v-else-if="isRegionField(field)"
              class="field-picker"
              mode="multiSelector"
              :range="regionRange"
              :value="regionIndex"
              @columnchange="onRegionColumnChange"
              @change="e => onRegionPicked(field.fieldKey, e.detail.value)"
            >
              <view class="field-value" :class="{ placeholder: !formData[field.fieldKey] }">
                <text>{{ formData[field.fieldKey] || field.placeholder || field.fieldName }}</text>
                <text class="arrow">›</text>
              </view>
            </picker>

            <picker
              v-else-if="field.fieldType === 'date'"
              class="field-picker"
              mode="date"
              @change="e => onSelect(field.fieldKey, e.detail.value)"
            >
              <view class="field-value" :class="{ placeholder: !formData[field.fieldKey] }">
                <text>{{ formData[field.fieldKey] || field.placeholder || field.fieldName }}</text>
                <text class="arrow">›</text>
              </view>
            </picker>

            <view v-else-if="field.fieldType === 'checkbox'" class="checkbox-wrap">
              <view
                v-for="opt in parseOptions(field.optionsJson)"
                :key="opt"
                class="checkbox-item"
                :class="{ on: isChecked(field.fieldKey, opt) }"
                @click="toggleCheckbox(field.fieldKey, opt)"
              >
                <text class="checkbox-box">{{ isChecked(field.fieldKey, opt) ? '✓' : '' }}</text>
                <text>{{ opt }}</text>
              </view>
            </view>

            <textarea
              v-else-if="field.fieldType === 'textarea'"
              class="field-textarea"
              v-model="formData[field.fieldKey]"
              :placeholder="field.placeholder || field.fieldName"
              :maxlength="500"
              auto-height
            />

            <view v-else-if="field.fieldType === 'upload'" class="upload-wrap" @click="pickUpload(field)">
              <image v-if="formData[field.fieldKey]" class="upload-preview" :src="formData[field.fieldKey]" mode="aspectFill" />
              <text v-else class="field-value placeholder">{{ field.placeholder || '点击上传' }}</text>
              <text class="arrow">›</text>
            </view>

            <input
              v-else
              class="field-input"
              v-model="formData[field.fieldKey]"
              :placeholder="field.placeholder || field.fieldName"
            />
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
      <view class="submit-wrap">
        <button class="submit-btn" :loading="submitting" :disabled="!!selectedChannel?.closed" @click="submit">提交</button>
      </view>
    </template>

    <view v-if="showPhone" class="phone-mask">
      <view class="phone-box">
        <view class="phone-title">绑定手机号</view>
        <input class="bind-input" v-model="phone" type="number" maxlength="11" placeholder="请输入手机号" />
        <button class="bind-btn" @click="doBindPhone">确认绑定</button>
        <button class="bind-cancel" @click="showPhone = false">取消</button>
      </view>
    </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MeetingContentHeader from '@/components/MeetingContentHeader.vue'
import { getPortalActivity, getPortalApplyChannels, submitPortalApply, getMyPortalApply } from '@/api/portal/meeting'
import { getWxMe, bindWxPhone, getOauthUrl, buildMockOauthJump } from '@/api/portal/wx'
import { captureMpTokenFromQuery, hasMpToken, removeMpToken } from '@/utils/mpAuth'
import { buildH5PageUrl } from '@/utils/h5Route'
import chinaArea from '@/utils/chinaArea.js'
import { setupMeetingShare } from '@/utils/wxShare'

const loading = ref(true)
const loadingText = ref('正在获取微信登录信息...')
const authReady = ref(false)
const authError = ref('')
const submitting = ref(false)
const showPhone = ref(false)
const phone = ref('')
const activityId = ref('')
const activityName = ref('')
const channels = ref([])
const selectedChannelId = ref('')
const fields = ref([])
const formData = reactive({})
const me = ref({})
const regionIndex = ref([0, 0, 0])
const regionRange = ref([[], [], []])

/** 后台配置中归入「手机验证」分区的字段 */
const PHONE_KEYS = ['name', 'contactName', 'mobile', 'phone']

initRegionColumns(0, 0)

const selectedChannel = computed(() =>
  channels.value.find((item) => String(item.channelId) === String(selectedChannelId.value))
)

const enabledFields = computed(() =>
  (fields.value || [])
    .filter((item) => item.enabledFlag !== '0')
    .slice()
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
)

const visibleFields = computed(() =>
  enabledFields.value.filter((item) => matchShowCondition(item))
)

const phoneFields = computed(() =>
  visibleFields.value.filter((item) => PHONE_KEYS.includes(item.fieldKey))
)

const infoFields = computed(() =>
  visibleFields.value.filter((item) => !PHONE_KEYS.includes(item.fieldKey))
)

function isWxUserReady(user) {
  return !!(user && (user.userId || user.openid))
}

function isWechatBrowser() {
  if (typeof navigator === 'undefined') return false
  return /MicroMessenger/i.test(navigator.userAgent || '')
}

function buildApplyRedirect() {
  if (typeof location === 'undefined') return ''
  return buildH5PageUrl('/pages/meeting/apply/index', { activityId: activityId.value })
}

onLoad(async (options) => {
  captureMpTokenFromQuery(options || {})
  activityId.value = options?.activityId || ''
  if (!activityId.value) {
    loading.value = false
    authError.value = '缺少会议ID'
    return
  }
  setupMeetingShare(activityId.value)
  await bootstrapPage()
})

async function bootstrapPage() {
  loading.value = true
  authReady.value = false
  authError.value = ''
  loadingText.value = '正在获取微信登录信息...'

  if (!isWechatBrowser()) {
    authError.value = '请在微信中打开后再进行报名授权'
    loading.value = false
    return
  }

  if (!hasMpToken()) {
    await ensureMpLogin()
    return
  }

  const ok = await loadMe()
  if (!ok) {
    removeMpToken()
    await ensureMpLogin()
    return
  }

  loadingText.value = '加载报名信息...'
  // 已报名则直接进入报名成功页，避免重复填写
  if (await redirectIfAlreadyApplied()) {
    return
  }
  await Promise.all([loadActivity(), loadChannels()])
  authReady.value = true
  loading.value = false
}

async function redirectIfAlreadyApplied() {
  try {
    const res = await getMyPortalApply(activityId.value)
    const list = res.data || []
    const active = list.find((item) => String(item.orderStatus) === '0')
    if (active) {
      uni.redirectTo({ url: `/pages/meeting/apply/mine?activityId=${activityId.value}` })
      return true
    }
  } catch (e) {
    // 查询失败不阻断报名页
  }
  return false
}

async function retryAuth() {
  removeMpToken()
  me.value = {}
  await bootstrapPage()
}

async function ensureMpLogin() {
  loading.value = true
  authReady.value = false
  loadingText.value = '正在跳转微信授权，请确认授权昵称和头像...'
  try {
    const redirect = buildApplyRedirect()
    const res = await getOauthUrl(activityId.value, redirect)
    const data = res.data || {}
    if (data.mode === 'mock' && typeof window !== 'undefined') {
      window.location.href = buildMockOauthJump(activityId.value, redirect)
      return
    }
    if (data.url && typeof window !== 'undefined') {
      window.location.replace(data.url)
      return
    }
    authError.value = '未能获取微信授权地址，请稍后重试'
    loading.value = false
  } catch (e) {
    authError.value = '获取微信授权失败，请稍后重试'
    loading.value = false
  }
}

async function loadActivity() {
  try {
    const res = await getPortalActivity(activityId.value)
    activityName.value = (res.data && res.data.activity && res.data.activity.activityName) || ''
    if (activityName.value) {
      setupMeetingShare(activityId.value, activityName.value)
    }
  } catch (e) {
    activityName.value = ''
  }
}

async function loadChannels() {
  const res = await getPortalApplyChannels(activityId.value)
  channels.value = (res.data && res.data.channels) || []
  const preferred = channels.value.find((item) => !item.closed) || channels.value[0]
  if (preferred) {
    applyChannel(preferred, { silent: true })
  }
}

async function loadMe() {
  if (!hasMpToken()) return false
  try {
    const res = await getWxMe()
    me.value = res.data || {}
    if (!isWxUserReady(me.value)) {
      me.value = {}
      return false
    }
    if (me.value.phone && !formData.mobile) {
      formData.mobile = me.value.phone
    }
    return true
  } catch (e) {
    me.value = {}
    return false
  }
}

function channelDisplayName(item) {
  return item.channelName || activityName.value || '报名通道'
}

function selectChannel(item) {
  applyChannel(item, { silent: false })
}

function applyChannel(item, options = {}) {
  if (!item) return
  if (item.closed && !options.silent) {
    uni.showToast({ title: item.closedMessage || '本次报名已截止', icon: 'none' })
  }
  selectedChannelId.value = item.channelId
  fields.value = item.fields || []
  resetFormForFields(fields.value)
}

function resetFormForFields(list) {
  Object.keys(formData).forEach((key) => {
    delete formData[key]
  })
  ;(list || []).forEach((field) => {
    if (!field || !field.fieldKey) return
    formData[field.fieldKey] = field.fieldType === 'checkbox' ? [] : ''
  })
  if (me.value.phone) {
    formData.mobile = me.value.phone
  }
}

function isMobileKey(field) {
  return field.fieldKey === 'mobile' || field.fieldKey === 'phone'
}

function isOptionPicker(field) {
  if (['select', 'radio'].includes(field.fieldType)) return true
  // system 组件：非省市区时按选项选择（如酒店）
  if (field.fieldType === 'system' && field.fieldKey !== 'region') {
    return parseOptions(field.optionsJson).length > 0
  }
  return false
}

function isRegionField(field) {
  return field.fieldType === 'system' && field.fieldKey === 'region'
}

function initRegionColumns(provinceIdx = 0, cityIdx = 0) {
  const provinces = chinaArea || []
  const p = provinces[provinceIdx] || provinces[0]
  const cities = (p && p.children) || []
  const c = cities[cityIdx] || cities[0]
  const districts = (c && c.children) || []
  regionRange.value = [
    provinces.map((item) => item.name),
    cities.map((item) => item.name),
    districts.map((item) => item.name)
  ]
  regionIndex.value = [
    Math.min(provinceIdx, Math.max(provinces.length - 1, 0)),
    Math.min(cityIdx, Math.max(cities.length - 1, 0)),
    0
  ]
}

function onRegionColumnChange(e) {
  const column = Number(e.detail.column)
  const value = Number(e.detail.value)
  const next = [...regionIndex.value]
  next[column] = value
  if (column === 0) {
    initRegionColumns(value, 0)
    return
  }
  if (column === 1) {
    initRegionColumns(next[0], value)
    return
  }
  regionIndex.value = next
}

function onRegionPicked(key, values) {
  const idxs = Array.isArray(values) ? values.map(Number) : regionIndex.value
  const province = chinaArea[idxs[0]]
  const city = province && province.children ? province.children[idxs[1]] : null
  const district = city && city.children ? city.children[idxs[2]] : null
  const labels = [province && province.name, city && city.name, district && district.name].filter(Boolean)
  formData[key] = labels.join(' ')
  regionIndex.value = idxs
}

function parseOptions(json) {
  if (!json) return []
  try {
    const arr = typeof json === 'string' ? JSON.parse(json) : json
    if (Array.isArray(arr)) {
      return arr.map((i) => (typeof i === 'string' ? i : (i.label || i.value || String(i)))).filter(Boolean)
    }
  } catch (e) {}
  return String(json).split(/[\n,，/／|]/).map((s) => s.trim()).filter(Boolean)
}

function parseCondition(raw) {
  if (!raw) return null
  try {
    const obj = typeof raw === 'string' ? JSON.parse(raw) : raw
    if (obj && obj.fieldKey && obj.value !== undefined && obj.value !== null && String(obj.value).trim() !== '') {
      return { fieldKey: String(obj.fieldKey), value: String(obj.value) }
    }
  } catch (e) {}
  return null
}

function matchShowCondition(field) {
  const cond = parseCondition(field.showCondition)
  if (!cond) return true
  const current = formData[cond.fieldKey]
  if (Array.isArray(current)) return current.map(String).includes(cond.value)
  return String(current ?? '') === cond.value
}

function displayValue(field) {
  const value = formData[field.fieldKey]
  if (Array.isArray(value)) return value.length ? value.join('、') : (field.placeholder || field.fieldName)
  return value || field.placeholder || field.fieldName
}

function onSelect(key, value) {
  formData[key] = value
}

function isChecked(key, opt) {
  const current = formData[key]
  return Array.isArray(current) && current.includes(opt)
}

function toggleCheckbox(key, opt) {
  const current = Array.isArray(formData[key]) ? [...formData[key]] : []
  const idx = current.indexOf(opt)
  if (idx >= 0) current.splice(idx, 1)
  else current.push(opt)
  formData[key] = current
}

function pickUpload(field) {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      const path = (res.tempFilePaths && res.tempFilePaths[0]) || ''
      if (path) formData[field.fieldKey] = path
    }
  })
}

function fieldFilled(field) {
  const value = formData[field.fieldKey]
  if (Array.isArray(value)) return value.length > 0
  return value !== undefined && value !== null && String(value).trim() !== ''
}

async function ensurePhone() {
  if (me.value.phoneBound || me.value.phone) return true
  if (/^1\d{10}$/.test(formData.mobile || '')) {
    try {
      await bindWxPhone(formData.mobile)
      await loadMe()
      return true
    } catch (e) {
      return false
    }
  }
  showPhone.value = true
  return false
}

async function doBindPhone() {
  if (!/^1\d{10}$/.test(phone.value)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  await bindWxPhone(phone.value)
  formData.mobile = phone.value
  showPhone.value = false
  await loadMe()
  uni.showToast({ title: '绑定成功', icon: 'success' })
  setTimeout(() => { submit() }, 300)
}

async function submit() {
  if (!selectedChannel.value) {
    uni.showToast({ title: '请选择报名通道', icon: 'none' })
    return
  }
  if (selectedChannel.value.closed) {
    uni.showToast({ title: selectedChannel.value.closedMessage || '本次报名已截止', icon: 'none' })
    return
  }
  for (const field of visibleFields.value) {
    if (field.requiredFlag === '1' && !fieldFilled(field)) {
      uni.showToast({ title: '请填写' + field.fieldName, icon: 'none' })
      return
    }
  }
  if (formData.mobile && !/^1\d{10}$/.test(formData.mobile)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  if (!(await ensurePhone())) return

  const payloadForm = { ...formData }
  Object.keys(payloadForm).forEach((key) => {
    if (Array.isArray(payloadForm[key])) {
      payloadForm[key] = payloadForm[key].join(',')
    }
  })

  submitting.value = true
  try {
    await submitPortalApply({
      activityId: Number(activityId.value),
      channelId: Number(selectedChannelId.value),
      contactName: formData.name || formData.contactName || '',
      company: formData.company || formData.hospital || '',
      gender: formData.gender || '',
      formData: payloadForm
    })
    uni.showToast({ title: '报名成功', icon: 'success' })
    setTimeout(() => {
      uni.redirectTo({ url: `/pages/meeting/apply/mine?activityId=${activityId.value}` })
    }, 500)
  } catch (e) {
    // 后端拦截重复报名时，直接进入成功页
    await redirectIfAlreadyApplied()
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.apply-page {
  min-height: 100vh;
  box-sizing: border-box;
  background: #f5f6f8;
}
.apply-body {
  box-sizing: border-box;
  min-height: 100vh;
  padding: calc(49px + 24rpx) 24rpx 180rpx;
}
.wx-user-card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 24rpx;
  padding: 22rpx 24rpx;
  border-radius: 16rpx;
  background: #fff;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}
.wx-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #f0f2f5;
}
.wx-user-meta {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}
.wx-user-title {
  color: #909399;
  font-size: 22rpx;
}
.wx-user-name {
  color: #303133;
  font-size: 28rpx;
  font-weight: 600;
}
.wx-reauth-btn {
  margin-left: auto;
  padding: 0 20rpx;
  height: 56rpx;
  line-height: 56rpx;
  border: 1px solid #dcdfe6;
  border-radius: 28rpx;
  background: #fff;
  color: #606266;
  font-size: 22rpx;
}
.section { margin-bottom: 28rpx; }
.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
  color: #1f2329;
  font-size: 30rpx;
  font-weight: 600;
}
.section-bar {
  width: 8rpx;
  height: 28rpx;
  margin-right: 12rpx;
  border-radius: 4rpx;
  background: #4c6ef5;
}
.card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}
.channel-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 28rpx 24rpx;
}
.channel-row.closed { opacity: 0.55; }
.channel-name {
  flex: 1;
  color: #303133;
  font-size: 28rpx;
  line-height: 1.4;
}
.channel-check {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  border: 2rpx solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 22rpx;
  box-sizing: border-box;
}
.channel-check.on {
  border-color: #7c4dff;
  background: #7c4dff;
}
.field-row {
  display: flex;
  align-items: center;
  min-height: 96rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
}
.field-row--top {
  align-items: flex-start;
  padding-top: 24rpx;
  padding-bottom: 24rpx;
}
.field-row.border {
  border-bottom: 1px solid #f0f2f5;
}
.field-label {
  flex: 0 0 180rpx;
  color: #303133;
  font-size: 28rpx;
}
.req {
  margin-right: 4rpx;
  color: #f56c6c;
}
.field-input,
.field-picker,
.field-textarea,
.upload-wrap,
.checkbox-wrap {
  flex: 1;
  min-width: 0;
}
.field-input {
  height: 96rpx;
  text-align: right;
  color: #303133;
  font-size: 28rpx;
}
.field-textarea {
  min-height: 120rpx;
  padding: 8rpx 0;
  text-align: right;
  color: #303133;
  font-size: 28rpx;
  line-height: 1.5;
}
.field-value {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8rpx;
  min-height: 96rpx;
  color: #303133;
  font-size: 28rpx;
}
.field-value.placeholder { color: #c0c4cc; }
.arrow {
  color: #c0c4cc;
  font-size: 34rpx;
  line-height: 1;
}
.checkbox-wrap {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 12rpx;
  padding: 8rpx 0;
}
.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  border: 1px solid #dcdfe6;
  color: #606266;
  font-size: 24rpx;
}
.checkbox-item.on {
  border-color: #7c4dff;
  color: #7c4dff;
  background: rgba(124, 77, 255, 0.06);
}
.checkbox-box {
  width: 24rpx;
  height: 24rpx;
  border-radius: 4rpx;
  border: 1px solid currentColor;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18rpx;
  line-height: 1;
}
.upload-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12rpx;
  min-height: 96rpx;
}
.upload-preview {
  width: 72rpx;
  height: 72rpx;
  border-radius: 8rpx;
  background: #f5f7fa;
}
.state {
  padding: 180rpx 24rpx;
  text-align: center;
  color: #909399;
  line-height: 1.6;
}
.retry-btn {
  margin-top: 32rpx;
  display: inline-block;
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 40rpx;
  border: none;
  border-radius: 999rpx;
  color: #fff;
  background: #7c4dff;
  font-size: 28rpx;
}
.retry-btn::after { border: none; }
.bottom-space { height: 20rpx; }
.submit-wrap {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  padding: 20rpx 40rpx calc(20rpx + env(safe-area-inset-bottom));
  background: linear-gradient(180deg, rgba(245, 246, 248, 0), #f5f6f8 28%);
}
.submit-btn {
  height: 88rpx;
  line-height: 88rpx;
  border: none;
  border-radius: 999rpx;
  color: #fff;
  background: #7c4dff;
  font-size: 32rpx;
  font-weight: 600;
}
.submit-btn[disabled] {
  opacity: 0.55;
}
.submit-btn::after { border: none; }
.phone-mask {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
}
.phone-box {
  width: 78%;
  padding: 36rpx;
  border-radius: 20rpx;
  background: #fff;
}
.phone-title {
  margin-bottom: 24rpx;
  color: #303133;
  font-size: 30rpx;
  font-weight: 600;
}
.bind-input {
  height: 80rpx;
  padding: 0 20rpx;
  border-radius: 12rpx;
  background: #f5f7fa;
  font-size: 28rpx;
}
.bind-btn,
.bind-cancel {
  margin-top: 20rpx;
  border-radius: 12rpx;
}
.bind-btn {
  color: #fff;
  background: #7c4dff;
}
</style>
