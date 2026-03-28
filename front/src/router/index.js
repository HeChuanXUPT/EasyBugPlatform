import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Project from '../views/Project.vue'
import User from '../views/User.vue'
import IssueList from '../views/IssueList.vue'
import IssueAdd from '../views/IssueAdd.vue'
import IssueDetail from '../views/IssueDetail.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/project', component: Project },
  { path: '/user', component: User },
  { path: '/issue', component: IssueList },
  { path: '/issue/add', component: IssueAdd },
  { path: '/issue/detail/:id', component: IssueDetail }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录拦截（核心代码）
router.beforeEach((to, from, next) => {
  const user = sessionStorage.getItem('user')

  // 去登录页 → 直接放行
  if (to.path === '/login') {
    next()
  }
  // 未登录 → 强制跳登录
  else if (!user) {
    next('/login')
  }
  // 已登录 → 放行
  else {
    next()
  }
})

export default router