<template>
  <div class="pcard">
    <div class="img" :style="{ backgroundImage: 'url('+ (product.image||placeholder) +')' }"></div>
    <div class="info">
      <div class="title">{{ product.name }}</div>
      <div class="price">{{ priceLabel }}</div>
      <div class="actions">
        <button @click="$emit('view', product)">Xem</button>
        <button class="add" @click="addToCart">Thêm vào giỏ</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ product: { type: Object, required: true } })
const emit = defineEmits(['view'])

const placeholder = '/src/assets/logo.svg'

const priceLabel = computed(()=> {
  const p = props.product.price ?? 0
  return p.toLocaleString('vi-VN') + ' ₫'
})

function addToCart(){
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  cart.push({ ...props.product, addedAt: Date.now() })
  localStorage.setItem('cart', JSON.stringify(cart))
  alert('Đã thêm vào giỏ')
}
</script>

<style scoped>
.pcard{ background:#fff; border-radius:8px; box-shadow:0 1px 6px rgba(0,0,0,0.04); overflow:hidden; display:flex; flex-direction:column; }
.img{ height:160px; background-size:cover; background-position:center; }
.info{ padding:10px; display:flex; flex-direction:column; gap:8px; }
.title{ font-weight:600; font-size:14px; color:#111; }
.price{ color:#0b5ed7; font-weight:700; }
.actions{ display:flex; gap:6px; }
.actions button{ padding:6px 8px; border-radius:6px; border:1px solid #ddd; background:transparent; cursor:pointer; }
.actions .add{ background:#111; color:#fff; border:0; }
</style>
