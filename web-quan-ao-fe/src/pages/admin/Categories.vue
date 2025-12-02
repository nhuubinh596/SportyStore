<template>
  <div class="page-container">
    <h2>Quản lý Danh mục</h2>

    <div class="form-card">
      <div class="input-group">
        <input 
          v-model="form.name" 
          placeholder="Nhập tên danh mục (Ví dụ: Áo khoác, Giày...)" 
          @keyup.enter="handleSubmit"
        />
        <button 
          :class="isEditing ? 'btn-update' : 'btn-add'" 
          @click="handleSubmit"
        >
          {{ isEditing ? 'Cập nhật' : '+ Thêm mới' }}
        </button>
        
        <button v-if="isEditing" class="btn-cancel" @click="cancelEdit">Hủy</button>
      </div>
    </div>

    <div v-if="loading" class="text-center">Đang tải...</div>

    <div v-else class="table-responsive">
      <table class="custom-table">
        <thead>
          <tr>
            <th width="80">ID</th>
            <th>Tên danh mục</th>
            <th width="150">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in categories" :key="c.id">
            <td>#{{ c.id }}</td>
            <td class="fw-bold">{{ c.name }}</td>
            <td>
              <button class="btn-icon edit" @click="startEdit(c)" title="Sửa">✏️</button>
              <button class="btn-icon delete" @click="onDelete(c.id)" title="Xóa">🗑️</button>
            </td>
          </tr>
          <tr v-if="categories.length === 0">
            <td colspan="3" class="text-center">Chưa có danh mục nào.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import API from "@/api";

const categories = ref([]);
const loading = ref(false);

// State cho form
const form = ref({ id: null, name: "" });
const isEditing = ref(false);

async function load() {
  loading.value = true;
  try {
    const res = await API.get("/admin/categories");
    categories.value = res.data || res;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

// Xử lý nút Submit (Tự động hiểu là Thêm hay Sửa)
async function handleSubmit() {
  if (!form.value.name.trim()) return alert("Vui lòng nhập tên!");

  try {
    if (isEditing.value) {
      // Logic Sửa
      await API.put(`/admin/categories/${form.value.id}`, { name: form.value.name });
      alert("Cập nhật thành công!");
    } else {
      // Logic Thêm
      await API.post("/admin/categories", { name: form.value.name });
      alert("Thêm mới thành công!");
    }
    
    // Reset form và tải lại bảng
    cancelEdit();
    load();
  } catch (e) {
    alert("Lỗi: " + e.message);
  }
}

// Chuyển sang chế độ sửa
function startEdit(category) {
  form.value = { ...category }; // Copy dữ liệu vào form
  isEditing.value = true;
}

// Hủy chế độ sửa
function cancelEdit() {
  form.value = { id: null, name: "" };
  isEditing.value = false;
}

async function onDelete(id) {
  if (!confirm("Xóa danh mục này? Lưu ý: Các sản phẩm thuộc danh mục này sẽ bị mất liên kết!")) return;
  try {
    await API.delete(`/admin/categories/${id}`);
    load();
  } catch (e) {
    alert("Không thể xóa (Có lỗi server hoặc ràng buộc dữ liệu)");
  }
}

onMounted(load);
</script>

<style scoped>
.page-container { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
h2 { color: #333; font-weight: bold; margin-bottom: 20px; }

/* Form Style */
.form-card { background: #f9fafb; padding: 20px; border-radius: 8px; margin-bottom: 25px; border: 1px solid #eee; }
.input-group { display: flex; gap: 10px; }
.input-group input { 
  flex: 1; padding: 10px 15px; border: 1px solid #ccc; border-radius: 6px; outline: none; font-size: 14px;
}
.input-group input:focus { border-color: #007bff; }

.btn-add { background: #28a745; color: white; border: none; padding: 0 25px; border-radius: 6px; font-weight: bold; cursor: pointer; }
.btn-add:hover { background: #218838; }

.btn-update { background: #ffc107; color: #333; border: none; padding: 0 25px; border-radius: 6px; font-weight: bold; cursor: pointer; }
.btn-update:hover { background: #e0a800; }

.btn-cancel { background: #6c757d; color: white; border: none; padding: 0 15px; border-radius: 6px; cursor: pointer; }

/* Table Style */
.custom-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #333; }
.custom-table th { background: #f8f9fa; padding: 12px; text-align: left; border-bottom: 2px solid #dee2e6; color: #555; }
.custom-table td { padding: 12px; border-bottom: 1px solid #eee; vertical-align: middle; }
.fw-bold { font-weight: 600; }
.text-center { text-align: center; }

.btn-icon { border: none; background: none; font-size: 18px; cursor: pointer; margin-right: 10px; transition: 0.2s; }
.btn-icon:hover { transform: scale(1.2); }
.edit { color: #007bff; }
.delete { color: #dc3545; }
</style>