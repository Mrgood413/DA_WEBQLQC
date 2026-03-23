package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.PaymentRequest;
import com.example.WebCafe.dto.request.StaffProductUpdateRequest;
import com.example.WebCafe.dto.response.OrderQueueResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.model.Category;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

	private final ProductRepository productRepository;

	public StaffServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listProductsForStaff() {
		return productRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public ProductResponse updateProduct(Integer productId, StaffProductUpdateRequest request) {
		Product p = productRepository.findById(productId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món"));
		if (request.quantity() != null) {
			p.setQuantity(request.quantity());
		}
		if (request.available() != null) {
			p.setAvailable(request.available());
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
