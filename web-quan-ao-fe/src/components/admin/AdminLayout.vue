<template>
  <div class="admin-layout">
    <div class="layout-header">
      <AdminHeader @toggle="toggleSidebar" />
    </div>

    <div class="layout-body">
      <aside class="layout-sidebar" :class="{ 'collapsed': isCollapsed }">
        <AdminSidebar />
      </aside>

      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import AdminHeader from './AdminHeader.vue';
import AdminSidebar from './AdminSidebar.vue';

const isCollapsed = ref(false);
function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value;
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f3f4f6;
  overflow: hidden;
}

.layout-header {
  height: 64px;
  flex-shrink: 0;
  z-index: 100;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  background: white;
}

.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.layout-sidebar {
  width: 250px;
  background: white;
  border-right: 1px solid #e5e7eb;
  transition: width 0.3s;
  overflow-y: auto;
}

.layout-sidebar.collapsed {
  width: 0;
  overflow: hidden;
}

.layout-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto; 
  color: #1f2937; 
}
</style>