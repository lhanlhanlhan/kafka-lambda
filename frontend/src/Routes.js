import Vue from 'vue';
import Router from 'vue-router';

// 主页
import HomePage from '@/pages/HomePage/HomePage';

import Layout from '@/components/Layout/Layout';
import ErrorPage from '@/pages/Error/Error';


Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/404',
      name: 'Error',
      component: ErrorPage,
    },
    // 程序主要路径
    {
      path: '/app',
      name: 'Layout',  // 父页面
      component: Layout,
      children: [
        {  // /app/index 主页
          path: 'index',
          name: '主页',
          component: HomePage,
        },
      ],
    },
    // 泛解析
    {
      path: '/',
      name: 'Index',
      component: HomePage,
    },
    // 404
    {
      path: "*", // 此处需特别注意置于最底部
      redirect: "/404"
    }
  ],
});
