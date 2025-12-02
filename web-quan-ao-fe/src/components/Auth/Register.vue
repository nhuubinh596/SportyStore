<template>
  <div class="auth-container">
    <div class="card shadow-sm">
      <div class="card-body">
        <h3 class="card-title text-center mb-4">Đăng Ký Tài Khoản</h3>
        
        <form @submit.prevent="handleRegister">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Tên đăng nhập *</label>
              <input v-model="form.username" class="form-control" required placeholder="user123" />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Họ và tên *</label>
              <input v-model="form.name" class="form-control" required placeholder="Nguyễn Văn A" />
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Email *</label>
              <input v-model="form.email" type="email" class="form-control" required placeholder="email@example.com" />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Số điện thoại</label>
              <input v-model="form.phone" class="form-control" placeholder="09xx..." />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Mật khẩu *</label>
            <input v-model="form.password" type="password" class="form-control" required placeholder="***" />
          </div>

          <div class="mb-3">
            <label class="form-label">Nhập lại mật khẩu *</label>
            <input v-model="confirmPassword" type="password" class="form-control" required placeholder="***" />
          </div>

          <div class="d-grid gap-2 mt-4">
            <button type="submit" class="btn btn-dark" :disabled="loading">
              {{ loading ? 'Đang xử lý...' : 'ĐĂNG KÝ NGAY' }}
            </button>
          </div>

          <div class="mt-3 text-center">
            <small v-if="errorMsg" class="text-danger fw-bold">{{ errorMsg }}</small>
          </div>

          <hr />
          <div class="text-center">
            <small>Đã có tài khoản? <router-link to="/login">Đăng nhập ngay</router-link></small>
          </div>
        </form>
      </div>
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

const form = ref({
  username: '',
  password: '',
  name: '',
  email: '',
  phone: ''
});

async function handleRegister() {
  errorMsg.value = '';

  // 1. Validate mật khẩu
  if (form.value.password !== confirmPassword.value) {
    errorMsg.value = "Mật khẩu nhập lại không khớp!";
    return;
  }

  loading.value = true;

  try {
    // 2. Gọi API đăng ký
    await API.post('/auth/register', form.value);
    
    alert("Đăng ký thành công! Hãy đăng nhập ngay.");
    router.push('/login');
  } catch (err) {
    console.error(err);
    const msg = err.response?.data?.message || err.message || "Lỗi đăng ký";
    errorMsg.value = "Thất bại: " + msg;
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* Style đồng bộ với Login */
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f6f7f9;
  padding: 20px;
}
.card {
  width: 100%;
  max-width: 600px; /* Rộng hơn login xíu để chứa 2 cột */
  border-radius: 10px;
  border: 1px solid #eee;
}
.btn-dark {
  background-color: #212529;
  border-color: #212529;
  padding: 10px;
  font-weight: 600;
}
.btn-dark:hover { background-color: #424649; }

/* CSS phụ trợ giống bootstrap grid nếu project chưa có bootstrap full */
.row { display: flex; gap: 15px; flex-wrap: wrap; }
.col-md-6 { flex: 1 1 45%; min-width: 200px; }
.form-control { width: 100%; padding: 8px 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
.d-grid { display: grid; }
.text-center { text-align: center; }
.text-danger { color: #dc3545; }
.mb-3 { margin-bottom: 1rem; }
.mt-4 { margin-top: 1.5rem; }
</style>