<template>
  <div class="cart-page">
    <div class="header-row">
      <h2 class="page-title">Giỏ hàng của bạn</h2>
      <router-link to="/user" class="back-link">Tiếp tục mua hàng</router-link>
    </div>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <img src="https://cdni.iconscout.com/illustration/premium/thumb/empty-cart-2130356-1800917.png" alt="Empty" />
      <p>Giỏ hàng chưa có sản phẩm nào.</p>
      <router-link to="/user" class="btn-continue">Quay lại trang chủ</router-link>
    </div>

    <div v-else class="cart-content">
      <div class="cart-table">
        <table>
          <thead>
            <tr>
              <th width="100">Sản phẩm</th>
              <th>Tên</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Thành tiền</th>
              <th width="50"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in cartItems" :key="item.id">
              <td>
                <img :src="item.imageUrl || 'https://placehold.co/100?text=No+Img'" class="thumb" />
              </td>
              <td>
                <div class="p-name">{{ item.name }}</div>
                <div class="p-cat">{{ item.categoryName }}</div>
              </td>
              <td class="price">{{ formatPrice(item.price) }}</td>
              <td>
                <div class="qty-control">
                  <button @click="updateQty(index, -1)">-</button>
                  <input type="number" :value="item.qty" readonly />
                  <button @click="updateQty(index, 1)">+</button>
                </div>
              </td>
              <td class="total-col">{{ formatPrice(item.price * item.qty) }}</td>
              <td>
                <button class="btn-del" @click="removeItem(index)">🗑️</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="cart-summary">
        <div class="summary-row">
          <span>Tạm tính:</span>
          <span>{{ formatPrice(totalAmount) }}</span>
        </div>
        <div class="summary-row total">
          <span>Tổng cộng:</span>
          <span class="final-price">{{ formatPrice(totalAmount) }}</span>
        </div>
        
        <button class="btn-checkout" @click="checkout">Tiến hành đặt hàng</button>
        
        <router-link to="/user" class="btn-continue-shopping">
          ← Chọn thêm sản phẩm khác
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const cartItems = ref([]);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

onMounted(() => {
  const savedCart = localStorage.getItem('cart');
  if (savedCart) {
    cartItems.value = JSON.parse(savedCart);
  }
});

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.qty), 0);
});

function updateQty(index, delta) {
  const item = cartItems.value[index];
  const newQty = item.qty + delta;
  
  if (newQty > 0) {
    item.qty = newQty;
    saveCart();
  }
}

function removeItem(index) {
  if (confirm("Xóa sản phẩm này?")) {
    cartItems.value.splice(index, 1);
    saveCart();
    window.location.reload();
  }
}

function saveCart() {
  localStorage.setItem('cart', JSON.stringify(cartItems.value));
}

function checkout() {
  router.push('/user/checkout'); 
}
</script>

<style scoped>
.cart-page { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 24px; font-weight: bold; color: #333; margin: 0; }
.back-link { color: #ff6b35; text-decoration: none; font-weight: 500; }

.empty-cart { text-align: center; padding: 50px 0; background: white; border-radius: 8px; }
.empty-cart img { width: 200px; margin-bottom: 20px; opacity: 0.8; }
.btn-continue { display: inline-block; padding: 10px 20px; background: #ff6b35; color: white; text-decoration: none; border-radius: 4px; font-weight: bold; }

.cart-content { display: grid; grid-template-columns: 3fr 1fr; gap: 30px; }
.cart-table { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
table { width: 100%; border-collapse: collapse; }
th { text-align: left; padding: 15px; border-bottom: 2px solid #eee; color: #666; font-weight: 600; }
td { padding: 15px; border-bottom: 1px solid #eee; vertical-align: middle; }

.thumb { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #f0f0f0; }
.p-name { font-weight: 600; color: #333; margin-bottom: 5px; }
.p-cat { font-size: 12px; color: #999; }
.total-col { color: #ff6b35; font-weight: bold; }

.qty-control { display: flex; border: 1px solid #ddd; width: fit-content; border-radius: 4px; }
.qty-control button { width: 30px; height: 30px; border: none; background: white; cursor: pointer; }
.qty-control input { width: 40px; text-align: center; border: none; border-left: 1px solid #ddd; border-right: 1px solid #ddd; outline: none; }
.btn-del { border: none; background: none; cursor: pointer; font-size: 18px; }

.cart-summary { background: white; padding: 25px; border-radius: 8px; height: fit-content; box-shadow: 0 2px 5px rgba(0,0,0,0.05); position: sticky; top: 90px; }
.summary-row { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 14px; color: #666; }
.summary-row.total { font-size: 18px; font-weight: bold; color: #333; border-top: 1px solid #eee; padding-top: 15px; margin-top: 15px; }
.final-price { color: #ff6b35; font-size: 22px; }

.btn-checkout { width: 100%; padding: 15px; background: #ff6b35; color: white; border: none; border-radius: 4px; font-weight: bold; font-size: 16px; cursor: pointer; margin-top: 20px; }
.btn-checkout:hover { background: #e65a2d; }

.btn-continue-shopping {
  display: block;
  text-align: center;
  margin-top: 15px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: 0.2s;
}
.btn-continue-shopping:hover {
  background: #f9f9f9;
  color: #333;
}
</style>