<template>
  <div class="page">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-bg"></div>
      <button class="admin-entry" @click="router.push('/m-admin')">⚙ 管理</button>
      <div class="eyebrow">炭火现烤 · 夜宵优选</div>
      <h1>今晚吃点<br>热辣烧烤</h1>
      <p>手机上就能轻松点单，肉串、海鲜、蔬菜和主食一页搞定，支持现点现烤与口味备注。</p>
      <div class="hero-meta">
        <div class="hero-stat">
          <strong>18 min</strong>
          <span>平均出餐</span>
        </div>
        <div class="hero-stat">
          <strong>4.9 分</strong>
          <span>人气好评</span>
        </div>
        <div class="hero-stat">
          <strong>¥ 20 起送</strong>
          <span>深夜可点</span>
        </div>
      </div>
    </section>

    <!-- 分类 -->
    <div class="section-title">
      <h2>招牌分类</h2>
      <span>左右滑动切换</span>
    </div>
    <div class="chips">
      <button
        v-for="cat in categories"
        :key="cat.id"
        class="chip"
        :class="{ active: activeCategory === cat.id }"
        @click="activeCategory = cat.id"
      >{{ cat.name }}</button>
    </div>

    <!-- 菜品列表 -->
    <div class="section-title">
      <h2>{{ currentCategoryName }}</h2>
      <span>新鲜现串，辣度可调</span>
    </div>
    <section class="menu-list">
      <article
        v-for="(item, index) in filteredProducts"
        :key="item.id"
        class="card"
        :style="{ animationDelay: index * 60 + 'ms' }"
      >
        <div class="thumb">{{ item.emoji }}</div>
        <div>
          <h3>{{ item.name }}</h3>
          <p class="desc">{{ item.description }}</p>
          <div class="tags">
            <span class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</span>
          </div>
          <div class="card-bottom">
            <div class="price">
              <strong>¥{{ item.price.toFixed(0) }}</strong>
              <span>{{ item.unit }} / 份量足</span>
            </div>
            <div class="stepper">
              <button type="button" v-if="getQty(item.id) > 0" @click="cart.removeItem(item.id)">-</button>
              <strong v-if="getQty(item.id) > 0">{{ getQty(item.id) }}</strong>
              <button type="button" class="plus" @click="cart.addItem(item)">+</button>
            </div>
          </div>
        </div>
      </article>
    </section>

    <section class="notice">
      温馨提示：默认微辣微孜然，如需"免辣 / 加辣 / 不要香菜 / 单独打包"，下单时可备注。
    </section>

    <!-- 购物车悬浮栏 -->
    <aside class="cart" v-if="cart.totalCount > 0">
      <div class="cart-top">
        <div>
          <strong>已选烧烤</strong><br>
          <small>{{ cartSummary }}</small>
        </div>
        <div class="cart-count">{{ cart.totalCount }} 串</div>
      </div>
      <div class="cart-bottom">
        <div class="total">
          <strong>¥{{ cart.totalPrice.toFixed(0) }}</strong>
          <span>配送费另计，支持到店自取</span>
        </div>
        <button class="checkout" @click="goCart">提交订单</button>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getProducts } from '../api'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cart = useCartStore()

const activeCategory = ref(null)
const categories = ref([])
const products = ref([])

const emojiMap = {
  '羊肉串': '🍢', '牛肉串': '🥩', '鸡翅': '🍗', '五花肉': '🥓',
  '烤腰子': '🫘', '烤生蚝': '🦪', '烤鱿鱼': '🦑', '烤大虾': '🦐',
  '烤馒头片': '🍞', '烤韭菜': '🥬', '烤茄子': '🍆',
  '啤酒': '🍺', '王老吉': '🧃', '酸梅汤': '🥤',
}

const tagsMap = {
  '羊肉串': ['销量王', '推荐加辣'], '牛肉串': ['低脂感', '鲜嫩'],
  '鸡翅': ['招牌酱料', '现烤'], '五花肉': ['肥瘦相间', '经典'],
  '烤腰子': ['滋补', '限量'], '烤生蚝': ['蒜蓉控', '海味'],
  '烤鱿鱼': ['弹嫩', '甜辣口'], '烤大虾': ['鲜甜', '必点'],
  '烤馒头片': ['碳水快乐', '解馋'], '烤韭菜': ['解腻', '清香'],
  '烤茄子': ['蒜香爆棚', '经典'], '啤酒': ['冰镇', '必搭'],
  '王老吉': ['降火', '冰爽'], '酸梅汤': ['自制', '解渴'],
}

