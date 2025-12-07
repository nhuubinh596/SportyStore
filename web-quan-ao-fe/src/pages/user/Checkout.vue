<template>
  <div class="checkout-page">
    <h2 class="page-title">Thanh toán</h2>

    <div class="checkout-grid">
      <div class="info-section">
        <div class="card mb-4">
          <h3 class="card-header">Thông tin người nhận</h3>
          <div class="card-body">
            <div class="form-group">
              <label>Họ và tên</label>
              <input v-model="form.fullName" class="input-control" placeholder="Nguyễn Văn A" required />
            </div>
            <div class="form-group">
              <label>Số điện thoại</label>
              <input v-model="form.phone" class="input-control" placeholder="09xx..." required />
            </div>
            <div class="form-group">
              <label>Địa chỉ nhận hàng</label>
              <textarea v-model="form.address" class="input-control" rows="3" placeholder="Số nhà, đường, phường/xã..." required></textarea>
            </div>
          </div>
        </div>

        <div class="card">
          <h3 class="card-header">Phương thức thanh toán</h3>
          <div class="card-body">
            <div class="payment-options">
              <label class="payment-option">
                <input type="radio" v-model="form.paymentMethod" value="COD" />
                <span class="payment-label">💵 Thanh toán khi nhận hàng (COD)</span>
              </label>
              <label class="payment-option">
                <input type="radio" v-model="form.paymentMethod" value="BANK" />
                <span class="payment-label">🏦 Chuyển khoản ngân hàng</span>
              </label>
            </div>
            </div>
        </div>
      </div>

      <div class="summary-section">
        <div class="card summary-card">
          <h3 class="card-header">Đơn hàng</h3>
          <div class="summary-body">
            <div class="order-items">
              <div v-for="item in cart" :key="item.id" class="item-row">
                <div class="item-info">
                  <span class="item-qty">{{ item.qty }}x</span>
                  <span class="item-name">{{ item.name }}</span>
                </div>
                <span class="item-price">{{ formatPrice(item.price * item.qty) }}</span>
              </div>
            </div>
            
            <hr />

            <div class="voucher-box">
              <input 
                v-model="voucherCode" 
                type="text" 
                placeholder="Nhập mã giảm giá" 
                :disabled="discountAmount > 0"
                @keyup.enter="applyVoucher"
              />
              <button @click="applyVoucher" :disabled="discountAmount > 0 || !voucherCode">
                {{ discountAmount > 0 ? 'Đã dùng' : 'Áp dụng' }}
              </button>
            </div>
            <small v-if="voucherMessage" :class="discountAmount > 0 ? 'text-success' : 'text-danger'">
              {{ voucherMessage }}
            </small>
            
            <hr />
            
            <div class="price-row">
              <span>Tạm tính:</span>
              <span>{{ formatPrice(totalAmount) }}</span>
            </div>
            <div class="price-row">
              <span>Phí vận chuyển:</span>
              <span>{{ shippingFee === 0 ? 'Miễn phí' : formatPrice(shippingFee) }}</span>
            </div>
            
            <div v-if="discountAmount > 0" class="price-row discount-row">
              <span>Voucher giảm:</span>
              <span>-{{ formatPrice(discountAmount) }}</span>
            </div>
            
            <hr />
            
            <div class="total-row">
              <span>Tổng thanh toán:</span>
              <span class="final-price">{{ formatPrice(finalTotal) }}</span>
            </div>

            <button class="btn-checkout" @click="submitOrder" :disabled="loading">
              {{ loading ? 'Đang xử lý...' : 'ĐẶT HÀNG NGAY' }}
            </button>
            
            <router-link to="/user/cart" class="back-link">← Quay lại giỏ hàng</router-link>
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

const voucherCode = ref('');
const discountAmount = ref(0);
const voucherMessage = ref('');

const form = ref({
  fullName: '',
  phone: '',
  address: '',
  paymentMethod: 'COD'
});

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

onMounted(() => {
  const savedCart = localStorage.getItem('cart');
  if (savedCart) {
    cart.value = JSON.parse(savedCart);
  } else {
    router.push('/user');
  }

  try {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
    if (user) {
      form.value.fullName = user.name || '';
      form.value.phone = user.phone || '';
    }
  } catch(e){}
});

const totalAmount = computed(() => cart.value.reduce((sum, i) => sum + (i.price * i.qty), 0));
const shippingFee = computed(() => totalAmount.value > 500000 ? 0 : 30000); 

