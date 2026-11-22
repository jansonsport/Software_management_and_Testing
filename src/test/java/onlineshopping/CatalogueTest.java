package onlineshopping;

import java.math.BigDecimal;
import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Test;

public class CatalogueTest {

    @Test
    public void testSupportsTheThreeCustomerTypes() {
        // The brief defines three customer tiers. Each one should be accepted and retained by Customer.
        EnumSet<CustomerType> types = EnumSet.of(CustomerType.REGULAR, CustomerType.PREMIUM, CustomerType.VIP);
        Assert.assertEquals("Supported customer types", types, EnumSet.allOf(CustomerType.class));

        for (CustomerType type : types) {
            Assert.assertEquals("Customer type should match the constructor argument",
                    type, new Customer("Taylor", type).getCustomerType());
        }
    }

    @Test
    public void testSupportsTheThreeProductCategories() {
        // The brief defines three product categories. Each should be accepted and retained by Product.
        EnumSet<ProductCategory> categories = EnumSet.of(
                ProductCategory.LAPTOP, ProductCategory.ACCESSORY, ProductCategory.OTHER);
        Assert.assertEquals("Supported product categories", categories, EnumSet.allOf(ProductCategory.class));

        for (ProductCategory category : categories) {
            Assert.assertEquals("Product category should match the constructor argument",
                    category, new Product("Sample", BigDecimal.ONE, 1, category).getCategory());
        }
    }
}
