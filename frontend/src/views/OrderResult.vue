<template>
  <div class="result-page">
    <div class="page">
      <div class="success-anim">
        <div class="success-circle">
          <span class="success-check">✓</span>
        </div>
      </div>
      <h2 class="result-title">下单成功!</h2>
      <p class="result-desc">您的订单已提交，后厨正在备餐中</p>
      <div class="result-emoji">🍖🔥😋</div>
      <button class="back-btn" @click="router.replace('/')">继续点单</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.result-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page {
  position: relative;
  z-index: 1;
  width: min(100%, 430px);
  margin: 0 auto;
  padding: 40px 24px;
  text-align: center;
}

.success-anim { margin-bottom: 24px; }

.success-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  animation: pop 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 12px 28px rgba(67,233,123,0.3);
}

.success-check {
  font-size: 40px;
  color: #fff;
  font-weight: 700;
}

.result-title {
  font-size: 26px;
  font-weight: 800;
  color: var(--text);
  margin: 0 0 10px;
}

.result-desc {
  font-size: 14px;
  color: var(--muted);
  margin: 0 0 18px;
  line-height: 1.6;
}

.result-emoji {
  font-size: 36px;
  margin-bottom: 40px;
  letter-spacing: 10px;
}

.back-btn {
  background: linear-gradient(135deg, var(--accent), #de7743);
  color: #fff;
  border: none;
  padding: 14px 48px;
  border-radius: 999px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 18px rgba(201, 79, 45, 0.22);
  transition: transform 0.15s;
}

.back-btn:active { transform: scale(0.95); }
</style>
