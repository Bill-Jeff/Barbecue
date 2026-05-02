<template>
  <div class="cart-page">
    <div class="page">
      <div class="cart-header">
        <button class="back-btn" @click="router.back()">←</button>
        <h2>购物车</h2>
        <span class="item-count">{{ cart.totalCount }} 件商品</span>
      </div>

      <div v-if="cart.items.length === 0" class="empty-cart">
        <div class="empty-icon">🛒</div>
        <p>购物车空空如也~</p>
        <button class="go-shop-btn" @click="router.replace('/')">去逛逛</button>
      </div>

      <div v-else>
        <div class="cart-list">
          <div class="cart-item" v-for="item in cart.items" :key="item.productId">
            <div class="cart-item__thumb">{{ getEmoji(item.name) }}</div>
            <div class="cart-item__info">
              <div class="cart-item__name">{{ item.name }}</div>
              <div class="cart-item__price">¥{{ item.price.toFixed(0) }}</div>
            </div>
            <div class="stepper">
              <button type="button" @click="cart.removeItem(item.productId)">-</button>
              <strong>{{ item.quantity }}</strong>
              <button type="button" class="plus" @click="cart.addItem({ id: item.productId, name: item.name, price: item.price })">+</button>
            </div>
            <button class="del-btn" @click="removeAll(item.productId)">✕</button>
          </div>
        </div>

        <div class="table-input">
          <div class="table-label">桌号</div>
          <input v-model="tableNo" type="number" placeholder="请输入桌号" class="table-field" />
        </div>

        <div class="order-summary">
          <div class="summary-row">
            <span>共 {{ cart.totalCount }} 件</span>
            <span class="summary-total">合计 <em>¥{{ cart.totalPrice.toFixed(0) }}</em></span>
          </div>
          <button class="submit-btn" :class="{ loading: submitting }" @click="submitOrder">
            {{ submitting ? '下单中...' : '确认下单' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { createOrder } from '../api'

const router = useRouter()
const cart = useCartStore()
const tableNo = ref('')
const submitting = ref(false)

const emojiMap = {
  '羊肉串': '🍢', '牛肉串': '🥩', '鸡翅': '🍗', '五花肉': '🥓',
  '烤腰子': '🫘', '烤生蚝': '🦪', '烤鱿鱼': '🦑', '烤大虾': '🦐',
  '烤馒头片': '🍞', '烤韭菜': '🥬', '烤茄子': '🍆',
  '啤酒': '🍺', '王老吉': '🧃', '酸梅汤': '🥤',
}

function getEmoji(name) { return emojiMap[name] || '🔥' }
function removeAll(productId) { cart.items = cart.items.filter(i => i.productId !== productId) }

async function submitOrder() {
  if (!tableNo.value) { alert('请输入桌号'); return }
  submitting.value = true
  try {
    await createOrder({
      tableNo: tableNo.value,
      items: cart.items.map(i => ({
        productId: i.productId, productName: i.name, price: i.price, quantity: i.quantity,
      })),
    })
    cart.clear()
    router.replace('/order-result')
  } catch {
    alert('下单失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
}

.page {
  position: relative;
  z-index: 1;
  width: min(100%, 430px);
  margin: 0 auto;
  padding: 0 16px 140px;
}

.cart-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 40px 0 20px;
}

.cart-header h2 {
  margin: 0;
  font-size: 20px;
  flex: 1;
  font-weight: 800;
}

.back-btn {
  background: var(--surface);
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  font-size: 18px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(94, 44, 20, 0.1);
  color: var(--text);
}

.item-count {
  font-size: 13px;
  color: var(--muted);
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
}

.empty-icon { font-size: 64px; margin-bottom: 16px; opacity: 0.4; }
.empty-cart p { color: var(--muted); font-size: 15px; }

.go-shop-btn {
  margin-top: 16px;
  background: linear-gradient(135deg, var(--accent), #de7743);
  color: #fff;
  border: none;
  padding: 12px 36px;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 18px rgba(201, 79, 45, 0.22);
}

.cart-list {
  background: var(--surface);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 12px 28px rgba(97, 51, 29, 0.08);
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 14px 14px;
  border-bottom: 1px solid var(--line);
  gap: 12px;
}

.cart-item:last-child { border-bottom: none; }

.cart-item__thumb {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-sm);
  background:
    radial-gradient(circle at 35% 30%, rgba(255, 219, 171, 0.95), transparent 40%),
    linear-gradient(180deg, #7b2d18, #492015);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  box-shadow: inset 0 -6px 14px rgba(0,0,0,0.12);
}

.cart-item__info { flex: 1; }

.cart-item__name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
}

.cart-item__price {
  font-size: 16px;
  color: var(--accent-deep);
  font-weight: 700;
  margin-top: 4px;
}

.stepper {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px;
  border-radius: 999px;
  background: #fff;
  box-shadow: inset 0 0 0 1px rgba(125, 73, 39, 0.08);
}

.stepper button {
  width: 28px;
  height: 28px;
  border: 0;
  border-radius: 50%;
  font-size: 16px;
  background: #f4e2d3;
  color: var(--accent-deep);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stepper button.plus {
  background: linear-gradient(135deg, var(--accent), #e97b46);
  color: #fff;
}

.stepper strong {
  width: 20px;
  text-align: center;
  font-size: 15px;
}

.del-btn {
  background: none;
  border: none;
  color: rgba(126, 95, 77, 0.3);
  font-size: 16px;
  cursor: pointer;
  padding: 4px;
}

.del-btn:hover { color: var(--accent); }

.table-input {
  background: var(--surface);
  border-radius: var(--radius-lg);
  margin-top: 14px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 12px 28px rgba(97, 51, 29, 0.08);
}

.table-label {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
  white-space: nowrap;
}

.table-field {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  text-align: right;
  background: transparent;
  color: var(--text);
}

.order-summary {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: min(100%, 430px);
  background: rgba(42, 20, 12, 0.94);
  backdrop-filter: blur(10px);
  padding: 14px 16px;
  padding-bottom: max(14px, env(safe-area-inset-bottom));
  border-radius: 24px 24px 0 0;
  color: #fff5ea;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
  color: rgba(255, 233, 214, 0.72);
}

.summary-total em {
  font-style: normal;
  font-size: 24px;
  font-weight: 800;
  color: #fff5ea;
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #f0b04f, #ff8848);
  color: #35180c;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 12px 20px rgba(255, 166, 84, 0.28);
  transition: transform 0.15s;
}

.submit-btn:active { transform: scale(0.97); }
.submit-btn.loading { opacity: 0.7; }
</style>
