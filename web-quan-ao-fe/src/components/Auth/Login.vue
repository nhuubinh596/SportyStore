<template>
  <div class="auth-wrapper">
    <div class="auth-box">
      <div class="auth-header">
        <h2 class="brand">Sporty<span class="highlight">Store</span></h2>
        <p class="subtitle">Đăng nhập để tiếp tục mua sắm</p>
      </div>

      <form @submit.prevent>
        <div class="form-group">
          <label>Tài khoản</label>
          <div class="input-wrapper">
            <span class="icon">👤</span>
            <input 
              v-model="username" 
              type="text" 
              placeholder="Nhập username" 
              @keyup.enter="handleLogin"
            />
          </div>
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <div class="input-wrapper">
            <span class="icon">🔒</span>
            <input 
              v-model="password" 
              type="password" 
              placeholder="Nhập mật khẩu" 
              @keyup.enter="handleLogin"
            />
          </div>
        </div>

        <button class="btn-submit" @click="handleLogin" :disabled="loading">
          {{ loading ? 'Đang xử lý...' : 'ĐĂNG NHẬP' }}
        </button>

        <div class="error-msg" v-if="errorMsg">
          ⚠️ {{ errorMsg }}
        </div>

        <div class="auth-footer">
          <p>Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link></p>
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
  if(!username.value || !password.value) {
    errorMsg.value = "Vui lòng nhập đầy đủ thông tin!";
    return;
  }
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
      errorMsg.value = "Lỗi xác thực!";
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
.auth-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f6f8; 
}

.auth-box {
  background: white;
  width: 100%;
  max-width: 420px;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08); 
}

.auth-header { text-align: center; margin-bottom: 30px; }
.brand { font-size: 32px; font-weight: 800; color: #333; margin: 0; letter-spacing: -1px; }
.highlight { color: #ff6b35; }
.subtitle { color: #666; font-size: 14px; margin-top: 5px; }

.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #333; font-size: 14px; }

.input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 0 12px;
  transition: 0.2s;
  background: #fff;
}
.input-wrapper:focus-within { border-color: #ff6b35; box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1); }

.icon { font-size: 18px; margin-right: 10px; opacity: 0.6; }
.input-wrapper input {
  flex: 1;
  border: none;
  outline: none;
  padding: 12px 0;
  font-size: 15px;
  color: #333;
}

.btn-submit {
  width: 100%;
  padding: 14px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: 0.2s;
  margin-top: 10px;
}
.btn-submit:hover { background: #e65a2d; transform: translateY(-1px); }
.btn-submit:disabled { background: #ffd5c2; cursor: not-allowed; }

.error-msg { color: #d32f2f; background: #ffebee; padding: 10px; border-radius: 6px; margin-top: 15px; font-size: 13px; text-align: center; }

.auth-footer { text-align: center; margin-top: 25px; border-top: 1px solid #f0f0f0; padding-top: 20px; font-size: 14px; color: #666; }
.auth-footer a { color: #ff6b35; font-weight: 600; text-decoration: none; }
.auth-footer a:hover { text-decoration: underline; }
</style>