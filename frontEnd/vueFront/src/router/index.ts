import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/pages/HomeView.vue'),
    },
    {
      path: '/doctor/profile',
      name: 'doctorProfile',
      component: () => import('@/pages/doctor/profile/Index.vue'),
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
  ],
})

export default router
