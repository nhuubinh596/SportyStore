<template>
  <div class="checkout-page">
    <h2 class="title">Thanh toán</h2>

    <div class="checkout-grid">
      <div class="info-col">
        <div class="card">
          <h3>Thông tin giao hàng</h3>
          <form @submit.prevent="submitOrder">
            <div class="form-group">
              <label>Họ và tên</label>
              <input v-model="form.fullName" class="input-control" required placeholder="Nguyễn Văn A" />
            </div>
            <div class="form-group">
              <label>Số điện thoại</label>
              <input v-model="form.phone" class="input-control" required placeholder="0912..." />
            </div>
            <div class="form-group">
              <label>Địa chỉ nhận hàng</label>
              <textarea v-model="form.address" class="input-control" rows="3" required placeholder="Số nhà, đường, phường/xã..."></textarea>
            </div>
            
            <button type="submit" class="btn-confirm" :disabled="loading">
              {{ loading ? 'Đang xử lý...' : 'XÁC NHẬN ĐẶT HÀNG' }}
            </button>
          </form>
        </div>
      </div>

      <div class="summary-col">
        <div class="card summary-card">
          <h3>Đơn hàng của bạn</h3>
          <div class="item-list">
            <div v-for="item in cart" :key="item.id" class="item-row">
              <div class="item-name">
                <b>{{ item.qty }}x</b> {{ item.name }}
              </div>
              <div class="item-price">{{ formatPrice(item.price * item.qty) }}</div>
            </div>
          </div>
          <hr />
          <div class="total-row">
            <span>Tổng cộng:</span>
            <span class="total-price">{{ formatPrice(totalAmount) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import API from '@/api';

const router = useRouter();
const cart = ref([]);
const loading = ref(false);

const form = ref({
  fullName: '',
  phone: '',
  address: ''
});

// Format tiền
const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

onMounted(() => {
  // Load giỏ hàng
  const savedCart = localStorage.getItem('cart');
  if (savedCart) {
    cart.value = JSON.parse(savedCart);
  }
  
  // Nếu giỏ trống thì đá về trang chủ
  if (cart.value.length === 0) {
    alert("Giỏ hàng trống!");
    router.push('/user');
  }

  // Tự điền thông tin nếu đã đăng nhập
  const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
  if (user) {
    form.value.fullName = user.name || '';
    form.value.phone = user.phone || '';
  }
});

const totalAmount = computed(() => cart.value.reduce((sum, i) => sum + (i.price * i.qty), 0));

async function submitOrder() {
  if (!confirm("Xác nhận đặt hàng?")) return;
  
  loading.value = true;
  try {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
    
    // Chuẩn bị dữ liệu gửi lên Backend
    const payload = {
      fullName: form.value.fullName,
      phone: form.value.phone,
      address: form.value.address,
      username: user.username || null, // Gửi username nếu đã login
      items: cart.value.map(item => ({
        productId: item.id,
        quantity: item.qty,
        price: item.price
      }))
    };

    // Gọi API
    await API.post('/public/orders', payload);
    
    // Thành công
    alert("Đặt hàng thành công!");
    localStorage.removeItem('cart'); // Xóa giỏ hàng
    router.push('/user/success'); // Chuyển sang trang thông báo
    
  } catch (e) {
    console.error(e);
    alert("Lỗi đặt hàng: " + (e.response?.data?.message || e.message));
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.checkout-page { max-width: 1000px; margin: 30px auto; padding: 0 20px; }
.title { text-align: center; margin-bottom: 30px; font-weight: bold; color: #333; }

.checkout-grid { display: grid; grid-template-columns: 1.5fr 1fr; gap: 30px; }
.card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
h3 { margin-bottom: 20px; font-size: 18px; font-weight: bold; border-bottom: 1px solid #eee; padding-bottom: 10px;}

.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 500; }
.input-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }

.btn-confirm { width: 100%; padding: 12px; background: #28a745; color: white; border: none; font-weight: bold; border-radius: 4px; cursor: pointer; margin-top: 10px; font-size: 16px;}
.btn-confirm:hover { background: #218838; }

/* Summary */
.item-row { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }
.total-row { display: flex; justify-content: space-between; font-size: 18px; font-weight: bold; color: #d63384; margin-top: 15px; }
</style>