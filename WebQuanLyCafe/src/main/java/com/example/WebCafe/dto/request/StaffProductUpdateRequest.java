package com.example.WebCafe.dto.request;

public record StaffProductUpdateRequest(
		Integer quantity,
		Boolean available
) {
}
