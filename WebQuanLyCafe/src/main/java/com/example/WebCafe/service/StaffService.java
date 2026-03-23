package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.PaymentRequest;
import com.example.WebCafe.dto.request.StaffProductUpdateRequest;
import com.example.WebCafe.dto.response.OrderQueueResponse;
import com.example.WebCafe.dto.response.ProductResponse;

import java.util.List;

public interface StaffService {

	List<ProductResponse> listProductsForStaff();

	ProductResponse updateProduct(Integer productId, StaffProductUpdateRequest request);

	List<OrderQueueResponse> listQueue();

	void confirmOrder(Integer orderId);

	void recordPayment(Integer orderId, PaymentRequest request);
}
