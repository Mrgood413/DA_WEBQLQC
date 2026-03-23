package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
		@NotNull LoginMode mode,
		@Size(max = 50) String username,
		@Size(max = 255) String password
) {
}