const finalTotal = computed(() => {
  const total = totalAmount.value + shippingFee.value - discountAmount.value;
  return total > 0 ? total : 0; 
});

function applyVoucher() {
  voucherMessage.value = '';
  const code = voucherCode.value.toUpperCase().trim();

  if (code === 'SPORTY50') {
    discountAmount.value = 50000;
    voucherMessage.value = "Áp dụng mã SPORTY50 thành công (-50k)";
  } else if (code === 'FREESHIP') {
    if (shippingFee.value > 0) {
        discountAmount.value = shippingFee.value;
        voucherMessage.value = "Đã miễn phí vận chuyển!";
    } else {
        voucherMessage.value = "Đơn hàng này đã được Freeship sẵn rồi!";
        discountAmount.value = 0;
    }
  } else if (code === 'VIP') {
    discountAmount.value = totalAmount.value * 0.1; 
    voucherMessage.value = "Áp dụng mã VIP thành công (-10%)";
  } else {
    discountAmount.value = 0;
    voucherMessage.value = "Mã giảm giá không hợp lệ!";
  }
}

async function submitOrder() {
  if (!form.value.fullName || !form.value.phone || !form.value.address) {
    alert("Vui lòng điền thông tin giao hàng!");
    return;
  }
  
  if (!confirm(`Xác nhận đặt đơn hàng trị giá ${formatPrice(finalTotal.value)}?`)) return;
  
  loading.value = true;
  try {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
    
    const payload = {
      fullName: form.value.fullName,
      phone: form.value.phone,
      address: form.value.address,
      username: user.username || null,
      paymentMethod: form.value.paymentMethod,
      totalAmount: finalTotal.value, 
      items: cart.value.map(item => ({
        productId: item.id,
        quantity: item.qty,
        price: item.price
      }))
    };

    await API.post('/public/orders', payload);
    
    localStorage.removeItem('cart');
    window.dispatchEvent(new Event('cart-updated'));
    router.push('/user/success');
    
  } catch (e) {
    alert("Lỗi đặt hàng: " + (e.response?.data?.message || e.message));
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.checkout-page { max-width: 1100px; margin: 30px auto; padding: 0 20px; }
.page-title { text-align: center; margin-bottom: 30px; font-weight: bold; color: #333; }

.checkout-grid { display: grid; grid-template-columns: 1.5fr 1fr; gap: 30px; }

.card { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); overflow: hidden; margin-bottom: 20px; }
.card-header { background: #f9f9f9; padding: 15px 20px; font-size: 16px; font-weight: bold; border-bottom: 1px solid #eee; margin: 0; color: #333; }
.card-body, .summary-body { padding: 20px; }

.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: 500; font-size: 14px; }
.input-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; outline: none; }
.input-control:focus { border-color: #ff6b35; }

.payment-options { display: flex; flex-direction: column; gap: 10px; }
.payment-option { display: flex; align-items: center; gap: 10px; padding: 10px; border: 1px solid #eee; border-radius: 6px; cursor: pointer; }
.payment-option:hover { background: #fff0e6; border-color: #ff6b35; }
.payment-label { font-weight: 500; }

.summary-card { position: sticky; top: 90px; }
.item-row { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14px; }
.item-name { flex: 1; margin-right: 10px; color: #555; }
.item-price { font-weight: 600; }

.voucher-box { display: flex; gap: 10px; margin: 15px 0 5px; }
.voucher-box input { flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 4px; outline: none; text-transform: uppercase; }
.voucher-box button { padding: 8px 15px; background: #333; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 13px;}
.voucher-box button:disabled { background: #ccc; cursor: not-allowed; }
.text-success { color: #28a745; font-size: 13px; font-style: italic; }
.text-danger { color: #dc3545; font-size: 13px; font-style: italic; }

.price-row { display: flex; justify-content: space-between; margin-bottom: 8px; color: #666; font-size: 14px; }
.discount-row { color: #28a745; font-weight: bold; }

.total-row { display: flex; justify-content: space-between; margin-top: 15px; font-size: 18px; font-weight: bold; color: #333; }
.final-price { color: #ff6b35; font-size: 24px; }

.btn-checkout { width: 100%; padding: 15px; background: #ff6b35; color: white; border: none; border-radius: 6px; font-weight: bold; font-size: 16px; margin-top: 20px; cursor: pointer; transition: 0.2s; }
.btn-checkout:hover { background: #e65a2d; }
.back-link { display: block; text-align: center; margin-top: 15px; color: #666; text-decoration: none; font-size: 14px; }
.back-link:hover { color: #ff6b35; }
</style>