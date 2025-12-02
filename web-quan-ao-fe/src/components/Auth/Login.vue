<template>
  <div class="auth-container">
    <div class="auth-card">
      <h3 class="auth-title">Đăng Nhập</h3>
      
      <form>
        <div class="form-group">
          <label>Tài khoản</label>
          <input v-model="username" type="text" class="input-control" placeholder="Username" @keyup.enter="handleLogin" />
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input v-model="password" type="password" class="input-control" placeholder="Password" @keyup.enter="handleLogin" />
        </div>

        <button type="button" class="btn-primary" @click="handleLogin" :disabled="loading">
          {{ loading ? 'Đang kiểm tra...' : 'Đăng Nhập' }}
        </button>

        <p v-if="errorMsg" class="error-text">{{ errorMsg }}</p>

        <div class="link-text">
          Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import API from '@/api'; 

const username = ref('');
const password = ref('');
const loading = ref(false);
const errorMsg = ref('');

onMounted(() => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('currentUser');
});

async function handleLogin() {
  errorMsg.value = '';
  loading.value = true;

  try {
    const res = await API.post('/auth/login', {
      username: username.value,
      password: password.value
    });

    const data = res.data || res;
    const token = data.token || data.accessToken;
    const user = data.user || {};

    if (token) {
      localStorage.setItem('authToken', token);
      localStorage.setItem('currentUser', JSON.stringify(user));

      const rolesString = JSON.stringify(user.roles || []).toLowerCase();
      if (rolesString.includes("admin")) {
        window.location.href = "/admin";
      } else {
        window.location.href = "/user";
      }
    } else {
      errorMsg.value = "Lỗi: Không nhận được Token!";
    }
  } catch (err) {
    if (err.response && (err.response.status === 403 || err.response.status === 401)) {
      errorMsg.value = "Sai tài khoản hoặc mật khẩu!";
    } else {
      errorMsg.value = "Lỗi kết nối Server!";
    }
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* Dùng chung style với Register cho đồng bộ */
.auth-container { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f0f2f5; }
.auth-card { background: white; width: 100%; max-width: 400px; padding: 30px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
.auth-title { text-align: center; margin-bottom: 25px; color: #333; font-weight: bold; font-size: 24px;}

.form-group { margin-bottom: 15px; }
label { display: block; margin-bottom: 5px; font-weight: 600; font-size: 14px; color: #555; }
.input-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; outline: none; transition: 0.2s; }
.input-control:focus { border-color: #ff6b35; }

.btn-primary { width: 100%; padding: 12px; background: #333; color: white; border: none; border-radius: 6px; font-weight: bold; cursor: pointer; transition: 0.2s; margin-top: 10px;}
.btn-primary:hover { background: #555; }

.error-text { color: red; text-align: center; margin-top: 15px; font-size: 14px; font-weight: bold; }
.link-text { text-align: center; margin-top: 20px; font-size: 14px; }
.link-text a { color: #ff6b35; text-decoration: none; font-weight: 600; }
</style>