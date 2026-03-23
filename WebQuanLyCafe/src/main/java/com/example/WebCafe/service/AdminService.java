package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.RevenueQueryRequest;
import com.example.WebCafe.dto.request.StaffUpsertRequest;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.dto.response.RevenueDashboardResponse;
import com.example.WebCafe.dto.response.StaffListResponse;

import java.util.List;

public interface AdminService {

	List<ProductResponse> listProducts();

	ProductResponse getProduct(Integer id);

	ProductResponse createProduct(AdminProductRequest request);

	ProductResponse updateProduct(Integer id, AdminProductRequest request);

	void deleteProduct(Integer id);

	List<StaffListResponse> listStaff();

	void createStaff(StaffUpsertRequest request);

	void updateStaff(Integer userId, StaffUpsertRequest request);

	void deleteStaff(Integer userId);

	RevenueDashboardResponse revenue(RevenueQueryRequest query);

	byte[] exportRevenueExcel(RevenueQueryRequest query);
}
