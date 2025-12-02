<template>
  <div class="home-page">
    
    <div class="filter-bar">
      <div class="filter-left">
        <label>Sắp xếp:</label>
        <button class="active">Mới nhất</button>
        <button>Giá thấp - cao</button>
      </div>
      <div class="filter-right">
        <span>Hiển thị {{ products.length }} sản phẩm</span>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">Đang tải sản phẩm...</div>

    <div v-else class="product-grid">
      <div class="product-card" v-for="p in products" :key="p.id" @click="goToDetail(p.id)">
        <div class="card-img">
          <img :src="p.imageUrl || 'https://placehold.co/300?text=No+Image'" @error="handleImageError" />
          
          <div v-if="p.salePrice && p.salePrice < p.price" class="sale-badge">
            -{{ Math.round((1 - p.salePrice/p.price) * 100) }}%
          </div>

          <div class="overlay">
            <button class="btn-quick-add" @click.stop="addToCart(p)">
              + Thêm vào giỏ
            </button>
          </div>
        </div>

        <div class="card-info">
          <h3 class="product-title">{{ p.name }}</h3>
          
          <div class="price-row">
            <div v-if="p.salePrice && p.salePrice < p.price" class="sale-wrapper">
              <span class="old-price">{{ formatPrice(p.price) }}</span>
              <span class="new-price">{{ formatPrice(p.salePrice) }}</span>
            </div>
            <div v-else>
              <span class="new-price">{{ formatPrice(p.price) }}</span>
            </div>
          </div>
          
          <div class="category-tag">{{ p.categoryName || 'Mới' }}</div>
        </div>
      </div>
    </div>
    
    <div v-if="!loading && products.length === 0" class="empty-state">
      <p>Chưa có sản phẩm nào.</p>
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

async function loadProducts() {
  loading.value = true;
  try {
    const res = await API.get('/public/products');
    products.value = res.data || res;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function handleImageError(e) {
  const fb = "https://placehold.co/300?text=No+Image";
  if (e.target.src !== fb) e.target.src = fb;
}

function goToDetail(id) {
  router.push(`/user/product/${id}`);
}

// --- HÀM QUAN TRỌNG ĐÃ SỬA ---
function addToCart(p) {
  try {
    // 1. Lấy giỏ cũ
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    
    // 2. Tính toán
    const existItem = cart.find(item => item.id === p.id);
    const finalPrice = (p.salePrice && p.salePrice < p.price) ? p.salePrice : p.price;

    if (existItem) {
      existItem.qty += 1;
      existItem.price = finalPrice;
    } else {
      cart.push({
        id: p.id,
        name: p.name,
        price: finalPrice,
        imageUrl: p.imageUrl,
        categoryName: p.categoryName,
        qty: 1
      });
    }
    
    // 3. Lưu vào LocalStorage
    localStorage.setItem('cart', JSON.stringify(cart));
    
    // 4. Thông báo và RELOAD TRANG NGAY LẬP TỨC
    if(confirm(`Đã thêm "${p.name}" vào giỏ! Bạn có muốn đến Giỏ hàng ngay không?`)) {
      // Nếu chọn OK -> Đi tới giỏ hàng
      window.location.href = "/user/cart"; 
    } else {
      // Nếu chọn Cancel -> Ở lại và F5 để cập nhật số lượng trên Header
      window.location.reload();
    }

  } catch (e) {
    alert("Lỗi: " + e.message);
  }
}

onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.home-page { padding: 20px; max-width: 1200px; margin: 0 auto; width: 100%; }

.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; background: white; padding: 15px; border-radius: 8px; }
.filter-left button { border: 1px solid #ddd; background: white; padding: 6px 12px; margin-left: 10px; border-radius: 4px; cursor: pointer; }
.filter-left button.active { background: #ff6b35; color: white; border-color: #ff6b35; }

.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 20px; }

.product-card { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.06); transition: 0.3s; cursor: pointer; position: relative; display: flex; flex-direction: column; }
.product-card:hover { transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0,0,0,0.1); }

.card-img { position: relative; width: 100%; padding-top: 100%; overflow: hidden; }
.card-img img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; }

.sale-badge { position: absolute; top: 10px; right: 10px; background: #ffe100; color: #d32f2f; padding: 2px 6px; font-size: 12px; font-weight: bold; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }

.overlay { position: absolute; bottom: 0; left: 0; right: 0; background: rgba(255,255,255,0.9); padding: 10px; transform: translateY(100%); transition: 0.3s; }
.product-card:hover .overlay { transform: translateY(0); }

.btn-quick-add { width: 100%; background: #ff6b35; color: white; border: none; padding: 8px; border-radius: 4px; font-weight: bold; cursor: pointer; }
.btn-quick-add:hover { background: #e65a2d; }

.card-info { padding: 12px; flex: 1; display: flex; flex-direction: column; }
.product-title { font-size: 15px; font-weight: 600; margin: 0 0 8px; color: #333; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 42px; }

.price-row { margin-top: auto; display: flex; align-items: flex-end; }
.sale-wrapper { display: flex; flex-direction: column; }
.old-price { font-size: 13px; color: #999; text-decoration: line-through; }
.new-price { color: #ff6b35; font-weight: bold; font-size: 16px; }

.category-tag { font-size: 12px; color: #888; margin-top: 5px; }
.empty-state { text-align: center; padding: 50px; color: #888; }
</style>