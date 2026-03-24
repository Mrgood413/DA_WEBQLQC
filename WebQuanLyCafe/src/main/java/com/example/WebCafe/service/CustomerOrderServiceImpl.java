package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AddCartItemRequest;
import com.example.WebCafe.dto.response.OrderDetailResponse;
import com.example.WebCafe.dto.response.OrderSummaryResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Khung xử lý giỏ / đơn — logic lưu DB sẽ bổ sung sau (phiên khách, order CART, v.v.).
 */
@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

	@Override
	public List<OrderSummaryResponse> listOrders(HttpSession session) {
		return List.of();
	}

	@Override
	public OrderDetailResponse getOrder(Integer orderId, HttpSession session) {
		return new OrderDetailResponse(
				orderId,
				false,
				null,
				null,
				Collections.emptyList(),
				"Giỏ hàng",
				true);
	}

	@Override
	public OrderDetailResponse createCart(HttpSession session) {
		return new OrderDetailResponse(
				null,
				false,
				null,
				null,
				Collections.emptyList(),
				"Giỏ hàng",
				true);
	}

	@Override
	public OrderDetailResponse addItem(Integer orderId, AddCartItemRequest request, HttpSession session) {
		return getOrder(orderId, session);
	}

	@Override
	public void confirmOrder(Integer orderId, HttpSession session) {
		// TODO: chuyển trạng thái đơn sau khi khách xác nhận
	}

	@Override
	public void cancelOrder(Integer orderId, HttpSession session) {
		// TODO: kiểm tra trạng thái trước khi hủy
	}
}
