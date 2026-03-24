package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.PaymentRequest;
import com.example.WebCafe.dto.request.StaffProductUpdateRequest;
import com.example.WebCafe.dto.response.CategoryOptionResponse;
import com.example.WebCafe.dto.response.OrderQueueResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.model.Category;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.repository.CategoryRepository;
import com.example.WebCafe.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public StaffServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
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
		return List.of();
	}

	@Override
	public void confirmOrder(Integer orderId) {
		// TODO: xác nhận đơn phía nhân viên
	}

	@Override
	public void recordPayment(Integer orderId, PaymentRequest request) {
		// TODO: ghi payment + cập nhật trạng thái đơn
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
