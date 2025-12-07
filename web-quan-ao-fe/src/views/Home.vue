<template>
  <div style="padding:24px;">
    <div class="header">
      <div style="font-size:20px;font-weight:600">Shop Quần Áo</div>
      <div>
        <router-link to="/login" v-if="!me">Login</router-link>
        <span v-else class="small-muted">Xin chào, {{ me.username }}</span>
      </div>
    </div>

    <div class="card">
      <h4>Danh sách sản phẩm (demo)</h4>
      <div v-if="loading">Loading...</div>
      <div v-else>
        <table class="table">
          <thead><tr><th>Tên</th><th>Giá</th><th>Kho</th></tr></thead>
          <tbody>
            <tr v-for="p in products" :key="p.id">
              <td>{{ p.name }}</td>
              <td>{{ p.price }}</td>
              <td>{{ p.stock ?? '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchProducts } from '@/api.js';
const products = ref([]);
const loading = ref(false);
const me = JSON.parse(localStorage.getItem('currentUser') || 'null');

onMounted(async () => {
  loading.value = true;
  try {
    const res = await fetchProducts();
    if (res?.success && Array.isArray(res.data)) products.value = res.data;
    else if (Array.isArray(res)) products.value = res;
    else products.value = [
      { id:1, name:'T-Shirt Basic', price:199, stock:10 },
      { id:2, name:'Jeans Slim', price:499, stock:5 }
    ];
  } catch (e) {
    products.value = [
      { id:1, name:'T-Shirt Basic', price:199, stock:10 },
      { id:2, name:'Jeans Slim', price:499, stock:5 }
    ];
  } finally { loading.value = false; }
});
</script>
