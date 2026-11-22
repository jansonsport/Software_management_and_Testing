package onlineshopping;

import java.math.BigDecimal;

import onlineshopping.DeliveryService.DeliveryOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeliveryServiceTest {

    private DeliveryService service;

    @Before
    public void setUp() {
        service = new DeliveryService();
    }

    @Test
    public void testChargesRegularStandardBelowThreshold() {
        // A Regular customer's final total is one penny below £75, so Standard costs £4.99.
        BigDecimal merchandiseTotal = new BigDecimal("74.99");
        BigDecimal expectedFee = new BigDecimal("4.99");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.REGULAR, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testWaivesRegularStandardAtThreshold() {
        // The £75 threshold is inclusive for Regular Standard delivery.
        BigDecimal merchandiseTotal = new BigDecimal("75.00");
        BigDecimal expectedFee = new BigDecimal("0.00");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.REGULAR, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testWaivesRegularStandardAboveThreshold() {
        // A Regular customer's £75.01 total stays eligible for free Standard delivery.
        BigDecimal merchandiseTotal = new BigDecimal("75.01");
        BigDecimal expectedFee = new BigDecimal("0.00");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.REGULAR, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testChargesPremiumStandardBelowThreshold() {
        // A Premium customer's final total is one penny below £75, so Standard costs £4.99.
        BigDecimal merchandiseTotal = new BigDecimal("74.99");
        BigDecimal expectedFee = new BigDecimal("4.99");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.PREMIUM, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testWaivesPremiumStandardAtThreshold() {
        // The £75 threshold is inclusive for Premium Standard delivery.
        BigDecimal merchandiseTotal = new BigDecimal("75.00");
        BigDecimal expectedFee = new BigDecimal("0.00");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.PREMIUM, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testWaivesPremiumStandardAboveThreshold() {
        // A Premium customer's £75.01 total stays eligible for free Standard delivery.
        BigDecimal merchandiseTotal = new BigDecimal("75.01");
        BigDecimal expectedFee = new BigDecimal("0.00");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.PREMIUM, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testWaivesStandardDeliveryForVipCustomer() {
        // A £0 merchandise total makes clear that VIP Standard delivery has no minimum.
        BigDecimal merchandiseTotal = new BigDecimal("0.00");
        BigDecimal expectedFee = new BigDecimal("0.00");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.VIP, DeliveryOption.STANDARD);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testChargesExpressDeliveryForVipCustomer() {
        // VIP status waives Standard delivery only. Express still costs £9.99, even at £0.
        BigDecimal merchandiseTotal = new BigDecimal("0.00");
        BigDecimal expectedFee = new BigDecimal("9.99");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.VIP, DeliveryOption.EXPRESS);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testChargesExpressDeliveryForRegularCustomer() {
        // The £75.01 total would qualify for free Standard delivery, but Express costs £9.99.
        BigDecimal merchandiseTotal = new BigDecimal("75.01");
        BigDecimal expectedFee = new BigDecimal("9.99");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.REGULAR, DeliveryOption.EXPRESS);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testChargesExpressDeliveryForPremiumCustomer() {
        // A Premium customer above the Standard threshold still pays the £9.99 Express fee.
        BigDecimal merchandiseTotal = new BigDecimal("75.01");
        BigDecimal expectedFee = new BigDecimal("9.99");

        BigDecimal actualFee = service.calculateDeliveryFee(
                merchandiseTotal, CustomerType.PREMIUM, DeliveryOption.EXPRESS);

        Assert.assertEquals("Delivery fee", expectedFee, actualFee);
    }

    @Test
    public void testRejectsUnsupportedDeliveryOptionName() {
        // An unrecognised option name cannot become a DeliveryOption.
        // The enum rejects it before the order can reach the delivery service.
        Assert.assertThrows("DRONE is not a supported delivery option",
                IllegalArgumentException.class, () -> DeliveryOption.valueOf("DRONE"));
    }

    @Test
    public void testRejectsMissingDeliveryOption() {
        // The order has a valid total and customer type, but no delivery option was supplied.
        Assert.assertThrows("A delivery option is required",
                NullPointerException.class,
                () -> service.calculateDeliveryFee(new BigDecimal("75.00"), CustomerType.REGULAR, null));
    }
}
