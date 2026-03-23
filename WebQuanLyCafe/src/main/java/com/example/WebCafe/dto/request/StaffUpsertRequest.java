package com.example.WebCafe.dto.request;

import com.example.WebCafe.model.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StaffUpsertRequest(
		@NotBlank @Size(max = 50) String username,
		@Size(max = 255) String password,
		@NotBlank @Size(max = 100) String fullName,
		@Size(max = 20) String phone,
		@NotNull Gender gender,
		@Min(0) Integer age,
		Boolean active
) {
}
