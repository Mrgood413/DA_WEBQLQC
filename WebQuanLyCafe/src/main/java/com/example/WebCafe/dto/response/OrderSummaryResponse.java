package com.example.WebCafe.dto.response;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
		Integer id,
		boolean paid,
		Integer tableNumber,
		LocalDateTime createdAt,
		String progressLabel
) {
}
