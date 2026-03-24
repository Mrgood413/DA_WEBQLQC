package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
		Integer id,
		OrderStatus status,
		Integer tableNumber,
		LocalDateTime createdAt,
		String progressLabel
) {
	@JsonProperty("paid")
	public boolean paid() {
		return status == OrderStatus.PAID;
	}
}
