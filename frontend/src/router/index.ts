import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'focus', component: () => import('@/views/FocusView.vue') },
  { path: '/settings', name: 'settings', component: () => import('@/views/SettingsView.vue') },
  { path: '/statistics', name: 'statistics', component: () => import('@/views/StatisticsView.vue') },
  { path: '/statistics/detail', name: 'focus-detail', component: () => import('@/views/FocusDetailView.vue') },
  { path: '/subjects', name: 'subjects', component: () => import('@/views/SubjectManageView.vue') },
  { path: '/interface', name: 'interface', component: () => import('@/views/InterfaceView.vue') }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})
