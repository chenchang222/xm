import{createRouter,createWebHistory} from "vue-router";
import Login from "@/views/Login.vue";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes:[
      {path:'/',redirect:'/login'},
      {path:'/manager',name:'manager',meta:{title:'主页'},component:()=>import('../views/Manager.vue'),children:[
        {path:'home',name:'home',meta:{title:'主页'},component:()=>import('../views/Home.vue')},
        {path:'text',name:'text',meta:{title:'测试页'},component:()=>import('../views/text.vue')},
        {path:'data',name:'data',meta:{title:'数据'},component:()=>import('../views/Data.vue')},
        {path:'employee',meta:{title:'管理员信息页面'},component:()=>import('../views/Employee.vue')},
        {path:'volunteer',meta:{title:'志愿者信息页面'},component:()=>import('../views/Volunteer.vue')},
        {path:'person',meta:{title:'个人信息'},component:()=>import('../views/Person.vue')},
        {path:'activity',meta:{title:'活动管理'},component:()=>import('../views/Activity.vue')},
        {path:'activitySignUp',meta:{title:'活动报名'},component:()=>import('../views/ActivitySignUp.vue')},
        {path:'activityFavorite',meta:{title:'我的收藏'},component:()=>import('../views/ActivityFavorite.vue')},
        {path:'activityReview',meta:{title:'我的评价'},component:()=>import('../views/ActivityReview.vue')},
        {path:'activityReviewManage',meta:{title:'活动评价管理'},component:()=>import('../views/ActivityReviewManage.vue')},
        {path:'activityApproval',meta:{title:'活动报名审核'},component:()=>import('../views/ActivityApproval.vue')},
        {path:'announcement',meta:{title:'系统公告管理'},component:()=>import('../views/Announcement.vue')},
        {path:'activityNotification',meta:{title:'活动提醒通知'},component:()=>import('../views/ActivityNotification.vue')},
        {path:'messageCenter',meta:{title:'消息中心'},component:()=>import('../views/MessageCenter.vue')},
        {path:'myPosts',meta:{title:'我的发帖'},component:()=>import('../views/MyPosts.vue')},
        {path:'mySignUps',meta:{title:'我的报名'},component:()=>import('../views/MySignUps.vue')},
        {path:'postManage',meta:{title:'论坛帖子管理'},component:()=>import('../views/PostManage.vue')},
        {
          path: 'postList',
          name: 'PostList',
          component: () => import('../views/PostList.vue')
        },
        {
          path: 'postDetail/:id',
          name: 'PostDetail',
          component: () => import('../views/PostDetail.vue')
        }
      ]},
      {path:'/login',name:'login',meta:{title:'登陆系统'},component:()=>import('../views/Login.vue')},
      {path:'/404',name:'404',meta:{title:'404notfound'},component:()=>import('../views/404.vue')},
      {path:'/:pathMatch(.*)',redirect:'/404'},
  ]
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title;
    next();
})

export default router;
