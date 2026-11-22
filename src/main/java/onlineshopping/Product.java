package onlineshopping;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private String name;
    private BigDecimal price;
    private int stock;
    private ProductCategory category;

    // Constructor for existing code that does not specify a category.
    // The category defaults to OTHER.
    public Product(String name, BigDecimal price, int stock) {
        this(name, price, stock, ProductCategory.OTHER);
    }

    // Constructor with an explicitly specified product category.
    public Product(String name, BigDecimal price, int stock,
                   ProductCategory category) {
        this.name = Objects.requireNonNull(name, "Product name cannot be null").trim();
        this.category = Objects.requireNonNull(
                category, "Product category cannot be null");

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty");
        }

       Objects.requireNonNull(
                price, "Product price cannot be null");

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Product price must be finite and non-negative");
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative");
        }

        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setPrice(BigDecimal price) {
        Objects.requireNonNull(
                price, "Product price cannot be null");

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Product price must be finite and non-negative");
        }

        this.price = price;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative");
        }

        this.stock = stock;
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive");
        }

        if (quantity > stock) {
            throw new IllegalArgumentException(
                    "Insufficient stock");
        }

        stock -= quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category=" + category +
                '}';
    }
}
