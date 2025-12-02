<template>
  <div class="success-page">
    <div class="card">
      <div class="icon">✅</div>
      <h2>Đặt hàng thành công!</h2>
      <p>Cảm ơn bạn đã mua sắm tại SportyStore.</p>
      <p>Chúng tôi sẽ sớm liên hệ để giao hàng.</p>
      
      <div class="actions">
        <router-link to="/user" class="btn-home">Về trang chủ</router-link>
        <router-link to="/user/orders" class="btn-order">Xem đơn hàng</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.success-page { display: flex; justify-content: center; padding-top: 50px; }
.card { text-align: center; background: white; padding: 40px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); max-width: 500px; width: 100%; }
.icon { font-size: 60px; margin-bottom: 20px; }
h2 { color: #28a745; margin-bottom: 15px; }
p { color: #666; margin-bottom: 10px; }
.actions { margin-top: 30px; display: flex; justify-content: center; gap: 15px; }
.btn-home { padding: 10px 20px; background: #333; color: white; text-decoration: none; border-radius: 4px; }
.btn-order { padding: 10px 20px; background: #ff6b35; color: white; text-decoration: none; border-radius: 4px; }
</style>