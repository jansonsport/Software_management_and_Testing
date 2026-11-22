
package onlineshopping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class DiscountService {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    public static class DiscountResult {
        public final BigDecimal originalSubtotal;
        public final BigDecimal quantityDiscount;
        public final BigDecimal accessoryDiscount;
        public final BigDecimal couponDiscount;
        public final BigDecimal finalTotal;

        public DiscountResult(
                BigDecimal originalSubtotal,
                BigDecimal quantityDiscount,
                BigDecimal accessoryDiscount,
                BigDecimal couponDiscount,
                BigDecimal finalTotal) {
            this.originalSubtotal = originalSubtotal;
            this.quantityDiscount = quantityDiscount;
            this.accessoryDiscount = accessoryDiscount;
            this.couponDiscount = couponDiscount;
            this.finalTotal = finalTotal;
        }
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }


    public DiscountResult calculateDiscount(
            List<CartItem> items,
            CustomerType customerType,
            String couponCode) {

        validateInputs(items, customerType);

        BigDecimal originalSubtotal = money(calculateSubtotal(items));

        // Apply customer-specific quantity discounts first.
        BigDecimal quantityDiscount =
                money(calculateQuantityDiscount(items, customerType));

        BigDecimal subtotalAfterQuantityDiscount = money(
                originalSubtotal.subtract(quantityDiscount).max(ZERO));

        // Apply the accessory discount to qualifying accessories
        BigDecimal accessoryDiscount = calculateAccessoryDiscount(
                items, customerType);

        BigDecimal productTotal = money(
                subtotalAfterQuantityDiscount
                        .subtract(accessoryDiscount)
                        .max(ZERO));

        // Apply the coupon after both product-level discounts.
        BigDecimal couponDiscount = calculateCouponDiscount(
                couponCode,
                customerType,
                originalSubtotal,
                productTotal);

        BigDecimal finalTotal = money(
                productTotal.subtract(couponDiscount).max(ZERO));

        return new DiscountResult(
                originalSubtotal,
                quantityDiscount,
                accessoryDiscount,
                couponDiscount,
                finalTotal);
    }


    private void validateInputs(
            List<CartItem> items,
            CustomerType customerType) {

        Objects.requireNonNull(items, "Items are required");
        Objects.requireNonNull(customerType,
                "Customer type is required");

        for (CartItem item : items) {
            Objects.requireNonNull(item, "Cart item cannot be null");
            Objects.requireNonNull(item.getProduct(),
                    "Product cannot be null");

            int quantity = item.getQuantity();
            BigDecimal rawPrice = item.getProduct().getPrice();

            if (quantity <= 0 || rawPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Quantity must be positive and price non-negative");
            }
        }
    }

    private BigDecimal calculateSubtotal(List<CartItem> items) {
        BigDecimal subtotal = ZERO;

        for (CartItem item : items) {
            BigDecimal price = item.getProduct().getPrice();

            subtotal = subtotal.add(
                    price.multiply(BigDecimal.valueOf(
                            item.getQuantity())));
        }

        return subtotal;
    }


    private BigDecimal getQuantityDiscountRate(
            int quantity,
            CustomerType customerType) {

        if (customerType == CustomerType.VIP && quantity >= 3) {
            return new BigDecimal("0.10");
        }

        if (customerType == CustomerType.PREMIUM && quantity >= 3) {
            return new BigDecimal("0.05");
        }

        if (customerType == CustomerType.REGULAR && quantity > 5) {
            return new BigDecimal("0.05");
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateQuantityDiscount(
            List<CartItem> items,
            CustomerType customerType) {

        BigDecimal discount = ZERO;

        for (CartItem item : items) {

            Product product = item.getProduct();

            if (product.getCategory() == ProductCategory.ACCESSORY) {
                continue;
            }

            BigDecimal price = product.getPrice();

            int quantity = item.getQuantity();

            BigDecimal rate = getQuantityDiscountRate(
                    quantity, customerType);

            BigDecimal lineDiscount = price
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(rate);

            discount = discount.add(lineDiscount);
        }

        return discount;
    }


    private BigDecimal calculateAccessoryDiscount(
            List<CartItem> items,
            CustomerType customerType) {

        BigDecimal totalDiscount = ZERO;

        for (CartItem item : items) {
            Product product = item.getProduct();

            if (product.getCategory() != ProductCategory.ACCESSORY
                    || item.getQuantity() < 3) {
                continue;
            }

            BigDecimal originalLineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal accessoryDiscount = money(
                    originalLineTotal.multiply(new BigDecimal("0.10")));

            totalDiscount = totalDiscount.add(accessoryDiscount);
        }

        return money(totalDiscount);
    }

    private BigDecimal calculateCouponDiscount(
            String couponCode,
            CustomerType customerType,
            BigDecimal originalSubtotal,
            BigDecimal productTotal) {

        String coupon = couponCode == null
                ? ""
                : couponCode.trim();

        if (coupon.isEmpty()) {
            return ZERO;
        }

        if ("DISCOUNT10".equals(coupon)) {
            if (productTotal.compareTo(new BigDecimal("100.00")) >= 0) {
                return money(new BigDecimal("10.00")
                        .min(productTotal));
            }

            // Coupon is valid, but the order does not qualify.
            return money(BigDecimal.ZERO);
        }

        if ("VIP15".equals(coupon)) {
            if (customerType == CustomerType.VIP
                    && productTotal.compareTo(
                    new BigDecimal("150.00")) >= 0) {
                return money(new BigDecimal("15.00")
                        .min(productTotal));
            }

            // Coupon is valid, but the customer/order is ineligible.
            return money(BigDecimal.ZERO);

        }

        throw new IllegalArgumentException("Invalid coupon");
    }
}
