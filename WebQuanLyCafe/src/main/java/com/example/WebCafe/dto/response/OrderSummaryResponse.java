package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
		Integer id,
		OrderStatus status,
		Integer tableNumber,
		LocalDateTime createdAt,
		LocalDateTime confirmedAt,
		String progressLabel
) {
}
