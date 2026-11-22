# Requirements and test scenarios

This table maps the 14 requirements in the [coursework brief](CourseworkDescription.pdf) to the current JUnit tests. Each row represents one test method. Where a scenario checks more than one requirement, the row lists all relevant numbers. A dash marks supporting behaviour that has no direct equivalent in the 14 numbered requirements.

The **expected result** column records what the test asserts. Some assertions fail against the supplied application. See the [fault review notes and DD graphs](test-design.txt) for the observed behaviour and structural analysis.

## Customer types and product categories

Test file: [CatalogueTest.java](../src/test/java/onlineshopping/CatalogueTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 1 | Construct a customer for each supported type. | The available types are exactly Regular, Premium, and VIP. Each customer retains its selected type. | `testSupportsTheThreeCustomerTypes` |
| 2 | Construct a product for each supported category. | The available categories are exactly Laptop, Accessory, and Other. Each product retains its selected category. | `testSupportsTheThreeProductCategories` |

## Product discounts

Test file: [DiscountServiceTest.java](../src/test/java/onlineshopping/DiscountServiceTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 3 | Regular customer, 4 laptops at £20 each (below the minimum). | Quantity discount of £0.00. | `testRegularBelowThreshold` |
| 3 | Regular customer, 5 laptops at £20 each (at the minimum). | Quantity discount of £5.00. | `testRegularAtThreshold` |
| 3 | Regular customer, 6 laptops at £20 each (above the minimum). | Quantity discount of £6.00. | `testRegularAboveThreshold` |
| 3 | Premium customer, 2 laptops at £20 each (below the minimum). | Quantity discount of £0.00. | `testPremiumBelowThreshold` |
| 3 | Premium customer, 3 laptops at £20 each (at the minimum). | Quantity discount of £3.00. | `testPremiumAtThreshold` |
| 3 | Premium customer, 4 laptops at £20 each (above the minimum). | Quantity discount of £4.00. | `testPremiumAboveThreshold` |
| 3 | VIP customer, 2 laptops at £20 each (below the minimum). | Quantity discount of £0.00. | `testVipBelowThreshold` |
| 3 | VIP customer, 3 laptops at £20 each (at the minimum). | Quantity discount of £6.00. | `testVipAtThreshold` |
| 3 | VIP customer, 4 laptops at £20 each (above the minimum). | Quantity discount of £8.00. | `testVipAboveThreshold` |
| 3 | A VIP buys 1 laptop and 2 more under the same name. | Combine the quantities and deduct £6.00. | `testCombinesSameNameNonAccessoryEntries` |
| 3 | A VIP buys 2 laptops and 1 monitor. | Keep the quantities separate; neither reaches three, so deduct £0.00. | `testDoesNotCombineDifferentProductNames` |
| 3, 4 | A VIP buys 2 mice at £10 each. | No accessory or customer quantity discount. | `testAccessoryBelowThreshold` |
| 3, 4 | A VIP buys 3 mice at £10 each. | £3.00 accessory discount and no customer quantity discount. | `testAccessoryAtThreshold` |
| 3, 4 | A VIP buys 4 mice at £10 each. | £4.00 accessory discount and no customer quantity discount. | `testAccessoryAboveThreshold` |
| 4 | A Regular customer buys 1 mouse and 2 more under the same name. | Combine the quantities and deduct £3.00. | `testCombinesSameNameAccessoryEntries` |
| 4 | A Regular customer buys 3 mice and 1 keyboard. | Deduct £3.00 for the qualifying mice only. | `testDiscountsOnlyQualifyingAccessories` |

## Coupons and discount limits

Test file: [DiscountServiceTest.java](../src/test/java/onlineshopping/DiscountServiceTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 5, 7, 8 | Apply `DISCOUNT10` at a merchandise subtotal of £99.99. | Reject the ineligible coupon with `IllegalArgumentException`. | `testRejectsDiscount10BelowThreshold` |
| 5, 8 | Apply `DISCOUNT10` at a merchandise subtotal of exactly £100.00. | Deduct £10.00. | `testAppliesDiscount10AtThreshold` |
| 5, 8 | Apply `DISCOUNT10` at a merchandise subtotal of £100.01. | Deduct £10.00. | `testAppliesDiscount10AboveThreshold` |
| 5, 7, 8 | A VIP quantity discount reduces £105.00 to £94.50 before `DISCOUNT10`. | Reject the ineligible coupon with `IllegalArgumentException`. | `testRejectsDiscount10WhenProductDiscountDropsTotalBelowThreshold` |
| 4, 5, 8 | An accessory discount reduces £103.00 to exactly £100.00 before `DISCOUNT10`. | Apply the £3.00 accessory discount and the £10.00 coupon. Final merchandise total is £90.00. | `testAppliesDiscount10WhenAccessoryDiscountLeavesExactlyOneHundredPounds` |
| 6, 7, 8 | A VIP applies `VIP15` at an original subtotal of £149.99. | Reject the ineligible coupon with `IllegalArgumentException`. | `testRejectsVip15BelowThreshold` |
| 3, 6, 8, 9 | A VIP applies `VIP15` after the quantity discount reduces the original £150.00 to £135.00. | Qualify using the original subtotal, deduct £15.00, and return £120.00. | `testAppliesVip15AtOriginalSubtotalThreshold` |
| 6, 8, 9 | A VIP applies `VIP15` at an original subtotal of £150.01. | Deduct the stated £15.00. | `testAppliesVip15AboveThreshold` |
| 6, 7 | A Premium customer applies `VIP15` at £150.00. | Reject the ineligible coupon with `IllegalArgumentException`. | `testRejectsVip15ForPremiumCustomer` |
| 7 | Enter ` discount10 ` on an eligible £100.00 basket. | Accept the lower case code with surrounding spaces and deduct £10.00. | `testIgnoresCouponCaseAndSurroundingSpaces` |
| 7 | Enter `UNKNOWN` on a £200.00 basket. | Reject the unknown coupon with `IllegalArgumentException`. | `testRejectsUnknownCoupon` |
| 7 | Enter `VIP15,DISCOUNT10` on a £200.00 VIP basket. | Reject the combined coupon input with `IllegalArgumentException`. | `testRejectsMultipleCoupons` |
| 3, 4, 5, 8, 9 | A £150.00 VIP basket qualifies for both product discounts and `DISCOUNT10`. | Deduct £12.00 for quantity, £3.00 for accessories and £10.00 for the coupon. Final total is £125.00. | `testCapsDiscount10AtTenPoundsWhenBothProductDiscountsApply` |
| 9, 10 | A basket contains a free item and no coupon. | Final merchandise total is £0.00. | `testReturnsZeroForZeroPricedMerchandise` |

## Delivery fees and option validation

Test file: [DeliveryServiceTest.java](../src/test/java/onlineshopping/DeliveryServiceTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 11, 12 | Regular, Standard, discounted total £74.99. | Charge £4.99. | `testChargesRegularStandardBelowThreshold` |
| 12 | Regular, Standard, discounted total £75.00. | Charge £0.00. | `testWaivesRegularStandardAtThreshold` |
| 12 | Regular, Standard, discounted total £75.01. | Charge £0.00. | `testWaivesRegularStandardAboveThreshold` |
| 11, 12 | Premium, Standard, discounted total £74.99. | Charge £4.99. | `testChargesPremiumStandardBelowThreshold` |
| 12 | Premium, Standard, discounted total £75.00. | Charge £0.00. | `testWaivesPremiumStandardAtThreshold` |
| 12 | Premium, Standard, discounted total £75.01. | Charge £0.00. | `testWaivesPremiumStandardAboveThreshold` |
| 12 | VIP, Standard, discounted total £0.00. | Charge £0.00. | `testWaivesStandardDeliveryForVipCustomer` |
| 11, 12 | VIP, Express, discounted total £0.00. | Charge £9.99. | `testChargesExpressDeliveryForVipCustomer` |
| 11, 12 | Regular, Express, discounted total £75.01. | Charge £9.99. | `testChargesExpressDeliveryForRegularCustomer` |
| 11, 12 | Premium, Express, discounted total £75.01. | Charge £9.99. | `testChargesExpressDeliveryForPremiumCustomer` |
| 13 | Convert the unsupported name `DRONE` to a delivery option. | `DeliveryOption.valueOf` throws `IllegalArgumentException` before the service is called. | `testRejectsUnsupportedDeliveryOptionName` |
| 13 | Call the delivery service with a valid total and customer type, and a missing option (`null`). | The service throws `NullPointerException`. | `testRejectsMissingDeliveryOption` |

## Order behaviour with real services

Test file: [OrderIntegrationTest.java](../src/test/java/onlineshopping/OrderIntegrationTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 3, 10, 12, 13, 14 | A Premium Standard order starts at £78.00 and a quantity discount reduces it to £74.10. | Charge £4.99 for delivery and expect £79.09 payable. | `testChargesStandardDeliveryWhenProductDiscountTakesTotalBelowThreshold` |
| 5, 8, 12, 13, 14 | A Regular Standard order starts at £100.00 and `DISCOUNT10` reduces it to £90.00. | Delivery is free and £90.00 is payable. | `testUsesCouponDiscountInAmountPayable` |
| — | Submit a valid customer and delivery choice with an empty cart. | Reject the order with `IllegalArgumentException`. | `testRejectsEmptyOrder` |

## Order service in isolation

Test file: [OrderServiceTest.java](../src/test/java/onlineshopping/OrderServiceTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 13, 14 | Stub the discount service to return £100.00 original and £90.00 final, with delivery stubbed at £0.00. | Pass £90.00 to delivery and expect £90.00 payable. The test verifies the argument with Mockito. | `testPassesFinalDiscountedTotalToDeliveryService` |

## Monetary rounding and supporting cart behaviour

Test file: [ShoppingCartTest.java](../src/test/java/onlineshopping/ShoppingCartTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| 10 | Add one item priced at £5.126. | Round the subtotal to £5.13. | `testRoundsTheSubtotalToTwoDecimalPlaces` |
| — | Add the same £26.00 monitor twice, for a total of three units. | Keep one cart entry with three units and a subtotal of £78.00. | `testCombinesRepeatedAddsOfTheSameProduct` |
| — | Add two more units when the cart already contains two and stock is three. | Reject the addition and keep the original two units. | `testRejectsAnAddThatWouldExceedStockAndKeepsExistingQuantity` |
| — | Update a £26.00 monitor from one unit to three. | Report three units and a subtotal of £78.00. | `testUpdatesQuantityAndSubtotal` |
| — | Remove a monitor entry with two units from a cart that also contains a £5.00 cable. | Remove both units of the monitor and leave the cable as the only entry. | `testRemovesAllUnitsOfTheSelectedProduct` |
| — | Clear a populated cart. | The cart is empty and the subtotal is £0.00. | `testClearsTheCart` |

## Structural tests added after inspecting baseline coverage

Test file: [StructuralCoverageTest.java](../src/test/java/onlineshopping/StructuralCoverageTest.java)

| Requirement | Scenario | Expected result | Test method |
| --- | --- | --- | --- |
| — | Calculate Standard delivery for a negative total of −£0.01. | Throw `IllegalArgumentException`. | `testRejectsNegativeDeliveryTotal` |
| — | Calculate discounts for a list containing a null item. | Throw `NullPointerException`. | `testRejectsNullEntryInDiscountInput` |
| — | Try to add four units when stock is three. | Reject the addition and leave the cart empty. | `testRejectsAnInitialQuantityAboveStock` |
| — | Try to update one unit to four when stock is three. | Reject the update and keep one unit. | `testRejectsAnUpdateAboveStockAndKeepsExistingQuantity` |
| — | Try to update an existing item to zero units. | Reject the update and keep one unit. | `testRejectsAnUpdateToZeroAndKeepsExistingQuantity` |
| — | Update a product absent from the cart to a valid in-stock quantity. | Throw `IllegalArgumentException`. | `testRejectsAnUpdateForAProductOutsideTheCart` |
| — | Remove a product absent from the cart. | Throw `IllegalArgumentException`. | `testRejectsRemovalOfAProductOutsideTheCart` |
| — | Add zero units of an in-stock product. | Throw `IllegalArgumentException`. | `testRejectsAddingZeroUnits` |

## Limits of the evidence

| Requirement | What the suite can show |
| --- | --- |
| 7. Coupon validation | The formatting test combines lower case and surrounding spaces in one input. It exercises both together, so a failure on that row alone does not tell you which part caused it. The combined coupon test checks one string containing two coupon names; the Java API accepts a single coupon argument. |
| 9. Discount limits | The suite checks the stated coupon values and the zero boundary. With the available coupons and discount rules, a qualifying order cannot leave a balance smaller than its coupon through the public API. The zero-priced item test starts at £0.00 and does not directly exercise a coupon being clipped to the remaining balance. |
| 10. Monetary calculations | The cart test directly exercises rounding of a fractional penny. Pricing tests assert exact two-decimal amounts, but the suite does not check rounding at every intermediate step. |
| 13. Delivery eligibility and validation | The real-service test distinguishes the original total from the total after a product discount. The Mockito test checks that the post-coupon total reaches delivery. For non-VIP customers, DISCOUNT10 leaves at least £90.00 and VIP15 is unavailable, so a valid coupon alone cannot change eligibility at £75.00. The enum rejects an unsupported name before the delivery service is invoked; the service test separately checks a missing option. |
