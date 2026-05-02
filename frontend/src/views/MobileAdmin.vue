<template>
  <div class="m-admin">
    <van-nav-bar title="店铺管理" left-arrow @click-left="router.back()" placeholder fixed />

    <van-tabs v-model:active="tab" sticky offset-top="46" color="#e8622c" title-active-color="#ff7a3d" :border="false">
      <!-- ===== 菜品管理 ===== -->
      <van-tab title="菜品管理">
        <div class="tab-body">
          <div class="product-list">
            <div
              v-for="p in products"
              :key="p.id"
              class="product-card"
            >
              <div class="prod-thumb">
                <img v-if="p.image" :src="p.image" />
                <span v-else>{{ getEmoji(p.name) }}</span>
              </div>
              <div class="prod-info">
                <div class="prod-name">
                  {{ p.name }}
                  <span v-if="p.status === 0" class="prod-status--off">已下架</span>
                </div>
                <div class="prod-meta">{{ getCategoryName(p.categoryId) }}</div>
                <div class="prod-price">¥{{ Number(p.price).toFixed(2) }}</div>
              </div>
              <div class="prod-actions">
                <button class="action-btn" @click="openProductEditor(p)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </button>
                <button class="action-btn" @click="toggleStatus(p)" :class="p.status === 1 ? 'action--on' : 'action--off'">
                  <svg v-if="p.status === 1" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                  <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                </button>
                <button class="action-btn action--danger" @click="confirmDeleteProduct(p)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                </button>
              </div>
            </div>
          </div>

          <div v-if="products.length === 0" class="empty-tab">
            <p>暂无菜品</p>
          </div>
        </div>
      </van-tab>

      <!-- ===== 分类管理 ===== -->
      <van-tab title="分类管理">
        <div class="tab-body">
          <div class="simple-list">
            <div class="simple-item" v-for="c in categories" :key="c.id">
              <div class="simple-item__main" @click="openCategoryEditor(c)">
                <span class="simple-item__name">{{ c.name }}</span>
                <span class="simple-item__value">排序 {{ c.sortOrder }}</span>
              </div>
              <button class="action-btn action--danger" @click="confirmDeleteCategory(c)">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </div>
          </div>
        </div>
      </van-tab>

      <!-- ===== 管理员 ===== -->
      <van-tab title="管理员">
        <div class="tab-body">
          <div class="simple-list">
            <div class="simple-item" v-for="u in users" :key="u.id">
              <div class="user-row" @click="openUserEditor(u)">
                <div class="user-avatar">{{ u.nickname?.charAt(0) || u.username?.charAt(0) }}</div>
                <div class="user-info">
                  <div class="user-name">
                    {{ u.nickname || u.username }}
                    <span v-if="u.role === 'admin'" class="role-badge role-badge--admin">管理</span>
                    <span v-else class="role-badge role-badge--user">用户</span>
                  </div>
                  <div class="user-meta">{{ u.username }}</div>
                </div>
              </div>
              <button class="action-btn action--danger" @click="confirmDeleteUser(u)">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </div>
          </div>

          <div v-if="users.length === 0" class="empty-tab">
            <p>暂无管理员</p>
          </div>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 悬浮新增按钮 -->
    <button class="fab" @click="fabClick">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
    </button>

    <!-- ===== 菜品编辑弹窗 ===== -->
    <van-action-sheet v-model:show="showProduct" title="编辑菜品" :close-on-click-overlay="false">
      <div class="sheet-body">
        <van-cell-group inset>
          <van-field v-model="productForm.name" label="名称" placeholder="如：招牌羊肉串" />
          <van-field label="分类">
            <template #input>
              <van-radio-group v-model="productForm.categoryId" direction="horizontal">
                <van-radio
                  v-for="c in categories"
                  :key="c.id"
                  :name="c.id"
                  icon-size="16"
                  checked-color="#e8622c"
                >{{ c.name }}</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field v-model.number="productForm.price" label="单价" type="number" placeholder="0.00">
            <template #button><span style="color: var(--muted-soft);">元</span></template>
          </van-field>
          <van-field v-model="productForm.description" label="描述" placeholder="简短描述" />
          <van-field v-model.number="productForm.sortOrder" label="排序" type="number" placeholder="0" />
        </van-cell-group>

        <div class="upload-section">
          <div class="upload-label">菜品图片</div>
          <van-uploader
            :after-read="handleUpload"
            :max-count="1"
            :deletable="true"
            @delete="productForm.image = ''"
          >
            <div class="upload-trigger">
              <img v-if="productForm.image" :src="productForm.image" class="upload-preview" />
              <div v-else class="upload-placeholder">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" opacity="0.4"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
                <span>上传图片</span>
              </div>
            </div>
          </van-uploader>
        </div>

        <div class="sheet-footer">
          <van-button block round type="primary" color="linear-gradient(135deg, #e8622c, #ff7a3d)" :loading="saving" @click="saveProduct">
            {{ saving ? '保存中...' : '保存' }}
          </van-button>
        </div>
      </div>
    </van-action-sheet>

    <!-- ===== 分类编辑弹窗 ===== -->
    <van-action-sheet v-model:show="showCategory" title="编辑分类" :close-on-click-overlay="false">
      <div class="sheet-body">
        <van-cell-group inset>
          <van-field v-model="categoryForm.name" label="名称" placeholder="如：烤串" />
          <van-field v-model.number="categoryForm.sortOrder" label="排序" type="number" placeholder="0" />
        </van-cell-group>
        <div class="sheet-footer">
          <van-button block round type="primary" color="linear-gradient(135deg, #e8622c, #ff7a3d)" @click="saveCategory">保存</van-button>
        </div>
      </div>
    </van-action-sheet>

    <!-- ===== 用户编辑弹窗 ===== -->
    <van-action-sheet v-model:show="showUser" title="编辑管理员" :close-on-click-overlay="false">
      <div class="sheet-body">
        <van-cell-group inset>
          <van-field v-model="userForm.username" label="账号" placeholder="登录账号" :disabled="!!userForm.id" />
          <van-field v-model="userForm.password" label="密码" type="password" :placeholder="userForm.id ? '不修改请留空' : '请输入密码'" />
          <van-field v-model="userForm.nickname" label="昵称" placeholder="显示名称" />
          <van-field label="角色">
            <template #input>
              <van-radio-group v-model="userForm.role" direction="horizontal">
                <van-radio name="admin" icon-size="16" checked-color="#e8622c">管理员</van-radio>
                <van-radio name="user" icon-size="16" checked-color="#e8622c">普通用户</van-radio>
              </van-radio-group>
            </template>
          </van-field>
        </van-cell-group>
        <div class="sheet-footer">
          <van-button block round type="primary" color="linear-gradient(135deg, #e8622c, #ff7a3d)" @click="saveUser">保存</van-button>
        </div>
      </div>
    </van-action-sheet>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import {
  adminListProducts, adminSaveProduct, adminDeleteProduct,
  adminListCategories, adminSaveCategory, adminDeleteCategory,
  adminListUsers, adminSaveUser, adminDeleteUser,
  adminUpload,
} from '../api'

