package onlineshopping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {

    private final List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds a product to the cart.
     * If the product is already present, its quantity is increased.
     */
    public void addProduct(Product product, int quantity) {
        Objects.requireNonNull(product, "Product cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive");
        }

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName());
        }

        CartItem existingItem = findItem(product);

        int newQuantity = quantity;

        if (existingItem != null) {
            newQuantity += existingItem.getQuantity();

            if (newQuantity > product.getStock()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            existingItem.setQuantity(newQuantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    /**
     * Removes a product from the cart completely.
     */
    public void removeProduct(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");

        CartItem item = findItem(product);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Product is not in the cart");
        }

        items.remove(item);
    }

    /**
     * Changes the quantity of a product already in the cart.
     */
    public void updateQuantity(Product product, int quantity) {
        Objects.requireNonNull(product, "Product cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive");
        }

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName());
        }

        CartItem item = findItem(product);

        if (item == null) {
            throw new IllegalArgumentException(
                    "Product is not in the cart");
        }

        item.setQuantity(quantity);
    }

    /**
     * Returns an unmodifiable view of the cart items.
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns the number of distinct product entries in the cart.
     */
    public int getNumberOfItems() {
        return items.size();
    }

    /**
     * Returns the total number of units across all cart entries.
     */
    public int getTotalQuantity() {
        int total = 0;

        for (CartItem item : items) {
            total += item.getQuantity();
        }

        return total;
    }

    /**
     * Calculates the original merchandise subtotal before discounts.
     */
    public BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : items) {
            BigDecimal price = item.getProduct().getPrice();

            BigDecimal quantity = BigDecimal.valueOf(
                    item.getQuantity());

            subtotal = subtotal.add(price.multiply(quantity));
        }

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Checks whether the cart contains any products.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Removes all entries from the cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Finds a cart entry by product object.
     */
    private CartItem findItem(Product product) {
        for (CartItem item : items) {
            if (item.getProduct() == product) {
                return item;
            }
        }

        return null;
    }
}
