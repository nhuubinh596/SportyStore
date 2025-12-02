<template>
  <div class="wishlist-page">
    <h2 class="page-title">Sản phẩm yêu thích ❤️</h2>

    <div v-if="loading" class="text-center py-5">Đang tải...</div>

    <div v-else-if="products.length === 0" class="empty-state">
      <p>Bạn chưa yêu thích sản phẩm nào.</p>
      <router-link to="/user" class="btn-shop">Dạo một vòng xem sao</router-link>
    </div>

    <div v-else class="product-grid">
      <div class="product-card" v-for="p in products" :key="p.id">
        <div class="card-img" @click="$router.push(`/user/product/${p.id}`)">
          <img :src="p.imageUrl || 'https://placehold.co/300?text=No+Image'" />
          <button class="btn-remove" @click.stop="removeWishlist(p)" title="Bỏ thích">×</button>
        </div>

        <div class="card-info">
          <h3 class="product-title" @click="$router.push(`/user/product/${p.id}`)">{{ p.name }}</h3>
          <div class="price-row">
            <span class="price">{{ formatPrice(p.salePrice || p.price) }}</span>
          </div>
          <button class="btn-add-cart" @click="addToCart(p)">Thêm vào giỏ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import API from '@/api';

const products = ref([]);
const loading = ref(false);
const currentUser = JSON.parse(localStorage.getItem('currentUser'));

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);

async function loadWishlist() {
  if (!currentUser) return;
  loading.value = true;
  try {
    const res = await API.get(`/public/wishlist?username=${currentUser.username}`);
    products.value = res.data || res;
  } catch (e) { console.error(e); } 
  finally { loading.value = false; }
}

async function removeWishlist(p) {
  if(!confirm("Bỏ thích sản phẩm này?")) return;
  try {
    // Gọi lại API toggle để bỏ like
    await API.post('/public/wishlist/toggle', {
      username: currentUser.username,
      productId: p.id
    });
    // Xóa khỏi danh sách hiển thị luôn cho nhanh (khỏi load lại API)
    products.value = products.value.filter(item => item.id !== p.id);
  } catch(e) { alert("Lỗi: " + e.message); }
}

function addToCart(p) {
  let cart = JSON.parse(localStorage.getItem('cart') || '[]');
  const exist = cart.find(x => x.id === p.id);
  const price = (p.salePrice && p.salePrice < p.price) ? p.salePrice : p.price;
  
  if(exist) { exist.qty++; exist.price = price; }
  else cart.push({ ...p, price: price, qty: 1 });

  localStorage.setItem('cart', JSON.stringify(cart));
  window.dispatchEvent(new Event('cart-updated'));
  alert("Đã thêm vào giỏ!");
}

onMounted(loadWishlist);
</script>

<style scoped>
.wishlist-page { max-width: 1200px; margin: 30px auto; padding: 0 20px; color: #333; }
.page-title { font-size: 24px; font-weight: bold; margin-bottom: 20px; }

.empty-state { text-align: center; padding: 50px; background: white; border-radius: 8px; color: #666; }
.btn-shop { display: inline-block; margin-top: 15px; color: #ff6b35; font-weight: bold; text-decoration: none; border: 1px solid #ff6b35; padding: 8px 20px; border-radius: 4px;}

/* Grid giống UserHome */
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; }
.product-card { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.05); transition: 0.3s; }
.product-card:hover { transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0,0,0,0.1); }

.card-img { position: relative; height: 220px; overflow: hidden; cursor: pointer; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }

.btn-remove { 
  position: absolute; top: 10px; right: 10px; 
  width: 30px; height: 30px; border-radius: 50%; 
  background: rgba(255,255,255,0.9); border: none; 
  color: #ff0000; font-size: 20px; cursor: pointer; line-height: 1;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}
.btn-remove:hover { background: #ff0000; color: white; }

.card-info { padding: 12px; }
.product-title { font-size: 15px; margin: 0 0 5px; font-weight: 600; cursor: pointer; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;}
.price { color: #ff6b35; font-weight: bold; font-size: 16px; }
.btn-add-cart { width: 100%; margin-top: 10px; padding: 8px; border: 1px solid #333; background: white; border-radius: 4px; cursor: pointer; transition: 0.2s;}
.btn-add-cart:hover { background: #333; color: white; }
</style>