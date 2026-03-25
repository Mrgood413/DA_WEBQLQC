package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCategoryRequest(
		@NotBlank @Size(max = 100) String name,
		@Size(max = 255) String imageUrl,
		Boolean hidden
) {
}
