package onlineshopping;

import java.math.BigDecimal;
import java.util.Collections;

import onlineshopping.DeliveryService.DeliveryOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StructuralCoverageTest {

    private ShoppingCart cart;
    private Product monitor;

    @Before
    public void setUp() {
        cart = new ShoppingCart();
        monitor = new Product("Monitor", new BigDecimal("26.00"), 3);
    }

    @Test
    public void testRejectsNegativeDeliveryTotal() {
        // A negative total reaches delivery's amount validation with otherwise valid inputs.
        DeliveryService delivery = new DeliveryService();

        Assert.assertThrows("Delivery cannot be calculated for a negative total",
                IllegalArgumentException.class,
                () -> delivery.calculateDeliveryFee(new BigDecimal("-0.01"),
                        CustomerType.REGULAR, DeliveryOption.STANDARD));
    }

    @Test
    public void testRejectsNullEntryInDiscountInput() {
        // A list can contain a null entry even though the list itself is present.
        DiscountService discounts = new DiscountService();

        Assert.assertThrows("A discount input list cannot contain a null item",
                NullPointerException.class,
                () -> discounts.calculateDiscount(Collections.<CartItem>singletonList(null),
                        CustomerType.REGULAR, null));
    }

    @Test
    public void testRejectsAnInitialQuantityAboveStock() {
        // An initial request for four monitors exceeds the stock of three and must leave the cart empty.

        Assert.assertThrows("Initial quantity must stay within stock",
                IllegalArgumentException.class,
                () -> cart.addProduct(monitor, 4));
        Assert.assertTrue("Cart should be empty", cart.isEmpty());
    }

    @Test
    public void testRejectsAnUpdateAboveStockAndKeepsExistingQuantity() {
        // An update from one to four exceeds the stock of three and must preserve the existing quantity.
        cart.addProduct(monitor, 1);

        Assert.assertThrows("Updated quantity must stay within stock",
                IllegalArgumentException.class,
                () -> cart.updateQuantity(monitor, 4));
        Assert.assertEquals("Units in the cart", 1, cart.getTotalQuantity());
    }

    @Test
    public void testRejectsAnUpdateToZeroAndKeepsExistingQuantity() {
        // An update to zero is rejected, and the one monitor already in the cart should remain.
        cart.addProduct(monitor, 1);

        Assert.assertThrows("Updated quantity must be positive",
                IllegalArgumentException.class,
                () -> cart.updateQuantity(monitor, 0));
        Assert.assertEquals("Units in the cart", 1, cart.getTotalQuantity());
    }

    @Test
    public void testRejectsAnUpdateForAProductOutsideTheCart() {
        // A valid quantity within stock cannot update a monitor that is absent from the cart.

        Assert.assertThrows("Quantity can only be updated for a product in the cart",
                IllegalArgumentException.class,
                () -> cart.updateQuantity(monitor, 1));
    }

    @Test
    public void testRejectsRemovalOfAProductOutsideTheCart() {
        // Removing a valid product from an empty cart should report that the product is absent.

        Assert.assertThrows("Only a product in the cart can be removed",
                IllegalArgumentException.class,
                () -> cart.removeProduct(monitor));
    }

    @Test
    public void testRejectsAddingZeroUnits() {
        // Zero is invalid even when the product is valid and in stock.

        Assert.assertThrows("An added quantity must be positive",
                IllegalArgumentException.class,
                () -> cart.addProduct(monitor, 0));
    }
}
