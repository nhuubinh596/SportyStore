<template>
  <div class="order-detail-page">
    <div class="header-action">
      <button class="btn-back" @click="$router.push('/admin/orders')">← Quay lại</button>
      <h2>Chi tiết đơn hàng #{{ order.id }}</h2>
    </div>

    <div v-if="order" class="content-grid">
      
      <div class="info-section">
        <div class="card">
          <h3>Thông tin khách hàng</h3>
          <p><strong>Họ tên:</strong> {{ order.user?.name || order.user?.username || 'Khách lẻ' }}</p>
          <p><strong>Email:</strong> {{ order.user?.email || '---' }}</p>
          <p><strong>SĐT:</strong> {{ order.user?.phone || '---' }}</p>
          <p><strong>Ngày đặt:</strong> {{ formatDate(order.createdAt) }}</p>
        </div>

        <div class="card mt-3">
          <h3>Cập nhật trạng thái</h3>
          <div class="status-control">
            <select v-model="currentStatus" class="status-select">
              <option value="PENDING">Chờ xử lý</option>
              <option value="SHIPPING">Đang giao hàng</option>
              <option value="COMPLETED">Đã giao thành công</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
            <button class="btn-update" @click="updateStatus">Cập nhật</button>
          </div>
          <p class="current-status">Hiện tại: <span :class="getStatusColor(order.status)">{{ order.status }}</span></p>
        </div>
      </div>

      <div class="items-section card">
        <h3>Sản phẩm đã đặt</h3>
        <table class="item-table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Đơn giá</th>
              <th>Số lượng</th>
              <th>Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in order.items" :key="item.id">
              <td>
                <div class="product-info">
                  <span class="p-name">{{ item.product?.name || 'Sản phẩm đã xóa' }}</span>
                </div>
              </td>
              <td>{{ formatPrice(item.price) }}</td>
              <td>x{{ item.quantity || item.qty }}</td>
              <td class="fw-bold">{{ formatPrice(item.price * (item.quantity || item.qty)) }}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3" class="text-right"><strong>Tổng cộng:</strong></td>
              <td class="total-price">{{ formatPrice(order.totalAmount) }}</td>
            </tr>
          </tfoot>
        </table>
      </div>

    </div>
    <div v-else class="loading">Đang tải thông tin...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import API from '@/api';

const route = useRoute();
const orderId = route.params.id;
const order = ref(null);
const currentStatus = ref('');

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const formatDate = (d) => new Date(d).toLocaleString('vi-VN');

async function loadOrder() {
  try {
    const res = await API.get(`/admin/orders/${orderId}`);
    order.value = res.data || res;
    currentStatus.value = order.value.status;
  } catch (e) {
    alert("Lỗi tải đơn hàng");
  }
}

async function updateStatus() {
  try {
    await API.put(`/admin/orders/${orderId}/status`, { status: currentStatus.value });
    alert("Cập nhật trạng thái thành công!");
    loadOrder(); // Tải lại để cập nhật giao diện
  } catch (e) {
    alert("Lỗi cập nhật: " + e.message);
  }
}

function getStatusColor(status) {
  if (status === 'PENDING') return 'text-warning';
  if (status === 'COMPLETED') return 'text-success';
  if (status === 'CANCELLED') return 'text-danger';
  return 'text-primary';
}

onMounted(() => {
  loadOrder();
});
</script>

<style scoped>
.order-detail-page { padding: 20px; }
.header-action { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; }
.btn-back { background: none; border: 1px solid #ddd; padding: 5px 10px; border-radius: 4px; cursor: pointer; }

.content-grid { display: grid; grid-template-columns: 1fr 2fr; gap: 20px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.mt-3 { margin-top: 20px; }

h3 { border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px; font-size: 16px; font-weight: bold; color: #333; }

.status-control { display: flex; gap: 10px; margin-bottom: 10px; }
.status-select { flex: 1; padding: 8px; border-radius: 4px; border: 1px solid #ddd; }
.btn-update { background: #ff6b35; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; }

.item-table { width: 100%; border-collapse: collapse; }
.item-table th, .item-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: left; }
.item-table th { background: #f9f9f9; font-weight: 600; color: #555; }
.text-right { text-align: right; }
.total-price { color: #d63384; font-weight: bold; font-size: 18px; }

.text-warning { color: #f6c23e; font-weight: bold; }
.text-success { color: #1cc88a; font-weight: bold; }
.text-danger { color: #e74a3b; font-weight: bold; }
.text-primary { color: #4e73df; font-weight: bold; }
</style>