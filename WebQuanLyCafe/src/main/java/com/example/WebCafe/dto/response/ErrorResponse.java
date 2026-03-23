package com.example.WebCafe.dto.response;

import java.util.Map;

public record ErrorResponse(
		String error,
		String message,
		Map<String, String> fieldErrors
) {
}
