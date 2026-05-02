import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器：自动带 token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：401 跳登录
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// ========== 前台接口 ==========

export function getCategories() {
  return api.get('/categories')
}

export function getProducts(categoryId) {
  return api.get('/products', { params: { categoryId } })
}

export function createOrder(data) {
  return api.post('/orders', data)
}

// ========== 登录 ==========

export function adminLogin(username, password) {
  return api.post('/admin/login', { username, password })
}

export function adminCheck() {
  return api.get('/admin/check')
}

// ========== 后台管理接口 ==========

export function adminListCategories() {
  return api.get('/admin/categories')
}

export function adminSaveCategory(data) {
  return api.post('/admin/categories', data)
}

export function adminDeleteCategory(id) {
  return api.delete(`/admin/categories/${id}`)
}

export function adminListProducts() {
  return api.get('/admin/products')
}

export function adminSaveProduct(data) {
  return api.post('/admin/products', data)
}

export function adminDeleteProduct(id) {
  return api.delete(`/admin/products/${id}`)
}

export function adminUpload(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

// ========== 用户管理 ==========

export function adminListUsers() {
  return api.get('/admin/users')
}

export function adminSaveUser(data) {
  return api.post('/admin/users', data)
}

export function adminDeleteUser(id) {
  return api.delete(`/admin/users/${id}`)
}
