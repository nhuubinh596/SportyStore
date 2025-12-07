package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.model.*;
import com.example.backendquanaothethao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:5173")
public class InteractionController {

    @Autowired private ReviewRepository reviewRepo;
    @Autowired private WishlistRepository wishlistRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ProductRepository productRepo;

    @GetMapping("/reviews/{productId}")
    public List<Review> getReviews(@PathVariable Long productId) {
        return reviewRepo.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            Long productId = Long.valueOf(payload.get("productId").toString());
            int rating = Integer.parseInt(payload.get("rating").toString());
            String comment = (String) payload.get("comment");

            UserAccount user = userRepo.findByUsername(username).orElse(null);
            Product product = productRepo.findById(productId).orElse(null);

            if (user != null && product != null) {
                Review review = new Review();
                review.setUser(user);
                review.setProduct(product);
                review.setRating(rating);
                review.setComment(comment);
                review.setCreatedAt(LocalDateTime.now());

                reviewRepo.save(review);
                return ResponseEntity.ok("Đánh giá thành công!");
            }
            return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy user hoặc sản phẩm");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/wishlist")
    public List<Product> getMyWishlist(@RequestParam("username") String username) {
        UserAccount user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return List.of();
        return wishlistRepo.findByUserId(user.getId())
                .stream()
                .map(Wishlist::getProduct)
                .toList();
    }

    @PostMapping("/wishlist/toggle")
    public ResponseEntity<?> toggleWishlist(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Long productId = Long.valueOf(payload.get("productId").toString());

        UserAccount user = userRepo.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("Chưa đăng nhập");
        var exist = wishlistRepo.findByUserIdAndProductId(user.getId(), productId);

        if (exist.isPresent()) {
            wishlistRepo.delete(exist.get());
            return ResponseEntity.ok(Map.of("liked", false, "message", "Đã bỏ yêu thích"));
        } else {
            Wishlist w = new Wishlist();
            w.setUser(user);
            w.setProduct(productRepo.findById(productId).get());
            w.setCreatedAt(LocalDateTime.now());
            wishlistRepo.save(w); // Chưa like thì thêm (Like)
            return ResponseEntity.ok(Map.of("liked", true, "message", "Đã thêm vào yêu thích"));
        }
    }
}