const router = useRouter()
const tab = ref(0)
const products = ref([])
const categories = ref([])
const users = ref([])
const saving = ref(false)

const emojiMap = {
  '羊肉串': '🍢', '牛肉串': '🥩', '鸡翅': '🍗', '五花肉': '🥓',
  '烤腰子': '🫘', '烤生蚝': '🦪', '烤鱿鱼': '🦑', '烤大虾': '🦐',
  '烤馒头片': '🍞', '烤韭菜': '🥬', '烤茄子': '🍆',
  '啤酒': '🍺', '王老吉': '🧃', '酸梅汤': '🥤',
}
function getEmoji(name) { return emojiMap[name] || '🔥' }
function getCategoryName(id) {
  const c = categories.value.find(c => c.id === id)
  return c ? c.name : '-'
}

async function loadAll() {
  const [pRes, cRes, uRes] = await Promise.all([adminListProducts(), adminListCategories(), adminListUsers()])
  products.value = pRes.data
  categories.value = cRes.data
  users.value = uRes.data
}
onMounted(loadAll)

// ===== 菜品 CRUD =====
const showProduct = ref(false)
const productForm = reactive({
  id: null, name: '', categoryId: null, price: 0, image: '', description: '', sortOrder: 0, status: 1,
})

function openProductEditor(p) {
  if (p) {
    Object.assign(productForm, { ...p })
  } else {
    Object.assign(productForm, {
      id: null, name: '', categoryId: categories.value[0]?.id, price: 0,
      image: '', description: '', sortOrder: 0, status: 1,
    })
  }
  showProduct.value = true
}

