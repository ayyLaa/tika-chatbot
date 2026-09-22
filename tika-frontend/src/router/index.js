import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import ChatView from '../views/ChatView.vue'
import AdminView from '../views/AdminView.vue'

const routes = [
  { path: '/login', name: 'login', component: LoginView },
  { path: '/chat', name: 'chat', component: ChatView, meta: { requiresAuth: true } },
  { path: '/accept-invite', name: 'AcceptInvite', component: LoginView },
  { path: '/admin', name: 'admin', component: AdminView, meta: { requiresAuth: true, requiresAdmin: true } },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/ResetPasswordView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = (localStorage.getItem('role') || '').toLowerCase()

  if (to.meta.requiresAuth && !token) {
    localStorage.setItem('redirectUrl', to.fullPath)
    next('/login')
    return
  }

  if (to.meta.requiresAdmin && role !== 'admin') {
    next('/chat')
    return
  }

  next()
})

export default router