const unitMap = {
  '啤酒': '瓶', '王老吉': '罐', '酸梅汤': '杯',
}

const enrichedProducts = computed(() =>
  products.value.map(p => ({
    ...p,
    emoji: emojiMap[p.name] || '🔥',
    tags: tagsMap[p.name] || ['推荐'],
    unit: unitMap[p.name] || '串',
  }))
)

const filteredProducts = computed(() =>
  enrichedProducts.value.filter(p => p.categoryId === activeCategory.value)
)

const currentCategoryName = computed(() => {
  const cat = categories.value.find(c => c.id === activeCategory.value)
  return cat ? cat.name : '推荐菜品'
})

const cartSummary = computed(() => {
  if (cart.items.length === 0) return '先挑几串喜欢的吧'
  const names = cart.items.slice(0, 3).map(i => `${i.name} x${i.quantity}`).join('、')
  return cart.items.length > 3 ? `${names} 等 ${cart.items.length} 款` : names
})

function getQty(productId) {
  const item = cart.items.find(i => i.productId === productId)
  return item ? item.quantity : 0
}

function goCart() {
  router.push('/cart')
}

onMounted(async () => {
  const [catRes, prodRes] = await Promise.all([getCategories(), getProducts()])
  categories.value = catRes.data
  products.value = prodRes.data.list
  if (categories.value.length > 0) {
    activeCategory.value = categories.value[0].id
  }
})
</script>

<style scoped>
.page {
  position: relative;
  z-index: 1;
  width: min(100%, 430px);
  margin: 0 auto;
  padding: 18px 16px calc(120px + env(safe-area-inset-bottom));
}

/* Hero */
.hero {
  position: relative;
  overflow: hidden;
  padding: 22px 20px 20px;
  border-radius: var(--radius-xl);
  color: #fff5ea;
  background:
    linear-gradient(135deg, rgba(47, 20, 8, 0.94), rgba(119, 40, 16, 0.92)),
    linear-gradient(180deg, rgba(255,255,255,0.06), rgba(255,255,255,0));
  box-shadow: var(--shadow);
  animation: rise-in 0.8s ease-out both;
}

.hero::after {
  content: "";
  position: absolute;
  right: -18px;
  top: -28px;
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, rgba(255, 179, 90, 0.55) 0%, rgba(255, 179, 90, 0) 68%);
  transform: rotate(18deg);
}

.admin-entry {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 2;
  padding: 6px 14px;
  border-radius: 999px;
  border: 1px solid rgba(255, 236, 212, 0.2);
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 239, 224, 0.75);
  font-size: 12px;
  cursor: pointer;
  backdrop-filter: blur(4px);
  transition: all 0.2s;
}

.admin-entry:active {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(0.95);
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  border: 1px solid rgba(255, 236, 212, 0.16);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero h1 {
  margin: 14px 0 10px;
  font-family: var(--font-display);
  font-size: 34px;
  line-height: 1.08;
  font-weight: 700;
}

.hero p {
  margin: 0;
  max-width: 260px;
  color: rgba(255, 239, 224, 0.82);
  font-size: 14px;
  line-height: 1.6;
}

.hero-meta {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 18px;
}

.hero-stat {
  padding: 12px 10px;
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(4px);
}

.hero-stat strong {
  display: block;
  margin-bottom: 4px;
  font-size: 16px;
}

.hero-stat span {
  font-size: 11px;
  color: rgba(255, 240, 225, 0.78);
}

/* Section title */
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: end;
  margin: 22px 4px 12px;
}

.section-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
}

.section-title span {
  color: var(--muted);
  font-size: 12px;
}

/* Chips */
.chips {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 4px 2px;
  scrollbar-width: none;
}

.chip {
  flex: 0 0 auto;
  border: 0;
  padding: 12px 16px;
  border-radius: 999px;
  background: rgba(255, 248, 239, 0.72);
  color: var(--muted);
  box-shadow: inset 0 0 0 1px rgba(105, 54, 28, 0.08);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s ease, background 0.2s ease, color 0.2s ease;
}

