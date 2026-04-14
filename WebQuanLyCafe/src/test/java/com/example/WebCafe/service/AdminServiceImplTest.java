package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.RevenuePeriod;
import com.example.WebCafe.dto.request.RevenueQueryRequest;
import com.example.WebCafe.repository.AdminRepository;
import com.example.WebCafe.repository.CafeOrderRepository;
import com.example.WebCafe.repository.CategoryRepository;
import com.example.WebCafe.repository.PaymentRepository;
import com.example.WebCafe.repository.ProductRepository;
import com.example.WebCafe.repository.ShiftRepository;
import com.example.WebCafe.repository.StaffRepository;
import com.example.WebCafe.repository.StaffShiftRepository;
import com.example.WebCafe.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private StaffRepository staffRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private AdminRepository adminRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private ShiftRepository shiftRepository;
	@Mock
	private StaffShiftRepository staffShiftRepository;
	@Mock
	private CafeOrderRepository cafeOrderRepository;
	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private AdminServiceImpl adminService;

	@Test
	void wbAdmin01_queryNull_returnsNull() {
		Object dateRange = ReflectionTestUtils.invokeMethod(adminService, "resolveDateRange", (Object) null);

		assertNull(dateRange);
	}

	@Test
	void wbAdmin02_validFromTo_returnsExpectedDateRange() {
		RevenueQueryRequest query = new RevenueQueryRequest(null, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 10));

		Object dateRange = ReflectionTestUtils.invokeMethod(adminService, "resolveDateRange", query);

		assertNotNull(dateRange);
		LocalDateTime from = (LocalDateTime) ReflectionTestUtils.getField(dateRange, "from");
		LocalDateTime toExclusive = (LocalDateTime) ReflectionTestUtils.getField(dateRange, "toExclusive");
		assertEquals(LocalDateTime.of(2026, 4, 1, 0, 0), from);
		assertEquals(LocalDateTime.of(2026, 4, 11, 0, 0), toExclusive);
	}

	@Test
	void wbAdmin03_invalidFromTo_throwsBadRequest() {
		RevenueQueryRequest query = new RevenueQueryRequest(null, LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 1));

		ResponseStatusException ex = assertThrows(
				ResponseStatusException.class,
				() -> ReflectionTestUtils.invokeMethod(adminService, "resolveDateRange", query));

		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void wbAdmin04_periodDayWeekMonthYear_returnsNonNullDateRange() {
		for (RevenuePeriod period : RevenuePeriod.values()) {
			RevenueQueryRequest query = new RevenueQueryRequest(period, null, null);
			Object dateRange = ReflectionTestUtils.invokeMethod(adminService, "resolveDateRange", query);
			assertNotNull(dateRange);
		}
	}
}
