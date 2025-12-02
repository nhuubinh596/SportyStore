<template>
  <div class="user-layout">
    <div class="layout-header-wrapper">
      <UserHeader />
    </div>

    <div class="body-container">
      <aside class="user-sidebar">
        <div class="menu-group">
          <div class="menu-title">Mua sắm</div>
          <ul class="menu-list">
            <li>
              <router-link to="/user" active-class="active" exact>
                🏠 Trang chủ
              </router-link>
            </li>
            <li>
              <router-link
                to="/user/cart"
                active-class="active"
                class="highlight-item"
              >
                🛒 Giỏ hàng
              </router-link>
            </li>
          </ul>
        </div>

        <div class="menu-group">
          <div class="menu-title">Tài khoản</div>
          <ul class="menu-list">
            <li>
              <router-link to="/user/orders" active-class="active"
                >📦 Đơn mua</router-link
              >
            </li>

            <li>
              <router-link to="/user/wishlist" active-class="active"
                >❤️ Yêu thích</router-link
              >
            </li>

            <li>
              <router-link to="/user/profile" active-class="active"
                >⚙️ Thông tin cá nhân</router-link
              >
            </li>
          </ul>
        </div>
      </aside>

      <main class="user-content">
        <router-view />
      </main>
    </div>

    <UserFooter />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import UserHeader from "./UserHeader.vue";
import UserFooter from "./UserFooter.vue";

const router = useRouter();
const currentUser = ref(null);

onMounted(() => {
  const raw = localStorage.getItem("currentUser");
  if (raw) currentUser.value = JSON.parse(raw);
});

function onLogout() {
  if (confirm("Bạn muốn đăng xuất?")) {
    localStorage.clear();
    router.push("/login");
  }
}
</script>

<style scoped>
/* Tổng thể trang */
.user-layout {
  background-color: #f5f5f5;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header Wrapper để giữ vị trí */
.layout-header-wrapper {
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
}

/* Phần thân: Flexbox chia cột */
.body-container {
  display: flex;
  width: 100%;
  gap: 20px;
  padding: 20px 30px;
  box-sizing: border-box;
  flex: 1; /* Đẩy footer xuống đáy */
  align-items: flex-start; /* Sidebar không bị giãn chiều cao theo content */
}

/* Sidebar bên trái */
.user-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
  position: sticky; /* Trượt theo khi cuộn */
  top: 90px;
}

.menu-group {
  margin-bottom: 20px;
}
.menu-title {
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 14px;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.menu-list li {
  margin-bottom: 5px;
}

.menu-list a {
  display: block;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s;
  font-weight: 500;
}

.menu-list a:hover {
  background-color: #f0f0f0;
}

/* Style riêng cho nút đang chọn (Active) */
.menu-list a.active {
  background-color: #fff0e6; /* Nền cam nhạt */
  color: #ff6b35; /* Chữ cam */
  font-weight: bold;
}

/* Style riêng cho nút Giỏ hàng để nổi bật */
.highlight-item {
  color: #ff6b35 !important;
}

/* Nội dung chính bên phải */
.user-content {
  flex: 1;
  min-width: 0; /* Fix lỗi tràn layout */
}
</style>
