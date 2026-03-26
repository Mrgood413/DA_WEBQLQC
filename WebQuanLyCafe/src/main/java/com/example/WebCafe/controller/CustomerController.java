package com.example.WebCafe.controller;

import com.example.WebCafe.dto.request.AddCartItemRequest;
import com.example.WebCafe.dto.request.ConfirmOrderRequest;
import com.example.WebCafe.dto.request.SetTableRequest;
import com.example.WebCafe.dto.response.CustomerTableResponse;
import com.example.WebCafe.dto.response.OrderDetailResponse;
import com.example.WebCafe.dto.response.OrderSummaryResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.service.CustomerOrderService;
import com.example.WebCafe.service.CustomerSessionService;
import com.example.WebCafe.service.MenuService;
import com.example.WebCafe.service.OrderMilestoneEventService;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@Validated
public class CustomerController {

	private final CustomerSessionService customerSessionService;
	private final MenuService menuService;
	private final CustomerOrderService customerOrderService;
	private final OrderMilestoneEventService orderMilestoneEventService;

	public CustomerController(CustomerSessionService customerSessionService, MenuService menuService,
			CustomerOrderService customerOrderService,
			OrderMilestoneEventService orderMilestoneEventService) {
		this.customerSessionService = customerSessionService;
		this.menuService = menuService;
		this.customerOrderService = customerOrderService;
		this.orderMilestoneEventService = orderMilestoneEventService;
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
	public ResponseEntity<OrderDetailResponse> confirm(@PathVariable Integer orderId,
			@Valid @RequestBody ConfirmOrderRequest request,
			HttpSession session) {
		OrderDetailResponse res = customerOrderService.confirmOrder(orderId, request, session);
		return ResponseEntity.ok(res);
	}

	@PostMapping("/orders/{orderId}/cancel")
	public ResponseEntity<Void> cancel(@PathVariable Integer orderId, HttpSession session) {
		customerOrderService.cancelOrder(orderId, session);
		return ResponseEntity.noContent().build();
	}

	/**
	 * SSE stream: milestone 2–3 khi staff xác nhận, milestone 4 khi thanh toán.
	 */
	@GetMapping("/orders/{orderId}/events")
	public SseEmitter events(@PathVariable Integer orderId) {
		return orderMilestoneEventService.register(orderId);
	}
}
