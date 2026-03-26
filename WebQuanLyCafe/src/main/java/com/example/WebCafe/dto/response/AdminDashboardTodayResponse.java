package com.example.WebCafe.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response cho trang admin dashboard (hôm nay).
 */
public record AdminDashboardTodayResponse(
		long activeStaffCount,
		long ordersToday,
		BigDecimal revenueToday,
		List<AdminStaffShiftTodayRow> staffToday
) {
}

