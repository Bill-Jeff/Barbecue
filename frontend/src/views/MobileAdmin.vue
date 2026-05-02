<template>
  <div class="m-admin">
    <van-nav-bar title="店铺管理" left-arrow @click-left="router.back()" placeholder fixed />

    <van-tabs v-model:active="tab" sticky offset-top="46" color="#c94f2d" title-active-color="#c94f2d">
      <!-- ===== 菜品管理 ===== -->
      <van-tab title="菜品管理">
        <div class="tab-body">
          <van-cell-group inset>
            <van-cell
              v-for="p in products"
              :key="p.id"
              class="product-cell"
              :clickable="false"
            >
              <template #title>
                <div class="prod-title">
                  <div class="prod-thumb">
                    <img v-if="p.image" :src="p.image" />
                    <span v-else>{{ getEmoji(p.name) }}</span>
                  </div>
                  <div class="prod-info">
                    <div class="prod-name">
                      {{ p.name }}
                      <van-tag v-if="p.status === 0" type="warning" size="small">已下架</van-tag>
                    </div>
                    <div class="prod-meta">{{ getCategoryName(p.categoryId) }}</div>
                    <div class="prod-price">¥{{ Number(p.price).toFixed(2) }}</div>
                  </div>
                </div>
              </template>
              <template #right-icon>
                <div class="prod-actions">
                  <van-icon name="edit" size="20" color="#c94f2d" @click="openProductEditor(p)" />
                  <van-icon
                    :name="p.status === 1 ? 'eye' : 'closed-eye'"
                    size="20"
                    :color="p.status === 1 ? '#07c160' : '#ee0a24'"
                    @click="toggleStatus(p)"
                  />
                  <van-icon name="delete-o" size="20" color="#ee0a24" @click="confirmDeleteProduct(p)" />
                </div>
              </template>
            </van-cell>
          </van-cell-group>

          <div v-if="products.length === 0" style="padding: 40px 0;">
            <van-empty description="暂无菜品" />
          </div>
        </div>
      </van-tab>

      <!-- ===== 分类管理 ===== -->
      <van-tab title="分类管理">
        <div class="tab-body">
          <van-cell-group inset>
            <van-swipe-cell v-for="c in categories" :key="c.id">
              <van-cell :title="c.name" :value="'排序 ' + c.sortOrder" @click="openCategoryEditor(c)">
                <template #right-icon>
                  <van-icon name="edit" size="18" color="#c94f2d" style="margin-left: 8px;" />
                </template>
              </van-cell>
              <template #right>
                <van-button square type="danger" text="删除" style="height: 100%;" @click="confirmDeleteCategory(c)" />
              </template>
            </van-swipe-cell>
          </van-cell-group>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 悬浮新增按钮 -->
    <button class="fab" @click="tab === 0 ? openProductEditor() : openCategoryEditor()">+</button>

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
                  checked-color="#c94f2d"
                >{{ c.name }}</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field v-model.number="productForm.price" label="单价" type="number" placeholder="0.00">
            <template #button><span style="color: #999;">元</span></template>
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
                <van-icon name="photograph" size="28" color="#ccc" />
                <span>上传图片</span>
              </div>
            </div>
          </van-uploader>
        </div>

        <div class="sheet-footer">
          <van-button block round type="primary" color="linear-gradient(135deg, #c94f2d, #de7743)" :loading="saving" @click="saveProduct">
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
          <van-button block round type="primary" color="linear-gradient(135deg, #c94f2d, #de7743)" @click="saveCategory">保存</van-button>
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
  adminUpload,
} from '../api'

const router = useRouter()
const tab = ref(0)
const products = ref([])
const categories = ref([])
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
  const [pRes, cRes] = await Promise.all([adminListProducts(), adminListCategories()])
  products.value = pRes.data
  categories.value = cRes.data
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
</script>

<style scoped>
.m-admin {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

.tab-body {
  padding: 12px;
}

/* 菜品卡片 */
.product-cell {
  margin-bottom: 8px;
  border-radius: 12px !important;
  overflow: hidden;
}

.product-cell :deep(.van-cell__title) {
  flex: 1;
}

.prod-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.prod-thumb {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: linear-gradient(135deg, #7b2d18, #492015);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  overflow: hidden;
  box-shadow: inset 0 -6px 14px rgba(0,0,0,0.12);
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
  font-size: 15px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}

.prod-meta {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.prod-price {
  font-size: 16px;
  font-weight: 700;
  color: #c94f2d;
  margin-top: 2px;
}

.prod-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-left: 12px;
}

/* 底部弹窗 */
.sheet-body {
  padding: 16px 12px;
  padding-bottom: max(24px, env(safe-area-inset-bottom));
}

.sheet-footer {
  padding: 20px 16px 0;
}

/* 上传 */
.upload-section {
  padding: 16px;
  margin: 8px 16px;
  background: #fff;
  border-radius: 8px;
}

.upload-label {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  margin-bottom: 12px;
}

.upload-trigger {
  width: 100px;
  height: 100px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #ccc;
  font-size: 12px;
}

/* 悬浮按钮 */
.fab {
  position: fixed;
  right: 20px;
  bottom: calc(24px + env(safe-area-inset-bottom));
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #c94f2d, #de7743);
  color: #fff;
  font-size: 28px;
  font-weight: 300;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(201, 79, 45, 0.35);
  z-index: 50;
  cursor: pointer;
  transition: transform 0.15s;
}

.fab:active {
  transform: scale(0.9);
}
</style>
