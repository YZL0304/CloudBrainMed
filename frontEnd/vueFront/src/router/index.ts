import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/pages/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/doctor/home',
      name: 'DoctorHome',
      component: () => import('@/pages/DoctorHome.vue'),
      meta: { requiresAuth: true, role: 2 }
    },
    {
      path: '/doctor/profile',
      name: 'doctorProfile',
      component: () => import('@/pages/doctor/profile/Index.vue'),
      meta: { requiresAuth: true, role: 2 }
    },
    {
      path: '/doctor/consult',
      name: 'doctorConsult',
      component: () => import('@/pages/doctor/consult/List.vue'),
    },
    {
      path: '/doctor/consult/:registerId',
      name: 'doctorConsultDetail',
      component: () => import('@/pages/doctor/consult/Detail.vue'),
    },
    {
      path: '/doctor/ai-medicine',
      name: 'aiMedicine',
      component: () => import('@/pages/doctor/ai-medicine/Index.vue'),
    },
    {
      path: '/admin/ml/dashboard',
      name: 'mlDashboard',
      component: () => import('@/pages/admin/ml/Dashboard.vue'),
    },
    {
      path: '/admin/ml/samples',
      name: 'mlSamples',
      component: () => import('@/pages/admin/ml/Samples.vue'),
    },
    {
      path: '/admin/ml/models',
      name: 'mlModels',
      component: () => import('@/pages/admin/ml/Models.vue'),
    },
    {
      path: '/admin/ml/ct-inference',
      name: 'ctInference',
      component: () => import('@/pages/admin/ml/CTInference.vue'),
      meta: { requiresAuth: true, role: 3 }
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')
  const roleType = sessionStorage.getItem('roleType')

  // 如果访问登录页面，直接放行
  if (to.path === '/login') {
    next()
    return
  }

  // 需要验证权限的路由
  if (to.meta.requiresAuth) {
    if (!token) {
      // 没有 token，跳转到登录页
      next('/login')
      return
    }

    // 检查角色权限
    if (to.meta.role) {
      const userRole = parseInt(roleType || '0')
      if (userRole !== to.meta.role) {
        next('/')
        return
      }
    }
  }

  next()
})

export default router