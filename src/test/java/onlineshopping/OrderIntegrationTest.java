package onlineshopping;

import java.math.BigDecimal;

import onlineshopping.DeliveryService.DeliveryOption;
import onlineshopping.OrderService.OrderResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderIntegrationTest {

    private OrderService service;

    @Before
    public void setUp() {
        service = new OrderService(new DiscountService(), new DeliveryService());
    }

    @Test
    public void testChargesStandardDeliveryWhenProductDiscountTakesTotalBelowThreshold() {
        // Three £26 monitors start at £78, and the Premium discount brings them to £74.10.
        // Standard delivery then adds £4.99, making the total payable £79.09.
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(new Product("Monitor", new BigDecimal("26.00"), 3), 3);

        OrderResult order = service.createOrder(cart,
                CustomerType.PREMIUM, null, DeliveryOption.STANDARD);

        Assert.assertEquals("Original merchandise subtotal",
                new BigDecimal("78.00"), order.getOriginalSubtotal());
        Assert.assertEquals("Quantity discount", new BigDecimal("3.90"), order.getQuantityDiscount());
        Assert.assertEquals("Discounted merchandise total",
                new BigDecimal("74.10"), order.getDiscountedMerchandiseTotal());
        Assert.assertEquals("Delivery fee", new BigDecimal("4.99"), order.getDeliveryFee());
        Assert.assertEquals("Total payable", new BigDecimal("79.09"), order.getTotalPayable());
    }

    @Test
    public void testUsesCouponDiscountInAmountPayable() {
        // DISCOUNT10 brings £100 down to £90. The order still qualifies for free Standard delivery,
        // so £90 is also the amount payable.
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(new Product("Monitor", new BigDecimal("100.00"), 1), 1);

        OrderResult order = service.createOrder(cart,
                CustomerType.REGULAR, "DISCOUNT10", DeliveryOption.STANDARD);

        Assert.assertEquals("Coupon discount", new BigDecimal("10.00"), order.getCouponDiscount());
        Assert.assertEquals("Delivery fee", new BigDecimal("0.00"), order.getDeliveryFee());
        Assert.assertEquals("Total payable", new BigDecimal("90.00"), order.getTotalPayable());
    }

    @Test
    public void testRejectsEmptyOrder() {
        // The customer and delivery option are valid, but an empty cart cannot become an order.
        ShoppingCart cart = new ShoppingCart();

        Assert.assertThrows("An empty cart cannot become an order",
                IllegalArgumentException.class,
                () -> service.createOrder(cart,
                        CustomerType.REGULAR, null, DeliveryOption.STANDARD));
    }
}
