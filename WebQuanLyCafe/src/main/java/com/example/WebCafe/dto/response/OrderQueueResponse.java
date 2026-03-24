package com.example.WebCafe.dto.response;

public record OrderQueueResponse(
		Integer orderId,
		Integer tableNumber,
		boolean paid,
		int itemCount
) {
}
