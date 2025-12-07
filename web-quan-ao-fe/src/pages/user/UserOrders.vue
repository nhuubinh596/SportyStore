<template>
  <div class="orders-page">
    <h2 class="page-title">Đơn hàng của tôi</h2>

    <div v-if="loading" class="text-center py-5">Đang tải dữ liệu...</div>

    <div v-else-if="orders.length === 0" class="empty-state">
      <p>Bạn chưa có đơn hàng nào.</p>
      <router-link to="/user" class="btn-shop">Mua sắm ngay</router-link>
    </div>

    <div v-else class="order-table-wrapper">
      <table class="order-table">
        <thead>
          <tr>
            <th>STT</th> <th>Ngày đặt</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
            <th>Chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(order, index) in orders" :key="order.id">
            <td>#{{ index + 1 }}</td>
            
            <td>{{ formatDate(order.createdAt) }}</td>
            <td class="price">{{ formatPrice(order.totalAmount) }}</td>
            <td>
              <span :class="['status-badge', getStatusColor(order.status)]">
                {{ formatStatus(order.status) }}
              </span>
            </td>
            <td>
              <button class="btn-view" @click="openDetail(order)">Xem</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="selectedOrder" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Chi tiết đơn hàng (Mã hệ thống: #{{ selectedOrder.id }})</h3>
          <button class="btn-close" @click="closeDetail">×</button>
        </div>
        
        <div class="modal-body">
          <div class="info-group">
            <p><strong>Người nhận:</strong> {{ selectedOrder.fullName || '---' }}</p>
            <p><strong>SĐT:</strong> {{ selectedOrder.phone || '---' }}</p>
            <p><strong>Địa chỉ:</strong> {{ selectedOrder.address || '---' }}</p>
            <p><strong>Thanh toán:</strong> {{ selectedOrder.paymentMethod === 'COD' ? 'Tiền mặt' : 'Chuyển khoản' }}</p>
          </div>

          <table class="detail-table">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th>Giá</th>
                <th>SL</th>
                <th>Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in selectedOrder.items" :key="item.id">
                <td>{{ item.product?.name || 'Sản phẩm đã xóa' }}</td>
                <td>{{ formatPrice(item.price) }}</td>
                <td>x{{ item.qty }}</td>
                <td class="fw-bold">{{ formatPrice(item.price * item.qty) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="3" class="text-right">Tổng cộng:</td>
                <td class="total-price">{{ formatPrice(selectedOrder.totalAmount) }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import API from '@/api';

const orders = ref([]);
const loading = ref(false);
const selectedOrder = ref(null);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const formatDate = (d) => new Date(d).toLocaleDateString('vi-VN') + ' ' + new Date(d).toLocaleTimeString('vi-VN');

const formatStatus = (s) => {
  const map = { 'PENDING': 'Chờ xử lý', 'SHIPPING': 'Đang giao', 'COMPLETED': 'Hoàn thành', 'CANCELLED': 'Đã hủy' };
  return map[s] || s;
};

const getStatusColor = (s) => {
  if (s === 'PENDING') return 'status-orange';
  if (s === 'COMPLETED') return 'status-green';
  if (s === 'CANCELLED') return 'status-red';
  return 'status-blue';
};

function openDetail(order) {
  selectedOrder.value = order;
}

function closeDetail() {
  selectedOrder.value = null;
}

onMounted(async () => {
  const user = JSON.parse(localStorage.getItem('currentUser'));
  if (!user) return;

  loading.value = true;
  try {
    const res = await API.get(`/public/orders?username=${user.username}`);
    orders.value = res.data || res;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.orders-page { max-width: 1000px; margin: 30px auto; padding: 0 20px; color: #333; }
.page-title { font-size: 24px; font-weight: bold; margin-bottom: 20px; color: #333; }

.order-table-wrapper { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); overflow: hidden; }
.order-table { width: 100%; border-collapse: collapse; }
.order-table th, .order-table td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; color: #333; }
.order-table th { background: #f9f9f9; font-weight: 600; color: #555; }

.price { font-weight: bold; color: #333; }
.btn-view { padding: 6px 12px; border: 1px solid #ff6b35; background: white; color: #ff6b35; border-radius: 4px; cursor: pointer; transition: 0.2s; font-size: 13px; font-weight: 500;}
.btn-view:hover { background: #ff6b35; color: white; }

.status-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.status-orange { background: #fff3cd; color: #856404; }
.status-green { background: #d4edda; color: #155724; }
.status-red { background: #f8d7da; color: #721c24; }
.status-blue { background: #cce5ff; color: #004085; }

.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; }
.modal-content { background: white; width: 90%; max-width: 600px; border-radius: 8px; overflow: hidden; color: #333; }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; background: #f9f9f9; }
.modal-header h3 { margin: 0; font-size: 18px; color: #333; font-weight: bold; }
.btn-close { border: none; background: none; font-size: 24px; cursor: pointer; color: #999; }
.btn-close:hover { color: #333; }

.modal-body { padding: 20px; max-height: 70vh; overflow-y: auto; color: #333; }

.info-group { margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px dashed #eee; font-size: 14px; line-height: 1.6; color: #444; }
.info-group strong { color: #000; font-weight: 600; }

.detail-table { width: 100%; border-collapse: collapse; font-size: 14px; }
.detail-table th { text-align: left; padding: 8px; border-bottom: 1px solid #ddd; color: #555; font-weight: 600;}
.detail-table td { padding: 8px; border-bottom: 1px solid #eee; color: #333; }
.text-right { text-align: right; }
.total-price { color: #ff6b35; font-weight: bold; font-size: 16px; }
</style>