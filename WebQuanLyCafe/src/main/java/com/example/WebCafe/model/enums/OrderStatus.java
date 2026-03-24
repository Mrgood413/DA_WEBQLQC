package com.example.WebCafe.model.enums;

/**
 * Trạng thái đơn (một cột): tiến độ phục vụ và thanh toán — {@link #PAID} = đã thanh toán / kết thúc.
 */
public enum OrderStatus {
	PENDING,
	PREPARING,
	DONE,
	/** Đã thanh toán (thay cho cột {@code paid} riêng). */
	PAID
}
