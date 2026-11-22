package onlineshopping;

import java.math.BigDecimal;
import java.util.List;

import onlineshopping.DiscountService.DiscountResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiscountServiceTest {

    private DiscountService service;

    @Before
    public void setUp() {
        service = new DiscountService();
    }

    @Test
    public void testRegularBelowThreshold() {
        // Four laptops are one short of the Regular minimum of five.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 4);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.REGULAR, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testRegularAtThreshold() {
        // Five £20 laptops qualify for 5% off the full £100 subtotal.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 5);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.REGULAR, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("5.00"), result.quantityDiscount);
    }

    @Test
    public void testRegularAboveThreshold() {
        // Six £20 laptops qualify for 5% off all six units, giving £6.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 6);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.REGULAR, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("6.00"), result.quantityDiscount);
    }

    @Test
    public void testPremiumBelowThreshold() {
        // Two laptops are one short of the Premium minimum of three.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 2);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.PREMIUM, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testPremiumAtThreshold() {
        // Three £20 laptops meet the Premium minimum and earn 5% of £60.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 3);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.PREMIUM, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("3.00"), result.quantityDiscount);
    }

    @Test
    public void testPremiumAboveThreshold() {
        // Four £20 laptops earn the Premium rate of 5% on the full £80.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 4);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.PREMIUM, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("4.00"), result.quantityDiscount);
    }

    @Test
    public void testVipBelowThreshold() {
        // Two laptops are one short of the VIP minimum of three.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 2);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.VIP, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testVipAtThreshold() {
        // Three £20 laptops meet the VIP minimum and earn 10% of £60.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 3);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.VIP, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("6.00"), result.quantityDiscount);
    }

    @Test
    public void testVipAboveThreshold() {
        // Four £20 laptops earn the VIP rate of 10% on the full £80.
        CartItem laptops = item("Laptop", "20.00", ProductCategory.LAPTOP, 4);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.VIP, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("8.00"), result.quantityDiscount);
    }

    @Test
    public void testCombinesSameNameNonAccessoryEntries() {
        // Two separate entries with the same name supply three laptops in total.
        // Together they qualify for 10% off the £60 combined subtotal.
        CartItem first = item("Laptop", "20.00", ProductCategory.LAPTOP, 1);
        CartItem second = item("Laptop", "20.00", ProductCategory.LAPTOP, 2);

        DiscountResult result = service.calculateDiscount(List.of(first, second),
                CustomerType.VIP, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("6.00"), result.quantityDiscount);
    }

    @Test
    public void testDoesNotCombineDifferentProductNames() {
        // The cart contains three units, but neither product name has three units.
        // A VIP discount should therefore apply to neither line.
        CartItem laptop = item("Laptop", "20.00", ProductCategory.LAPTOP, 2);
        CartItem monitor = item("Monitor", "20.00", ProductCategory.OTHER, 1);

        DiscountResult result = service.calculateDiscount(List.of(laptop, monitor),
                CustomerType.VIP, null);

        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testAccessoryBelowThreshold() {
        // Two mice are below the accessory minimum of three.
        // VIP status does not turn them into qualifying non-accessory items.
        CartItem mice = item("Mouse", "10.00", ProductCategory.ACCESSORY, 2);

        DiscountResult result = service.calculateDiscount(List.of(mice),
                CustomerType.VIP, null);

        Assert.assertEquals("Accessory discount", new BigDecimal("0.00"), result.accessoryDiscount);
        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testAccessoryAtThreshold() {
        // Three £10 mice qualify for the 10% accessory rate.
        // The VIP quantity rate must not be applied on top of it.
        CartItem mice = item("Mouse", "10.00", ProductCategory.ACCESSORY, 3);

        DiscountResult result = service.calculateDiscount(List.of(mice),
                CustomerType.VIP, null);

        Assert.assertEquals("Accessory discount", new BigDecimal("3.00"), result.accessoryDiscount);
        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testAccessoryAboveThreshold() {
        // Four £10 mice still get 10% off the full line, with no separate VIP discount.
        CartItem mice = item("Mouse", "10.00", ProductCategory.ACCESSORY, 4);

        DiscountResult result = service.calculateDiscount(List.of(mice),
                CustomerType.VIP, null);

        Assert.assertEquals("Accessory discount", new BigDecimal("4.00"), result.accessoryDiscount);
        Assert.assertEquals("Quantity discount", new BigDecimal("0.00"), result.quantityDiscount);
    }

    @Test
    public void testCombinesSameNameAccessoryEntries() {
        // One mouse and two more with the same name count as three units.
        // The 10% accessory discount should be based on their combined £30.
        CartItem first = item("Mouse", "10.00", ProductCategory.ACCESSORY, 1);
        CartItem second = item("Mouse", "10.00", ProductCategory.ACCESSORY, 2);

        DiscountResult result = service.calculateDiscount(List.of(first, second),
                CustomerType.REGULAR, null);

        Assert.assertEquals("Accessory discount", new BigDecimal("3.00"), result.accessoryDiscount);
    }

    @Test
    public void testDiscountsOnlyQualifyingAccessories() {
        // The three mice qualify for 10% off their £30 line.
        // The single keyboard does not qualify and adds nothing to the discount.
        CartItem mice = item("Mouse", "10.00", ProductCategory.ACCESSORY, 3);
        CartItem keyboard = item("Keyboard", "20.00", ProductCategory.ACCESSORY, 1);

        DiscountResult result = service.calculateDiscount(List.of(mice, keyboard),
                CustomerType.REGULAR, null);

        Assert.assertEquals("Accessory discount", new BigDecimal("3.00"), result.accessoryDiscount);
    }

    @Test
    public void testAppliesDiscount10AtThreshold() {
        // One £100 laptop has no quantity discount, so the coupon sees exactly £100.
        CartItem laptop = item("Laptop", "100.00", ProductCategory.LAPTOP, 1);

        DiscountResult result = service.calculateDiscount(List.of(laptop),
                CustomerType.REGULAR, "DISCOUNT10");

        Assert.assertEquals("Coupon discount", new BigDecimal("10.00"), result.couponDiscount);
    }

    @Test
    public void testAppliesDiscount10AboveThreshold() {
        // A £100.01 subtotal is one penny above the minimum and still gets £10 off.
        CartItem laptop = item("Laptop", "100.01", ProductCategory.LAPTOP, 1);

        DiscountResult result = service.calculateDiscount(List.of(laptop),
                CustomerType.REGULAR, "DISCOUNT10");

        Assert.assertEquals("Coupon discount", new BigDecimal("10.00"), result.couponDiscount);
    }

    @Test
    public void testAppliesDiscount10WhenAccessoryDiscountLeavesExactlyOneHundredPounds() {
        // The £3 accessory discount takes £103 down to exactly £100.
        // DISCOUNT10 still qualifies and leaves £90 to pay for merchandise.
        CartItem monitor = item("Monitor", "73.00", ProductCategory.OTHER, 1);
        CartItem mice = item("Mouse", "10.00", ProductCategory.ACCESSORY, 3);

        DiscountResult result = service.calculateDiscount(List.of(monitor, mice),
                CustomerType.REGULAR, "DISCOUNT10");

        Assert.assertEquals("Original merchandise subtotal",
                new BigDecimal("103.00"), result.originalSubtotal);
        Assert.assertEquals("Accessory discount", new BigDecimal("3.00"), result.accessoryDiscount);
        Assert.assertEquals("Coupon discount", new BigDecimal("10.00"), result.couponDiscount);
        Assert.assertEquals("Final merchandise total", new BigDecimal("90.00"), result.finalTotal);
    }

    @Test
    public void testIgnoresCouponCaseAndSurroundingSpaces() {
        // A £100 cart qualifies, so only the case and surrounding spaces vary.
        // The supplied text should be treated as DISCOUNT10.
        CartItem laptop = item("Laptop", "100.00", ProductCategory.LAPTOP, 1);

        DiscountResult result = service.calculateDiscount(List.of(laptop),
                CustomerType.REGULAR, " discount10 ");

        Assert.assertEquals("Coupon discount", new BigDecimal("10.00"), result.couponDiscount);
    }

    @Test
    public void testAppliesVip15AboveThreshold() {
        // A VIP order at £150.01 is one penny above the original-subtotal minimum.
        CartItem laptop = item("Laptop", "150.01", ProductCategory.LAPTOP, 1);

        DiscountResult result = service.calculateDiscount(List.of(laptop),
                CustomerType.VIP, "VIP15");

        Assert.assertEquals("Coupon discount", new BigDecimal("15.00"), result.couponDiscount);
    }

    @Test
    public void testRejectsDiscount10BelowThreshold() {
        // With no product discount, £99.99 is still one penny short of the coupon minimum.
        CartItem laptop = item("Laptop", "99.99", ProductCategory.LAPTOP, 1);

        Assert.assertThrows("DISCOUNT10 requires at least £100",
                IllegalArgumentException.class,
                () -> service.calculateDiscount(List.of(laptop), CustomerType.REGULAR, "DISCOUNT10"));
    }

    @Test
    public void testRejectsDiscount10WhenProductDiscountDropsTotalBelowThreshold() {
        // The laptops start at £105, but the VIP quantity discount leaves £94.50.
        // DISCOUNT10 should be rejected because it uses the amount after product discounts.
        CartItem laptops = item("Laptop", "35.00", ProductCategory.LAPTOP, 3);

        Assert.assertThrows("DISCOUNT10 uses the subtotal after product discounts",
                IllegalArgumentException.class,
                () -> service.calculateDiscount(List.of(laptops), CustomerType.VIP, "DISCOUNT10"));
    }

    @Test
    public void testAppliesVip15AtOriginalSubtotalThreshold() {
        // Three £50 laptops start at £150 and drop to £135 after the VIP quantity discount.
        // VIP15 qualifies on the original £150 and leaves £120 after both discounts.
        CartItem laptops = item("Laptop", "50.00", ProductCategory.LAPTOP, 3);

        DiscountResult result = service.calculateDiscount(List.of(laptops),
                CustomerType.VIP, "VIP15");

        Assert.assertEquals("Original merchandise subtotal",
                new BigDecimal("150.00"), result.originalSubtotal);
        Assert.assertEquals("Quantity discount", new BigDecimal("15.00"), result.quantityDiscount);
        Assert.assertEquals("Coupon discount", new BigDecimal("15.00"), result.couponDiscount);
        Assert.assertEquals("Final merchandise total", new BigDecimal("120.00"), result.finalTotal);
    }

    @Test
    public void testRejectsVip15BelowThreshold() {
        // VIP status alone is insufficient when the original subtotal is £149.99.
        CartItem laptop = item("Laptop", "149.99", ProductCategory.LAPTOP, 1);

        Assert.assertThrows("VIP15 requires an original subtotal of at least £150",
                IllegalArgumentException.class,
                () -> service.calculateDiscount(List.of(laptop), CustomerType.VIP, "VIP15"));
    }

    @Test
    public void testRejectsVip15ForPremiumCustomer() {
        // The order meets the £150 minimum, so the Premium tier is the reason to reject VIP15.
        CartItem laptop = item("Laptop", "150.00", ProductCategory.LAPTOP, 1);

        Assert.assertThrows("VIP15 is reserved for VIP customers",
                IllegalArgumentException.class,
                () -> service.calculateDiscount(List.of(laptop), CustomerType.PREMIUM, "VIP15"));
    }

    @Test
    public void testRejectsUnknownCoupon() {
        // The order is large enough for a valid coupon, but UNKNOWN is not a recognised code.
        CartItem laptop = item("Laptop", "200.00", ProductCategory.LAPTOP, 1);

        Assert.assertThrows("Unrecognised coupon codes must be rejected",
                IllegalArgumentException.class,
                () -> service.calculateDiscount(List.of(laptop), CustomerType.VIP, "UNKNOWN"));
    }

    @Test
    public void testRejectsMultipleCoupons() {
        // The order qualifies for each coupon individually, but the code field accepts only one.
        CartItem laptop = item("Laptop", "200.00", ProductCategory.LAPTOP, 1);

        Assert.assertThrows("Only one coupon is allowed per order",
                IllegalArgumentException.class,
                () -> service.calculateDiscount(List.of(laptop), CustomerType.VIP, "VIP15,DISCOUNT10"));
    }

    @Test
    public void testCapsDiscount10AtTenPoundsWhenBothProductDiscountsApply() {
        // The £150 basket earns £12 off the laptops and £3 off the mice.
        // DISCOUNT10 can take £10 from the remaining £135, leaving £125.
        CartItem laptops = item("Laptop", "40.00", ProductCategory.LAPTOP, 3);
        CartItem mice = item("Mouse", "10.00", ProductCategory.ACCESSORY, 3);

        DiscountResult result = service.calculateDiscount(
                List.of(laptops, mice), CustomerType.VIP, "DISCOUNT10");

        Assert.assertEquals("Quantity discount", new BigDecimal("12.00"), result.quantityDiscount);
        Assert.assertEquals("Accessory discount", new BigDecimal("3.00"), result.accessoryDiscount);
        Assert.assertEquals("Coupon discount must stay at £10", new BigDecimal("10.00"), result.couponDiscount);
        Assert.assertEquals("Final merchandise total", new BigDecimal("125.00"), result.finalTotal);
    }

    @Test
    public void testReturnsZeroForZeroPricedMerchandise() {
        // A free item without a coupon should leave the merchandise total at exactly £0.00.
        CartItem freeItem = item("Sample", "0.00", ProductCategory.OTHER, 1);

        DiscountResult result = service.calculateDiscount(List.of(freeItem),
                CustomerType.REGULAR, null);

        Assert.assertEquals("Final merchandise total", new BigDecimal("0.00"), result.finalTotal);
    }

    private static CartItem item(String name, String price, ProductCategory category, int quantity) {
        return new CartItem(new Product(name, new BigDecimal(price), 20, category), quantity);
    }
}
