package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;

public record OrderQueueResponse(
		Integer orderId,
		Integer tableNumber,
		OrderStatus status,
		int itemCount
) {
}
