package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AddCartItemRequest;
import com.example.WebCafe.dto.request.ConfirmOrderRequest;
import com.example.WebCafe.dto.response.OrderDetailResponse;
import com.example.WebCafe.dto.response.OrderSummaryResponse;
import com.example.WebCafe.dto.response.OrderItemResponse;
import com.example.WebCafe.model.CafeOrder;
import com.example.WebCafe.model.CafeTable;
import com.example.WebCafe.model.OrderItem;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.model.enums.OrderStatus;
import com.example.WebCafe.repository.CafeOrderRepository;
import com.example.WebCafe.repository.CafeTableRepository;
import com.example.WebCafe.repository.ProductRepository;
import com.example.WebCafe.security.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Khung xử lý giỏ / đơn — logic lưu DB sẽ bổ sung sau (phiên khách, order CART, v.v.).
 */
@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

	private final CafeOrderRepository cafeOrderRepository;
	private final CafeTableRepository cafeTableRepository;
	private final ProductRepository productRepository;
	private final StaffQueueUpdateEventService staffQueueUpdateEventService;

	public CustomerOrderServiceImpl(CafeOrderRepository cafeOrderRepository,
			CafeTableRepository cafeTableRepository,
			ProductRepository productRepository,
			StaffQueueUpdateEventService staffQueueUpdateEventService) {
		this.cafeOrderRepository = cafeOrderRepository;
		this.cafeTableRepository = cafeTableRepository;
		this.productRepository = productRepository;
		this.staffQueueUpdateEventService = staffQueueUpdateEventService;
	}

	private Integer getTableNumber(HttpSession session) {
		Object attr = session.getAttribute(SessionKeys.TABLE_NUMBER);
		if (!(attr instanceof Integer)) return null;
		return (Integer) attr;
	}

	private String progressLabel(OrderStatus status) {
		return switch (status) {
			case PENDING -> "Chờ nhân viên xác nhận";
			case PREPARING -> "Đang chuẩn bị";
			case DONE -> "Hoàn thành";
			case PAID -> "Đã thanh toán";
		};
	}

	private OrderDetailResponse toDetail(CafeOrder o) {
		Integer tableNumber = o.getTable() != null ? o.getTable().getTableNumber() : null;
		List<OrderItemResponse> items = o.getItems() == null ? List.of() :
				o.getItems().stream()
						.map(this::toItem)
						.toList();
		boolean canCancel = o.getStatus() == OrderStatus.PENDING;
		return new OrderDetailResponse(
				o.getId(),
				o.getStatus(),
				tableNumber,
				o.getCreatedAt(),
				items,
				progressLabel(o.getStatus()),
				canCancel);
	}

	private OrderItemResponse toItem(OrderItem oi) {
		Product p = oi.getProduct();
		Integer productId = p != null ? p.getId() : null;
		String productName = p != null ? p.getName() : null;
		return new OrderItemResponse(
				oi.getId(),
				productId,
				productName,
				oi.getQuantity(),
				oi.getPrice(),
				oi.getPrice().multiply(java.math.BigDecimal.valueOf(oi.getQuantity())),
				oi.getUpdated());
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderSummaryResponse> listOrders(HttpSession session) {
		Integer tableNumber = getTableNumber(session);
		if (tableNumber == null) return List.of();
		return cafeOrderRepository.findByTable_TableNumberOrderByIdDesc(tableNumber)
				.stream()
				.map(o -> new OrderSummaryResponse(
						o.getId(),
						o.getStatus(),
						o.getTable().getTableNumber(),
						o.getCreatedAt(),
						progressLabel(o.getStatus())))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderDetailResponse getOrder(Integer orderId, HttpSession session) {
		Integer tableNumber = getTableNumber(session);
		if (tableNumber == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa chọn bàn");
		}
		CafeOrder o = cafeOrderRepository.findByIdAndTable_TableNumber(orderId, tableNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy order"));
		return toDetail(o);
	}

	@Override
	@Transactional
	public OrderDetailResponse createCart(HttpSession session) {
		Integer tableNumber = getTableNumber(session);
		if (tableNumber == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa chọn bàn");
		}

		CafeTable table = cafeTableRepository.findByTableNumber(tableNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bàn không tồn tại"));

		CafeOrder order = new CafeOrder();
		order.setTable(table);
		order.setStatus(OrderStatus.PENDING);

		CafeOrder saved = cafeOrderRepository.save(order);
		return toDetail(saved);
	}

	@Override
	@Transactional
	public OrderDetailResponse addItem(Integer orderId, AddCartItemRequest request, HttpSession session) {
		Integer tableNumber = getTableNumber(session);
		if (tableNumber == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa chọn bàn");
		}
		CafeOrder o = cafeOrderRepository.findByIdAndTable_TableNumber(orderId, tableNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy order"));

		// Gọi thêm khi đang pha chế hoặc đã hoàn thành món; sau khi thanh toán (PAID) không thêm được nữa
		if (o.getStatus() != OrderStatus.PREPARING && o.getStatus() != OrderStatus.DONE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể gọi thêm món");
		}

		Product p = productRepository.findById(request.productId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món"));

		// Upsert order_item theo product
		OrderItem target = o.getItems() == null ? null :
				o.getItems().stream()
						.filter(oi -> oi.getProduct() != null && oi.getProduct().getId().equals(p.getId()))
						.findFirst()
						.orElse(null);

		LocalDateTime now = LocalDateTime.now();
		if (target != null) {
			target.setQuantity(target.getQuantity() + request.quantity());
			target.setUpdated(now);
		} else {
			OrderItem oi = new OrderItem();
			oi.setOrder(o);
			oi.setProduct(p);
			oi.setQuantity(request.quantity());
			oi.setPrice(p.getPrice());
			oi.setUpdated(now);
			o.getItems().add(oi);
		}

		CafeOrder saved = cafeOrderRepository.save(o);
		staffQueueUpdateEventService.emitQueueUpdated();
		return toDetail(saved);
	}

	@Override
	@Transactional
	public OrderDetailResponse confirmOrder(Integer orderId, ConfirmOrderRequest request, HttpSession session) {
		Integer tableNumber = getTableNumber(session);
		if (tableNumber == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa chọn bàn");
		}
		if (request == null || request.items() == null || request.items().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giỏ hàng trống");
		}

		CafeTable table = cafeTableRepository.findByTableNumber(tableNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bàn không tồn tại"));

		// Nếu đã có order còn trạng thái chờ nhân viên xác nhận => xóa để tạo mới
		List<CafeOrder> existingPending = cafeOrderRepository.findByTable_TableNumberAndStatus(tableNumber, OrderStatus.PENDING);
		existingPending.forEach(cafeOrderRepository::delete);

		CafeOrder order = new CafeOrder();
		order.setTable(table);
		order.setStatus(OrderStatus.PENDING);

		LocalDateTime now = LocalDateTime.now();
		for (AddCartItemRequest itemReq : request.items()) {
			Product p = productRepository.findById(itemReq.productId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món"));

			OrderItem oi = new OrderItem();
			oi.setOrder(order);
			oi.setProduct(p);
			oi.setQuantity(itemReq.quantity());
			oi.setPrice(p.getPrice());
			oi.setUpdated(now);
			order.getItems().add(oi);
		}

		CafeOrder saved = cafeOrderRepository.save(order);
		staffQueueUpdateEventService.emitQueueUpdated();
		return toDetail(saved);
	}

	@Override
	@Transactional
	public void cancelOrder(Integer orderId, HttpSession session) {
		Integer tableNumber = getTableNumber(session);
		if (tableNumber == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa chọn bàn");
		}
		CafeOrder o = cafeOrderRepository.findByIdAndTable_TableNumber(orderId, tableNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy order"));

		if (o.getStatus() != OrderStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể hủy đơn lúc này");
		}
		cafeOrderRepository.delete(o);
		staffQueueUpdateEventService.emitQueueUpdated();
	}
}
