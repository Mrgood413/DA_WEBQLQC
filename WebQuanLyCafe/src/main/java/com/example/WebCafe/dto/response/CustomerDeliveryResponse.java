package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.DeliveryStatus;
import com.example.WebCafe.model.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerDeliveryResponse(
		Integer deliveryId,
		Integer orderId,
		DeliveryStatus status,
		PaymentMethod paymentMethod,
		String address,
		List<String> orderedItems,
		LocalDateTime createdAt,
		LocalDateTime deliveredAt
) {
}
