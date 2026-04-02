package com.example.WebCafe.service;

import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.model.Category;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

	private final ProductRepository productRepository;
	private final ProductInventoryService productInventoryService;

	public MenuServiceImpl(ProductRepository productRepository, ProductInventoryService productInventoryService) {
		this.productRepository = productRepository;
		this.productInventoryService = productInventoryService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listMenuItems() {
		return productRepository.findByAvailableTrueOrderByNameAsc().stream()
				.filter(p -> {
					Category c = p.getCategory();
					if (c == null) {
						return true;
					}
					return !Boolean.TRUE.equals(c.getHidden());
				})
				.filter(productInventoryService::isVisibleOnMenu)
				.map(this::toResponse)
				.toList();
	}

	private ProductResponse toResponse(Product p) {
		Category c = p.getCategory();
		Integer categoryId = c != null ? c.getId() : null;
		String categoryName = c != null ? c.getName() : null;
		String categoryImageUrl = c != null ? c.getImageUrl() : null;
		return new ProductResponse(
				p.getId(),
				p.getName(),
				p.getDescription(),
				p.getPrice(),
				p.getImageUrl(),
				p.getQuantity(),
				p.getAvailable(),
				categoryId,
				categoryName,
				categoryImageUrl);
	}
}