.chip.active {
  background: linear-gradient(135deg, var(--accent), #de7743);
  color: #fffaf3;
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(201, 79, 45, 0.22);
}

/* Menu */
.menu-list {
  display: grid;
  gap: 14px;
  margin-top: 14px;
}

.card {
  display: grid;
  grid-template-columns: 88px 1fr;
  gap: 14px;
  padding: 14px;
  border-radius: var(--radius-lg);
  background: var(--surface);
  box-shadow: 0 12px 28px rgba(97, 51, 29, 0.08);
  backdrop-filter: blur(8px);
  animation: rise-in 0.5s ease both;
}

.thumb {
  display: grid;
  place-items: center;
  border-radius: var(--radius-md);
  background:
    radial-gradient(circle at 35% 30%, rgba(255, 219, 171, 0.95), transparent 40%),
    linear-gradient(180deg, #7b2d18, #492015);
  font-size: 40px;
  box-shadow: inset 0 -10px 24px rgba(0, 0, 0, 0.16);
}

.card h3 {
  margin: 2px 0 6px;
  font-size: 17px;
}

.desc {
  margin: 0;
  color: var(--muted);
  font-size: 12px;
  line-height: 1.6;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.tag {
  padding: 5px 8px;
  border-radius: 999px;
  background: rgba(240, 176, 79, 0.15);
  color: var(--accent-deep);
  font-size: 11px;
  font-weight: 700;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: end;
  margin-top: 14px;
  gap: 12px;
}

.price {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price strong {
  font-size: 22px;
  line-height: 1;
  color: var(--accent-deep);
}

.price span {
  font-size: 11px;
  color: var(--muted);
}

.stepper {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 6px;
  border-radius: 999px;
  background: #fff;
  box-shadow: inset 0 0 0 1px rgba(125, 73, 39, 0.08);
}

.stepper button {
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 50%;
  font-size: 18px;
  line-height: 1;
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
  box-shadow: 0 6px 14px rgba(201, 79, 45, 0.25);
}

.stepper button:active {
  transform: scale(0.92);
}

.stepper strong {
  width: 22px;
  text-align: center;
  font-size: 16px;
}

/* Cart */
.cart {
  position: fixed;
  left: 50%;
  bottom: 12px;
  transform: translateX(-50%);
  width: min(calc(100% - 20px), 410px);
  padding: 14px;
  border-radius: 24px;
  background: rgba(42, 20, 12, 0.94);
  color: #fff5ea;
  box-shadow: 0 24px 48px rgba(40, 20, 12, 0.28);
  backdrop-filter: blur(10px);
  z-index: 5;
  animation: float-up 0.9s ease-out both;
}

.cart-top,
.cart-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.cart-top small {
  color: rgba(255, 233, 214, 0.72);
  font-size: 12px;
}

.cart-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.12);
  font-weight: 700;
}

.cart-bottom {
  margin-top: 12px;
}

.total strong {
  display: block;
  font-size: 24px;
  letter-spacing: 0.01em;
}

.total span {
  color: rgba(255, 233, 214, 0.72);
  font-size: 12px;
}

.checkout {
  border: 0;
  padding: 14px 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f0b04f, #ff8848);
  color: #35180c;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 12px 20px rgba(255, 166, 84, 0.28);
  transition: transform 0.15s;
}

.checkout:active {
  transform: scale(0.95);
}

/* Notice */
.notice {
  margin: 18px 2px 0;
  padding: 16px 18px;
  border-radius: var(--radius-lg);
  background: rgba(255, 246, 236, 0.78);
  color: var(--muted);
  font-size: 13px;
  line-height: 1.7;
  box-shadow: inset 0 0 0 1px rgba(122, 73, 38, 0.08);
}

@media (max-width: 380px) {
  .hero h1 { font-size: 30px; }
  .hero-meta { grid-template-columns: 1fr; }
  .card { grid-template-columns: 1fr; }
  .thumb { height: 86px; }
  .cart-bottom { align-items: stretch; flex-direction: column; }
  .checkout { width: 100%; }
}
</style>
