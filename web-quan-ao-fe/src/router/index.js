import { createRouter, createWebHistory } from "vue-router";

// --- 1. AUTH (Đăng nhập - Đăng ký) ---
import Login from "@/components/Auth/Login.vue";
import Register from "@/components/Auth/Register.vue"; // <-- QUAN TRỌNG: Import trang đăng ký

// --- 2. LAYOUTS ---
import AdminLayout from "@/components/admin/AdminLayout.vue";
import UserLayout from "@/components/user/UserLayout.vue";

// --- 3. PAGES ADMIN ---
import AdminDashboard from "@/pages/admin/AdminDashboard.vue";
import UsersList from "@/pages/admin/UsersList.vue";
import OrdersList from "@/pages/admin/OrdersList.vue";
import Categories from "@/pages/admin/Categories.vue";
import ProductsList from "@/pages/admin/ProductsList.vue";
import AddProduct from "@/pages/admin/AddProduct.vue";
import EditProduct from "@/pages/admin/EditProduct.vue"; // Trang sửa sản phẩm
import AdminProfile from "@/pages/admin/AdminProfile.vue";
import OrderDetail from "@/pages/admin/OrderDetails.vue";

// --- 4. PAGES USER ---
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
  // --- PUBLIC ROUTES ---
  { path: "/login", name: "Login", component: Login },
  { path: "/register", name: "Register", component: Register }, // <-- QUAN TRỌNG: Route đăng ký

  // --- ADMIN ROUTES ---
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

  // --- USER ROUTES ---
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

  // Fallback: Vào link linh tinh thì đá về Login
  { path: "/:pathMatch(.*)*", redirect: "/login" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// --- KIỂM TRA QUYỀN (Guard) ---
router.beforeEach((to, from, next) => {
  const publicPages = ["/login", "/register"]; // Danh sách trang không cần đăng nhập
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem("authToken");

  // 1. Nếu vào trang cần mật khẩu mà chưa đăng nhập -> Đá về Login
  if (authRequired && !loggedIn) {
    return next("/login");
  }

  // 2. Nếu đã đăng nhập mà cố vào Login/Register -> Đá vào trang trong
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
