package com.example.WebCafe.service;

import com.example.WebCafe.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductInventoryServiceTest {

	private final ProductInventoryService service = new ProductInventoryService();

	@Test
	void wbInv01_inputInvalid_requestedQtyZero_returnsZero() {
		Product product = new Product();
		product.setAvailable(true);
		product.setQuantity(5);

		int allocated = service.allocateForNewOrderLine(product, 0, 0);

		assertEquals(0, allocated);
	}

	@Test
	void wbInv02_productUnavailable_returnsZero() {
		Product product = new Product();
		product.setAvailable(false);
		product.setQuantity(10);

		int allocated = service.allocateForNewOrderLine(product, 2, 0);

		assertEquals(0, allocated);
	}

	@Test
	void wbInv03_unlimitedStock_returnsRequestedQty() {
		Product product = new Product();
		product.setAvailable(true);
		product.setQuantity(null);

		int allocated = service.allocateForNewOrderLine(product, 3, 0);

		assertEquals(3, allocated);
	}

	@Test
	void wbInv04_limitedStock_returnsRemainingAfterAlreadyAllocated() {
		Product product = new Product();
		product.setAvailable(true);
		product.setQuantity(3);

		int allocated = service.allocateForNewOrderLine(product, 5, 1);

		assertEquals(2, allocated);
	}
}
