package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record OrderQueueResponse(
		Integer orderId,
		Integer tableNumber,
		OrderStatus status,
		int itemCount,
		LocalDateTime createdAt,
		LocalDateTime lastItemUpdatedAt,
		boolean hasMoreItems,
		List<OrderItemResponse> items
) {
	@JsonProperty("paid")
	public boolean paid() {
		return status == OrderStatus.PAID;
	}
}
