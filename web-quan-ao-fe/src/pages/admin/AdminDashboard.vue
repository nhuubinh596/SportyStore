<template>
  <div class="dashboard-wrapper">
    <h2 class="page-title">Tổng quan hệ thống</h2>

    <div class="stats-row">
      <div class="stat-card blue">
        <div class="stat-value">{{ stats.products }}</div>
        <div class="stat-label">Sản phẩm</div>
      </div>
      <div class="stat-card green">
        <div class="stat-value">{{ stats.categories }}</div>
        <div class="stat-label">Danh mục</div>
      </div>
      <div class="stat-card orange">
        <div class="stat-value">{{ stats.orders }}</div>
        <div class="stat-label">Đơn hàng</div>
      </div>
      <div class="stat-card red">
        <div class="stat-value">{{ stats.users }}</div>
        <div class="stat-label">Khách hàng</div>
      </div>
    </div>

    <div class="table-section">
      <h3 class="section-title">Đơn hàng mới nhất</h3>
      
      <div class="table-responsive">
        <table class="data-table">
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
            <tr v-for="o in recentOrders" :key="o.id">
              <td>#{{ o.id }}</td>
              <td class="fw-bold">{{ o.user ? o.user.username : 'Khách lẻ' }}</td>
              <td>{{ formatDate(o.createdAt) }}</td>
              <td class="text-price">{{ formatPrice(o.totalAmount) }}</td>
              <td>
                <span class="status-badge">{{ o.status || 'PENDING' }}</span>
              </td>
            </tr>
            <tr v-if="recentOrders.length === 0">
              <td colspan="5" class="text-center">Chưa có dữ liệu</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import API from '@/api';

const stats = ref({ products: 0, categories: 0, orders: 0, users: 0 });
const recentOrders = ref([]);

function formatDate(d) {
  if (!d) return '-';
  return new Date(d).toLocaleDateString('vi-VN');
}

function formatPrice(v) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v || 0);
}

async function loadDashboard() {
  try {
    const [p, c, o, u] = await Promise.all([
      API.get('/admin/products'),
      API.get('/admin/categories'),
      API.get('/admin/orders'),
      API.get('/admin/users')
    ]);

    const products = p.data || p;
    const categories = c.data || c;
    const orders = o.data || o;
    const users = u.data || u;

    stats.value = { 
      products: products.length, 
      categories: categories.length, 
      orders: orders.length, 
      users: users.length 
    };
    
    // Lấy 5 đơn mới nhất
    recentOrders.value = orders.slice(0, 5);

  } catch (error) {
    console.error("Lỗi Dashboard:", error);
  }
}

onMounted(() => loadDashboard());
</script>

<style scoped>
.dashboard-wrapper {
  color: #333; /* Quan trọng: Đặt màu chữ chính là đen */
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 24px;
  color: #111;
}

/* CARDS */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  padding: 24px;
  border-radius: 12px;
  color: white; /* Chữ trong thẻ màu thì để trắng cho nổi */
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-value { font-size: 36px; font-weight: 800; line-height: 1.2; }
.stat-label { font-size: 14px; opacity: 0.9; text-transform: uppercase; letter-spacing: 1px; margin-top: 5px; }

.blue { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.green { background: linear-gradient(135deg, #10b981, #059669); }
.orange { background: linear-gradient(135deg, #f59e0b, #d97706); }
.red { background: linear-gradient(135deg, #ef4444, #dc2626); }

/* TABLE SECTION */
.table-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #374151;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  text-align: left;
  padding: 12px 16px;
  background-color: #f9fafb;
  color: #6b7280; /* Màu xám chữ tiêu đề */
  font-weight: 600;
  border-bottom: 1px solid #e5e7eb;
}

.data-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  color: #1f2937; /* Chữ nội dung màu đen xám */
  vertical-align: middle;
}

.fw-bold { font-weight: 600; }
.text-price { color: #d97706; font-weight: 700; font-family: monospace; }
.text-center { text-align: center; }

.status-badge {
  padding: 4px 10px;
  background-color: #e0f2fe;
  color: #0369a1;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
</style>