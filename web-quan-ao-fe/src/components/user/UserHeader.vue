<template>
  <header class="user-header">
    <div class="header-left">
      <a href="/user" class="brand">SportyStore</a>
      
      <div class="search-box">
        <input 
          type="text" 
          v-model="keyword" 
          placeholder="Tìm kiếm quần áo, giày dép..." 
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">Tìm</button>
      </div>
    </div>

    <div class="header-right">
      <a href="/user/cart" class="cart-btn-wrapper">
        <div class="cart-icon-box">
          <span style="font-size: 28px;">🛒</span>
          <span class="badge" v-if="cartCount > 0">{{ cartCount }}</span>
        </div>
      </a>

      <div class="user-info">
        <span class="username">Xin chào, {{ currentUser?.username || 'Khách' }}</span>
        <button class="btn-logout" @click="logout">Thoát</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router'; // Import router để chuyển trang

const router = useRouter();
const currentUser = ref(null);
const cartCount = ref(0);
const keyword = ref(''); // Biến lưu từ khóa

// Xử lý tìm kiếm
function handleSearch() {
  // Đẩy từ khóa lên URL (Query Param) -> Trang chủ sẽ bắt lấy và lọc
  router.push({ path: '/user', query: { q: keyword.value } });
}

function updateCartCount() {
  try {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]');
    cartCount.value = cart.reduce((sum, item) => sum + (item.qty || 1), 0);
  } catch (e) {
    cartCount.value = 0;
  }
}

function logout() {
  if(confirm("Đăng xuất?")) {
    localStorage.clear();
    window.location.href = '/login';
  }
}

onMounted(() => {
  try {
    const u = localStorage.getItem('currentUser');
    if (u) currentUser.value = JSON.parse(u);
  } catch(e){}

  updateCartCount();
  window.addEventListener('cart-updated', updateCartCount);
});

onUnmounted(() => {
  window.removeEventListener('cart-updated', updateCartCount);
});
</script>

<style scoped>
/* CSS Giữ nguyên như cũ */
.user-header { height: 70px; background: white; display: flex; align-items: center; justify-content: space-between; padding: 0 40px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 9999; }
.brand { font-size: 24px; font-weight: bold; color: #ff6b35; text-decoration: none; margin-right: 20px;}
.header-left { display: flex; align-items: center; gap: 20px; flex: 1;}
.search-box { display: flex; flex: 1; max-width: 500px; }
.search-box input { flex: 1; padding: 8px; border: 1px solid #ddd; outline: none; }
.search-box button { padding: 0 15px; background: #ff6b35; color: white; border: none; cursor: pointer; }
.header-right { display: flex; align-items: center; gap: 25px; }
.cart-btn-wrapper { text-decoration: none; color: inherit; cursor: pointer; position: relative; z-index: 10000; pointer-events: auto !important; }
.cart-icon-box { position: relative; padding: 5px; display: flex; align-items: center; }
.cart-btn-wrapper:hover .cart-icon-box { transform: scale(1.1); }
.badge { position: absolute; top: -8px; right: -8px; background: red; color: white; font-size: 12px; font-weight: bold; padding: 2px 6px; border-radius: 10px; border: 2px solid white; }
.user-info { font-size: 14px; display: flex; gap: 10px; align-items: center; }
.btn-logout { border: 1px solid #ddd; background: white; padding: 4px 8px; cursor: pointer; border-radius: 4px; }
.user-info { 
  font-size: 14px; 
  display: flex; gap: 10px; align-items: center; 
  color: #333 !important; /* <--- Thêm !important để ép màu đen */
}

.username {
  font-weight: 700;
  color: #000 !important; /* Tên user đen tuyền */
}
</style>