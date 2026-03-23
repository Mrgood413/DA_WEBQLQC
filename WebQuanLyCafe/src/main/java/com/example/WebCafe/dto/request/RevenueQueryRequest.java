package com.example.WebCafe.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record RevenueQueryRequest(
		RevenuePeriod period,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
) {
}
