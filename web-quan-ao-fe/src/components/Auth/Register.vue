<template>
  <div class="auth-wrapper">
    <div class="auth-box register-box">
      <div class="auth-header">
        <h2 class="brand">Sporty<span class="highlight">Store</span></h2>
        <p class="subtitle">Tạo tài khoản mới</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="row">
          <div class="col">
            <label>Tên đăng nhập *</label>
            <input v-model="form.username" class="custom-input" required placeholder="user123" />
          </div>
          <div class="col">
            <label>Họ và tên *</label>
            <input v-model="form.name" class="custom-input" required placeholder="Nguyễn Văn A" />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <label>Email *</label>
            <input v-model="form.email" type="email" class="custom-input" required placeholder="email@example.com" />
          </div>
          <div class="col">
            <label>Số điện thoại</label>
            <input v-model="form.phone" class="custom-input" placeholder="09xx..." />
          </div>
        </div>

        <div class="row">
          <div class="col">
            <label>Mật khẩu *</label>
            <input v-model="form.password" type="password" class="custom-input" required placeholder="***" />
          </div>
          <div class="col">
            <label>Nhập lại mật khẩu *</label>
            <input v-model="confirmPassword" type="password" class="custom-input" required placeholder="***" />
          </div>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Đang xử lý...' : 'ĐĂNG KÝ NGAY' }}
        </button>

        <div class="error-msg" v-if="errorMsg">⚠️ {{ errorMsg }}</div>

        <div class="auth-footer">
          <p>Đã có tài khoản? <router-link to="/login">Đăng nhập ngay</router-link></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import API from '@/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(false);
const errorMsg = ref('');
const confirmPassword = ref('');

const form = ref({ username: '', password: '', name: '', email: '', phone: '' });

async function handleRegister() {
  errorMsg.value = '';
  if (form.value.password !== confirmPassword.value) {
    errorMsg.value = "Mật khẩu nhập lại không khớp!";
    return;
  }
  loading.value = true;

  try {
    await API.post('/auth/register', form.value);
    alert("Đăng ký thành công! Hãy đăng nhập ngay.");
    router.push('/login');
  } catch (err) {
    errorMsg.value = "Lỗi: " + (err.response?.data?.message || err.message);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* Style dùng chung giống Login nhưng rộng hơn */
.auth-wrapper { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f4f6f8; padding: 20px; }
.auth-box { background: white; width: 100%; padding: 40px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.08); }
.register-box { max-width: 600px; } /* Rộng hơn Login */

.auth-header { text-align: center; margin-bottom: 30px; }
.brand { font-size: 32px; font-weight: 800; color: #333; margin: 0; letter-spacing: -1px; }
.highlight { color: #ff6b35; }
.subtitle { color: #666; font-size: 14px; margin-top: 5px; }

.row { display: flex; gap: 20px; margin-bottom: 15px; flex-wrap: wrap; }
.col { flex: 1; min-width: 200px; }

label { display: block; margin-bottom: 8px; font-weight: 600; color: #333; font-size: 14px; }
.custom-input { 
  width: 100%; padding: 12px; border: 1px solid #e0e0e0; border-radius: 8px; font-size: 14px; 
  box-sizing: border-box; transition: 0.2s; outline: none;
}
.custom-input:focus { border-color: #ff6b35; box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1); }

.btn-submit { width: 100%; padding: 14px; background: #ff6b35; color: white; border: none; border-radius: 8px; font-weight: 700; font-size: 16px; cursor: pointer; transition: 0.2s; margin-top: 15px; }
.btn-submit:hover { background: #e65a2d; transform: translateY(-1px); }

.error-msg { color: #d32f2f; background: #ffebee; padding: 10px; border-radius: 6px; margin-top: 15px; font-size: 13px; text-align: center; }
.auth-footer { text-align: center; margin-top: 25px; border-top: 1px solid #f0f0f0; padding-top: 20px; font-size: 14px; color: #666; }
.auth-footer a { color: #ff6b35; font-weight: 600; text-decoration: none; }
.auth-footer a:hover { text-decoration: underline; }
</style>