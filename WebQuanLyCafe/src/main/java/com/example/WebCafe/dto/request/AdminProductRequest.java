package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdminProductRequest(
		@NotBlank @Size(max = 150) String name,
		String description,
		@NotNull @DecimalMin("0.0") BigDecimal price,
		@Size(max = 255) String imageUrl,
		/** Bị bỏ qua: tồn kho do nhân viên cập nhật qua API riêng. */
		Integer quantity,
		Boolean available,
		@NotNull Integer categoryId
) {
}
