package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
		Integer id,
		OrderStatus status,
		Integer tableNumber,
		LocalDateTime createdAt,
		List<OrderItemResponse> items,
		String progressLabel,
		boolean canCancel
) {
	/** Tương đương {@code status == PAID} (JSON vẫn có field {@code paid}). */
	@JsonProperty("paid")
	public boolean paid() {
		return status == OrderStatus.PAID;
	}
}
