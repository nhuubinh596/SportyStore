<template>
  <div class="page-container">
    <h2>Danh sách đơn hàng</h2>
    <div v-if="loading">Đang tải...</div>
    
    <table v-else class="custom-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Khách hàng</th>
          <th>Ngày đặt</th>
          <th>Tổng tiền</th>
          <th>Trạng thái</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in orders" :key="o.id">
          <td>#{{ o.id }}</td>
          <td>{{ o.user ? o.user.username : 'Khách lẻ' }}</td>
          <td>{{ o.createdAt ? new Date(o.createdAt).toLocaleDateString('vi-VN') : '-' }}</td>
          <td class="text-price">{{ formatPrice(o.totalAmount || 0) }}</td>
          <td>
            <span class="badge">{{ o.status || 'PENDING' }}</span>
            <button class="btn-icon view" @click="$router.push(`/admin/orders/${o.id}`)" title="Xem chi tiết">
                👁️
            </button>
          </td>
        </tr>
        <tr v-if="orders.length === 0">
          <td colspan="5" class="text-center">Chưa có đơn hàng nào.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import API from "@/api";

const orders = ref([]);
const loading = ref(false);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

async function load() {
  loading.value = true;
  try {
    // SỬA: Thêm /admin
    const res = await API.get("/admin/orders");
    orders.value = res.data || res;
  } catch (e) {
    console.error("Lỗi tải đơn hàng:", e);
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.page-container { background: white; padding: 20px; border-radius: 8px; }
.custom-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
.custom-table th, .custom-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: left; }
.text-price { color: #d63384; font-weight: bold; }
.badge { background: #17a2b8; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; }
.view { color: #17a2b8; }
</style>