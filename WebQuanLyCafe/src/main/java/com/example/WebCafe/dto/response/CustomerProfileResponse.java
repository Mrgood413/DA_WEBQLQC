package com.example.WebCafe.dto.response;

public record CustomerProfileResponse(
		String username,
		String fullName,
		String phone,
		String address
) {
}
