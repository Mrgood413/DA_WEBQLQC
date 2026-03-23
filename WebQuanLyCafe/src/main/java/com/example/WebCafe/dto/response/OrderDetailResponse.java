package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
		Integer id,
		OrderStatus status,
		Integer tableNumber,
		LocalDateTime createdAt,
		LocalDateTime confirmedAt,
		List<OrderItemResponse> items,
		String progressLabel,
		boolean canCancel
) {
}
