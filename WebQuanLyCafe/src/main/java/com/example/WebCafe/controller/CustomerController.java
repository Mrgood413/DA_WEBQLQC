package com.example.WebCafe.controller;

import com.example.WebCafe.dto.request.AddCartItemRequest;
import com.example.WebCafe.dto.request.SetTableRequest;
import com.example.WebCafe.dto.response.CustomerTableResponse;
import com.example.WebCafe.dto.response.OrderDetailResponse;
import com.example.WebCafe.dto.response.OrderSummaryResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.service.CustomerOrderService;
import com.example.WebCafe.service.CustomerSessionService;
import com.example.WebCafe.service.MenuService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@Validated
public class CustomerController {

	private final CustomerSessionService customerSessionService;
	private final MenuService menuService;
	private final CustomerOrderService customerOrderService;

	public CustomerController(CustomerSessionService customerSessionService, MenuService menuService,
			CustomerOrderService customerOrderService) {
		this.customerSessionService = customerSessionService;
		this.menuService = menuService;
		this.customerOrderService = customerOrderService;
	}

	@PostMapping("/table")
	public ResponseEntity<Void> setTable(@Valid @RequestBody SetTableRequest request, HttpSession session) {
		customerSessionService.setTable(session, request.tableNumber());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/table")
	public CustomerTableResponse getTable(HttpSession session) {
		return new CustomerTableResponse(customerSessionService.getTableNumber(session));
	}

	@GetMapping("/menu")
	public List<ProductResponse> menu() {
		return menuService.listMenuItems();
	}

	@GetMapping("/orders")
	public List<OrderSummaryResponse> listOrders(HttpSession session) {
		return customerOrderService.listOrders(session);
	}

	@GetMapping("/orders/{orderId}")
	public OrderDetailResponse getOrder(@PathVariable Integer orderId, HttpSession session) {
		return customerOrderService.getOrder(orderId, session);
	}

	/** Tạo đơn giỏ (CART) — logic DB sẽ bổ sung sau. */
	@PostMapping("/orders")
	public OrderDetailResponse createCart(HttpSession session) {
		return customerOrderService.createCart(session);
	}

	@PostMapping("/orders/{orderId}/items")
	public OrderDetailResponse addItem(@PathVariable Integer orderId,
			@Valid @RequestBody AddCartItemRequest request,
			HttpSession session) {
		return customerOrderService.addItem(orderId, request, session);
	}

	@PostMapping("/orders/{orderId}/confirm")
	public ResponseEntity<Void> confirm(@PathVariable Integer orderId, HttpSession session) {
		customerOrderService.confirmOrder(orderId, session);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/orders/{orderId}/cancel")
	public ResponseEntity<Void> cancel(@PathVariable Integer orderId, HttpSession session) {
		customerOrderService.cancelOrder(orderId, session);
		return ResponseEntity.noContent().build();
	}
}
