package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.AdminCategoryRequest;
import com.example.WebCafe.dto.request.AdminProductRequest;
import com.example.WebCafe.dto.request.RevenueQueryRequest;
import com.example.WebCafe.dto.request.StaffShiftsUpdateRequest;
import com.example.WebCafe.dto.request.StaffUpsertRequest;
import com.example.WebCafe.dto.response.CategoryAdminResponse;
import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.dto.response.RevenueByProductRow;
import com.example.WebCafe.dto.response.RevenueChartPoint;
import com.example.WebCafe.dto.response.RevenueDashboardResponse;
import com.example.WebCafe.dto.response.ShiftResponse;
import com.example.WebCafe.dto.response.StaffListResponse;
import com.example.WebCafe.model.Category;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.model.Shift;
import com.example.WebCafe.model.Staff;
import com.example.WebCafe.model.StaffShift;
import com.example.WebCafe.model.User;
import com.example.WebCafe.model.enums.ShiftTime;
import com.example.WebCafe.model.enums.Weekday;
import com.example.WebCafe.repository.AdminRepository;
import com.example.WebCafe.repository.CategoryRepository;
import com.example.WebCafe.repository.ProductRepository;
import com.example.WebCafe.repository.ShiftRepository;
import com.example.WebCafe.repository.StaffRepository;
import com.example.WebCafe.repository.StaffShiftRepository;
import com.example.WebCafe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final StaffRepository staffRepository;
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;
	private final ShiftRepository shiftRepository;
	private final StaffShiftRepository staffShiftRepository;

	public AdminServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
			StaffRepository staffRepository, UserRepository userRepository, AdminRepository adminRepository,
			PasswordEncoder passwordEncoder, ShiftRepository shiftRepository,
			StaffShiftRepository staffShiftRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.staffRepository = staffRepository;
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
		this.shiftRepository = shiftRepository;
		this.staffShiftRepository = staffShiftRepository;
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
	public List<CategoryAdminResponse> listCategories() {
		return categoryRepository.findAll().stream()
				.sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
				.map(this::toCategoryAdmin)
				.toList();
	}

	@Override
	@Transactional
	public CategoryAdminResponse createCategory(AdminCategoryRequest request) {
		Category c = new Category();
		c.setName(request.name().trim());
		c.setImageUrl(request.imageUrl());
		c.setHidden(Boolean.TRUE.equals(request.hidden()));
		categoryRepository.save(c);
		return toCategoryAdmin(c);
	}

	@Override
	@Transactional
	public CategoryAdminResponse updateCategory(Integer id, AdminCategoryRequest request) {
		Category c = categoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"));
		c.setName(request.name().trim());
		c.setImageUrl(request.imageUrl());
		if (request.hidden() != null) {
			c.setHidden(request.hidden());
		}
		return toCategoryAdmin(c);
	}

	@Override
	@Transactional
	public void deleteCategory(Integer id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục");
		}
		if (productRepository.countByCategory_Id(id) > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Còn sản phẩm trong danh mục — gỡ hoặc chuyển món trước");
		}
		categoryRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StaffListResponse> listStaff() {
		return staffRepository.findAll().stream().map(this::toStaffRow).toList();
	}

	@Override
	@Transactional
	public StaffListResponse createStaff(StaffUpsertRequest request) {
		if (request.password() == null || request.password().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần mật khẩu cho tài khoản mới");
		}
		if (userRepository.existsByUsername(request.username())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
		}
		User u = new User();
		u.setUsername(request.username().trim());
		u.setPassword(passwordEncoder.encode(request.password()));
		u.setFullName(request.fullName().trim());
		u.setPhone(request.phone());
		userRepository.save(u);

		Staff s = new Staff();
		s.setUser(u);
		s.setGender(request.gender());
		s.setAge(request.age());
		s.setActive(request.active() != null ? request.active() : true);
		staffRepository.save(s);

		return toStaffRow(staffRepository.findById(u.getId()).orElseThrow());
	}

	@Override
	@Transactional
	public void updateStaff(Integer userId, StaffUpsertRequest request) {
		Staff s = staffRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));
		if (adminRepository.existsById(userId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không sửa tài khoản quản trị tại đây");
		}
		User u = s.getUser();
		String newName = request.username().trim();
		if (!newName.equals(u.getUsername()) && userRepository.existsByUsername(newName)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
		}
		u.setUsername(newName);
		if (request.password() != null && !request.password().isBlank()) {
			u.setPassword(passwordEncoder.encode(request.password()));
		}
		u.setFullName(request.fullName().trim());
		u.setPhone(request.phone());
		s.setGender(request.gender());
		s.setAge(request.age());
		if (request.active() != null) {
			s.setActive(request.active());
		}
	}

	@Override
	@Transactional
	public void deleteStaff(Integer userId) {
		if (!staffRepository.existsById(userId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên");
		}
		if (adminRepository.existsById(userId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không xóa tài khoản quản trị");
		}
		staffShiftRepository.deleteByStaffUserId(userId);
		staffRepository.deleteById(userId);
		userRepository.deleteById(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ShiftResponse> listShiftDefinitions() {
		return shiftRepository.findAll().stream()
				.sorted(Comparator
						.comparing(Shift::getDayOfWeek, Comparator.comparingInt(Weekday::ordinal))
						.thenComparing(Shift::getShiftTime, Comparator.comparingInt(ShiftTime::ordinal)))
				.map(sh -> new ShiftResponse(sh.getId(), sh.getDayOfWeek(), sh.getShiftTime()))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Integer> getStaffShiftIds(Integer userId) {
		if (!staffRepository.existsById(userId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên");
		}
		return staffShiftRepository.findByStaffUserId(userId).stream()
				.map(ss -> ss.getShift().getId())
				.sorted()
				.toList();
	}

	@Override
	@Transactional
	public void replaceStaffShifts(Integer userId, StaffShiftsUpdateRequest request) {
		Staff staff = staffRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));
		if (adminRepository.existsById(userId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không áp dụng ca cho quản trị");
		}
		staffShiftRepository.deleteByStaffUserId(userId);
		staffShiftRepository.flush();
		List<Integer> ids = request.shiftIds() == null ? List.of() : request.shiftIds();
		for (Integer sid : ids) {
			Shift shift = shiftRepository.findById(sid)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ca không hợp lệ: " + sid));
			StaffShift ss = new StaffShift();
			ss.setStaff(staff);
			ss.setShift(shift);
			staffShiftRepository.save(ss);
		}
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

	private CategoryAdminResponse toCategoryAdmin(Category c) {
		long cnt = productRepository.countByCategory_Id(c.getId());
		return new CategoryAdminResponse(
				c.getId(),
				c.getName(),
				c.getImageUrl(),
				Boolean.TRUE.equals(c.getHidden()),
				cnt);
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
		var u = s.getUser();
		return new StaffListResponse(
				s.getUserId(),
				u.getUsername(),
				u.getFullName(),
				u.getPhone(),
				s.getGender(),
				s.getAge(),
				s.getActive());
	}
}
