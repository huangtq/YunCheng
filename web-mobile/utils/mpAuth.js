const MP_TOKEN_KEY = 'Mp-Token'

export function getMpToken() {
  return uni.getStorageSync(MP_TOKEN_KEY)
}

export function setMpToken(token) {
  return uni.setStorageSync(MP_TOKEN_KEY, token)
}

export function removeMpToken() {
  return uni.removeStorageSync(MP_TOKEN_KEY)
}

export function hasMpToken() {
  return !!getMpToken()
}

export function captureMpTokenFromQuery(options = {}) {
  const token = options.mpToken || options.token
  if (token) {
    setMpToken(decodeURIComponent(token))
    return true
  }
  return false
}