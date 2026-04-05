package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.entity.Product;
import com.coffeeshop.backend.entity.ProductSize;
import com.coffeeshop.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    @GetMapping
    public String list(Model model) {
        List<Product> products = productService.findAll();
        long availableCount = products.stream().filter(p -> Boolean.TRUE.equals(p.getIsAvailable())).count();
        long unavailableCount = products.stream().filter(p -> !Boolean.TRUE.equals(p.getIsAvailable())).count();

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.findAllCategories());
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("unavailableCount", unavailableCount);
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
                       @RequestParam(required = false) List<String> sizeNames,
                       @RequestParam(required = false) List<BigDecimal> sizePrices) {
        Product saved = productService.save(product);
        if (sizeNames != null && sizePrices != null) {
            List<ProductSize> sizes = new ArrayList<>();
            for (int i = 0; i < sizeNames.size(); i++) {
                if (sizePrices.get(i) != null) {
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
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
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