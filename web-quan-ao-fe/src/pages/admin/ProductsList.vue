<template>
  <div class="page-container">
    <div class="header-row">
      <h2>Quản lý sản phẩm</h2>
      <router-link to="/admin/products/add" class="btn-add">
        + Thêm mới
      </router-link>
    </div>

    <div v-if="loading" class="text-center">Đang tải dữ liệu...</div>

    <div v-else class="table-responsive">
      <table class="custom-table">
        <thead>
          <tr>
            <th width="50">STT</th> <th width="80">Ảnh</th>
            <th>Tên sản phẩm</th>
            <th>Giá</th>
            <th>Danh mục</th>
            <th width="120">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(p, index) in products" :key="p.id">
            <td>#{{ index + 1 }}</td>
            
            <td>
              <img :src="p.imageUrl || 'https://placehold.co/50?text=No+Img'" class="thumb" />
            </td>
            <td class="fw-bold">{{ p.name }}</td>
            <td class="text-price">{{ formatPrice(p.price) }}</td>
            <td>{{ p.categoryName || '---' }}</td>
            <td>
              <button class="btn-icon edit" @click="onEdit(p)" title="Sửa">
                ✏️
              </button>
              
              <button class="btn-icon delete" @click="onDelete(p.id)" title="Xóa">
                🗑️
              </button>
            </td>
          </tr>
          <tr v-if="products.length === 0">
            <td colspan="6" class="text-center">Chưa có sản phẩm nào.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import API from '@/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const products = ref([]);
const loading = ref(false);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

async function loadData() {
  loading.value = true;
  try {
    const res = await API.get('/admin/products');
    products.value = res.data || res;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function onEdit(product) {
  router.push(`/admin/products/edit/${product.id}`);
}

async function onDelete(id) {
  if (!confirm("Bạn chắc chắn muốn xóa?")) return;
  try {
    await API.delete(`/admin/products/${id}`);
    alert("Đã xóa!");
    loadData();
  } catch (e) {
    alert("Lỗi xóa: " + e.message);
  }
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.page-container { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); color: #333; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
h2 { color: #333; font-weight: bold; }

.btn-add { 
  background: #28a745; color: white; padding: 10px 20px; 
  text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 14px;
  box-shadow: 0 2px 5px rgba(40, 167, 69, 0.3); transition: 0.2s;
}
.btn-add:hover { background: #218838; transform: translateY(-1px); }

.custom-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #333; }
.custom-table th { background: #f8f9fa; padding: 12px; text-align: left; border-bottom: 2px solid #dee2e6; color: #555; font-weight: 600; }
.custom-table td { padding: 12px; border-bottom: 1px solid #eee; vertical-align: middle; }

.thumb { width: 40px; height: 40px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd; }
.text-price { color: #d63384; font-weight: bold; }
.fw-bold { font-weight: 600; }

.btn-icon { border: none; background: none; font-size: 18px; cursor: pointer; margin-right: 8px; transition: 0.2s; }
.btn-icon:hover { transform: scale(1.2); }
.edit { color: #007bff; }
.delete { color: #dc3545; }
</style>