async function saveProduct() {
  if (!productForm.name) { showToast('请输入菜品名称'); return }
  if (!productForm.price) { showToast('请输入价格'); return }
  saving.value = true
  try {
    await adminSaveProduct({ ...productForm })
    showProduct.value = false
    showToast('保存成功')
    await loadAll()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(p) {
  const newStatus = p.status === 1 ? 0 : 1
  await adminSaveProduct({ ...p, status: newStatus })
  showToast(newStatus === 1 ? '已上架' : '已下架')
  await loadAll()
}

function confirmDeleteProduct(p) {
  showConfirmDialog({ title: '确认删除', message: `确定删除「${p.name}」吗？` })
    .then(async () => {
      await adminDeleteProduct(p.id)
      showToast('已删除')
      await loadAll()
    })
    .catch(() => {})
}

async function handleUpload(file) {
  const res = await adminUpload(file.file)
  productForm.image = res.data.url
}

// ===== 分类 CRUD =====
const showCategory = ref(false)
const categoryForm = reactive({ id: null, name: '', sortOrder: 0 })

function openCategoryEditor(c) {
  if (c) {
    Object.assign(categoryForm, { ...c })
  } else {
    Object.assign(categoryForm, { id: null, name: '', sortOrder: 0 })
  }
  showCategory.value = true
}

async function saveCategory() {
  if (!categoryForm.name) { showToast('请输入分类名称'); return }
  await adminSaveCategory({ ...categoryForm })
  showCategory.value = false
  showToast('保存成功')
  await loadAll()
}

function confirmDeleteCategory(c) {
  showConfirmDialog({ title: '确认删除', message: `确定删除分类「${c.name}」吗？` })
    .then(async () => {
      await adminDeleteCategory(c.id)
      showToast('已删除')
      await loadAll()
    })
    .catch(() => {})
}

// ===== 用户 CRUD =====
const showUser = ref(false)
const userForm = reactive({ id: null, username: '', password: '', nickname: '', role: 'admin', status: 1 })

function openUserEditor(u) {
  if (u) {
    Object.assign(userForm, { ...u, password: '' })
  } else {
    Object.assign(userForm, { id: null, username: '', password: '', nickname: '', role: 'admin', status: 1 })
  }
  showUser.value = true
}

async function saveUser() {
  if (!userForm.username) { showToast('请输入账号'); return }
  if (!userForm.id && !userForm.password) { showToast('请输入密码'); return }
  await adminSaveUser({ ...userForm })
  showUser.value = false
  showToast('保存成功')
  await loadAll()
}

function confirmDeleteUser(u) {
  showConfirmDialog({ title: '确认删除', message: `确定删除管理员「${u.username}」吗？` })
    .then(async () => {
      await adminDeleteUser(u.id)
      showToast('已删除')
      await loadAll()
    })
    .catch(() => {})
}

// ===== FAB 按钮 =====
function fabClick() {
  if (tab.value === 0) openProductEditor()
  else if (tab.value === 1) openCategoryEditor()
  else openUserEditor()
}
</script>

<style scoped>
.m-admin {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 80px;
}

.tab-body {
  padding: 14px;
}

/* ===== Product cards ===== */
.product-list {
  display: grid;
  gap: 10px;
}
.product-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: var(--surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--line);
}
.prod-thumb {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-sm);
  background: linear-gradient(145deg, #f8f0e8, #f0e8de);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  flex-shrink: 0;
  overflow: hidden;
}
.prod-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.prod-info {
  flex: 1;
  min-width: 0;
}
.prod-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-bright);
  display: flex;
  align-items: center;
  gap: 6px;
}
.prod-status--off {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(255, 152, 0, 0.12);
  color: #ff9800;
  font-weight: 500;
}
.prod-meta {
  font-size: 11px;
  color: var(--muted-soft);
  margin-top: 3px;
}
.prod-price {
  font-size: 16px;
  font-weight: 800;
  color: var(--accent-bright);
  margin-top: 3px;
}
.prod-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex-shrink: 0;
}

/* Action buttons */
.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid var(--line-bright);
  background: var(--surface-glass);
  color: var(--muted);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s;
}
.action-btn:active { transform: scale(0.9); }
.action--on { color: #66bb6a; border-color: rgba(76, 175, 80, 0.2); }
.action--off { color: var(--muted-soft); }
.action--danger { color: var(--ember); border-color: rgba(255, 78, 32, 0.15); }

/* Simple list (categories & users) */
.simple-list {
  background: var(--surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--line);
  overflow: hidden;
}
.simple-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid var(--line);
  gap: 12px;
}
.simple-item:last-child { border-bottom: none; }
.simple-item__main {
  flex: 1;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.simple-item__name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-bright);
}
.simple-item__value {
  font-size: 12px;
  color: var(--muted-soft);
}

/* User row */
.user-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), var(--gold));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  flex-shrink: 0;
}
.user-info { flex: 1; }
.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-bright);
  display: flex;
  align-items: center;
  gap: 6px;
}
.user-meta {
  font-size: 11px;
  color: var(--muted-soft);
  margin-top: 2px;
}
.role-badge {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 500;
}
.role-badge--admin {
  background: rgba(255, 78, 32, 0.12);
  color: var(--ember);
}
.role-badge--user {
  background: rgba(168, 149, 133, 0.1);
  color: var(--muted);
}

/* Empty state */
.empty-tab {
  text-align: center;
  padding: 60px 20px;
}
.empty-tab p {
  color: var(--muted-soft);
  font-size: 14px;
}

/* Sheet */
.sheet-body {
  padding: 16px 12px;
  padding-bottom: max(24px, env(safe-area-inset-bottom));
}
.sheet-footer {
  padding: 20px 16px 0;
}

/* Upload */
.upload-section {
  padding: 16px;
  margin: 8px 16px;
  background: var(--surface-raised);
  border-radius: var(--radius-md);
  border: 1px solid var(--line);
}
.upload-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--muted);
  margin-bottom: 12px;
}
.upload-trigger {
  width: 100px;
  height: 100px;
  border: 2px dashed var(--line-bright);
  border-radius: var(--radius-sm);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.2s;
}
.upload-trigger:active { border-color: var(--accent); }
.upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  color: var(--muted-soft);
  font-size: 11px;
}

/* FAB */
.fab {
  position: fixed;
  right: 20px;
  bottom: calc(24px + env(safe-area-inset-bottom));
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, var(--accent), var(--accent-bright));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 28px rgba(232, 98, 44, 0.4);
  z-index: 50;
  cursor: pointer;
  transition: transform 0.15s;
}
.fab:active { transform: scale(0.88); }
</style>
