<template>
  <div class="add-product-page">
    <div class="form-card">
      <h2 class="title">Sửa sản phẩm</h2>
      <form @submit.prevent="updateProduct">
        <div class="form-group">
          <label>Tên sản phẩm</label>
          <input v-model="form.name" class="input-control" required />
        </div>

        <div class="row-group">
          <div class="form-group half">
            <label>Giá gốc</label>
            <input type="number" v-model="form.price" class="input-control" required />
          </div>
          <div class="form-group half">
            <label>Giá khuyến mãi</label>
            <input type="number" v-model="form.salePrice" class="input-control" placeholder="Bỏ trống nếu không giảm" />
          </div>
        </div>

        <div class="form-group">
          <label>Danh mục</label>
          <select v-model="form.categoryId" class="input-control" required>
            <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
          </select>
        </div>

        <div class="form-group">
          <label>Hình ảnh</label>
          <input type="file" @change="handleFile" class="input-control" accept="image/*" />
          <div class="preview" v-if="preview || form.imageUrl">
            <img :src="preview || form.imageUrl" />
          </div>
        </div>

        <div class="form-group">
          <label>Mô tả</label>
          <textarea v-model="form.description" class="input-control" rows="3"></textarea>
        </div>

        <div class="btn-actions">
          <button type="button" class="btn-cancel" @click="$router.go(-1)">Hủy</button>
          <button type="submit" class="btn-save">Cập nhật</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import API from '@/api';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const categories = ref([]);
const form = ref({ name: '', price: 0, salePrice: '', categoryId: null, description: '', imageUrl: '' });
const file = ref(null);
const preview = ref(null);

onMounted(async () => {
  const catRes = await API.get('/admin/categories');
  categories.value = catRes.data || catRes;

  const prodRes = await API.get(`/admin/products/${id}`);
  const p = prodRes.data || prodRes;
  
  form.value = {
    name: p.name,
    price: p.price,
    salePrice: p.salePrice,
    categoryId: p.categoryId || (p.category ? p.category.id : null),
    description: p.description,
    imageUrl: p.imageUrl
  };
});

function handleFile(e) {
  file.value = e.target.files[0];
  if(file.value) preview.value = URL.createObjectURL(file.value);
}

async function updateProduct() {
  const fd = new FormData();
  fd.append('name', form.value.name);
  fd.append('price', form.value.price);
  if(form.value.salePrice) fd.append('salePrice', form.value.salePrice);
  else fd.append('salePrice', 0); // Reset sale nếu xóa trắng

  fd.append('categoryId', form.value.categoryId);
  fd.append('description', form.value.description);
  if(file.value) fd.append('image', file.value);

  try {
    const token = localStorage.getItem('authToken');
    await axios.put(`http://localhost:8080/api/admin/products/${id}`, fd, {
      headers: { 'Content-Type': 'multipart/form-data', 'Authorization': `Bearer ${token}` }
    });
    alert("Cập nhật thành công!");
    router.push('/admin/products');
  } catch (e) { alert("Lỗi: " + e.message); }
}
</script>

<style scoped>
/* Style giống AddProduct */
.add-product-page { display: flex; justify-content: center; padding: 20px; }
.form-card { background: white; padding: 30px; border-radius: 8px; width: 100%; max-width: 600px; }
.input-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; margin-top: 5px; }
.row-group { display: flex; gap: 15px; }
.half { flex: 1; }
.form-group { margin-bottom: 15px; }
.preview img { max-height: 150px; margin-top: 10px; }
.btn-save { background: #ff6b35; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; float: right; }
.btn-cancel { background: #eee; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; float: right; margin-right: 10px; }
</style>