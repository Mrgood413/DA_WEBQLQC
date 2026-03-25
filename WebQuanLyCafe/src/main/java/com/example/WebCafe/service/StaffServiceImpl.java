package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.PaymentRequest;
import com.example.WebCafe.dto.request.StaffProductUpdateRequest;
import com.example.WebCafe.dto.response.OrderItemResponse;
import com.example.WebCafe.dto.response.CategoryOptionResponse;
import com.example.WebCafe.dto.response.OrderQueueResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.model.CafeOrder;
import com.example.WebCafe.model.Category;
import com.example.WebCafe.model.OrderItem;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.model.Payment;
import com.example.WebCafe.model.enums.OrderStatus;
import com.example.WebCafe.repository.CafeOrderRepository;
import com.example.WebCafe.repository.CategoryRepository;
import com.example.WebCafe.repository.PaymentRepository;
import com.example.WebCafe.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class StaffServiceImpl implements StaffService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final CafeOrderRepository cafeOrderRepository;
	private final PaymentRepository paymentRepository;
	private final OrderMilestoneEventService orderMilestoneEventService;

	public StaffServiceImpl(ProductRepository productRepository,
			CategoryRepository categoryRepository,
			CafeOrderRepository cafeOrderRepository,
			PaymentRepository paymentRepository,
			OrderMilestoneEventService orderMilestoneEventService) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.cafeOrderRepository = cafeOrderRepository;
		this.paymentRepository = paymentRepository;
		this.orderMilestoneEventService = orderMilestoneEventService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listProductsForStaff() {
		return productRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryOptionResponse> listCategories() {
		return categoryRepository.findAll().stream()
				.sorted(Comparator.comparing(Category::getName))
				.map(c -> new CategoryOptionResponse(c.getId(), c.getName()))
				.toList();
	}

	@Override
	@Transactional
	public ProductResponse createProduct(AdminProductRequest request) {
		Category cat = categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại"));
		Product p = new Product();
		p.setName(request.name().trim());
		p.setDescription(request.description());
		p.setPrice(request.price());
		if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
			p.setImageUrl(request.imageUrl().trim());
		}
		p.setQuantity(request.quantity() != null ? request.quantity() : 0);
		p.setAvailable(request.available() != null ? request.available() : Boolean.TRUE);
		p.setCategory(cat);
		return toResponse(productRepository.save(p));
	}

	@Override
	@Transactional
	public ProductResponse updateProduct(Integer productId, StaffProductUpdateRequest request) {
		Product p = productRepository.findById(productId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món"));
		if (request.name() != null && !request.name().isBlank()) {
			p.setName(request.name().trim());
		}
		if (request.description() != null) {
			p.setDescription(request.description());
		}
		if (request.price() != null) {
			if (request.price().compareTo(BigDecimal.ZERO) < 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá không hợp lệ");
			}
			p.setPrice(request.price());
		}
		if (request.imageUrl() != null) {
			p.setImageUrl(request.imageUrl().isBlank() ? null : request.imageUrl().trim());
		}
		if (request.quantity() != null) {
			p.setQuantity(request.quantity());
		}
		if (request.available() != null) {
			p.setAvailable(request.available());
		}
		if (request.categoryId() != null) {
			Category cat = categoryRepository.findById(request.categoryId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại"));
			p.setCategory(cat);
		}
		return toResponse(productRepository.save(p));
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderQueueResponse> listQueue() {
		return cafeOrderRepository.findAll().stream()
				.filter(o -> o.getStatus() == OrderStatus.PENDING
						|| o.getStatus() == OrderStatus.PREPARING
						|| o.getStatus() == OrderStatus.DONE
						|| o.getStatus() == OrderStatus.PAID)
				.sorted(Comparator.comparing(CafeOrder::getId).reversed())
				.map(this::toQueue)
				.toList();
	}

	@Override
	public void confirmOrder(Integer orderId) {
		CafeOrder o = cafeOrderRepository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy order"));

		int milestone;
		OrderStatus next;
		if (o.getStatus() == OrderStatus.PENDING) {
			next = OrderStatus.PREPARING;
			milestone = 2;
		} else if (o.getStatus() == OrderStatus.PREPARING) {
			next = OrderStatus.DONE;
			milestone = 3;
		} else {
			// DONE/PAID: không xử lý bước tiếp theo tại đây
			return;
		}

		o.setStatus(next);
		cafeOrderRepository.save(o);
		orderMilestoneEventService.emitMilestone(orderId, milestone);
	}

	@Override
	public void recordPayment(Integer orderId, PaymentRequest request) {
		CafeOrder o = cafeOrderRepository.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy order"));

		// Chỉ cho phép thanh toán khi đã xong món
		if (o.getStatus() != OrderStatus.DONE) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chưa thể thanh toán lúc này");
		}

		BigDecimal total = o.getItems().stream()
				.map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Payment payment = new Payment();
		payment.setOrder(o);
		payment.setMethod(request.method());
		payment.setTotalAmount(total);

		paymentRepository.save(payment);

		o.setStatus(OrderStatus.PAID);
		cafeOrderRepository.save(o);
	}

	private OrderQueueResponse toQueue(CafeOrder o) {
		Integer tableNumber = o.getTable() != null ? o.getTable().getTableNumber() : null;
		LocalDateTime createdAt = o.getCreatedAt();

		List<OrderItemResponse> items = o.getItems() == null ? List.of() :
				o.getItems().stream().map(this::toItem).toList();

		LocalDateTime lastItemUpdatedAt = items.stream()
				.map(OrderItemResponse::updatedAt)
				.filter(x -> x != null)
				.max(LocalDateTime::compareTo)
				.orElse(null);

		boolean hasMoreItems = createdAt != null && lastItemUpdatedAt != null && lastItemUpdatedAt.isAfter(createdAt);

		return new OrderQueueResponse(
				o.getId(),
				tableNumber,
				o.getStatus(),
				items.size(),
				createdAt,
				lastItemUpdatedAt,
				hasMoreItems,
				items);
	}

	private OrderItemResponse toItem(OrderItem oi) {
		Product p = oi.getProduct();
		Integer productId = p != null ? p.getId() : null;
		String productName = p != null ? p.getName() : null;
		BigDecimal unitPrice = oi.getPrice();
		Integer qty = oi.getQuantity();
		BigDecimal lineTotal = (unitPrice == null || qty == null)
				? BigDecimal.ZERO
				: unitPrice.multiply(BigDecimal.valueOf(qty));
		return new OrderItemResponse(
				oi.getId(),
				productId,
				productName,
				qty,
				unitPrice,
				lineTotal,
				oi.getUpdated());
	}

	private ProductResponse toResponse(Product p) {
		Category c = p.getCategory();
		Integer categoryId = c != null ? c.getId() : null;
		String categoryName = c != null ? c.getName() : null;
		return new ProductResponse(
				p.getId(),
				p.getName(),
				p.getDescription(),
				p.getPrice(),
				p.getImageUrl(),
				p.getQuantity(),
				p.getAvailable(),
				categoryId,
				categoryName);
	}
}
