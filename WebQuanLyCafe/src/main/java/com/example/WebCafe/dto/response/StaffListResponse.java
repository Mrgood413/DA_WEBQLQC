package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.Gender;

public record StaffListResponse(
		Integer userId,
		String username,
		String fullName,
		Gender gender,
		Integer age,
		Boolean active
) {
}
