package com.example.WebCafe.security;

import java.io.Serializable;

/**
 * Khách hàng không có bản ghi {@code users}; phiên lưu id khách ẩn danh.
 */
public record CustomerPrincipal(String guestId) implements Serializable {
}
