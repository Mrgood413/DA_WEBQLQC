package com.example.WebCafe.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
		Integer id,
		String name,
		String description,
		BigDecimal price,
		String imageUrl,
		Integer quantity,
		Boolean available,
		Integer categoryId,
		String categoryName
) {
}
