import { createRouter, createWebHistory } from "vue-router";
import axios from 'axios'


import home from '../views/home.vue';
import login from '../views/login.vue';
import admin from '../views/admin.vue';
import merchant from '../views/merchant.vue';
import userat from '../views/user.vue';
import adminuserlist from '../components/adminuserlist.vue';
import ye404 from '../views/404.vue';
import ye403 from '../views/403.vue';
import adminGoods from '../components/adminGoods.vue';
import adminorder from '../components/adminorder.vue';
import adminhome from "../components/adminhome.vue";
import merchantorder from "../components/merchantorder.vue";
import merchantgoods from "../components/merchantgoodsList.vue";
import goodslist from "../components/goodslist.vue";
import goodsdetails from "../components/goodsdetails.vue";
import userorder from "../components/userorder.vue";
import ChatPage from "../views/ChatPage.vue";


import index from "../views/index.vue";


const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/home' },
    { path: '/home', component: home },
    { path: '/index', component: index },
    {
      path: '/admin',
      component: admin,
      children: [
        { path: 'adminuserlist', component: adminuserlist },
        { path: 'admingoods', component: adminGoods },
        { path: 'adminorder', component: adminorder },
        { path: 'adminhome', component: adminhome },
        { path: 'chat', component: ChatPage },
      ]
    },
    {
      path: '/merchant',
      component: merchant,
      children: [
        { path: 'merchantorder', component: merchantorder },
        { path: 'merchantgoods', component: merchantgoods },
        { path: 'merchanthome', component: ()=>import("../components/merchanthome.vue") },
        { path: 'chat', component: ChatPage },
      ]
    },
    { path: '/goodsdetails/:id', component: goodsdetails },
    {
      path: '/user',
      component: userat,
      children: [
        { path: 'goodslist', component: goodslist },
        { path: 'userorder', component: userorder },
        {path :'userhome', component:()=>import("../components/userhome.vue")},
        { path: 'chat', component: ChatPage }
      ]
    },
    { path: '/login', component: login, name: 'login' },
    { path: '/404', component: ye404, name: 'ye404' },
    { path: '/403', component: ye403, name: 'ye403' },
    {path:"/admindp",component:()=>import("../components/admindp.vue")},
    { path: '/:pathMatch(.*)*', redirect: '/404' }
  ]
});


router.beforeEach(async (to, from) => {
  const needAuth = to.path.startsWith('/admin') || 
                   to.path.startsWith('/merchant') || 
                   to.path.startsWith('/user');
  if (needAuth) {
    try {
      // 1. 等待后端校验登录状态
      const res = await axios.get("/api/user/isLogin");

      // 2. 未登录 → 清缓存 + 跳登录
      if (res.data !== true) {
        console.log("用户未登录");
        localStorage.removeItem('satoken');
        localStorage.removeItem('role');
        return { path: '/login' };
      }

      // 3. 已登录 → 获取角色
      const role = localStorage.getItem('role');
      if (role === 'admin') {
        return true; // 最高权限，直接放行！
      }
      // 4. 路由与角色匹配校验
      if (to.path.startsWith('/merchant') && role !== 'merchant') {
        return { path: '/403' }; 
      }
      if (to.path.startsWith('/user') && role !== 'user') {
        return { path: '/403' }; 
      }

      // 5. 全部校验通过 → 放行
      return true;
    } catch (err) {
      // 接口异常 → 跳登录（但首页等公开页面不受影响）
      console.log("接口异常，跳转到登录页");
      localStorage.removeItem('satoken');
      localStorage.removeItem('role');
      return { path: '/login' };
    }
  }
  // 不需要认证的页面（如首页）直接放行
  return true;
});

export default router