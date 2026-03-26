package com.example.WebCafe.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConfirmOrderRequest(
		@Valid List<AddCartItemRequest> items,
		@Size(max = 20) String paymentMethod,
		@Size(max = 255) String address,
		Boolean useProfileAddress
) {
}

