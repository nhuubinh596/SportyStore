package com.example.backendquanaothethao.repository;

import com.example.backendquanaothethao.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // Kiểm tra xem user đã like sản phẩm này chưa
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    // Lấy danh sách yêu thích của user (để hiển thị trang Wishlist sau này)
    List<Wishlist> findByUserId(Long userId);
}