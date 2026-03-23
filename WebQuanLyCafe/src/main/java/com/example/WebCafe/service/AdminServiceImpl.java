package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.RevenueQueryRequest;
import com.example.WebCafe.dto.request.StaffUpsertRequest;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.dto.response.RevenueByProductRow;
import com.example.WebCafe.dto.response.RevenueChartPoint;
import com.example.WebCafe.dto.response.RevenueDashboardResponse;
import com.example.WebCafe.dto.response.StaffListResponse;
import com.example.WebCafe.model.Category;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.model.Staff;
import com.example.WebCafe.repository.ProductRepository;
import com.example.WebCafe.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

	private final ProductRepository productRepository;
	private final StaffRepository staffRepository;

	public AdminServiceImpl(ProductRepository productRepository, StaffRepository staffRepository) {
		this.productRepository = productRepository;
		this.staffRepository = staffRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> listProducts() {
		return productRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProduct(Integer id) {
		return productRepository.findById(id).map(this::toResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món"));
	}

	@Override
	public ProductResponse createProduct(AdminProductRequest request) {
		throw new UnsupportedOperationException("Thêm món chi tiết — triển khai ở bước service tiếp theo");
	}

	@Override
	public ProductResponse updateProduct(Integer id, AdminProductRequest request) {
		throw new UnsupportedOperationException("Cập nhật món — triển khai ở bước service tiếp theo");
	}

	@Override
	public void deleteProduct(Integer id) {
		if (!productRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy món");
		}
		productRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StaffListResponse> listStaff() {
		return staffRepository.findAll().stream().map(this::toStaffRow).toList();
	}

	@Override
	public void createStaff(StaffUpsertRequest request) {
		throw new UnsupportedOperationException("Thêm nhân viên — triển khai ở bước service tiếp theo");
	}

	@Override
	public void updateStaff(Integer userId, StaffUpsertRequest request) {
		throw new UnsupportedOperationException("Sửa nhân viên — triển khai ở bước service tiếp theo");
	}

	@Override
	public void deleteStaff(Integer userId) {
		throw new UnsupportedOperationException("Xóa nhân viên — triển khai ở bước service tiếp theo");
	}

	@Override
	@Transactional(readOnly = true)
	public RevenueDashboardResponse revenue(RevenueQueryRequest query) {
		return new RevenueDashboardResponse(
				BigDecimal.ZERO,
				0L,
				List.<RevenueByProductRow>of(),
				List.<RevenueChartPoint>of());
	}

	@Override
	public byte[] exportRevenueExcel(RevenueQueryRequest query) {
		throw new UnsupportedOperationException("Xuất Excel — triển khai ở bước service tiếp theo");
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

	private StaffListResponse toStaffRow(Staff s) {
		return new StaffListResponse(
				s.getUserId(),
				s.getUser().getUsername(),
				s.getUser().getFullName(),
				s.getGender(),
				s.getAge(),
				s.getActive());
	}
}
