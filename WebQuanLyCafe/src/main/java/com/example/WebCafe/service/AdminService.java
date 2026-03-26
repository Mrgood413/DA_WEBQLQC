package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AdminCategoryRequest;
import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.RevenueQueryRequest;
import com.example.WebCafe.dto.request.StaffShiftsUpdateRequest;
import com.example.WebCafe.dto.request.StaffUpsertRequest;
import com.example.WebCafe.dto.response.AdminDashboardTodayResponse;
import com.example.WebCafe.dto.response.CategoryAdminResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.dto.response.RevenueDashboardResponse;
import com.example.WebCafe.dto.response.ShiftResponse;
import com.example.WebCafe.dto.response.StaffListResponse;

import java.util.List;

public interface AdminService {

	List<ProductResponse> listProducts();

	ProductResponse getProduct(Integer id);

	ProductResponse createProduct(AdminProductRequest request);

	ProductResponse updateProduct(Integer id, AdminProductRequest request);

	void deleteProduct(Integer id);

	List<CategoryAdminResponse> listCategories();

	CategoryAdminResponse createCategory(AdminCategoryRequest request);

	CategoryAdminResponse updateCategory(Integer id, AdminCategoryRequest request);

	void deleteCategory(Integer id);

	List<StaffListResponse> listStaff();

	StaffListResponse createStaff(StaffUpsertRequest request);

	void updateStaff(Integer userId, StaffUpsertRequest request);

	void deleteStaff(Integer userId);

	List<ShiftResponse> listShiftDefinitions();

	List<Integer> getStaffShiftIds(Integer userId);

	void replaceStaffShifts(Integer userId, StaffShiftsUpdateRequest request);

	RevenueDashboardResponse revenue(RevenueQueryRequest query);

	byte[] exportRevenueExcel(RevenueQueryRequest query);

	AdminDashboardTodayResponse dashboardToday();
}
