package com.example.backendquanaothethao.controller;

import com.example.backendquanaothethao.model.Category;
import com.example.backendquanaothethao.model.Product;
import com.example.backendquanaothethao.repository.CategoryRepository; // Import cái này
import com.example.backendquanaothethao.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @GetMapping
    public List<Product> getAll() {
        return productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return productRepo.findById(id).orElse(null);
    }

    @PostMapping
    public Product create(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam(value = "salePrice", required = false) Double salePrice,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        Product p = new Product();
        p.setName(name);
        p.setPrice(BigDecimal.valueOf(price));

        if (salePrice != null && salePrice > 0) {
            p.setSalePrice(BigDecimal.valueOf(salePrice));
        }

        p.setDescription(description);
        Category cat = categoryRepo.findById(categoryId).orElse(null);
        p.setCategory(cat);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveFile(imageFile);
            p.setImageUrl("http://localhost:8080/images/" + fileName);
        } else {
            p.setImageUrl("https://placehold.co/300?text=No+Image");
        }

        return productRepo.save(p);
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productRepo.deleteById(id);
    }

    @PutMapping("/{id}")
    public Product update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile // Ảnh là tùy chọn
    ) throws IOException {
        Product p = productRepo.findById(id).orElse(null);
        if (p == null) return null;
        p.setName(name);
        p.setPrice(BigDecimal.valueOf(price));
        p.setDescription(description);
        Category cat = categoryRepo.findById(categoryId).orElse(null);
        p.setCategory(cat);
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveFile(imageFile);
            p.setImageUrl("http://localhost:8080/images/" + fileName);
        }
        return productRepo.save(p);
    }
}