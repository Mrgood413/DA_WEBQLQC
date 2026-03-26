package com.example.WebCafe.controller;

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
import com.example.WebCafe.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/products")
	public List<ProductResponse> listProducts() {
		return adminService.listProducts();
	}

	@GetMapping("/products/{id}")
	public ProductResponse getProduct(@PathVariable Integer id) {
		return adminService.getProduct(id);
	}

	@PostMapping("/products")
	public ProductResponse createProduct(@Valid @RequestBody AdminProductRequest request) {
		return adminService.createProduct(request);
	}

	@PutMapping("/products/{id}")
	public ProductResponse updateProduct(@PathVariable Integer id, @Valid @RequestBody AdminProductRequest request) {
		return adminService.updateProduct(id, request);
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
		adminService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/categories")
	public List<CategoryAdminResponse> listCategories() {
		return adminService.listCategories();
	}

	@PostMapping("/categories")
	public ResponseEntity<CategoryAdminResponse> createCategory(@Valid @RequestBody AdminCategoryRequest request) {
		CategoryAdminResponse body = adminService.createCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PutMapping("/categories/{id}")
	public CategoryAdminResponse updateCategory(@PathVariable Integer id, @Valid @RequestBody AdminCategoryRequest request) {
		return adminService.updateCategory(id, request);
	}

	@DeleteMapping("/categories/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
		adminService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/shifts")
	public List<ShiftResponse> listShifts() {
		return adminService.listShiftDefinitions();
	}

	@GetMapping("/staff")
	public List<StaffListResponse> listStaff() {
		return adminService.listStaff();
	}

	@PostMapping("/staff")
	public ResponseEntity<StaffListResponse> createStaff(@Valid @RequestBody StaffUpsertRequest request) {
		StaffListResponse body = adminService.createStaff(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PutMapping("/staff/{userId}")
	public ResponseEntity<Void> updateStaff(@PathVariable Integer userId, @Valid @RequestBody StaffUpsertRequest request) {
		adminService.updateStaff(userId, request);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/staff/{userId}")
	public ResponseEntity<Void> deleteStaff(@PathVariable Integer userId) {
		adminService.deleteStaff(userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/staff/{userId}/shifts")
	public List<Integer> getStaffShifts(@PathVariable Integer userId) {
		return adminService.getStaffShiftIds(userId);
	}

	@PutMapping("/staff/{userId}/shifts")
	public ResponseEntity<Void> replaceStaffShifts(@PathVariable Integer userId,
			@RequestBody StaffShiftsUpdateRequest request) {
		adminService.replaceStaffShifts(userId, request != null ? request : new StaffShiftsUpdateRequest(List.of()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/revenue")
	public RevenueDashboardResponse revenue(@ModelAttribute RevenueQueryRequest query) {
		return adminService.revenue(query);
	}

	@GetMapping("/revenue/export")
	public ResponseEntity<byte[]> exportRevenue(@ModelAttribute RevenueQueryRequest query) {
		byte[] data = adminService.exportRevenueExcel(query);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"revenue.xlsx\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(data);
	}

	@GetMapping("/dashboard/today")
	public AdminDashboardTodayResponse dashboardToday() {
		return adminService.dashboardToday();
	}
}
