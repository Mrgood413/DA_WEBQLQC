package com.example.WebCafe.dto.response;

import com.example.WebCafe.model.enums.ShiftTime;
import com.example.WebCafe.model.enums.Weekday;

public record ShiftResponse(
		Integer id,
		Weekday dayOfWeek,
		ShiftTime shiftTime
) {
}
