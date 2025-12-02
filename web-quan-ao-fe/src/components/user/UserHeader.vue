<template>
  <header class="user-header">
    <div class="header-left">
      <div class="brand" @click="$router.push('/user')">SportyStore</div>
      <div class="search-box">
        <input type="text" placeholder="Tìm kiếm..." />
        <button>Tìm</button>
      </div>
    </div>

    <div class="header-right">
      <div class="cart-btn" @click="goToCart">
        <span class="icon">🛒</span>
        <span class="badge">{{ cartCount }}</span>
      </div>

      <div class="user-info">
        <span class="username">Xin chào, {{ currentUser?.username || 'Khách' }}</span>
        <button class="btn-logout" @click="logout">Thoát</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentUser = ref(null);
const cartCount = ref(0);

function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]');
  cartCount.value = cart.reduce((sum, item) => sum + (item.qty || 1), 0);
}

// Hàm chuyển trang khi bấm vào icon
function goToCart() {
  router.push('/user/cart');
}

function logout() {
  if(confirm("Đăng xuất?")) {
    localStorage.clear();
    router.push('/login');
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
.user-header { height: 70px; background: white; display: flex; align-items: center; justify-content: space-between; padding: 0 40px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: sticky; top: 0; z-index: 1000; }
.brand { font-size: 24px; font-weight: bold; color: #ff6b35; cursor: pointer; margin-right: 20px;}
.header-left { display: flex; align-items: center; gap: 20px; flex: 1;}
.search-box { display: flex; flex: 1; max-width: 500px; }
.search-box input { flex: 1; padding: 8px; border: 1px solid #ddd; outline: none; }
.search-box button { padding: 0 15px; background: #ff6b35; color: white; border: none; cursor: pointer; }

.header-right { display: flex; align-items: center; gap: 25px; }

/* CSS Nút Giỏ Hàng - Thêm cursor pointer để biết là bấm được */
.cart-btn { 
  position: relative; 
  cursor: pointer; 
  padding: 5px; 
  transition: transform 0.2s;
  display: flex;
  align-items: center;
}
.cart-btn .icon { font-size: 26px; }
.cart-btn:hover { transform: scale(1.1); }

.badge { 
  position: absolute; top: -5px; right: -8px; 
  background: red; color: white; 
  font-size: 11px; font-weight: bold; 
  padding: 2px 6px; border-radius: 10px; 
}

.user-info { font-size: 14px; display: flex; gap: 10px; align-items: center; }
.btn-logout { border: 1px solid #ddd; background: white; padding: 4px 8px; cursor: pointer; border-radius: 4px; }
</style>