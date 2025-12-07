import { createRouter, createWebHistory } from "vue-router";

import Login from "@/components/Auth/Login.vue";
import Register from "@/components/Auth/Register.vue"; 

import AdminLayout from "@/components/admin/AdminLayout.vue";
import UserLayout from "@/components/user/UserLayout.vue";

import AdminDashboard from "@/pages/admin/AdminDashboard.vue";
import UsersList from "@/pages/admin/UsersList.vue";
import OrdersList from "@/pages/admin/OrdersList.vue";
import Categories from "@/pages/admin/Categories.vue";
import ProductsList from "@/pages/admin/ProductsList.vue";
import AddProduct from "@/pages/admin/AddProduct.vue";
import EditProduct from "@/pages/admin/EditProduct.vue"; 
import AdminProfile from "@/pages/admin/AdminProfile.vue";
import OrderDetail from "@/pages/admin/OrderDetails.vue";

import UserHome from "@/pages/user/UserHome.vue";
import UserOrders from "@/pages/user/UserOrders.vue";
import UserProfile from "@/pages/user/UserProfile.vue";
import OrderDetails from "@/pages/admin/OrderDetails.vue";
import ProductDetail from "@/pages/user/ProductDetail.vue";
import Cart from "@/pages/user/Cart.vue";
import Checkout from "@/pages/user/Checkout.vue";
import OrderSuccess from "@/pages/user/OrderSuccess.vue";
import UserWishlist from '@/pages/user/UserWishlist.vue';

const routes = [
  { path: "/login", name: "Login", component: Login },
  { path: "/register", name: "Register", component: Register },
  
  {
    path: "/admin",
    component: AdminLayout,
    children: [
      { path: "", name: "AdminDashboard", component: AdminDashboard },
      { path: "users", component: UsersList },
      { path: "orders", component: OrdersList },
      { path: "orders/:id", component: OrderDetails },
      { path: "categories", component: Categories },
      { path: "products", component: ProductsList },
      { path: "products/add", component: AddProduct },
      { path: "products/edit/:id", component: EditProduct },
      { path: "profile", component: AdminProfile },
      { path: "cart", component: Cart },
    ],
  },

  {
    path: "/user",
    component: UserLayout,
    children: [
      { path: "", name: "UserHome", component: UserHome },
      { path: "product/:id", component: ProductDetail },
      { path: "orders", component: UserOrders },
      { path: "profile", component: UserProfile },
      { path: "cart", component: Cart },
      { path: "checkout", component: Checkout },
      { path: "success", component: OrderSuccess },
      { path: "wishlist", component: UserWishlist },
    ],
  },

  { path: "/:pathMatch(.*)*", redirect: "/login" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const publicPages = ["/login", "/register"]; 
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem("authToken");

  if (authRequired && !loggedIn) {
    return next("/login");
  }

  if (loggedIn && publicPages.includes(to.path)) {
    const user = JSON.parse(localStorage.getItem("currentUser") || "{}");
    const roles = JSON.stringify(user.roles || []).toLowerCase();

    if (roles.includes("admin")) {
      return next("/admin");
    } else {
      return next("/user");
    }
  }

  next();
});

export default router;
