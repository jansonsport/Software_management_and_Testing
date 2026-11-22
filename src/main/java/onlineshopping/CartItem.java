package onlineshopping;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = Objects.requireNonNull(product, "Product cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}

