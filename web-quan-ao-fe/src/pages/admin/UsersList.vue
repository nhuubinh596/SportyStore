<template>
  <div class="page-container">
    <div class="header-row">
      <h2>Quản lý Tài khoản</h2>
      <div class="user-count">Tổng: {{ users.length }} người dùng</div>
    </div>

    <div v-if="loading" class="text-center">Đang tải dữ liệu...</div>

    <div v-else class="table-responsive">
      <table class="custom-table">
        <thead>
          <tr>
            <th width="50">ID</th>
            <th width="60">Avatar</th>
            <th>Thông tin cá nhân</th>
            <th>Liên hệ</th>
            <th>Vai trò</th>
            <th width="100">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td>#{{ u.id }}</td>
            <td>
              <img :src="`https://ui-avatars.com/api/?name=${u.name}&background=random`" class="avatar-img" />
            </td>
            <td>
              <div class="user-name">{{ u.name || 'Chưa cập nhật' }}</div>
              <div class="user-username">@{{ u.username }}</div>
            </td>
            <td>
              <div class="contact-info">📧 {{ u.email }}</div>
              <div class="contact-info">📞 {{ u.phone || '---' }}</div>
            </td>
            <td>
              <span :class="getRoleClass(u)">
                {{ formatRole(u) }}
              </span>
            </td>
            <td>
              <button 
                v-if="currentUser && currentUser.username !== u.username"
                class="btn-icon delete" 
                @click="onDelete(u)" 
                title="Xóa người dùng"
              >
                🗑️
              </button>
              <span v-else class="text-muted" style="font-size: 12px;">(Bạn)</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import API from "@/api";

const users = ref([]);
const loading = ref(false);
const currentUser = ref(null);

try {
  currentUser.value = JSON.parse(localStorage.getItem('currentUser'));
} catch(e){}

async function loadData() {
  loading.value = true;
  try {
    const res = await API.get("/admin/users");
    users.value = res.data || res;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function formatRole(user) {
  const roles = user.roles || [];
  const roleNames = roles.map(r => r.name || r);
  
  if (roleNames.some(r => r.includes('ADMIN'))) return 'Quản trị viên';
  return 'Khách hàng';
}

function getRoleClass(user) {
  const roleName = formatRole(user);
  return roleName === 'Quản trị viên' ? 'badge-admin' : 'badge-user';
}

async function onDelete(user) {
  if (!confirm(`Bạn chắc chắn muốn xóa tài khoản "${user.username}"?\nCảnh báo: Dữ liệu đơn hàng của người này có thể bị ảnh hưởng.`)) return;
  
  try {
    await API.delete(`/admin/users/${user.id}`);
    alert("Đã xóa thành công!");
    loadData();
  } catch (e) {
    alert("Không thể xóa (User này đang có đơn hàng hoặc lỗi server)");
  }
}

onMounted(loadData);
</script>

<style scoped>
.page-container { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
h2 { color: #333; font-weight: bold; margin: 0; }
.user-count { background: #eef2f5; padding: 5px 10px; border-radius: 4px; font-size: 13px; font-weight: 600; color: #666; }

.custom-table { width: 100%; border-collapse: collapse; font-size: 14px; color: #333; }
.custom-table th { background: #f8f9fa; padding: 12px; text-align: left; border-bottom: 2px solid #dee2e6; color: #555; }
.custom-table td { padding: 12px; border-bottom: 1px solid #eee; vertical-align: middle; }

.avatar-img { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }

.user-name { font-weight: 600; color: #222; }
.user-username { font-size: 12px; color: #888; }
.contact-info { font-size: 13px; color: #555; margin-bottom: 2px; }

.badge-admin { background: #ffebee; color: #c62828; padding: 4px 8px; border-radius: 4px; font-weight: 600; font-size: 12px; border: 1px solid #ffcdd2; }
.badge-user { background: #e3f2fd; color: #1565c0; padding: 4px 8px; border-radius: 4px; font-weight: 600; font-size: 12px; border: 1px solid #bbdefb; }

.btn-icon { border: none; background: none; font-size: 18px; cursor: pointer; transition: 0.2s; }
.delete:hover { color: red; transform: scale(1.2); }
.text-muted { color: #aaa; font-style: italic; }
</style>