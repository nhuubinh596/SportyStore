<template>
  <div class="product-detail-container">
    <div v-if="loading" class="text-center py-5">Đang tải...</div>

    <div v-else-if="product" class="detail-wrapper">
      <div class="gallery-col">
        <div class="main-image">
          <img :src="product.imageUrl || 'https://placehold.co/500?text=No+Image'" />
          <span v-if="isSale" class="sale-tag">-{{ salePercent }}%</span>
        </div>
      </div>

      <div class="info-col">
        <div class="title-row">
          <h1 class="product-title">{{ product.name }}</h1>
          <button class="btn-wishlist" @click="toggleWishlist" :class="{ 'active': isLiked }">
            {{ isLiked ? '❤️ Đã thích' : '🤍 Yêu thích' }}
          </button>
        </div>
        
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

    <div class="review-section" v-if="product">
      <h3>Đánh giá sản phẩm ({{ reviews.length }})</h3>
      
      <div class="review-form" v-if="currentUser">
        <div class="star-rating">
          <span>Chọn sao: </span>
          <select v-model="myRating">
            <option value="5">⭐⭐⭐⭐⭐ (Tuyệt vời)</option>
            <option value="4">⭐⭐⭐⭐ (Tốt)</option>
            <option value="3">⭐⭐⭐ (Bình thường)</option>
            <option value="2">⭐⭐ (Tệ)</option>
            <option value="1">⭐ (Rất tệ)</option>
          </select>
        </div>
        <textarea v-model="myComment" placeholder="Viết cảm nhận của bạn về sản phẩm..."></textarea>
        <button @click="submitReview">Gửi đánh giá</button>
      </div>
      <div v-else class="login-prompt">
        <router-link to="/login">Đăng nhập</router-link> để viết đánh giá.
      </div>

      <div class="review-list">
        <div v-for="r in reviews" :key="r.id" class="review-item">
          <div class="review-header">
            <strong class="user-name">{{ r.user?.username || 'Ẩn danh' }}</strong>
            <span class="rating-stars">{{ '⭐'.repeat(r.rating) }}</span>
            <span class="review-date">{{ formatDate(r.createdAt) }}</span>
          </div>
          <p class="review-content">{{ r.comment }}</p>
        </div>
        <div v-if="reviews.length === 0" class="no-reviews">Chưa có đánh giá nào.</div>
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
const currentUser = ref(null);

// Review & Wishlist State
const reviews = ref([]);
const isLiked = ref(false);
const myRating = ref(5);
const myComment = ref('');

const isSale = computed(() => product.value && product.value.salePrice && product.value.salePrice < product.value.price);
const salePercent = computed(() => isSale.value ? Math.round((1 - product.value.salePrice / product.value.price) * 100) : 0);

const formatPrice = (v) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
const formatDate = (d) => new Date(d).toLocaleDateString('vi-VN');

async function loadData() {
  loading.value = true;
  try {
    // 1. Load Product
    const res = await API.get(`/public/products/${route.params.id}`);
    product.value = res.data || res;
    
    // 2. Load Reviews
    const resRev = await API.get(`/public/reviews/${route.params.id}`);
    reviews.value = resRev.data || resRev;

    // 3. Check Wishlist (Nếu đã login)
    // (Phần này làm đơn giản: FE tự quản lý trạng thái click, chưa cần gọi API check lúc load để code đỡ phức tạp)
  } catch (e) { console.error(e); } 
  finally { loading.value = false; }
}

// Hàm thả tim (Đã Fix lỗi)
async function toggleWishlist() {
  // 1. Kiểm tra đăng nhập
  if (!currentUser.value) {
    if(confirm("Bạn cần đăng nhập để lưu sản phẩm yêu thích. Đi đến trang đăng nhập?")) {
      router.push('/login');
    }
    return;
  }

  try {
    // 2. Gọi API
    const res = await API.post('/public/wishlist/toggle', {
      username: currentUser.value.username,
      productId: product.value.id
    });

    // 3. FIX LỖI QUAN TRỌNG: Xử lý response linh hoạt
    // Nếu API trả về full response (có .data) thì lấy .data
    // Nếu API đã lọc sẵn dữ liệu thì lấy chính nó
    const data = res.data || res; 

    // 4. Cập nhật trạng thái
    if (data) {
      isLiked.value = data.liked; // Cập nhật trái tim (Đỏ/Trắng)
      
      // Dùng Toast hoặc Alert nhỏ thôi cho đỡ phiền
      console.log(data.message); 
    }

  } catch(e) {
    console.error("Lỗi Wishlist:", e);
    // Chỉ hiện alert nếu lỗi thực sự nghiêm trọng
    // alert("Lỗi: " + (e.response?.data || e.message));
  }
}

