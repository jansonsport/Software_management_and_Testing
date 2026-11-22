
package onlineshopping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DeliveryService {

    public enum DeliveryOption {
        STANDARD,
        EXPRESS
    }

    private static final BigDecimal STANDARD_FEE =
            new BigDecimal("4.99");
    private static final BigDecimal EXPRESS_FEE =
            new BigDecimal("9.99");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD =
            new BigDecimal("75.00");

    public BigDecimal calculateDeliveryFee(
            BigDecimal discountedMerchandiseTotal,
            CustomerType customerType,
            DeliveryOption option) {

        validateInputs(discountedMerchandiseTotal,
                customerType, option);

        if (customerType == CustomerType.VIP) {
            return BigDecimal.ZERO.setScale(2);
        }

        if (option == DeliveryOption.EXPRESS) {
            return EXPRESS_FEE;
        }

        if (isEligibleForFreeStandardDelivery(
                discountedMerchandiseTotal, customerType)) {
            return BigDecimal.ZERO.setScale(2);
        }

        return STANDARD_FEE.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateInputs(
            BigDecimal merchandiseTotal,
            CustomerType customerType,
            DeliveryOption option) {

        Objects.requireNonNull(merchandiseTotal,
                "Merchandise total is required");
        Objects.requireNonNull(customerType,
                "Customer type is required");
        Objects.requireNonNull(option,
                "Delivery option is required");

        if (merchandiseTotal.signum() < 0) {
            throw new IllegalArgumentException(
                    "Merchandise total cannot be negative");
        }
    }

    private boolean isEligibleForFreeStandardDelivery(
            BigDecimal merchandiseTotal,
            CustomerType customerType) {

        return customerType == CustomerType.VIP
                || merchandiseTotal.compareTo(
                FREE_DELIVERY_THRESHOLD) >= 0;
    }
}
