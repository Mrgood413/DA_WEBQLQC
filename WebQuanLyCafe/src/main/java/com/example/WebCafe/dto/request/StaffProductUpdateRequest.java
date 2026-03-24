package com.example.WebCafe.dto.request;

import java.math.BigDecimal;

/**
 * PATCH món: chỉ các trường khác {@code null} mới được cập nhật.
 */
public record StaffProductUpdateRequest(
		String name,
		String description,
		BigDecimal price,
		String imageUrl,
		Integer quantity,
		Boolean available,
		Integer categoryId
) {
}
