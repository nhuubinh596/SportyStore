<template>
  <div class="orders box">
    <h3>Đơn hàng của bạn</h3>
    <div v-if="orders.length === 0">
      <p>Bạn chưa có đơn hàng. Thêm món và đặt hàng để thấy danh sách ở đây.</p>
    </div>

    <ul v-else class="order-list">
      <li v-for="o in orders" :key="o.id" class="order-item">
        <div><b>Đơn #{{ o.id }}</b> — {{ new Date(o.created).toLocaleString() }}</div>
        <div>Số sản phẩm: {{ o.items.length }}</div>
        <div>Tổng: {{ total(o) }}</div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const orders = ref([])

onMounted(()=>{
  try { orders.value = JSON.parse(localStorage.getItem('orders')||'[]') } catch(e){ orders.value = [] }
})

function total(o){
  const s = (o.items || []).reduce((acc,i)=> acc + (i.price||0) * (i.qty||1), 0)
  return s.toLocaleString('vi-VN') + ' ₫'
}
</script>

<style scoped>
.order-list{ list-style:none; padding:0; display:flex; flex-direction:column; gap:8px; }
.order-item{ padding:10px; background:#fff; border-radius:8px; box-shadow:0 1px 4px rgba(0,0,0,0.04); }
</style>
