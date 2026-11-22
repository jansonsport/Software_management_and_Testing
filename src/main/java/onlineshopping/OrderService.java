package onlineshopping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class OrderService {

    private final DiscountService discountService;
    private final DeliveryService deliveryService;

    public OrderService(
            DiscountService discountService,
            DeliveryService deliveryService) {

        this.discountService = Objects.requireNonNull(
                discountService, "DiscountService cannot be null");

        this.deliveryService = Objects.requireNonNull(
                deliveryService, "DeliveryService cannot be null");
    }

    /**
     * Creates an order summary by calculating merchandise discounts
     * and the delivery fee.
     *
     * This method does not process payment or deduct stock.
     */
    public OrderResult createOrder(
            ShoppingCart cart,
            CustomerType customerType,
            String couponCode,
            DeliveryService.DeliveryOption deliveryOption) {

        Objects.requireNonNull(cart, "Shopping cart cannot be null");
        Objects.requireNonNull(customerType, "Customer type cannot be null");
        Objects.requireNonNull(deliveryOption, "Delivery option cannot be null");

        if (cart.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot create an order from an empty cart");
        }

        // Step 1: Calculate product and coupon discounts.
        DiscountService.DiscountResult discountResult =
                discountService.calculateDiscount(
                        cart.getItems(),
                        customerType,
                        couponCode);

        // Step 2: Calculate delivery using the discounted merchandise total.
        BigDecimal deliveryFee = deliveryService.calculateDeliveryFee(
                discountResult.originalSubtotal,
                customerType,
                deliveryOption);

        // Step 3: Calculate the final amount payable.
        BigDecimal totalPayable = money(
                discountResult.finalTotal.add(deliveryFee));

        return new OrderResult(
                discountResult,
                deliveryOption,
                deliveryFee,
                totalPayable);
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Immutable summary of the calculated order.
     */
    public static class OrderResult {

        public final DiscountService.DiscountResult discountResult;
        public final DeliveryService.DeliveryOption deliveryOption;
        public final BigDecimal deliveryFee;
        public final BigDecimal totalPayable;

        public OrderResult(
                DiscountService.DiscountResult discountResult,
                DeliveryService.DeliveryOption deliveryOption,
                BigDecimal deliveryFee,
                BigDecimal totalPayable) {

            this.discountResult = Objects.requireNonNull(discountResult);
            this.deliveryOption = Objects.requireNonNull(deliveryOption);
            this.deliveryFee = Objects.requireNonNull(deliveryFee);
            this.totalPayable = Objects.requireNonNull(totalPayable);
        }

        public BigDecimal getOriginalSubtotal() {
            return discountResult.originalSubtotal;
        }

        public BigDecimal getQuantityDiscount() {
            return discountResult.quantityDiscount;
        }

        public BigDecimal getAccessoryDiscount() {
            return discountResult.accessoryDiscount;
        }

        public BigDecimal getCouponDiscount() {
            return discountResult.couponDiscount;
        }

        public BigDecimal getDiscountedMerchandiseTotal() {
            return discountResult.finalTotal;
        }

        public BigDecimal getDeliveryFee() {
            return deliveryFee;
        }

        public BigDecimal getTotalPayable() {
            return totalPayable;
        }

        @Override
        public String toString() {
            return "Order Summary\n"
                    + "-------------------------\n"
                    + "Original subtotal: £"
                    + getOriginalSubtotal() + "\n"
                    + "Quantity discount: £"
                    + getQuantityDiscount() + "\n"
                    + "Accessory discount: £"
                    + getAccessoryDiscount() + "\n"
                    + "Coupon discount: £"
                    + getCouponDiscount() + "\n"
                    + "Discounted merchandise total: £"
                    + getDiscountedMerchandiseTotal() + "\n"
                    + "Delivery option: "
                    + deliveryOption + "\n"
                    + "Delivery fee: £"
                    + getDeliveryFee() + "\n"
                    + "Total payable: £"
                    + getTotalPayable();
        }
    }
}
