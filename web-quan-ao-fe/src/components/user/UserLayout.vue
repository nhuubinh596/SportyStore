<template>
  <div class="user-layout">
    <header class="user-header">
      <div class="header-left">
        <div class="brand">SportyStore</div>
        <div class="search-box">
          <input type="text" placeholder="Tìm kiếm quần áo, giày dép..." />
          <button><i class="bi bi-search"></i> Tìm</button>
        </div>
      </div>

      <div class="header-right">
        <div class="cart-btn">
          <span>🛒</span>
          <span class="badge">0</span>
        </div>

        <div class="user-info">
          <span class="avatar">👤</span>
          <span class="username">{{ currentUser?.username || 'Khách' }}</span>
          <button class="btn-logout" @click="onLogout">Đăng xuất</button>
        </div>
      </div>
    </header>

    <div class="body-container">
      <aside class="user-sidebar">
        <div class="menu-title">Tài khoản của tôi</div>
        <ul class="menu-list">
          <li>
            <router-link to="/user" active-class="active">
              🏠 Trang chủ
            </router-link>
          </li>
          <li>
            <router-link to="/user/orders" active-class="active">
              📦 Đơn mua
            </router-link>
          </li>
          <li>
            <router-link to="/user/profile" active-class="active">
              ⚙️ Thông tin cá nhân
            </router-link>
          </li>
        </ul>
      </aside>

      <main class="user-content">
        <router-view />
      </main>
    </div>

    <UserFooter></UserFooter>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import UserFooter from './UserFooter.vue';

const router = useRouter();
const currentUser = ref(null);

onMounted(() => {
  const raw = localStorage.getItem('currentUser');
  if (raw) currentUser.value = JSON.parse(raw);
});

function onLogout() {
  if(confirm("Bạn muốn đăng xuất?")) {
    localStorage.clear();
    router.push('/login');
  }
}
</script>

<style scoped>
.user-layout {
  background-color: #f5f5f5;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* HEADER */
.user-header {
  height: 70px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px; /* Padding header */
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.brand { font-size: 24px; font-weight: bold; color: #ff6b35; margin-right: 40px; cursor: pointer;}
.header-left { display: flex; align-items: center; flex: 1; }
.search-box { display: flex; flex: 1; max-width: 600px; }
.search-box input { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 4px 0 0 4px; outline: none;}
.search-box button { padding: 0 20px; background: #ff6b35; color: white; border: none; border-radius: 0 4px 4px 0; cursor: pointer;}

.header-right { display: flex; align-items: center; gap: 20px; }
.cart-btn { position: relative; cursor: pointer; font-size: 20px;}
.cart-btn .badge { position: absolute; top: -5px; right: -8px; background: red; color: white; font-size: 10px; padding: 2px 6px; border-radius: 10px;}
.user-info { display: flex; align-items: center; gap: 10px; font-size: 14px;}
.btn-logout { border: none; background: none; color: #888; cursor: pointer; text-decoration: underline;}

/* BODY CONTAINER - ĐÃ SỬA: Bỏ max-width để full màn hình */
.body-container {
  display: flex;
  width: 100%;        /* Chiếm hết chiều ngang */
  gap: 20px;          /* Khoảng cách giữa Sidebar và Content */
  padding: 20px 30px; /* Cách lề trái phải một chút cho đẹp */
  box-sizing: border-box;
}

/* SIDEBAR */
.user-sidebar {
  width: 240px;       /* Cố định chiều rộng sidebar */
  flex-shrink: 0;     /* Không cho sidebar bị co lại */
  background: white;
  padding: 20px;
  border-radius: 8px;
  height: fit-content;
  position: sticky;   /* Để sidebar trượt theo khi cuộn (tuỳ chọn) */
  top: 90px;
}
.menu-title { font-weight: bold; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 10px;}
.menu-list { list-style: none; padding: 0; }
.menu-list li { margin-bottom: 5px; }
.menu-list a { 
  display: block; padding: 10px; color: #555; text-decoration: none; border-radius: 6px; transition: 0.2s;
}
.menu-list a:hover, .menu-list a.active {
  background-color: #fff0e6;
  color: #ff6b35;
  font-weight: 600;
}

/* MAIN CONTENT */
.user-content {
  flex: 1; /* Chiếm toàn bộ phần còn lại */
  min-width: 0; /* Fix lỗi tràn content flexbox */
}
</style>