<template>
  <div class="page-container">
    <h2>Quản lý Đơn hàng</h2>

    <div v-if="loading" class="text-center">Đang tải dữ liệu...</div>

    <div v-else class="table-responsive">
      <table class="custom-table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Ngày đặt</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
            <th>Chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td>#{{ o.id }}</td>
            <td class="fw-bold">{{ o.user ? o.user.username : 'Khách lẻ' }}</td>
            <td>{{ formatDate(o.createdAt) }}</td>
            <td class="text-price">{{ formatPrice(o.totalAmount) }}</td>
            <td>
              <span :class="['status-badge', getStatusColor(o.status)]">
                {{ formatStatus(o.status) }}
              </span>
            </td>
            <td>
              <button class="btn-view" @click="openDetail(o)">Xem</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="selectedOrder" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Chi tiết đơn hàng #{{ selectedOrder.id }}</h3>
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
                <td>{{ item.product?.name || 'SP đã xóa' }}</td>
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

          <div class="status-action">
            <label>Trạng thái:</label>
            <select v-model="selectedOrder.status" class="status-select">
              <option value="PENDING">Chờ xử lý</option>
              <option value="SHIPPING">Đang giao hàng</option>
              <option value="COMPLETED">Đã giao thành công</option>
              <option value="CANCELLED">Hủy đơn</option>
            </select>
            <button class="btn-save" @click="updateStatus">Cập nhật</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import API from "@/api";

const orders = ref([]);
const loading = ref(false);
const selectedOrder = ref(null);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const formatDate = (d) => d ? new Date(d).toLocaleString('vi-VN') : '-';

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

async function load() {
  loading.value = true;
  try {
    const res = await API.get("/admin/orders");
    orders.value = res.data || res;
  } catch (e) { console.error(e); } 
  finally { loading.value = false; }
}

async function openDetail(order) {
  try {
    // Gọi API lấy chi tiết để có full item
    const res = await API.get(`/admin/orders/${order.id}`);
    selectedOrder.value = res.data || res;
  } catch (e) {
    alert("Lỗi: " + e.message);
  }
}

function closeDetail() {
  selectedOrder.value = null;
}

async function updateStatus() {
  try {
    await API.put(`/admin/orders/${selectedOrder.value.id}/status`, { 
      status: selectedOrder.value.status 
    });
    alert("Đã cập nhật trạng thái!");
    closeDetail();
    load(); 
  } catch(e) {
    alert("Lỗi: " + e.message);
  }
}

onMounted(load);
</script>

<style scoped>
.page-container { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); color: #333; }
h2 { color: #333; font-weight: bold; margin-bottom: 20px; }

/* Table */
.custom-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #333; }
.custom-table th { background: #f8f9fa; padding: 12px; text-align: left; border-bottom: 2px solid #dee2e6; color: #555; font-weight: 600; }
.custom-table td { padding: 12px; border-bottom: 1px solid #eee; vertical-align: middle; }

.fw-bold { font-weight: 600; }
.text-price { color: #d63384; font-weight: bold; }
.text-right { text-align: right; }

.btn-view { padding: 5px 10px; border: 1px solid #17a2b8; background: white; color: #17a2b8; border-radius: 4px; cursor: pointer; transition: 0.2s; font-weight: 600;}
.btn-view:hover { background: #17a2b8; color: white; }

.status-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
.status-orange { background: #fff3cd; color: #856404; }
.status-green { background: #d4edda; color: #155724; }
.status-red { background: #f8d7da; color: #721c24; }
.status-blue { background: #cce5ff; color: #004085; }

/* MODAL STYLE (FIXED) */
.modal-overlay { 
  position: fixed; top: 0; left: 0; right: 0; bottom: 0; 
  background: rgba(0,0,0,0.5); 
  display: flex; align-items: center; justify-content: center; 
  z-index: 9999; /* Cao hơn tất cả để không bị che */
}

.modal-content { 
  background: white; width: 90%; max-width: 650px; 
  border-radius: 8px; overflow: hidden; 
  color: #333; /* Chữ đen */
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
}

.modal-header { padding: 15px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; background: #f8f9fa; }
.modal-header h3 { margin: 0; font-size: 18px; color: #333; font-weight: bold; }
.btn-close { border: none; background: none; font-size: 28px; cursor: pointer; color: #888; line-height: 1; }
.btn-close:hover { color: #000; }

.modal-body { padding: 20px; max-height: 80vh; overflow-y: auto; }

.info-group p { margin-bottom: 8px; font-size: 14px; }
.detail-table { width: 100%; border-collapse: collapse; margin: 20px 0; font-size: 14px; }
.detail-table th { border-bottom: 2px solid #eee; padding: 8px; text-align: left; color: #555; }
.detail-table td { border-bottom: 1px solid #eee; padding: 8px; }
.total-price { font-size: 18px; color: #d63384; font-weight: bold; }

.status-action { background: #f1f5f9; padding: 15px; border-radius: 6px; display: flex; align-items: center; gap: 10px; justify-content: flex-end; margin-top: 10px; }
.status-select { padding: 8px; border-radius: 4px; border: 1px solid #ccc; font-weight: 500; }
.btn-save { padding: 8px 15px; background: #ff6b35; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
.btn-save:hover { background: #e65a2d; }
</style>