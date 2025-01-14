import { createRouter, createWebHashHistory } from 'vue-router'
import RegisterView from '@/views/login/register-view.vue'
import LoginView from '@/views/login/login-view.vue'
import ChatView from '@/views/chat/chat-view.vue'
const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: ChatView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/admin-view.vue'),
      children: [
        {
          path: 'factory',
          component: () => import('@/views/ai-factory/ai-factory-view.vue')
        },
        {
          path: 'model',
          component: () => import('@/views/ai-model/ai-model-view.vue')
        },
        {
          path: 'key',
          component: () => import('@/views/ai-key/ai-key-view.vue')
        },
        {
          path: 'user-details',
          component: () => import('@/views/user/user-details-view.vue')
        }
      ]
    }
  ]
})

export default router
