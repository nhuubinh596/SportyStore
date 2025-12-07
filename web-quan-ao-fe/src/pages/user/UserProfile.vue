<template>
  <div class="admin-profile-container">
    <h2>Hồ sơ quản trị viên</h2>
    
    <div class="profile-card" v-if="user">
      <div class="info-row">
        <label>Username:</label>
        <span>{{ user.username }}</span>
      </div>
      <div class="info-row">
        <label>Họ tên:</label>
        <span>{{ user.name || 'Chưa cập nhật' }}</span>
      </div>
      <div class="info-row">
        <label>Email:</label>
        <span>{{ user.email }}</span>
      </div>
      <div class="info-row">
        <label>Quyền hạn:</label>
        <span class="role-tag">ADMIN</span>
      </div>
    </div>

    <div v-else class="loading">
      Đang tải thông tin...
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  name: "AdminProfile",
  setup() {
    const user = ref(null);

    onMounted(() => {
      try {
        const raw = localStorage.getItem("currentUser");
        if (raw) {
          user.value = JSON.parse(raw);
        }
      } catch (e) {
        console.error("Lỗi đọc user:", e);
      }
    });

    return { user };
  }
};
</script>

<style scoped>
.admin-profile-container { padding: 20px; }
.profile-card {
  background: white;
  padding: 24px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  max-width: 500px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.info-row:last-child { border-bottom: none; }
.info-row label { font-weight: 600; color: #666; }
.info-row span { font-weight: 500; color: #333; }
.role-tag { 
  background-color: #ff6b35; 
  color: white !important; 
  padding: 4px 10px; 
  border-radius: 12px; 
  font-size: 0.9em;
}
</style>