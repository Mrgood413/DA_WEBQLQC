package com.example.WebCafe.controller;

import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.PaymentRequest;
import com.example.WebCafe.dto.request.StaffProductUpdateRequest;
import com.example.WebCafe.dto.response.CategoryOptionResponse;
import com.example.WebCafe.dto.response.OrderQueueResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('STAFF')")
@Validated
public class StaffController {

	private final StaffService staffService;

	public StaffController(StaffService staffService) {
		this.staffService = staffService;
	}

	@GetMapping("/products")
	public List<ProductResponse> listProducts() {
		return staffService.listProductsForStaff();
	}

	@GetMapping("/categories")
	public List<CategoryOptionResponse> listCategories() {
		return staffService.listCategories();
	}

	@PostMapping("/products")
	public ProductResponse createProduct(@Valid @RequestBody AdminProductRequest request) {
		return staffService.createProduct(request);
	}

	@PatchMapping("/products/{productId}")
	public ProductResponse updateProduct(@PathVariable Integer productId,
			@RequestBody StaffProductUpdateRequest request) {
		return staffService.updateProduct(productId, request);
	}

	@GetMapping("/queue")
	public List<OrderQueueResponse> queue() {
		return staffService.listQueue();
	}

	@PostMapping("/orders/{orderId}/confirm")
	public ResponseEntity<Void> confirmOrder(@PathVariable Integer orderId) {
		staffService.confirmOrder(orderId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/orders/{orderId}/payment")
	public ResponseEntity<Void> payment(@PathVariable Integer orderId,
			@Valid @RequestBody PaymentRequest request) {
		staffService.recordPayment(orderId, request);
		return ResponseEntity.noContent().build();
	}
}
