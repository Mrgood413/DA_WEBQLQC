package com.example.WebCafe.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
		Integer id,
		Integer productId,
		String productName,
		Integer quantity,
		BigDecimal unitPrice,
		BigDecimal lineTotal,
		boolean updated
) {
}
