package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.service.FileUploadService;
import com.coffeeshop.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final FileUploadService fileUploadService;

    @GetMapping
    public String list(Model model) {
        List<Product> products = productService.findAll();
        long availableCount = products.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsAvailable())).count();
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("unavailableCount", products.size() - availableCount);
        return "admin/products";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", productService.findAllCategories());
        return "admin/product-form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id).orElseThrow());
        model.addAttribute("categories", productService.findAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam(required = false) MultipartFile imageFile,
                       @RequestParam(required = false) List<String> sizeNames,
                       @RequestParam(required = false) List<BigDecimal> sizePrices) {
        try {
            // ✅ Upload ảnh nếu có file mới
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xóa ảnh cũ nếu có
                if (product.getImageUrl() != null) {
                    fileUploadService.deleteFile(product.getImageUrl());
                }
                String imageUrl = fileUploadService.uploadImage(imageFile);
                product.setImageUrl(imageUrl);
            }

            Product saved = productService.save(product);

            // Lưu sizes
            if (sizeNames != null && sizePrices != null) {
                List<ProductSize> sizes = new ArrayList<>();
                for (int i = 0; i < sizeNames.size(); i++) {
                    if (i < sizePrices.size() && sizePrices.get(i) != null) {
                        try {
                            ProductSize ps = ProductSize.builder()
                                    .product(saved)
                                    .size(ProductSize.Size.valueOf(sizeNames.get(i)))
                                    .price(sizePrices.get(i))
                                    .stock(0)
                                    .build();
                            sizes.add(ps);
                        } catch (Exception ignored) {}
                    }
                }
                if (!sizes.isEmpty()) {
                    saved.setSizes(sizes);
                    productService.save(saved);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.findById(id).ifPresent(p -> {
            fileUploadService.deleteFile(p.getImageUrl());
            productService.deleteById(id);
        });
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        productService.findById(id).ifPresent(p -> {
            p.setIsAvailable(!Boolean.TRUE.equals(p.getIsAvailable()));
            productService.save(p);
        });
        return "redirect:/admin/products";
    }
}
