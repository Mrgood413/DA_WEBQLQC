package com.example.WebCafe.dto.response;

/**
 * Một ô trong lưới lịch (ngày trong tuần + khung ca).
 */
public record StaffMyShiftSlotResponse(
		String weekday,
		String shiftTime
) {
}
