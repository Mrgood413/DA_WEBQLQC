package com.example.WebCafe.dto.response;

import java.math.BigDecimal;

public record RevenueByProductRow(
		Integer productId,
		String productName,
		long quantitySold,
		BigDecimal revenue
) {
}
