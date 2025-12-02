<template>
  <div class="product-detail-container">
    <div v-if="loading" class="text-center py-5">Đang tải...</div>

    <div v-else-if="product" class="detail-wrapper">
      <div class="gallery-col">
        <div class="main-image">
          <img :src="product.imageUrl || 'https://placehold.co/500?text=No+Image'" @error="handleImageError"/>
          <span v-if="isSale" class="sale-tag">-{{ salePercent }}%</span>
        </div>
      </div>

      <div class="info-col">
        <h1 class="product-title">{{ product.name }}</h1>
        
        <div class="price-section">
          <div v-if="isSale">
            <span class="old-price">{{ formatPrice(product.price) }}</span>
            <span class="current-price">{{ formatPrice(product.salePrice) }}</span>
          </div>
          <div v-else>
            <span class="current-price">{{ formatPrice(product.price) }}</span>
          </div>
        </div>

        <div class="meta-info">
          <div class="row-info"><label>Danh mục:</label> <span>{{ product.categoryName }}</span></div>
          <div class="row-info"><label>Mô tả:</label> <p class="desc">{{ product.description }}</p></div>
        </div>

        <div class="quantity-section">
          <label>Số lượng:</label>
          <div class="qty-control">
            <button @click="qty > 1 ? qty-- : null">-</button>
            <input type="number" v-model="qty" readonly />
            <button @click="qty++">+</button>
          </div>
        </div>

        <div class="action-buttons">
          <button class="btn-add-cart" @click="addToCart">Thêm vào giỏ hàng</button>
          <button class="btn-buy-now" @click="buyNow">Mua ngay</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import API from '@/api';

const route = useRoute();
const router = useRouter();
const product = ref(null);
const loading = ref(false);
const qty = ref(1);

const isSale = computed(() => product.value && product.value.salePrice && product.value.salePrice < product.value.price);
const salePercent = computed(() => isSale.value ? Math.round((1 - product.value.salePrice / product.value.price) * 100) : 0);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

async function loadProduct() {
  loading.value = true;
  try {
    const res = await API.get(`/public/products/${route.params.id}`);
    product.value = res.data || res;
  } catch (e) { console.error(e); } 
  finally { loading.value = false; }
}

function handleImageError(e) {
  // Đổi từ via.placeholder.com -> placehold.co
  const fallback = "https://placehold.co/300x300?text=No+Image";
  
  // Chặn vòng lặp: Nếu ảnh hiện tại đã là ảnh thay thế rồi thì thôi
  if (e.target.src === fallback) {
    return;
  }
  
  e.target.src = fallback;
}

function addToCart() {
  if (!product.value) return;

  try {
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const item = product.value;
    
    // Giá thực tế
    const finalPrice = (isSale.value) ? item.salePrice : item.price;
    
    const exist = cart.find(x => x.id === item.id);
    
    if (exist) {
      exist.qty += qty.value;
      exist.price = finalPrice;
    } else {
      cart.push({
        id: item.id,
        name: item.name,
        price: finalPrice,
        imageUrl: item.imageUrl,
        categoryName: item.categoryName,
        qty: qty.value
      });
    }

    localStorage.setItem('cart', JSON.stringify(cart));
    
    // Cập nhật Header ngay lập tức
    window.dispatchEvent(new Event('cart-updated'));
    
    alert(`Đã thêm ${qty.value} sản phẩm vào giỏ!`);
    
  } catch (e) {
    console.error(e);
    alert("Lỗi thêm giỏ hàng");
  }
}

function buyNow() {
  addToCart();
  // router.push('/cart'); // Sẽ mở sau khi làm trang Cart
}

onMounted(loadProduct);
</script>

<style scoped>
.product-detail-container { max-width: 1100px; margin: 30px auto; background: white; padding: 30px; border-radius: 8px; }
.detail-wrapper { display: grid; grid-template-columns: 2fr 3fr; gap: 40px; }

.main-image { position: relative; border: 1px solid #eee; border-radius: 8px; overflow: hidden; }
.main-image img { width: 100%; display: block; }
.sale-tag { position: absolute; top: 10px; left: 10px; background: #ffeb3b; color: #d32f2f; padding: 5px 10px; font-weight: bold; border-radius: 4px; }

.product-title { font-size: 24px; color: #333; margin-bottom: 15px; }
.price-section { background: #fafafa; padding: 15px; margin-bottom: 20px; border-radius: 4px; }
.old-price { text-decoration: line-through; color: #999; margin-right: 10px; font-size: 16px; }
.current-price { color: #ff6b35; font-size: 28px; font-weight: bold; }

.meta-info { margin-bottom: 20px; }
.row-info { display: flex; margin-bottom: 10px; }
.row-info label { width: 100px; color: #666; font-weight: 500; }
.desc { white-space: pre-line; line-height: 1.5; color: #333; margin: 0; }

.quantity-section { display: flex; align-items: center; margin-bottom: 30px; }
.qty-control { display: flex; border: 1px solid #ddd; border-radius: 4px; }
.qty-control button { width: 35px; height: 35px; border: none; background: white; cursor: pointer; }
.qty-control button:hover { background: #eee; }
.qty-control input { width: 50px; text-align: center; border: none; border-left: 1px solid #ddd; border-right: 1px solid #ddd; outline: none; }

.action-buttons { display: flex; gap: 15px; }
.btn-add-cart { background: #fff0e6; color: #ff6b35; border: 1px solid #ff6b35; padding: 12px 20px; font-weight: bold; border-radius: 4px; cursor: pointer; }
.btn-buy-now { background: #ff6b35; color: white; border: none; padding: 12px 40px; font-weight: bold; border-radius: 4px; cursor: pointer; }
.btn-buy-now:hover { background: #e65a2d; }
</style>