<template>
  <header class="header-container">
    <div class="header-left">
      <div class="brand">Sporty<span class="highlight">Admin</span></div>
    </div>

    <div class="header-right">
      <div class="user-greeting">
        <span>Xin chào, </span>
        <span class="username">{{ currentUser?.username || 'Admin' }}</span>
      </div>
      
      <button class="btn-logout" @click="handleLogout">
        Đăng xuất
      </button>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentUser = ref(null);

onMounted(() => {
  try {
    const raw = localStorage.getItem('currentUser');
    if (raw) {
      currentUser.value = JSON.parse(raw);
    }
  } catch (e) {
    console.error(e);
  }
});

function handleLogout() {
  if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
    localStorage.clear(); // Xóa sạch token
    router.push('/login'); // Đá về login
  }
}
</script>

<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 24px;
  background-color: #ffffff; /* Nền trắng */
}

.brand {
  font-size: 20px;
  font-weight: 700;
  color: #374151; /* Màu chữ đậm */
}

.highlight {
  color: #ff6b35; /* Màu cam điểm nhấn */
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px; /* Khoảng cách giữa tên và nút */
}

.user-greeting {
  font-size: 14px;
  color: #4b5563; /* Màu xám đậm */
}

.username {
  font-weight: 700;
  color: #111827; /* Màu đen */
}

.btn-logout {
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #ef4444; /* Chữ đỏ */
  background-color: #fef2f2; /* Nền đỏ nhạt */
  border: 1px solid #fecaca;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-logout:hover {
  background-color: #ef4444;
  color: white;
}
</style>