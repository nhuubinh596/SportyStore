<template>
  <div class="order-detail-page">
    <div class="header-action">
      <button class="btn-back" @click="$router.push('/admin/orders')">← Quay lại danh sách</button>
      <h2 v-if="order">Chi tiết đơn hàng #{{ order.id }}</h2>
    </div>

    <div v-if="loading" class="text-center py-5">
      Đang tải dữ liệu...
    </div>

    <div v-else-if="order" class="content-grid">
      
      <div class="info-section">
        <div class="card mb-4">
          <h3>Thông tin giao hàng</h3>
          <div class="info-row">
            <strong>Người nhận:</strong> {{ order.fullName || '---' }}
          </div>
          <div class="info-row">
            <strong>SĐT:</strong> {{ order.phone || '---' }}
          </div>
          <div class="info-row">
            <strong>Địa chỉ:</strong> {{ order.address || '---' }}
          </div>
          <div class="info-row">
            <strong>Thanh toán:</strong> 
            <span class="badge-payment">{{ order.paymentMethod === 'COD' ? 'Tiền mặt' : 'Chuyển khoản' }}</span>
          </div>
        </div>

        <div class="card">
          <h3>Cập nhật trạng thái</h3>
          <div class="status-control">
            <select v-model="order.status" class="status-select">
              <option value="PENDING">Chờ xử lý</option>
              <option value="SHIPPING">Đang giao hàng</option>
              <option value="COMPLETED">Đã giao thành công</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
            <button class="btn-update" @click="updateStatus">Cập nhật</button>
          </div>
          <div class="current-status-text">
            Trạng thái hiện tại: <span :class="getStatusColor(order.status)">{{ formatStatus(order.status) }}</span>
          </div>
        </div>
      </div>

      <div class="items-section card">
        <h3>Danh sách sản phẩm</h3>
        <table class="item-table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th class="text-right">Giá</th>
              <th class="text-center">SL</th>
              <th class="text-right">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in order.items" :key="item.id">
              <td>
                <div class="product-info">
                  <span class="p-name">{{ item.product?.name || 'Sản phẩm đã xóa' }}</span>
                </div>
              </td>
              <td class="text-right">{{ formatPrice(item.price) }}</td>
              <td class="text-center">x{{ item.qty }}</td>
              <td class="text-right fw-bold">{{ formatPrice(item.price * item.qty) }}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3" class="text-right label-total">Tổng cộng:</td>
              <td class="total-price">{{ formatPrice(order.totalAmount) }}</td>
            </tr>
          </tfoot>
        </table>
      </div>

    </div>

    <div v-else class="text-center py-5">
      <p>Không tìm thấy đơn hàng này.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import API from '@/api';

const route = useRoute();
const order = ref(null); 
const loading = ref(false);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

const formatStatus = (s) => {
  const map = { 'PENDING': 'Chờ xử lý', 'SHIPPING': 'Đang giao', 'COMPLETED': 'Hoàn thành', 'CANCELLED': 'Đã hủy' };
  return map[s] || s;
};

const getStatusColor = (s) => {
  if (s === 'PENDING') return 'text-warning';
  if (s === 'COMPLETED') return 'text-success';
  if (s === 'CANCELLED') return 'text-danger';
  return 'text-primary';
};

async function loadOrder() {
  loading.value = true;
  try {
    const id = route.params.id;
    const res = await API.get(`/admin/orders/${id}`);
    order.value = res.data || res;
  } catch (e) {
    console.error(e);
    alert("Lỗi tải đơn hàng: " + e.message);
  } finally {
    loading.value = false;
  }
}

async function updateStatus() {
  try {
    await API.put(`/admin/orders/${order.value.id}/status`, { status: order.value.status });
    alert("Cập nhật thành công!");
    loadOrder(); 
  } catch (e) {
    alert("Lỗi cập nhật: " + e.message);
  }
}

onMounted(() => {
  loadOrder();
});
</script>

<style scoped>
.order-detail-page { padding: 20px; max-width: 1200px; margin: 0 auto; color: #333; }

.header-action { display: flex; align-items: center; gap: 20px; margin-bottom: 25px; }
.btn-back { 
  background: white; border: 1px solid #ddd; padding: 8px 15px; 
  border-radius: 6px; cursor: pointer; color: #555; font-weight: 500;
}
.btn-back:hover { background: #f5f5f5; color: #333; }

.content-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 25px; }

.card { 
  background: white; padding: 25px; border-radius: 8px; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-bottom: 20px; 
}

h3 { 
  border-bottom: 1px solid #eee; padding-bottom: 15px; margin-bottom: 20px; 
  font-size: 16px; font-weight: bold; color: #333; text-transform: uppercase;
}

.info-row { margin-bottom: 12px; font-size: 14px; display: flex; justify-content: space-between; }
.info-row strong { color: #555; }
.badge-payment { background: #eef2f5; padding: 2px 8px; border-radius: 4px; font-weight: 600; font-size: 12px; }

.status-control { display: flex; gap: 10px; margin-bottom: 15px; }
.status-select { flex: 1; padding: 10px; border-radius: 6px; border: 1px solid #ddd; outline: none; }
.btn-update { 
  background: #ff6b35; color: white; border: none; padding: 0 20px; 
  border-radius: 6px; cursor: pointer; font-weight: bold; 
}
.btn-update:hover { background: #e65a2d; }
.current-status-text { font-size: 14px; color: #666; }

.item-table { width: 100%; border-collapse: collapse; }
.item-table th { background: #f9f9f9; padding: 12px; text-align: left; color: #666; font-weight: 600; border-bottom: 2px solid #eee; }
.item-table td { padding: 12px; border-bottom: 1px solid #f5f5f5; vertical-align: middle; }

.text-right { text-align: right; }
.text-center { text-align: center; }
.fw-bold { font-weight: bold; }

.label-total { font-size: 16px; font-weight: bold; color: #333; padding-top: 20px; }
.total-price { color: #ff6b35; font-weight: bold; font-size: 20px; padding-top: 20px; }

.text-warning { color: #d97706; font-weight: bold; }
.text-success { color: #059669; font-weight: bold; }
.text-danger { color: #dc2626; font-weight: bold; }
.text-primary { color: #2563eb; font-weight: bold; }
</style>