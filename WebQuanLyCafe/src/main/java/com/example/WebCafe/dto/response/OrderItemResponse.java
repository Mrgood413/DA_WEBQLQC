package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderItemStatus;

import java.math.BigDecimal;

public record OrderItemResponse(
		Integer id,
		Integer productId,
		String productName,
		Integer quantity,
		BigDecimal unitPrice,
		BigDecimal lineTotal,
		OrderItemStatus status
) {
}
