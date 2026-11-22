package onlineshopping;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShoppingCartTest {

    private ShoppingCart cart;
    private Product monitor;

    @Before
    public void setUp() {
        cart = new ShoppingCart();
        monitor = new Product("Monitor", new BigDecimal("26.00"), 3);
    }

    @Test
    public void testCombinesRepeatedAddsOfTheSameProduct() {
        // Adding the same monitor twice should leave one cart line containing all three units.
        cart.addProduct(monitor, 1);
        cart.addProduct(monitor, 2);

        Assert.assertEquals("Distinct cart entries", 1, cart.getNumberOfItems());
        Assert.assertEquals("Units in the cart", 3, cart.getTotalQuantity());
        Assert.assertEquals("Cart subtotal", new BigDecimal("78.00"), cart.calculateSubtotal());
    }

    @Test
    public void testRejectsAnAddThatWouldExceedStockAndKeepsExistingQuantity() {
        // Adding two more monitors would bring the cart to four with only three in stock.
        // The rejected add should leave the original two units intact.
        cart.addProduct(monitor, 2);

        Assert.assertThrows("Combined quantity must stay within stock",
                IllegalArgumentException.class,
                () -> cart.addProduct(monitor, 2));
        Assert.assertEquals("Units in the cart", 2, cart.getTotalQuantity());
    }

    @Test
    public void testUpdatesQuantityAndSubtotal() {
        // Changing the quantity from one monitor to three should update both the count and subtotal.
        cart.addProduct(monitor, 1);

        cart.updateQuantity(monitor, 3);

        Assert.assertEquals("Units in the cart", 3, cart.getTotalQuantity());
        Assert.assertEquals("Cart subtotal", new BigDecimal("78.00"), cart.calculateSubtotal());
    }

    @Test
    public void testRemovesAllUnitsOfTheSelectedProduct() {
        // Removing the monitor should remove both units and leave the £5 cable in the cart.
        cart.addProduct(monitor, 2);
        Product cable = new Product("Cable", new BigDecimal("5.00"), 3);
        cart.addProduct(cable, 1);

        cart.removeProduct(monitor);

        Assert.assertEquals("Distinct cart entries", 1, cart.getNumberOfItems());
        Assert.assertEquals("Cart subtotal", new BigDecimal("5.00"), cart.calculateSubtotal());
    }

    @Test
    public void testClearsTheCart() {
        // Clearing a populated cart should leave it empty with a £0.00 subtotal.
        cart.addProduct(monitor, 2);

        cart.clear();

        Assert.assertTrue("Cart should be empty", cart.isEmpty());
        Assert.assertEquals("Cart subtotal", new BigDecimal("0.00"), cart.calculateSubtotal());
    }

    @Test
    public void testRoundsTheSubtotalToTwoDecimalPlaces() {
        // A £5.126 unit price makes the two-decimal rounding visible in the subtotal.
        Product cable = new Product("Cable", new BigDecimal("5.126"), 1);
        cart.addProduct(cable, 1);

        Assert.assertEquals("Cart subtotal", new BigDecimal("5.13"), cart.calculateSubtotal());
    }
}
