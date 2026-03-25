package com.example.WebCafe.dto.request;

import java.util.List;

public record StaffShiftsUpdateRequest(
		List<Integer> shiftIds
) {
}