// Gửi đánh giá
async function submitReview() {
  if (!myComment.value) return alert("Vui lòng viết nội dung!");
  try {
    await API.post('/public/reviews', {
      username: currentUser.value.username,
      productId: product.value.id,
      rating: myRating.value,
      comment: myComment.value
    });
    alert("Cảm ơn bạn đã đánh giá!");
    myComment.value = '';
    loadData(); // Reload để thấy review mới
  } catch(e) { alert("Lỗi: " + e.message); }
}

// Cart functions (Giữ nguyên)
function addToCart() {
  let cart = JSON.parse(localStorage.getItem('cart') || '[]');
  const item = product.value;
  const finalPrice = isSale.value ? item.salePrice : item.price;
  
  const exist = cart.find(x => x.id === item.id);
  if (exist) exist.qty += qty.value;
  else cart.push({ ...item, price: finalPrice, qty: qty.value });

  localStorage.setItem('cart', JSON.stringify(cart));
  window.dispatchEvent(new Event('cart-updated'));
  alert(`Đã thêm ${qty.value} sản phẩm vào giỏ!`);
}

function buyNow() {
  addToCart();
  router.push('/user/cart');
}

onMounted(() => {
  try { currentUser.value = JSON.parse(localStorage.getItem('currentUser')); } catch(e){}
  loadData();
});
</script>

<style scoped>
.product-detail-container { max-width: 1100px; margin: 30px auto; padding: 0 20px; color: #333; }
.detail-wrapper { display: grid; grid-template-columns: 2fr 3fr; gap: 40px; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }

.main-image { position: relative; border: 1px solid #eee; border-radius: 8px; overflow: hidden; }
.main-image img { width: 100%; display: block; }
.sale-tag { position: absolute; top: 10px; left: 10px; background: #ffeb3b; color: #d32f2f; padding: 5px 10px; font-weight: bold; border-radius: 4px; }

/* Title Row */
.title-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 15px; }
.product-title { font-size: 24px; margin: 0; flex: 1; }
.btn-wishlist { border: 1px solid #ddd; background: white; padding: 5px 10px; border-radius: 20px; cursor: pointer; transition: 0.2s; font-size: 14px; color: #555; }
.btn-wishlist.active { border-color: #e91e63; color: #e91e63; background: #fce4ec; }

.price-section { background: #fafafa; padding: 15px; margin-bottom: 20px; border-radius: 4px; }
.old-price { text-decoration: line-through; color: #999; margin-right: 10px; font-size: 16px; }
.current-price { color: #ff6b35; font-size: 28px; font-weight: bold; }

.meta-info { margin-bottom: 20px; }
.row-info { display: flex; margin-bottom: 10px; }
.row-info label { width: 100px; color: #666; font-weight: 600; }
.desc { white-space: pre-line; line-height: 1.5; margin: 0; }

.quantity-section { display: flex; align-items: center; margin-bottom: 30px; }
.qty-control { display: flex; border: 1px solid #ddd; border-radius: 4px; }
.qty-control button { width: 35px; height: 35px; border: none; background: white; cursor: pointer; }
.qty-control input { width: 50px; text-align: center; border: none; border-left: 1px solid #ddd; border-right: 1px solid #ddd; outline: none; }

.action-buttons { display: flex; gap: 15px; }
.btn-add-cart { background: #fff0e6; color: #ff6b35; border: 1px solid #ff6b35; padding: 12px 20px; font-weight: bold; border-radius: 4px; cursor: pointer; }
.btn-buy-now { background: #ff6b35; color: white; border: none; padding: 12px 40px; font-weight: bold; border-radius: 4px; cursor: pointer; }

/* REVIEW SECTION */
.review-section { margin-top: 30px; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.review-form { margin-bottom: 30px; background: #f9f9f9; padding: 20px; border-radius: 8px; }
.star-rating { margin-bottom: 10px; }
.star-rating select { padding: 5px; border-radius: 4px; border: 1px solid #ddd; }
.review-form textarea { width: 100%; height: 80px; padding: 10px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 10px; }
.review-form button { background: #333; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }

.review-item { border-bottom: 1px solid #eee; padding: 15px 0; }
.review-header { display: flex; gap: 10px; align-items: center; margin-bottom: 5px; }
.rating-stars { color: #ffc107; font-size: 12px; }
.review-date { font-size: 12px; color: #999; margin-left: auto; }
.no-reviews { text-align: center; color: #999; padding: 20px; font-style: italic; }
.login-prompt { text-align: center; padding: 20px; background: #f9f9f9; border-radius: 8px; }
.login-prompt a { color: #ff6b35; font-weight: bold; }
</style>