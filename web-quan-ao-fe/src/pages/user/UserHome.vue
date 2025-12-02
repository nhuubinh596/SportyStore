<template>
  <div class="home-page">
    
    <div class="filter-bar">
      <div class="filter-left">
        <label>Sắp xếp:</label>
        <button 
          :class="{ active: sortOption === 'newest' }" 
          @click="changeSort('newest')"
        >Mới nhất</button>

        <button 
          :class="{ active: sortOption === 'price_asc' }" 
          @click="changeSort('price_asc')"
        >Giá tăng dần</button>

        <button 
          :class="{ active: sortOption === 'price_desc' }" 
          @click="changeSort('price_desc')"
        >Giá giảm dần</button>
      </div>

      <div class="filter-right">
        <span v-if="keyword">Kết quả cho: "<strong>{{ keyword }}</strong>"</span>
        <span v-else>Hiển thị {{ products.length }} sản phẩm</span>
      </div>
    </div>

    <div v-if="loading && products.length === 0" class="text-center py-5">Đang tải sản phẩm...</div>

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
      <p>Không tìm thấy sản phẩm nào.</p>
    </div>

    <div v-if="!loading && hasMore" class="load-more-container">
      <button class="btn-load-more" @click="loadMore">Xem thêm sản phẩm ▼</button>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import API from '@/api';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const products = ref([]);
const loading = ref(false);
const page = ref(0);
const hasMore = ref(true);
const keyword = ref('');
const sortOption = ref('newest'); // Biến lưu trạng thái sắp xếp

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

// Hàm gọi API lấy dữ liệu
async function loadProducts(isLoadMore = false) {
  if (!isLoadMore) {
    page.value = 0;
    products.value = [];
    hasMore.value = true;
  }

  loading.value = true;
  try {
    const res = await API.get(`/public/products`, {
      params: {
        page: page.value,
        size: 10, // Lấy 10 cái mỗi lần
        keyword: keyword.value,
        sort: sortOption.value // Gửi tham số sắp xếp
      }
    });

    // Xử lý dữ liệu trả về (Spring Page)
    const newProducts = res.content || res.data || [];
    const isLast = res.last; // Kiểm tra xem đã hết trang chưa

    if (newProducts.length < 10 || isLast) {
      hasMore.value = false;
    }

    if (isLoadMore) {
      products.value = [...products.value, ...newProducts];
    } else {
      products.value = newProducts;
    }

  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

// Sự kiện đổi kiểu sắp xếp
function changeSort(option) {
  sortOption.value = option;
  loadProducts(false); // Reset và tải lại từ đầu
}

function loadMore() {
  page.value++;
  loadProducts(true);
}

function handleImageError(e) {
  const fallback = "https://placehold.co/300?text=No+Image";
  if (e.target.src !== fallback) e.target.src = fallback;
}

function goToDetail(id) {
  router.push(`/user/product/${id}`);
}

function addToCart(p) {
  let cart = JSON.parse(localStorage.getItem('cart') || '[]');
  const existItem = cart.find(item => item.id === p.id);
  const finalPrice = (p.salePrice && p.salePrice < p.price) ? p.salePrice : p.price;

  if (existItem) {
    existItem.qty += 1;
    existItem.price = finalPrice;
  } else {
    cart.push({ ...p, price: finalPrice, qty: 1 });
  }
  
  localStorage.setItem('cart', JSON.stringify(cart));
  window.dispatchEvent(new Event('cart-updated'));
  alert(`Đã thêm "${p.name}" vào giỏ!`);
}

// Theo dõi URL để tìm kiếm
watch(() => route.query.q, (newVal) => {
  keyword.value = newVal || '';
  loadProducts(false);
});

onMounted(() => {
  keyword.value = route.query.q || '';
  loadProducts(false);
});
</script>

<style scoped>
.home-page { width: 100%; color: #333; } /* Chữ đen toàn trang */

/* Filter Bar */
.filter-bar { 
  display: flex; justify-content: space-between; align-items: center; 
  margin-bottom: 20px; background: white; padding: 15px; border-radius: 8px; 
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.filter-left { display: flex; align-items: center; gap: 10px; }
.filter-left label { font-weight: bold; color: #555; margin-right: 5px; }

.filter-left button { 
  border: 1px solid #ddd; background: white; padding: 6px 15px; 
  border-radius: 20px; cursor: pointer; font-size: 14px; color: #555; transition: 0.2s;
}

/* Nút đang chọn (Active) */
.filter-left button.active { 
  background: #ff6b35; color: white; border-color: #ff6b35; font-weight: 600;
}

.filter-left button:hover:not(.active) { background: #f9f9f9; }

/* Grid System */
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 20px; }

/* Product Card */
.product-card { 
  background: white; border-radius: 8px; overflow: hidden; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.06); transition: 0.3s; cursor: pointer; 
  position: relative; display: flex; flex-direction: column; 
}
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); }

.card-img { position: relative; width: 100%; padding-top: 100%; overflow: hidden; }
.card-img img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; }

.sale-badge { 
  position: absolute; top: 10px; right: 10px; 
  background: #ffe100; color: #d32f2f; 
  padding: 3px 8px; font-size: 12px; font-weight: bold; 
  border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); 
}

.overlay { 
  position: absolute; bottom: 0; left: 0; right: 0; 
  background: rgba(255,255,255,0.95); padding: 15px; 
  transform: translateY(100%); transition: 0.3s; 
}
.product-card:hover .overlay { transform: translateY(0); }

.btn-quick-add { 
  width: 100%; background: #ff6b35; color: white; border: none; 
  padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer; 
}
.btn-quick-add:hover { background: #e65a2d; }

/* Info */
.card-info { padding: 15px; flex: 1; display: flex; flex-direction: column; }
.product-title { 
  font-size: 15px; font-weight: 600; margin: 0 0 10px; color: #222; 
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 42px; 
}

.price-row { margin-top: auto; display: flex; align-items: flex-end; }
.sale-wrapper { display: flex; flex-direction: column; }
.old-price { font-size: 13px; color: #999; text-decoration: line-through; }
.new-price { color: #ff6b35; font-weight: bold; font-size: 17px; }

.category-tag { font-size: 12px; color: #888; margin-top: 8px; }

.empty-state { text-align: center; padding: 50px; color: #888; font-style: italic; }

/* Load More */
.load-more-container { text-align: center; margin-top: 40px; margin-bottom: 20px; }
.btn-load-more {
  background: white; border: 1px solid #ff6b35; color: #ff6b35;
  padding: 10px 30px; border-radius: 20px; font-weight: bold; cursor: pointer; transition: 0.3s;
}
.btn-load-more:hover { background: #ff6b35; color: white; transform: translateY(-2px); box-shadow: 0 4px 10px rgba(255, 107, 53, 0.3); }
</style>