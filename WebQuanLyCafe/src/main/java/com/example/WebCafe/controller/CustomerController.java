package com.example.WebCafe.controller;

import com.example.WebCafe.dto.request.AddCartItemRequest;
import com.example.WebCafe.dto.request.ConfirmOrderRequest;
import com.example.WebCafe.dto.request.SetTableRequest;
import com.example.WebCafe.dto.request.UpdateCustomerProfileRequest;
import com.example.WebCafe.dto.response.CustomerDeliveryResponse;
import com.example.WebCafe.dto.response.CustomerProfileResponse;
import com.example.WebCafe.dto.response.CustomerTableResponse;
import com.example.WebCafe.dto.response.OrderDetailResponse;
import com.example.WebCafe.dto.response.OrderSummaryResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.model.Customer;
import com.example.WebCafe.model.Delivery;
import com.example.WebCafe.model.Payment;
import com.example.WebCafe.model.User;
import com.example.WebCafe.repository.CafeOrderRepository;
import com.example.WebCafe.repository.CustomerRepository;
import com.example.WebCafe.repository.DeliveryRepository;
import com.example.WebCafe.repository.PaymentRepository;
import com.example.WebCafe.repository.UserRepository;
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
import org.springframework.web.bind.annotation.PutMapping;
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
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final DeliveryRepository deliveryRepository;
	private final PaymentRepository paymentRepository;
	private final CafeOrderRepository cafeOrderRepository;

	public CustomerController(CustomerSessionService customerSessionService, MenuService menuService,
			CustomerOrderService customerOrderService,
			OrderMilestoneEventService orderMilestoneEventService,
			UserRepository userRepository,
			CustomerRepository customerRepository,
			DeliveryRepository deliveryRepository,
			PaymentRepository paymentRepository,
			CafeOrderRepository cafeOrderRepository) {
		this.customerSessionService = customerSessionService;
		this.menuService = menuService;
		this.customerOrderService = customerOrderService;
		this.orderMilestoneEventService = orderMilestoneEventService;
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.deliveryRepository = deliveryRepository;
		this.paymentRepository = paymentRepository;
		this.cafeOrderRepository = cafeOrderRepository;
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

	@GetMapping("/profile")
	public CustomerProfileResponse profile() {
		var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof String username)) {
			throw new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.BAD_REQUEST,
					"Chế độ khách không có hồ sơ tài khoản");
		}
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
		Customer customer = customerRepository.findById(user.getId())
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ khách hàng"));
		return new CustomerProfileResponse(
				user.getUsername(),
				user.getFullName(),
				user.getPhone(),
				customer.getAddress());
	}

	@PutMapping("/profile")
	public ResponseEntity<Void> updateProfile(@Valid @RequestBody UpdateCustomerProfileRequest request) {
		var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof String username)) {
			throw new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.BAD_REQUEST,
					"Chế độ khách không có hồ sơ tài khoản");
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
		Customer customer = customerRepository.findById(user.getId())
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ khách hàng"));

		String fullName = request.fullName() != null ? request.fullName().trim() : "";
		String address = request.address() != null ? request.address().trim() : "";

		if (fullName.isBlank() || address.isBlank()) {
			throw new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.BAD_REQUEST, "Thiếu thông tin cá nhân");
		}

		user.setFullName(fullName);
		customer.setAddress(address);
		userRepository.save(user);
		customerRepository.save(customer);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/deliveries")
	public List<CustomerDeliveryResponse> deliveries() {
		var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof String username)) {
			throw new org.springframework.web.server.ResponseStatusException(
					org.springframework.http.HttpStatus.BAD_REQUEST,
					"Chế độ khách không có lịch sử giao hàng");
		}
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
		Customer customer = customerRepository.findById(user.getId())
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ khách hàng"));

		return deliveryRepository.findHistoryByCustomerUserId(customer.getUserId())
				.stream()
				.map(this::toDeliveryResponse)
				.toList();
	}

	private CustomerDeliveryResponse toDeliveryResponse(Delivery d) {
		var createdAt = d.getCreatedAt();
		if (createdAt == null && d.getOrder() != null) {
			createdAt = d.getOrder().getCreatedAt();
		}
		Integer orderId = d.getOrder() != null ? d.getOrder().getId() : null;

		var hydratedOrder = orderId == null ? null : cafeOrderRepository.findWithItemsById(orderId).orElse(null);
		var sourceOrder = hydratedOrder != null ? hydratedOrder : d.getOrder();

		var orderedItems = sourceOrder == null || sourceOrder.getItems() == null
				? List.<String>of()
				: sourceOrder.getItems().stream()
						.map(oi -> {
							String name = oi.getProduct() != null ? oi.getProduct().getName() : "Món";
							Integer qty = oi.getQuantity() == null ? 0 : oi.getQuantity();
							return name + " x" + qty;
						})
						.toList();

		Payment payment = d.getPayment();
		if (payment == null && orderId != null) {
			payment = paymentRepository.findTopByOrder_IdOrderByIdDesc(orderId).orElse(null);
		}
		return new CustomerDeliveryResponse(
				d.getId(),
				orderId,
				d.getStatus(),
				payment != null ? payment.getMethod() : null,
				d.getCustomer() != null ? d.getCustomer().getAddress() : null,
				orderedItems,
				createdAt,
				d.getDeliveredAt());
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
