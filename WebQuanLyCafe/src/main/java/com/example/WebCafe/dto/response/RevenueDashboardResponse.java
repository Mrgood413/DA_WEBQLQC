package com.example.WebCafe.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record RevenueDashboardResponse(
		BigDecimal totalRevenue,
		long totalOrders,
		List<RevenueByProductRow> byProduct,
		List<RevenueChartPoint> chart
) {
}
