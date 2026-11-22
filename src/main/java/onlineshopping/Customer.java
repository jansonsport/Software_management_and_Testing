package onlineshopping;

import java.util.Objects;

public class Customer {
    private String name;
    private CustomerType customerType;

    public Customer(String name, CustomerType customerType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer name cannot be null or empty"
            );
        }

        this.name = name.trim();

        this.customerType = Objects.requireNonNull(
                customerType,
                "Customer type cannot be null"
        );
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer name cannot be null or empty"
            );
        }

        this.name = name.trim();
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = Objects.requireNonNull(
                customerType,
                "Customer type cannot be null"
        );
    }
}


