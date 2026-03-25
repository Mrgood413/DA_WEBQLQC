package com.example.WebCafe.dto.response;

import java.util.List;

public record AuthMeResponse(
		String mode,
		String username,
		String fullName,
		String guestId,
		List<String> roles,
		Integer tableNumber
) {
}
