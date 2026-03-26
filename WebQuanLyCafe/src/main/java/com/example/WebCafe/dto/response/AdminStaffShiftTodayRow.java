package com.example.WebCafe.dto.response;

import java.util.List;

/**
 * Dòng dữ liệu hiển thị cho bảng "Nhân viên có ca trong ngày" trên admin dashboard.
 */
public record AdminStaffShiftTodayRow(
		Integer userId,
		String username,
		String fullName,
		String phone,
		Integer age,
		String gender,
		Boolean active,
		List<String> shiftLabels
) {
}

