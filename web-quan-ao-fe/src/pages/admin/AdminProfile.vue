<template>
  <div class="admin-profile">
    <h2>Thông tin quản trị viên</h2>
    <div class="card" v-if="user">
      <div class="row">
        <label>Tên đăng nhập:</label>
        <span>{{ user.username }}</span>
      </div>
      <div class="row">
        <label>Họ tên:</label>
        <span>{{ user.name }}</span>
      </div>
      <div class="row">
        <label>Email:</label>
        <span>{{ user.email }}</span>
      </div>
      <div class="row">
        <label>Vai trò:</label>
        <span class="badge">ADMIN</span>
      </div>
    </div>
    <div v-else>
      Không tìm thấy thông tin. Vui lòng đăng nhập lại.
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  setup() {
    const user = ref(null);

    onMounted(() => {
      try {
        const raw = localStorage.getItem("currentUser");
        if (raw) {
          user.value = JSON.parse(raw);
        }
      } catch (e) {
        console.error(e);
      }
    });

    return { user };
  }
};
</script>

<style scoped>
.card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); max-width: 500px;}
.row { display: flex; justify-content: space-between; margin-bottom: 15px; border-bottom: 1px solid #f0f0f0; padding-bottom: 10px;}
.row label { font-weight: bold; color: #555; }
.badge { background: #ff6b35; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem;}
</style>