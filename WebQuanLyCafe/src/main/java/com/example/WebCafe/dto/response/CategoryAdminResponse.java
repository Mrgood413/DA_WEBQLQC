package com.example.WebCafe.dto.response;

public record CategoryAdminResponse(
		Integer id,
		String name,
		String imageUrl,
		boolean hidden,
		long productCount
) {
}
