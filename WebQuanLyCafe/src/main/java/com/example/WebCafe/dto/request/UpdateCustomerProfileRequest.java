package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerProfileRequest(
		@NotBlank @Size(max = 100) String fullName,
		@NotBlank @Size(max = 255) String address
) {
}

