package com.example.WebCafe.dto.response;

import java.math.BigDecimal;

public record RevenueChartPoint(
		String label,
		BigDecimal amount
) {
}
