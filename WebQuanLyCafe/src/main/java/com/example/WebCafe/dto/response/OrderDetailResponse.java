package com.example.WebCafe.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
		Integer id,
		boolean paid,
		Integer tableNumber,
		LocalDateTime createdAt,
		List<OrderItemResponse> items,
		String progressLabel,
		boolean canCancel
) {
}
