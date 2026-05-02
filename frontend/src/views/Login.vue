<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">🔥</div>
        <h2>店铺管理登录</h2>
        <p>请输入管理员账号密码</p>
      </div>

      <div class="login-form">
        <div class="input-group">
          <label>账号</label>
          <input
            v-model="username"
            type="text"
            placeholder="请输入账号"
            @keyup.enter="handleLogin"
          />
        </div>
        <div class="input-group">
          <label>密码</label>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </div>

        <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>

        <button
          class="login-btn"
          :class="{ loading: logging }"
          :disabled="logging"
          @click="handleLogin"
        >
          {{ logging ? '登录中...' : '登 录' }}
        </button>
      </div>

      <a href="/" class="back-link">← 返回前台</a>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '../api'

const router = useRouter()
const username = ref('')
const password = ref('')
const errorMsg = ref('')
const logging = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入账号和密码'
    return
  }
  errorMsg.value = ''
  logging.value = true
  try {
    const res = await adminLogin(username.value, password.value)
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.token)
      const redirect = router.currentRoute.value.query.redirect || '/m-admin'
      router.replace(redirect)
    } else {
      errorMsg.value = res.data.message || '登录失败'
    }
  } catch {
    errorMsg.value = '网络错误，请重试'
  } finally {
    logging.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background:
    radial-gradient(circle at top right, rgba(255, 216, 168, 0.9), transparent 30%),
    radial-gradient(circle at left 20%, rgba(201, 79, 45, 0.16), transparent 24%),
    linear-gradient(180deg, #fff4e9 0%, #f6eadb 45%, #eed9c6 100%);
  background-attachment: fixed;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: rgba(255, 248, 239, 0.95);
  border-radius: 28px;
  padding: 36px 28px;
  box-shadow: 0 18px 40px rgba(94, 44, 20, 0.15);
  backdrop-filter: blur(8px);
  text-align: center;
}

.login-header {
  margin-bottom: 28px;
}

.login-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.login-header h2 {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 800;
  color: #2b180f;
}

.login-header p {
  margin: 0;
  font-size: 13px;
  color: #7e5f4d;
}

.login-form {
  text-align: left;
}

.input-group {
  margin-bottom: 18px;
}

.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #7e5f4d;
  margin-bottom: 6px;
}

.input-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(125, 73, 39, 0.12);
  border-radius: 14px;
  font-size: 15px;
  background: #fff;
  color: #2b180f;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.input-group input:focus {
  border-color: #c94f2d;
  box-shadow: 0 0 0 3px rgba(201, 79, 45, 0.1);
}

.input-group input::placeholder {
  color: #c4b5a5;
}

.error-msg {
  padding: 10px 14px;
  margin-bottom: 16px;
  background: #fff2f0;
  border: 1px solid #ffccc7;
  border-radius: 10px;
  color: #cf1322;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #c94f2d, #de7743);
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 18px rgba(201, 79, 45, 0.22);
  transition: transform 0.15s, opacity 0.15s;
}

.login-btn:active {
  transform: scale(0.97);
}

.login-btn.loading {
  opacity: 0.7;
}

.back-link {
  display: inline-block;
  margin-top: 20px;
  font-size: 13px;
  color: #7e5f4d;
  text-decoration: none;
}

.back-link:hover {
  color: #c94f2d;
}
</style>
