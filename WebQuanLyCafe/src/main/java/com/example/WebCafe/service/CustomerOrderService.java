package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AddCartItemRequest;
import com.example.WebCafe.dto.request.ConfirmOrderRequest;
import com.example.WebCafe.dto.response.OrderDetailResponse;
import com.example.WebCafe.dto.response.OrderSummaryResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface CustomerOrderService {

	List<OrderSummaryResponse> listOrders(HttpSession session);

	OrderDetailResponse getOrder(Integer orderId, HttpSession session);

	OrderDetailResponse createCart(HttpSession session);

	OrderDetailResponse addItem(Integer orderId, AddCartItemRequest request, HttpSession session);

	OrderDetailResponse confirmOrder(Integer orderId, ConfirmOrderRequest request, HttpSession session);

	void cancelOrder(Integer orderId, HttpSession session);
}
