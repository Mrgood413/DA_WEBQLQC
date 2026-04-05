package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.Min;

/**
 * Nhân viên có thể cập nhật tồn kho và/hoặc trạng thái bán (ẩn món). Các trường khác do Admin.
 */
public record StaffProductPatchRequest(
		@Min(value = 0, message = "Số lượng không được âm")
		Integer quantity,
		Boolean available
) {
}
