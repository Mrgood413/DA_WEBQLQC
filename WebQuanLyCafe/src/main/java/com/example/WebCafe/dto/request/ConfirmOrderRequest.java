package com.example.WebCafe.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record ConfirmOrderRequest(
		@Valid List<AddCartItemRequest> items
) {
}

