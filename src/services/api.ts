import axios from 'axios'

export const api = axios.create({
  baseURL: '',
  timeout: 10000,
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// API service functions
export const adminAPI = {
  // Categories
  getCategories: () => api.post('/api/v2/Category.php?call=getAllCategory'),
  createCategory: (data: FormData) => api.post('/api/v2/Category.php?call=create', data),
  updateCategory: (data: FormData) => api.post('/api/v2/Category.php?call=updateWithImage', data),
  deleteCategory: (data: FormData) => api.post('/api/v2/Category.php?call=delete', data),

  // Diseases
  getDiseases: () => api.post('/api/v2/Disease.php?call=getAllDiseases'),
  createDisease: (data: FormData) => api.post('/api/v2/Disease.php?call=create', data),
  updateDisease: (data: FormData) => api.post('/api/v2/Disease.php?call=update', data),
  deleteDisease: (data: FormData) => api.post('/api/v2/Disease.php?call=delete', data),

  // Users
  getUsers: () => api.post('/api/v2/User.php?call=getAllUsers'),
  updateUserStatus: (data: FormData) => api.post('/api/v2/User.php?call=updateAccountStatus', data),

  // Payments
  getPayments: () => api.get('/api/v2/Payment.php?get=allPayment'),

  // Feedback
  getFeedback: () => api.get('/api/v2/Application.php?get=feedback'),

  // App Info
  getAppInfo: () => api.get('/api/v2/Application.php?get=info'),
  updateAppInfo: (data: FormData) => api.post('/api/v2/Application.php?update=info', data),
}