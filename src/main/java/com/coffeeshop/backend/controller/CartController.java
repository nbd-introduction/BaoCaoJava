package com.coffeeshop.backend.controller;

import com.coffeeshop.backend.dto.CartItem;
import com.coffeeshop.backend.entity.*;
import com.coffeeshop.backend.repository.OrderRepository;
import com.coffeeshop.backend.repository.UserRepository;
import com.coffeeshop.backend.repository.VoucherUsageRepository;
import com.coffeeshop.backend.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ProductService productService;
    private final BranchService branchService;
    private final VoucherService voucherService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final VoucherUsageRepository voucherUsageRepository;

    // =================== XEM GIỎ HÀNG ===================
    @GetMapping
    public String cart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        BigDecimal total = cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "user/cart";
    }

    // =================== THÊM VÀO GIỎ ===================
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productSizeId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {
        List<CartItem> cart = getCart(session);
        productService.findAll().stream()
                .flatMap(p -> p.getSizes() != null ? p.getSizes().stream() : java.util.stream.Stream.empty())
                .filter(ps -> ps.getId().equals(productSizeId))
                .findFirst()
                .ifPresent(ps -> {
                    Optional<CartItem> existing = cart.stream()
                            .filter(ci -> ci.getProductSizeId().equals(productSizeId))
                            .findFirst();
                    if (existing.isPresent()) {
                        existing.get().setQuantity(existing.get().getQuantity() + quantity);
                    } else {
                        cart.add(CartItem.builder()
                                .productSizeId(ps.getId())
                                .productId(ps.getProduct().getId())
                                .productName(ps.getProduct().getName())
                                .productImage(ps.getProduct().getImageUrl())
                                .size(ps.getSize().name())
                                .price(ps.getPrice())
                                .quantity(quantity)
                                .build());
                    }
                });
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // =================== XÓA KHỎI GIỎ ===================
    @PostMapping("/remove/{index}")
    public String removeFromCart(@PathVariable int index, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (index >= 0 && index < cart.size()) cart.remove(index);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // =================== CẬP NHẬT SỐ LƯỢNG ===================
    @PostMapping("/update/{index}")
    public String updateQuantity(@PathVariable int index,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (index >= 0 && index < cart.size()) {
            if (quantity <= 0) cart.remove(index);
            else cart.get(index).setQuantity(quantity);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // =================== TRANG CHECKOUT ===================
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, Authentication auth) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        BigDecimal subtotal = cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        // Tính tổng sau giảm giá
        BigDecimal discountAmount = (BigDecimal) session.getAttribute("discountAmount");
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discountAmount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        model.addAttribute("branches", branchService.findActive());
        model.addAttribute("appliedVoucher", session.getAttribute("appliedVoucher"));
        return "user/checkout";
    }

    // =================== ÁP DỤNG VOUCHER ===================
    @PostMapping("/apply-voucher")
    public String applyVoucher(@RequestParam String voucherCode,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Authentication auth) {
        if (voucherCode == null || voucherCode.isBlank()) {
            redirectAttributes.addFlashAttribute("voucherError", "Vui lòng nhập mã voucher!");
            return "redirect:/cart/checkout";
        }

        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        List<CartItem> cart = getCart(session);
        BigDecimal subtotal = cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<Voucher> voucherOpt = voucherService.findAll().stream()
                .filter(v -> v.getCode().equalsIgnoreCase(voucherCode.trim()))
                .findFirst();

        if (voucherOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("voucherError", "❌ Mã voucher không tồn tại!");
            return "redirect:/cart/checkout";
        }

        Voucher v = voucherOpt.get();

        // Kiểm tra các điều kiện
        if (!Boolean.TRUE.equals(v.getIsActive())) {
            redirectAttributes.addFlashAttribute("voucherError", "❌ Voucher đã bị vô hiệu hóa!");
        } else if (v.getUsedCount() >= v.getMaxUses()) {
            redirectAttributes.addFlashAttribute("voucherError", "❌ Voucher đã hết lượt sử dụng!");
        } else if (v.getExpiresAt() != null && v.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("voucherError", "❌ Voucher đã hết hạn sử dụng!");
        } else if (subtotal.compareTo(v.getMinOrderValue()) < 0) {
            redirectAttributes.addFlashAttribute("voucherError",
                    "❌ Đơn hàng tối thiểu " + String.format("%,.0fđ", v.getMinOrderValue().doubleValue()) + " để dùng voucher này!");
        } else if (voucherUsageRepository.existsByVoucherIdAndUserId(v.getId(), user.getId())) {
            // ✅ Kiểm tra user đã dùng voucher này chưa
            redirectAttributes.addFlashAttribute("voucherError", "❌ Bạn đã sử dụng voucher này rồi!");
        } else {
            // Voucher hợp lệ
            BigDecimal discount;
            if (v.getDiscountType() == Voucher.DiscountType.PERCENT) {
                discount = subtotal.multiply(v.getDiscountValue()).divide(BigDecimal.valueOf(100));
            } else {
                discount = v.getDiscountValue();
            }
            if (discount.compareTo(subtotal) > 0) discount = subtotal;

            session.setAttribute("appliedVoucher", v.getCode());
            session.setAttribute("discountAmount", discount);

            String discountText = v.getDiscountType() == Voucher.DiscountType.PERCENT
                    ? v.getDiscountValue().intValue() + "%"
                    : String.format("%,.0fđ", v.getDiscountValue().doubleValue());
            redirectAttributes.addFlashAttribute("voucherSuccess",
                    "✅ Áp dụng thành công! Giảm " + discountText);
        }
        return "redirect:/cart/checkout";
    }

    // =================== XÓA VOUCHER ===================
    @PostMapping("/remove-voucher")
    public String removeVoucher(HttpSession session) {
        session.removeAttribute("appliedVoucher");
        session.removeAttribute("discountAmount");
        return "redirect:/cart/checkout";
    }

    // =================== ĐẶT HÀNG ===================
    @PostMapping("/checkout")
    public String placeOrder(@RequestParam Integer branchId,
                             @RequestParam(defaultValue = "COD") String paymentMethod,
                             @RequestParam(required = false) String note,
                             HttpSession session,
                             Authentication auth) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Branch branch = branchService.findById(branchId).orElseThrow();

        BigDecimal subtotal = cart.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher appliedVoucher = null;

        // ✅ Lấy voucher từ session
        String savedVoucherCode = (String) session.getAttribute("appliedVoucher");
        if (savedVoucherCode != null) {
            Optional<Voucher> voucherOpt = voucherService.findAll().stream()
                    .filter(v -> v.getCode().equalsIgnoreCase(savedVoucherCode)
                            && Boolean.TRUE.equals(v.getIsActive())
                            && v.getUsedCount() < v.getMaxUses()
                            && !voucherUsageRepository.existsByVoucherIdAndUserId(v.getId(), user.getId()))
                    .findFirst();

            if (voucherOpt.isPresent()) {
                appliedVoucher = voucherOpt.get();
                BigDecimal d = (BigDecimal) session.getAttribute("discountAmount");
                discountAmount = d != null ? d : BigDecimal.ZERO;

                // ✅ Tăng usedCount toàn cục
                appliedVoucher.setUsedCount(appliedVoucher.getUsedCount() + 1);
                if (appliedVoucher.getUsedCount() >= appliedVoucher.getMaxUses()) {
                    appliedVoucher.setIsActive(false);
                }
                voucherService.save(appliedVoucher);

                // ✅ Ghi lại user đã dùng voucher này
                VoucherUsage usage = VoucherUsage.builder()
                        .voucher(appliedVoucher)
                        .user(user)
                        .build();
                voucherUsageRepository.save(usage);
            }
        }

        BigDecimal total = subtotal.subtract(discountAmount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        String orderCode = "CS-" + System.currentTimeMillis() % 1000000;

        Orders order = Orders.builder()
                .user(user)
                .branch(branch)
                .voucher(appliedVoucher)
                .orderCode(orderCode)
                .status(Orders.Status.PENDING)
                .paymentMethod(Orders.PaymentMethod.valueOf(paymentMethod))
                .paymentStatus(Orders.PaymentStatus.UNPAID)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .total(total)
                .pointsEarned((int)(total.doubleValue() / 1000))
                .note(note)
                .build();

        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cart) {
            productService.findAll().stream()
                    .flatMap(p -> p.getSizes() != null ? p.getSizes().stream() : java.util.stream.Stream.empty())
                    .filter(ps -> ps.getId().equals(ci.getProductSizeId()))
                    .findFirst()
                    .ifPresent(ps -> items.add(OrderItem.builder()
                            .order(order)
                            .productSize(ps)
                            .productName(ci.getProductName())
                            .size(ProductSize.Size.valueOf(ci.getSize()))
                            .price(ci.getPrice())
                            .quantity(ci.getQuantity())
                            .subtotal(ci.getSubtotal())
                            .build()));
        }
        order.setItems(items);

        // Cộng điểm
        int pts = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        user.setLoyaltyPoints(pts + order.getPointsEarned());
        userRepository.save(user);

        orderRepository.save(order);

        session.removeAttribute("cart");
        session.removeAttribute("appliedVoucher");
        session.removeAttribute("discountAmount");
        session.setAttribute("lastOrderCode", orderCode);

        return "redirect:/cart/success";
    }

    // =================== SUCCESS ===================
    @GetMapping("/success")
    public String orderSuccess(HttpSession session, Model model) {
        model.addAttribute("orderCode", session.getAttribute("lastOrderCode"));
        session.removeAttribute("lastOrderCode");
        return "user/order-success";
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) { cart = new ArrayList<>(); session.setAttribute("cart", cart); }
        return cart;
    }
}
