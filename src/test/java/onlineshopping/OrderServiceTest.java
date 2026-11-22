package onlineshopping;

import java.math.BigDecimal;

import onlineshopping.DeliveryService.DeliveryOption;
import onlineshopping.DiscountService.DiscountResult;
import onlineshopping.OrderService.OrderResult;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @Test
    public void testPassesFinalDiscountedTotalToDeliveryService() {
        // Stub the discounts to turn £100 into £90, then check what the order service passes to delivery.
        // The delivery stub returns the same fee for either amount so verification isolates the handoff.
        DiscountService discounts = mock(DiscountService.class);
        DeliveryService delivery = mock(DeliveryService.class);
        OrderService service = new OrderService(discounts, delivery);

        ShoppingCart cart = new ShoppingCart();
        cart.addProduct(new Product("Monitor", new BigDecimal("100.00"), 1), 1);
        BigDecimal discountedTotal = new BigDecimal("90.00");
        DiscountResult result = new DiscountResult(new BigDecimal("100.00"),
                new BigDecimal("0.00"), new BigDecimal("0.00"),
                new BigDecimal("10.00"), discountedTotal);
        when(discounts.calculateDiscount(cart.getItems(), CustomerType.REGULAR, "DISCOUNT10")).thenReturn(result);
        when(delivery.calculateDeliveryFee(
                any(BigDecimal.class), eq(CustomerType.REGULAR), eq(DeliveryOption.STANDARD)))
                .thenReturn(new BigDecimal("0.00"));

        OrderResult order = service.createOrder(cart,
                CustomerType.REGULAR, "DISCOUNT10", DeliveryOption.STANDARD);

        verify(delivery).calculateDeliveryFee(discountedTotal, CustomerType.REGULAR, DeliveryOption.STANDARD);
        Assert.assertEquals("Total payable", discountedTotal, order.getTotalPayable());
    }
}
