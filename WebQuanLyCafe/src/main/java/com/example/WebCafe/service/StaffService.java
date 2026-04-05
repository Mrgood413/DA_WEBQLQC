package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.PaymentRequest;
import com.example.WebCafe.dto.request.StaffProductPatchRequest;
import com.example.WebCafe.dto.response.CategoryOptionResponse;
import com.example.WebCafe.dto.response.OrderQueueResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.dto.response.StaffMyShiftSlotResponse;

import java.util.List;

public interface StaffService {

	List<ProductResponse> listProductsForStaff();

	List<CategoryOptionResponse> listCategories();

	ProductResponse patchProduct(Integer productId, StaffProductPatchRequest request);

	List<OrderQueueResponse> listQueue();

	List<StaffMyShiftSlotResponse> getMyShiftSlots();

	void confirmOrder(Integer orderId);

	void recordPayment(Integer orderId, PaymentRequest request);
}
