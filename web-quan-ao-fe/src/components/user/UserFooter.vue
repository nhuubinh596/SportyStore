<template>
  <footer class="footer-wrapper">
    <div class="footer-content">
      <div class="footer-col">
        <h3 class="footer-brand">SportyStore</h3>
        <p class="desc">
          Chuyên cung cấp quần áo, giày dép thể thao chính hãng, chất lượng cao với giá cả hợp lý nhất thị trường.
        </p>
        <div class="contact-info">
          <p>📍 123 Đường Cầu Giấy, Hà Nội</p>
          <p>📞 0912.345.678</p>
          <p>📧 support@sportystore.vn</p>
        </div>
      </div>

      <div class="footer-col">
        <h4>Về chúng tôi</h4>
        <ul>
          <li><a href="#">Giới thiệu</a></li>
          <li><a href="#">Tuyển dụng</a></li>
          <li><a href="#">Điều khoản sử dụng</a></li>
          <li><a href="#">Chính sách bảo mật</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Hỗ trợ khách hàng</h4>
        <ul>
          <li><a href="#">Hướng dẫn mua hàng</a></li>
          <li><a href="#">Chính sách đổi trả</a></li>
          <li><a href="#">Phương thức thanh toán</a></li>
          <li><a href="#">Vận chuyển & Giao nhận</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Kết nối với chúng tôi</h4>
        <div class="social-links">
          <a href="#" class="social-btn facebook">F</a>
          <a href="#" class="social-btn instagram">I</a>
          <a href="#" class="social-btn youtube">Y</a>
        </div>
        <div class="newsletter">
          <input placeholder="Email của bạn..." />
          <button>→</button>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      © 2025 SportyStore - All rights reserved. Designed by You.
    </div>
  </footer>
</template>

<style scoped>
.footer-wrapper {
  background-color: #222; 
  color: #bbb; 
  padding-top: 50px;
  margin-top: auto; 
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  padding: 0 30px 40px;
}

.footer-brand { color: #ff6b35; font-size: 24px; font-weight: bold; margin-bottom: 15px; }
.desc { line-height: 1.6; margin-bottom: 20px; font-size: 14px; }
.contact-info p { margin: 8px 0; font-size: 14px; }

h4 { color: white; margin-bottom: 20px; font-size: 16px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }

ul { list-style: none; padding: 0; }
ul li { margin-bottom: 10px; }
ul li a { color: #bbb; text-decoration: none; transition: 0.2s; font-size: 14px; }
ul li a:hover { color: #ff6b35; padding-left: 5px; }

.social-links { display: flex; gap: 10px; margin-bottom: 20px; }
.social-btn {
  width: 36px; height: 36px; background: #333; color: white;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%; text-decoration: none; font-weight: bold; transition: 0.3s;
}
.social-btn:hover { background: #ff6b35; transform: translateY(-3px); }

.newsletter { display: flex; }
.newsletter input { flex: 1; padding: 10px; border: none; outline: none; border-radius: 4px 0 0 4px; background: #333; color: white; }
.newsletter button { padding: 0 15px; background: #ff6b35; border: none; color: white; border-radius: 0 4px 4px 0; cursor: pointer; }

.footer-bottom {
  background: #111;
  text-align: center;
  padding: 20px;
  font-size: 13px;
  border-top: 1px solid #333;
}
</style>