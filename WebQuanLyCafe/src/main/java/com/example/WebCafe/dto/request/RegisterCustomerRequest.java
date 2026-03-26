package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
		@NotBlank @Size(max = 50) String username,
		@NotBlank @Size(max = 255) String password,
		@NotBlank @Size(max = 100) String fullName,
		@Size(max = 20) String phone,
		@NotBlank @Size(max = 255) String address
) {
}

