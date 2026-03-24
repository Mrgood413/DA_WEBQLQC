package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderQueueResponse(
		Integer orderId,
		Integer tableNumber,
		OrderStatus status,
		int itemCount
) {
	@JsonProperty("paid")
	public boolean paid() {
		return status == OrderStatus.PAID;
	}
}
