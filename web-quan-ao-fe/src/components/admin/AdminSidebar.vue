<template>
  <aside class="sidebar">
    <div class="menu-group">
      <router-link to="/admin" class="nav-item">Dashboard</router-link>
      <router-link to="/admin/users" class="nav-item">Tài khoản</router-link>
      <router-link to="/admin/orders" class="nav-item">Đơn hàng</router-link>
      <router-link to="/admin/categories" class="nav-item">Danh mục</router-link>
      <router-link to="/admin/products" class="nav-item">Sản phẩm</router-link>
      
      </div>

    <div class="footer">
      <router-link to="/admin/profile" class="nav-item">Trang cá nhân</router-link>
      <a @click.prevent="logout" class="nav-item logout">Đăng xuất</a>
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from "vue-router";
const router = useRouter();

function logout() {
  if(confirm("Đăng xuất?")) {
    localStorage.clear();
    router.push('/login');
  }
}
</script>

<style scoped>
.sidebar { display: flex; flex-direction: column; height: 100%; justify-content: space-between; padding: 15px; }
.nav-item { display: block; padding: 12px 16px; margin-bottom: 5px; color: #333; text-decoration: none; border-radius: 6px; font-weight: 500; transition: 0.2s; }
.nav-item:hover { background: #eef2f5; }
.router-link-active { background: #fff0e6; color: #ff6b35; font-weight: bold; }
.logout { color: #d9534f; cursor: pointer; border-top: 1px solid #eee; margin-top: 10px; }
</style>